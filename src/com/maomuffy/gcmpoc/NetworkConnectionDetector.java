package com.maomuffy.gcmpoc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectionDetector {
	private Context ct;

	public NetworkConnectionDetector(Context c) {
		this.ct = c;
	}

	public boolean isNetworkEnabled(){
        ConnectivityManager connectivity = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null)
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null)
                  for (int i = 0; i < info.length; i++)
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
	
	public void showDialog(Context context, String title, String message, Boolean status) {

    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        //.setIcon((status) ? R.drawable.success : R.drawable.fail)
        .setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog aDialog = builder.create();
        aDialog.show();
    }
	
}
