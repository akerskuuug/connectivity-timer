package com.connectivitymanager.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.connectivitymanager.utility.Constants;

public class RetryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get calendar
		Calendar cal = Calendar.getInstance();
		// Get the extras (properties) from the current Intent
		Bundle extras = intent.getExtras();

		AlarmManager am =
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		// Get Wi-Fi management functionality
		WifiManager wfMgr =
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		// Enable Wi-Fi
		wfMgr.setWifiEnabled(true);

		PendingIntent sender =
				PendingIntent.getBroadcast(context, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		// Wait until Wi-Fi has been enabled properly
		while (wfMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
		}

		ConnectivityManager cnMgr =
				(ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wait so that Wi-Fi has a possibility to connect (UGLY CODE)
		int i = 0;
		while (!cnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting() && i < 20000) {
			i++;
		}

		// If Wi-Fi is disconnected, disable it and try again in 30 minutes.
		// If it has obtained a connection, start monitoring the network
		// connection again

		if (!cnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting()) {

			// Disable Wi-Fi
			wfMgr.setWifiEnabled(false);

			cal.add(Constants.DURATION, 30);
			// Set the alarm
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		} else {
			am.cancel(sender);
			// Duration until Wi-Fi connection is tested again
			cal.add(Constants.DURATION, 15);

			// Create a new Intent which will trigger a DisconnectReceiver
			// instead
			Intent disconnectAlarmIntent =
					new Intent(context, DisconnectReceiver.class);

			// Put the Intents from the old intent into the new one
			disconnectAlarmIntent.putExtras(extras);

			PendingIntent disconnectSender =
					PendingIntent.getBroadcast(context, 0,
							disconnectAlarmIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

			am.cancel(disconnectSender);

			// Resume Wi-Fi disconnection checks
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					disconnectSender);

		}

	}
}
