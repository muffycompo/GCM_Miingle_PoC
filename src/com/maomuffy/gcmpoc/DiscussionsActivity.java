package com.maomuffy.gcmpoc;

import static com.maomuffy.gcmpoc.GCMPoCUtilities.DISPLAY_MESSAGE_ACTION;
import static com.maomuffy.gcmpoc.GCMPoCUtilities.EXTRA_MESSAGE;
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

import com.google.android.gcm.GCMRegistrar;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DiscussionsActivity extends Activity implements OnClickListener,
		OnKeyListener {

	Button btSend;
	TextView tvMessage, tvQuestion;
	EditText etComposeText;
	String composeMessageText, fullname, receivedMessage;
	Integer category_id, member_id;
	ProgressDialog pDialog;
	NetworkConnectionDetector con;
	Boolean isInternetAvailable = false;
	private final static String TAG = "GCM PoC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussions);

		con = new NetworkConnectionDetector(getApplicationContext());

		SharedPreferences sharedPref = getSharedPreferences(
				SHAREDPREF_LOCATION, MODE_PRIVATE);
		int isLoggedIn = sharedPref.getInt("isLoggedIn", 0);

		if (isLoggedIn == 1) {
			member_id = sharedPref.getInt("member_id", 0);
			fullname = sharedPref.getString("fullname", "");
			receivedMessage = sharedPref.getString("receivedMessage", "");

			tvMessage = (TextView) findViewById(R.id.tvMessage);
			tvQuestion = (TextView) findViewById(R.id.tvQuestion);
			etComposeText = (EditText) findViewById(R.id.etComposeText);
			btSend = (Button) findViewById(R.id.btSend);

			btSend.setOnClickListener(this);
			etComposeText.setOnKeyListener(this);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discussions, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		composeMessageText = etComposeText.getText().toString();
		category_id = 1;
		Log.i(TAG, "Sent: " + member_id);
		new discussionAPICall().execute(member_id.toString(),
				composeMessageText, category_id.toString());

	}

	private class discussionAPICall extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// Detach operation from UI thread
			
			// GCM Stuff
			GCMRegistrar.checkDevice(DiscussionsActivity.this);
			GCMRegistrar.checkManifest(DiscussionsActivity.this);
			registerReceiver(handleDiscussionMessage, new IntentFilter(
					DISPLAY_MESSAGE_ACTION));

			try {

				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
				HttpPost httppost = new HttpPost(
						"http://10.0.2.2/gcm_poc/public/discussions");

				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("member_id", params[0]));
				nvp.add(new BasicNameValuePair("body", params[1]));
				nvp.add(new BasicNameValuePair("category_id", params[2]));
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
			// pDialog = new ProgressDialog(DiscussionsActivity.this);
			// pDialog.setMessage("Creating Account...");
			// pDialog.setCancelable(true);
			// pDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... v) {
			super.onProgressUpdate(v);
			// pDialog.setProgress(v[0]);
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
					String dt = json.getString("data");
					// member_id = json.getInt("data");
					Log.i(TAG, "New Message: " + composeMessageText);
					Log.i(TAG, "Data: " + dt);

					// Store Returned User Profile Object in Shared Preferences

					// Create object of SharedPreferences.
					// SharedPreferences sharedPref =
					// getSharedPreferences(SHAREDPREF_LOCATION, 0);
					// // now get Editor
					// SharedPreferences.Editor editor = sharedPref.edit();
					// // put your value
					// editor.putInt("isLoggedIn", 1);
					// editor.putString("email", email);
					// editor.putString("fullname", fullname);
					// editor.putString("gsmno", gsmno);
					// editor.putInt("member_id", member_id);
					// // commits your edits
					// editor.commit();

					// Intent iDashboard = new Intent(ct,
					// DashboardActivity.class);
					// iDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// iDashboard.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					// startActivity(iDashboard);

					// pDialog.dismiss();
					// Notify User of Success
					// Toast toast = Toast.makeText(ct, "Signup Successful: "
					// + fullname, Toast.LENGTH_SHORT);
					// toast.show();
					// finish();
					if (receivedMessage.equalsIgnoreCase("")) {
						tvMessage.append("->" + composeMessageText + "\n\n");
					} else {
						tvMessage.append("<- " + receivedMessage + "\n\n");
						tvMessage.append("-> " + composeMessageText + "\n\n");
					}
					
					etComposeText.setText("");
				} else {

					// pDialog.dismiss();

					// Notify User of Failure
					Toast toast = Toast.makeText(ct, "An error occured!",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (JSONException e) {
				Toast.makeText(ct, "Network error...", Toast.LENGTH_SHORT)
						.show();
				// pDialog.dismiss();
			}
		}
	}

	private final BroadcastReceiver handleDiscussionMessage = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			WakeLockerUtility.acquire(getApplicationContext());
			
			SharedPreferences sharedPref =
					 getSharedPreferences(SHAREDPREF_LOCATION, 0);
					 SharedPreferences.Editor editor = sharedPref.edit();
					 editor.putString("receivedMessage", newMessage);
					 editor.commit();
			
			tvMessage.append("<- " + newMessage + "\n\n");
			//Toast.makeText(getApplicationContext(), "New Message Received: " + newMessage, Toast.LENGTH_LONG).show();
			WakeLockerUtility.release();
		}
		
	};
	
	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(handleDiscussionMessage);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if ((event.getAction() == KeyEvent.ACTION_DOWN)
				&& (keyCode == event.KEYCODE_ENTER)) {
			return true;
		}

		return false;
	}

}
