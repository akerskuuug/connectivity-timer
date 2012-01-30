package com.connectivitymanager.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.connectivitymanager.core.DisconnectTimerActivity;

public class TimedWifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		WifiManager wfMgr =
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		CharSequence tickerText = "";
		String description = "";

		boolean setWifiTo = intent.getExtras().getBoolean("wifi_enable", false);

		wfMgr.setWifiEnabled(setWifiTo);

		NotificationManager nm =
				(NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

		// Create the visuals for the notification
		int icon = android.R.drawable.star_on;

		if (setWifiTo) {
			tickerText =
					context.getString(com.connectivitymanager.R.string.wifi_enabled);
			description =
					context.getString(com.connectivitymanager.R.string.wifi_enabled_set_time);
		} else {

			tickerText =
					context.getString(com.connectivitymanager.R.string.wifi_disabled);
			description =
					context.getString(com.connectivitymanager.R.string.wifi_disabled_set_time);
		}
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// Decide what will be displayed in the notification bar
		// and what will happen when the notification is clicked
		Intent notificationIntent =
				new Intent(context, DisconnectTimerActivity.class);
		PendingIntent contentIntent =
				PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, tickerText, description,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Show the notification
		nm.notify(3218, notification);

	}

}
