package com.connectivitymanager.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.connectivitymanager.core.DisconnectTimerActivity;
import com.connectivitymanager.utility.Constants;
import com.connectivitymanager.utility.Tools;

public class DisconnectReceiver extends BroadcastReceiver {
	private WifiManager wfMgr;

	@Override
	public void onReceive(Context context, Intent intent) {

		Calendar cal = Calendar.getInstance();
		AlarmManager am =
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		// Get the WifiManager service to read and control Wi-Fi
		wfMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		// Get the maximum number of repeats, to turn off Wi-Fi at the right
		// time
		Bundle extras = intent.getExtras();
		int repeats = extras.getInt(Constants.WATCHER_ALARM_REPEATS, 0);
		int maxRepeats = extras.getInt(Constants.WATCHER_MAX_REPEATS, 3);
		boolean retry = extras.getBoolean(Constants.WATCHER_RETRY, false);
		boolean exit = extras.getBoolean(Constants.WATCHER_EXIT, true);
		boolean t_g_enable =
				extras.getBoolean(Constants.WATCHER_3G_ENABLE, false);
		boolean t_g_disable =
				extras.getBoolean(Constants.WATCHER_3G_DISABLE, false);
		boolean dis_by_this =
				extras.getBoolean(Constants.WATCHER_DISABLED_BY_THIS, false);

		repeats++;

		// If Wi-Fi is disabled, do nothing
		if (wfMgr.isWifiEnabled()) {

			// Check if Wi-Fi is connected
			if (wfMgr.getConnectionInfo().getNetworkId() == -1) {

				// When Wi-Fi has been disconnected for the preset
				// Constants.DURATION,
				// disable it
				if (repeats >= maxRepeats) {
					wfMgr.setWifiEnabled(false);

					if (t_g_enable || t_g_disable) {

						Tools.set3GEnabled(context, t_g_enable && !t_g_disable);

					}

					Tools.showNotification(
							context,
							DisconnectTimerActivity.class,
							context.getString(com.connectivitymanager.R.string.wifi_disabled),
							context.getString(com.connectivitymanager.R.string.wifi_disabled_lost_conn),
							3216);

					repeats = 0;
					dis_by_this = true;

				} else {

					// Wait 15 minutes before checking next time
					cal.add(Constants.DURATION, 15);

				}
			} else {

				// If Wi-Fi is connected, wait 15 minutes before checking
				// next time
				cal.add(Constants.DURATION, 15);
				repeats = 0;
			}

		} else if (!exit) {

			cal.add(Constants.DURATION, 15);

		} else if (exit) {
			// Save the preferences so that the service does not look activated
			// when opening setup screen
			SharedPreferences settings =
					context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
							Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(Constants.WATCHER_ENABLED, false);
			editor.commit();
		}

		if (!wfMgr.isWifiEnabled() && retry && dis_by_this) {
			cal.add(Constants.DURATION, 30);

			// Construct the next alarm
			Intent retryIntent = new Intent(context, RetryReceiver.class);
			retryIntent.putExtra(Constants.WATCHER_ALARM_REPEATS, repeats);
			retryIntent.putExtra(Constants.WATCHER_MAX_REPEATS, maxRepeats);
			retryIntent.putExtra(Constants.WATCHER_RETRY, retry);
			retryIntent.putExtra(Constants.WATCHER_EXIT, exit);
			retryIntent.putExtra(Constants.WATCHER_3G_ENABLE, t_g_enable);
			retryIntent.putExtra(Constants.WATCHER_3G_DISABLE, t_g_disable);

			PendingIntent retrySender =
					PendingIntent.getBroadcast(context, 0, retryIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

			// Cancel possibly conflicting alarms
			am.cancel(retrySender);

			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), retrySender);

		} else {

			// Construct the next alarm
			Intent nextAlarmIntent =
					new Intent(context, DisconnectReceiver.class);
			nextAlarmIntent.putExtra(Constants.WATCHER_ALARM_REPEATS, repeats);
			nextAlarmIntent.putExtra(Constants.WATCHER_MAX_REPEATS, maxRepeats);
			nextAlarmIntent.putExtra(Constants.WATCHER_RETRY, retry);
			nextAlarmIntent.putExtra(Constants.WATCHER_EXIT, exit);
			nextAlarmIntent.putExtra(Constants.WATCHER_3G_ENABLE, t_g_enable);
			nextAlarmIntent.putExtra(Constants.WATCHER_3G_DISABLE, t_g_disable);
			nextAlarmIntent.putExtra(Constants.WATCHER_DISABLED_BY_THIS,
					dis_by_this);

			PendingIntent sender =
					PendingIntent.getBroadcast(context, 0, nextAlarmIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

			// Cancel possibly conflicting alarms
			am.cancel(sender);

			if (wfMgr.isWifiEnabled() || !exit) {
				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
			}
		}
	}
}