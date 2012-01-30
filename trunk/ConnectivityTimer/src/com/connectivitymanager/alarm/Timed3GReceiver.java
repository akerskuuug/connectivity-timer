package com.connectivitymanager.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.connectivitymanager.core.Timed3GActivity;
import com.connectivitymanager.utility.Tools;

public class Timed3GReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean setEnabled = intent.getExtras().getBoolean("tg_enable", false);
		CharSequence tickerText = "";
		String description = "";

		Tools.set3GEnabled(context, setEnabled);

		NotificationManager nm =
				(NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

		// Create the visuals for the notification
		int icon = android.R.drawable.star_on;

		if (setEnabled) {
			tickerText =
					context.getString(com.connectivitymanager.R.string.tg_enabled);
			description =
					context.getString(com.connectivitymanager.R.string.tg_enabled_set_time);
		} else {

			tickerText =
					context.getString(com.connectivitymanager.R.string.tg_disabled);
			description =
					context.getString(com.connectivitymanager.R.string.tg_disabled_set_time);
		}
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// Decide what will be displayed in the notification bar
		// and what will happen when the notification is clicked
		Intent notificationIntent = new Intent(context, Timed3GActivity.class);
		PendingIntent contentIntent =
				PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, tickerText, description,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Show the notification
		nm.notify(3217, notification);

	}

}
