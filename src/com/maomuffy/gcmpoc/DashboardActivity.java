package com.maomuffy.gcmpoc;

import static com.maomuffy.gcmpoc.GCMPoCUtilities.SHAREDPREF_LOCATION;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DashboardActivity extends Activity implements OnClickListener {

	Integer member_id, isLoggedIn;
	String fullname;
	TextView tvDashboardName;
	Button btCategories, btProfile, btLogout;
	NetworkConnectionDetector con;
	Boolean isInternetAvailable = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		con = new NetworkConnectionDetector(getApplicationContext());

		isInternetAvailable = con.isNetworkEnabled();

		SharedPreferences sharedPref = getSharedPreferences(
				SHAREDPREF_LOCATION, MODE_PRIVATE);
		isLoggedIn = sharedPref.getInt("isLoggedIn", 0);
		if (isLoggedIn == 1) {
			member_id = sharedPref.getInt("member_id", 0);
			fullname = sharedPref.getString("fullname", "");

			tvDashboardName = (TextView) findViewById(R.id.tvDashboardName);
			btCategories = (Button) findViewById(R.id.btCategories);
			btProfile = (Button) findViewById(R.id.btProfile);
			btLogout = (Button) findViewById(R.id.btLogout);

			tvDashboardName.setText(fullname);

			btCategories.setOnClickListener(this);
			btProfile.setOnClickListener(this);
			btLogout.setOnClickListener(this);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btCategories:
			if (isInternetAvailable) {
//				Intent iCategories = new Intent(this, CategoriesActivity.class);
				Intent iCategories = new Intent(this, DiscussionsActivity.class);
				startActivity(iCategories);
			} else {
				con.showDialog(this, "Network Error", "Internet Required!",
						isInternetAvailable);
			}
			break;

		case R.id.btProfile:
			if (isInternetAvailable) {
//				Intent iProfile = new Intent(this, ProfileActivity.class);
//				startActivity(iProfile);
			} else {
				con.showDialog(this, "Network Error", "Internet Required!",
						isInternetAvailable);
			}
			break;

		case R.id.btLogout:
			// Destroy Shared Preferences
			SharedPreferences sharedPref = getSharedPreferences(SHAREDPREF_LOCATION, 0);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.clear(); // clear all data.
			editor.commit(); // Don't forgot to commit SharedPreferences.
			Intent iLogout = new Intent(this, MainActivity.class);
			iLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iLogout);
			break;

		default:
			break;
		}

	}

}
