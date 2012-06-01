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

import com.connectivitymanager.core.SchedulerActivity;
import com.connectivitymanager.database.AlarmDbAdapter;
import com.connectivitymanager.utility.Constants;
import com.connectivitymanager.utility.Tools;

public class SchedulerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		int alarmId = extras.getInt(Constants.SCHEDULER_ALARM_ID, 0);
		boolean enable = extras.getBoolean(Constants.SCHEDULER_ENABLE, false);
		boolean wifi =
				extras.getBoolean(Constants.SCHEDULER_DISABLE_WIFI, false);
		boolean mdata =
				extras.getBoolean(Constants.SCHEDULER_DISABLE_3G, false);

		AlarmDbAdapter dbAdapter = new AlarmDbAdapter(context);
		dbAdapter.open();

		// Get the Alarm manager to be able to set alarms
		AlarmManager am =
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// Get the shared preferences for the application, to find out if a
		// notification should be displayed
		SharedPreferences settings =
				context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Activity.MODE_PRIVATE);

		// Get the Wi-Fi manager
		WifiManager wfMgr =
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		// Only change the connections that we are supposed to change
		if (wifi) {
			wfMgr.setWifiEnabled(enable);
		}
		if (mdata) {
			Tools.set3GEnabled(context, enable);
		}

		Intent newIntent = new Intent(context, SchedulerReceiver.class);

		// Make the same extras available for the next alarm
		newIntent.putExtras(extras);

		PendingIntent sender =
				PendingIntent.getBroadcast(context, alarmId, newIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		// Set the alarm for the same time on the next enabled day
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, dbAdapter.daysUntilNextEnabled(alarmId));

		// Set the alarm
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		if (settings.getBoolean(Constants.SCHEDULER_SHOW_NOTIFICATION, true)) {

			if (enable) {
				Tools.showNotification(
						context,
						SchedulerActivity.class,
						context.getString(com.connectivitymanager.R.string.enabled),
						context.getString(com.connectivitymanager.R.string.enabled_schedule),
						3220);
			} else {
				Tools.showNotification(
						context,
						SchedulerActivity.class,
						context.getString(com.connectivitymanager.R.string.disabled),
						context.getString(com.connectivitymanager.R.string.disabled_schedule),
						3221);
			}

		}

		dbAdapter.close();

	}
}
