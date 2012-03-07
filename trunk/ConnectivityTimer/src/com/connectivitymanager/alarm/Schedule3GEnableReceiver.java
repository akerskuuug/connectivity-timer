package com.connectivitymanager.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.connectivitymanager.core.SchedulerActivity;
import com.connectivitymanager.utility.Constants;
import com.connectivitymanager.utility.Tools;

public class Schedule3GEnableReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get the shared preferences for the application, to find out if a
		// notification should be displayed
		SharedPreferences settings =
				context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Activity.MODE_PRIVATE);

		// Get the Alarm manager to be able to set alarms
		AlarmManager am =
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// Set the alarm for the same time next day
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 7);

		Intent newIntent = new Intent(context, Schedule3GEnableReceiver.class);
		PendingIntent sender =
				PendingIntent.getBroadcast(context, 1199, newIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		// Cancel any conflicting alarms
		am.cancel(sender);
		// Set the alarm
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		Tools.set3GEnabled(context, true);

		if (settings.getBoolean(Constants.SCHEDULER_SHOW_NOTIFICATION, true)) {
			Tools.showNotification(
					context,
					SchedulerActivity.class,
					context.getString(com.connectivitymanager.R.string.tg_enabled),
					context.getString(com.connectivitymanager.R.string.tg_enabled_schedule),
					3222);
		}
	}

}
