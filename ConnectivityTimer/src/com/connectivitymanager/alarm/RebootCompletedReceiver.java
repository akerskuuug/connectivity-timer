package com.connectivitymanager.alarm;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.connectivitymanager.R;
import com.connectivitymanager.core.Alarm;
import com.connectivitymanager.database.AlarmDbAdapter;
import com.connectivitymanager.utility.Constants;
import com.connectivitymanager.utility.Tools;

/**
 * This class is used to start up relevant services when phone is started.
 * 
 */
public class RebootCompletedReceiver extends BroadcastReceiver {

	AlarmManager am;
	SharedPreferences settings;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Save the preferences
		settings =
				context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Activity.MODE_PRIVATE);
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Constants.TIMED_WF_ENABLED, false);
		editor.putBoolean(Constants.TIMED_3G_ENABLED, false);
		editor.commit();

		if (settings.getBoolean(Constants.WATCHER_ENABLED, false)) {
			Intent newIntent = new Intent(context, DisconnectReceiver.class);

			int[] durationArray =
					context.getResources().getIntArray(
							R.array.watcher_durations_array);

			// Set the duration
			newIntent.putExtra(Constants.WATCHER_ALARM_REPEATS, 0);
			newIntent.putExtra(Constants.WATCHER_MAX_REPEATS,
					durationArray[settings.getInt(
							Constants.WATCHER_DURATION_POSITION, 0)] / 15);

			// "On disconnect" options
			newIntent.putExtra(Constants.WATCHER_RETRY,
					settings.getBoolean(Constants.WATCHER_RETRY, false));
			newIntent.putExtra(Constants.WATCHER_EXIT,
					settings.getBoolean(Constants.WATCHER_EXIT, true));
			newIntent.putExtra(Constants.WATCHER_3G_ENABLE,
					settings.getBoolean(Constants.WATCHER_3G_ENABLE, false));
			newIntent.putExtra(Constants.WATCHER_3G_DISABLE,
					settings.getBoolean(Constants.WATCHER_3G_DISABLE, false));

			PendingIntent sender =
					PendingIntent.getBroadcast(context, 0, newIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

			Calendar cal = Calendar.getInstance();
			cal.add(Constants.DURATION, 15);

			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		}

		// Calendars used to set all of the alarms
		Calendar calNow = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();

		// Get the current day of the week (starting with Sunday as 0)
		int currentDay = calNow.get(Calendar.DAY_OF_WEEK) - 1;

		AlarmDbAdapter dbAdapter = new AlarmDbAdapter(context);
		dbAdapter.open();

		Cursor alarmCursor = dbAdapter.fetchAllAlarms();
		// Checks if the cursor is empty (if we have any alarms)
		if (alarmCursor.moveToFirst()) {

			ArrayList<Alarm> alarm_data = new ArrayList<Alarm>();

			ArrayList<Integer> alarmIDs = new ArrayList<Integer>();

			while (!alarmCursor.isAfterLast()) {
				alarmIDs.add(alarmCursor.getInt(0));

				alarm_data.add(new Alarm(alarmCursor.getInt(1), alarmCursor
						.getInt(2), alarmCursor.getInt(10) == 1, alarmCursor
						.getInt(11) == 1, new boolean[] {
						alarmCursor.getInt(3) == 1, alarmCursor.getInt(4) == 1,
						alarmCursor.getInt(5) == 1, alarmCursor.getInt(6) == 1,
						alarmCursor.getInt(7) == 1, alarmCursor.getInt(8) == 1,
						alarmCursor.getInt(9) == 1, }));

				alarmCursor.moveToNext();

			}

			int fromHours, fromMinutes, toHours, toMinutes, disableId, enableId;

			for (int i = 0; i < alarm_data.size(); i++) {
				fromHours = alarm_data.get(i).from / 60;
				fromMinutes = alarm_data.get(i).from % 60;

				toHours = alarm_data.get(i).to / 60;
				toMinutes = alarm_data.get(i).to % 60;

				disableId = alarmIDs.get(i);
				enableId = disableId + 1;

				// Reset calendars for every new alarm
				calFrom = Calendar.getInstance();
				calTo = Calendar.getInstance();

				// Set the correct time for the calendars
				calFrom.set(Calendar.HOUR_OF_DAY, fromHours);
				calFrom.set(Calendar.MINUTE, fromMinutes);
				calFrom.set(Calendar.SECOND, 0);

				calTo.set(Calendar.HOUR_OF_DAY, toHours);
				calTo.set(Calendar.MINUTE, toMinutes);
				calTo.set(Calendar.SECOND, 0);

				// Get the boolean value representing the enabled status for all
				// weekdays
				boolean wdays[] =
						new boolean[] { alarm_data.get(i).sunday,
								alarm_data.get(i).monday,
								alarm_data.get(i).tuesday,
								alarm_data.get(i).wednesday,
								alarm_data.get(i).thursday,
								alarm_data.get(i).friday,
								alarm_data.get(i).saturday };

				// If the alarm is set to be enabled before it is disabled, set
				// it for 24 hours later
				if (calTo.compareTo(calFrom) <= 0) {
					calTo.add(Calendar.DATE, 1);
				}

				int daysUntil = dbAdapter.daysUntilNextEnabled(disableId);

				// If the alarm is disabled or both of the times have already
				// passed, set the alarms for the next enabled day
				if (!wdays[currentDay] || calFrom.compareTo(calNow) <= 0
						&& calTo.compareTo(calNow) <= 0) {

					calFrom.add(Calendar.DATE, daysUntil);
					calTo.add(Calendar.DATE, daysUntil);

				}

				// If the alarm is set to be disabled before now, set it for the
				// next enabled day
				if (calFrom.compareTo(calNow) <= 0) {

					calFrom.add(Calendar.DATE, daysUntil);
				}

				Intent schedulerIntent =
						new Intent(context, SchedulerReceiver.class);

				schedulerIntent.putExtra(Constants.SCHEDULER_ALARM_ID,
						disableId);
				schedulerIntent.putExtra(Constants.SCHEDULER_ENABLE, false);
				schedulerIntent.putExtra(Constants.SCHEDULER_DISABLE_WIFI,
						alarm_data.get(i).disableWifi);
				schedulerIntent.putExtra(Constants.SCHEDULER_DISABLE_3G,
						alarm_data.get(i).disable3g);

				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, calFrom.getTimeInMillis(),
						Tools.getDistinctPendingIntent(context,
								schedulerIntent, disableId));

				// Change the second alarm to enable connections
				schedulerIntent
						.putExtra(Constants.SCHEDULER_ALARM_ID, enableId);
				schedulerIntent.putExtra(Constants.SCHEDULER_ENABLE, true);

				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, calTo.getTimeInMillis(), Tools
						.getDistinctPendingIntent(context, schedulerIntent,
								enableId));

			}
		}
		dbAdapter.close();
	}
}
