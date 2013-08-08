package com.maomuffy.gcmpoc;
import static com.maomuffy.gcmpoc.GCMPoCUtilities.SHAREDPREF_LOCATION;
import static com.maomuffy.gcmpoc.GCMPoCUtilities.showMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.maomuffy.gcmpoc.DiscussionsActivity;
import com.maomuffy.gcmpoc.R;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context ct, String arg1) {
		
		
	}

	@Override
	protected void onMessage(Context ct, Intent intent) {
		Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("message");
        
        showMessage(ct, message);
        // notifies user
        generateNotification(ct, message);
		
	}

	@Override
	protected void onRegistered(Context ct, String arg1) {
		
		
	}

	@Override
	protected void onUnregistered(Context ct, String arg1) {
		
		// Tell API to update RegistrationID
	}
	
	private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, DiscussionsActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
//        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }
	
}