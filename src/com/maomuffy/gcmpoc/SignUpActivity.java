package com.maomuffy.gcmpoc;

import static com.maomuffy.gcmpoc.GCMPoCUtilities.DISPLAY_MESSAGE_ACTION;
import static com.maomuffy.gcmpoc.GCMPoCUtilities.EXTRA_MESSAGE;
import static com.maomuffy.gcmpoc.GCMPoCUtilities.SENDER_ID;
import static com.maomuffy.gcmpoc.GCMPoCUtilities.SHAREDPREF_LOCATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SignUpActivity extends Activity implements OnClickListener {

	EditText etFullName, etEmail, etGsmNo, etPassword;
	Button btCancel, btSignUp;
	ProgressDialog pDialog;
	GoogleCloudMessaging gcm;
	// flag for Internet connection status
	Boolean isInternetAvailable = false;
	NetworkConnectionDetector con;
	private final static String TAG = "GCM PoC";
	String regId, fullname, email, gsmno, password;
	Integer member_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		con = new NetworkConnectionDetector(getApplicationContext());

		SharedPreferences sharedPref = getSharedPreferences(
				SHAREDPREF_LOCATION, MODE_PRIVATE);
		int isLoggedIn = sharedPref.getInt("isLoggedIn", 0);

		etFullName = (EditText) findViewById(R.id.etFullName);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etGsmNo = (EditText) findViewById(R.id.etGsmNo);
		etPassword = (EditText) findViewById(R.id.etPassword);

		btCancel = (Button) findViewById(R.id.btCancel);
		btSignUp = (Button) findViewById(R.id.btSignUp);

		btCancel.setOnClickListener(this);
		btSignUp.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

	@Override
	protected void onPause() {
		
//		unregisterReceiver(handleReceivedMessage);
		super.onPause();
	}

//	@Override
//	protected void onStop() {
//		try {
//			unregisterReceiver(handleReceivedMessage);
//		} catch (IllegalArgumentException e) {
//			Log.i(TAG, "We have not registered any Receiver yet!");
//		}
//		
//		super.onStop();
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btSignUp:
			isInternetAvailable = con.isNetworkEnabled();
			if (isInternetAvailable) {
				
				// GCM Stuff
				GCMRegistrar.checkDevice(this);
				GCMRegistrar.checkManifest(this);
				registerReceiver(handleReceivedMessage, new IntentFilter(
						DISPLAY_MESSAGE_ACTION));
				
				fullname = etFullName.getText().toString();
				email = etEmail.getText().toString();
				gsmno = etGsmNo.getText().toString();
				password = etPassword.getText().toString();
				Log.i(TAG, "Sent: " + etFullName);
				new signupAPICall().execute(fullname, email, gsmno, password);
			} else {
				con.showDialog(this, "Network Error", "Internet Required!",
						isInternetAvailable);
			}
			break;
		case R.id.btCancel:
			Intent iCancel = new Intent(this, MainActivity.class);
			iCancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iCancel);
			break;

		default:
			break;
		}

	}

	private class signupAPICall extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// Detach operation from UI thread

			try {
				
				// Get regID from GCM
				regId = GCMRegistrar.getRegistrationId(SignUpActivity.this);
				
				if (regId.equals("")) {
					gcm = GoogleCloudMessaging.getInstance(SignUpActivity.this);
//					GCMRegistrar.register(SignUpActivity.this, SENDER_ID);
					regId = gcm.register(SENDER_ID);
				}
				Log.i(TAG, "Registered to GCM: " + regId);
				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
				HttpPost httppost = new HttpPost(
						"http://10.0.2.2/gcm_poc/public/members");

				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("fullname", params[0]));
				nvp.add(new BasicNameValuePair("email", params[1]));
				nvp.add(new BasicNameValuePair("gsmno", params[2]));
				nvp.add(new BasicNameValuePair("password", params[3]));
				nvp.add(new BasicNameValuePair("gcm_reg_code", regId));
				httppost.setEntity(new UrlEncodedFormEntity(nvp));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				MUtils ists = new MUtils();
				String str = ists.inputStreamToString(
						response.getEntity().getContent()).toString();
				// Return result as string
				Log.i(TAG, "Got: " + str);
				return str;

			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "Network error...",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Network error...",
						Toast.LENGTH_SHORT).show();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUpActivity.this);
			pDialog.setMessage("Creating Account...");
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... v) {
			super.onProgressUpdate(v);
			pDialog.setProgress(v[0]);
		}

		@Override
		protected void onPostExecute(String r) {
			// Attach result back to UI thread
			super.onPostExecute(r);

			Context ct = getApplicationContext();

			// Parse JSON from API Response
			try {
				JSONObject json = new JSONObject(r);

				if (json.getString("status").toLowerCase(Locale.getDefault())
						.equalsIgnoreCase("success")) {
					// // Get Data object
					member_id = json.getInt("data");
					Log.i(TAG, "Member ID: " + member_id);

					// Store Returned User Profile Object in Shared Preferences

					// Create object of SharedPreferences.
					 SharedPreferences sharedPref =
					 getSharedPreferences(SHAREDPREF_LOCATION, 0);
					// now get Editor
					 SharedPreferences.Editor editor = sharedPref.edit();
					// put your value
					 editor.putInt("isLoggedIn", 1);
					 editor.putString("email", email);
					 editor.putString("fullname", fullname);
					 editor.putString("gsmno", gsmno);
					 editor.putInt("member_id", member_id);
					// commits your edits
					 editor.commit();

					 Intent iDashboard = new Intent(ct, DashboardActivity.class);
//					 iDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					 iDashboard.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					 startActivity(iDashboard);

					pDialog.dismiss();
					// Notify User of Success
//					Toast toast = Toast.makeText(ct, "Signup Successful: "
//							+ fullname, Toast.LENGTH_SHORT);
//					toast.show();
//					finish();
				} else {

					pDialog.dismiss();

					// Notify User of Failure
					Toast toast = Toast.makeText(ct, "An error occured!",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (JSONException e) {
				Toast.makeText(ct, "Network error...", Toast.LENGTH_SHORT)
						.show();
				pDialog.dismiss();
			}
		}

	}
	
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver handleReceivedMessage = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			WakeLockerUtility.acquire(getApplicationContext());
			Toast.makeText(getApplicationContext(), "New Message Received: " + newMessage, Toast.LENGTH_LONG).show();
			WakeLockerUtility.release();
		}
		
	};
	
	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(handleReceivedMessage);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
