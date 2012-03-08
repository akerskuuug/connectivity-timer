package com.connectivitymanager.alarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.connectivitymanager.R;
import com.connectivitymanager.utility.Constants;

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
		if (settings.getBoolean(Constants.SCHEDULER_ENABLED, false)) {
			settings =
					context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
							Activity.MODE_PRIVATE);

			String[] timeArray =
					context.getResources().getStringArray(
							R.array.scheduler_time_array);
			String mode =
					settings.getString(Constants.SCHEDULER_MODE, "simple");
			String wfFrom, wfTo, tgFrom, tgTo;
			int[] wfFromHours = new int[7], wfToHours = new int[7], tgFromHours =
					new int[7], tgToHours = new int[7];
			int[] wfFromMinutes = new int[7], wfToMinutes = new int[7], tgFromMinutes =
					new int[7], tgToMinutes = new int[7];

			if (mode.equals("simple")) {
				wfFrom =
						timeArray[settings.getInt(
								Constants.SCHEDULER_WIFI_FROM_SELECTION, 0)];
				wfTo =
						timeArray[settings.getInt(
								Constants.SCHEDULER_WIFI_TO_SELECTION, 0)];
				tgFrom =
						timeArray[settings.getInt(
								Constants.SCHEDULER_3G_FROM_SELECTION, 0)];
				tgTo =
						timeArray[settings.getInt(
								Constants.SCHEDULER_3G_TO_SELECTION, 0)];
				for (int i = 0; i < 7; i++) {
					wfFromHours[i] =
							Integer.parseInt(wfFrom.substring(0,
									wfFrom.indexOf(':')));
					wfFromMinutes[i] =
							Integer.parseInt(wfFrom.substring(wfFrom
									.indexOf(':') + 1));

					wfToHours[i] =
							Integer.parseInt(wfTo.substring(0,
									wfTo.indexOf(':')));
					wfToMinutes[i] =
							Integer.parseInt(wfTo.substring(wfTo.indexOf(':') + 1));

					tgFromHours[i] =
							Integer.parseInt(tgFrom.substring(0,
									tgFrom.indexOf(':')));
					tgFromMinutes[i] =
							Integer.parseInt(tgFrom.substring(tgFrom
									.indexOf(':') + 1));

					tgToHours[i] =
							Integer.parseInt(tgTo.substring(0,
									tgTo.indexOf(':')));
					tgToMinutes[i] =
							Integer.parseInt(tgTo.substring(tgTo.indexOf(':') + 1));
				}

			} else if (mode.equals("medium")) {

				for (int i = 0; i < 7; i++) {
					if (i > 0 && i < 6) {
						wfFrom =
								timeArray[settings
										.getInt(Constants.SCHEDULER_WD_WIFI_FROM_SELECTION,
												0)];
						wfTo =
								timeArray[settings
										.getInt(Constants.SCHEDULER_WD_WIFI_TO_SELECTION,
												0)];
						tgFrom =
								timeArray[settings
										.getInt(Constants.SCHEDULER_WD_3G_FROM_SELECTION,
												0)];
						tgTo =
								timeArray[settings.getInt(
										Constants.SCHEDULER_WD_3G_TO_SELECTION,
										0)];

					} else {
						wfFrom =
								timeArray[settings
										.getInt(Constants.SCHEDULER_WE_WIFI_FROM_SELECTION,
												0)];
						wfTo =
								timeArray[settings
										.getInt(Constants.SCHEDULER_WE_WIFI_TO_SELECTION,
												0)];
						tgFrom =
								timeArray[settings
										.getInt(Constants.SCHEDULER_WE_3G_FROM_SELECTION,
												0)];
						tgTo =
								timeArray[settings.getInt(
										Constants.SCHEDULER_WE_3G_TO_SELECTION,
										0)];
					}

					wfFromHours[i] =
							Integer.parseInt(wfFrom.substring(0,
									wfFrom.indexOf(':')));
					wfFromMinutes[i] =
							Integer.parseInt(wfFrom.substring(wfFrom
									.indexOf(':') + 1));

					wfToHours[i] =
							Integer.parseInt(wfTo.substring(0,
									wfTo.indexOf(':')));
					wfToMinutes[i] =
							Integer.parseInt(wfTo.substring(wfTo.indexOf(':') + 1));

					tgFromHours[i] =
							Integer.parseInt(tgFrom.substring(0,
									tgFrom.indexOf(':')));
					tgFromMinutes[i] =
							Integer.parseInt(tgFrom.substring(tgFrom
									.indexOf(':') + 1));

					tgToHours[i] =
							Integer.parseInt(tgTo.substring(0,
									tgTo.indexOf(':')));
					tgToMinutes[i] =
							Integer.parseInt(tgTo.substring(tgTo.indexOf(':') + 1));

				}

			} else if (mode.equals("advanced")) {
				for (int i = 0; i < 7; i++) {

					wfFrom =
							timeArray[settings
									.getInt(Constants.SCHEDULER_ADVANCED_WEEKDAYS[2 * i],
											0)];
					wfTo =
							timeArray[settings
									.getInt(Constants.SCHEDULER_ADVANCED_WEEKDAYS[2 * i + 1],
											0)];
					tgFrom =
							timeArray[settings
									.getInt(Constants.SCHEDULER_ADVANCED_WEEKDAYS[2 * i + 14],
											0)];
					tgTo =
							timeArray[settings
									.getInt(Constants.SCHEDULER_ADVANCED_WEEKDAYS[2 * i + 15],
											0)];

					wfFromHours[i] =
							Integer.parseInt(wfFrom.substring(0,
									wfFrom.indexOf(':')));
					wfFromMinutes[i] =
							Integer.parseInt(wfFrom.substring(wfFrom
									.indexOf(':') + 1));

					wfToHours[i] =
							Integer.parseInt(wfTo.substring(0,
									wfTo.indexOf(':')));
					wfToMinutes[i] =
							Integer.parseInt(wfTo.substring(wfTo.indexOf(':') + 1));

					tgFromHours[i] =
							Integer.parseInt(tgFrom.substring(0,
									tgFrom.indexOf(':')));
					tgFromMinutes[i] =
							Integer.parseInt(tgFrom.substring(tgFrom
									.indexOf(':') + 1));

					tgToHours[i] =
							Integer.parseInt(tgTo.substring(0,
									tgTo.indexOf(':')));
					tgToMinutes[i] =
							Integer.parseInt(tgTo.substring(tgTo.indexOf(':') + 1));

				}
			}

			if (mode.equals("medium")) {
				if (wfFromHours[6] >= wfToHours[6]) {
					wfFromHours[5] = wfFromHours[6];
					wfToHours[5] = wfToHours[6];
				}
				if (tgFromHours[6] >= tgToHours[6]) {
					tgFromHours[5] = tgFromHours[6];
					tgToHours[5] = tgToHours[6];
				}
				if (wfFromHours[1] >= wfToHours[1]) {
					wfFromHours[0] = wfFromHours[1];
					wfToHours[0] = wfToHours[1];
				}
				if (tgFromHours[1] >= tgToHours[1]) {
					tgFromHours[0] = tgFromHours[1];
					tgToHours[0] = tgToHours[1];
				}

			}

			// Calendars used to set all of the alarms
			Calendar calNow = Calendar.getInstance();
			Calendar calWfFrom = Calendar.getInstance();
			Calendar calWfTo = Calendar.getInstance();
			Calendar calTgFrom = Calendar.getInstance();
			Calendar calTgTo = Calendar.getInstance();

			// Get the current day of the week (starting with Sunday as 0)
			int currentDay = calNow.get(Calendar.DAY_OF_WEEK) - 1;

			if (settings.getBoolean(Constants.SCHEDULER_DISABLE_WIFI, false)) {
				for (int i = 0; i < 7; i++) {
					calWfFrom.set(Calendar.HOUR_OF_DAY, wfFromHours[i]);
					calWfFrom.set(Calendar.MINUTE, wfFromMinutes[i]);
					calWfFrom.set(Calendar.SECOND, 0);

					calWfTo.set(Calendar.HOUR_OF_DAY, wfToHours[i]);
					calWfTo.set(Calendar.MINUTE, wfToMinutes[i]);
					calWfTo.set(Calendar.SECOND, 0);

					if (i < currentDay) {
						// If the day we are setting the alarm for has
						// already passed this week, set it for the same day
						// next week
						calWfFrom.add(Calendar.DATE, 7 - currentDay);
						calWfTo.add(Calendar.DATE, 7 - currentDay);
					} else if (i > currentDay) {
						// If the day is later this week, add the correct
						// number of days
						calWfFrom.add(Calendar.DATE, i - currentDay);
						calWfTo.add(Calendar.DATE, i - currentDay);
					} else {

						if (calWfFrom.compareTo(calNow) <= 0
								|| calWfTo.compareTo(calNow) <= 0) {
							calWfFrom.add(Calendar.DATE, 7);
							calWfTo.add(Calendar.DATE, 7);
						}

					}

					if (calWfTo.compareTo(calWfFrom) <= 0) {
						calWfTo.add(Calendar.DATE, 1);
					}

					Intent disableIntent =
							new Intent(context,
									ScheduleWifiDisableReceiver.class);

					PendingIntent disableSender =
							PendingIntent.getBroadcast(context, i,
									disableIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP,
							calWfFrom.getTimeInMillis(), disableSender);
					// /Disable related
					// ----------------------------------------------------- //
					//

					//
					// Enable related
					// ----------------------------------------------------- //
					Intent enableIntent =
							new Intent(context,
									ScheduleWifiEnableReceiver.class);

					PendingIntent enableSender =
							PendingIntent.getBroadcast(context, i + 8,
									enableIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP, calWfTo.getTimeInMillis(),
							enableSender);

					// /Enable related
					// ---------------------------------------------------- //
				}
			}
			// Only set a mobile data alarm if the SDK level is above 8
			if (Integer.valueOf(android.os.Build.VERSION.SDK) > 8
					&& settings.getBoolean(Constants.SCHEDULER_DISABLE_3G,
							false)) {
				for (int i = 0; i < 7; i++) {

					calTgFrom.set(Calendar.HOUR_OF_DAY, tgFromHours[i]);
					calTgFrom.set(Calendar.MINUTE, tgFromMinutes[i]);
					calTgFrom.set(Calendar.SECOND, 0);

					calTgTo.set(Calendar.HOUR_OF_DAY, tgToHours[i]);
					calTgTo.set(Calendar.MINUTE, tgToMinutes[i]);
					calTgTo.set(Calendar.SECOND, 0);

					if (i < currentDay) {
						// If the day we are setting the alarm for has
						// already passed this week, set it for the same day
						// next week
						calTgFrom.add(Calendar.DATE, 7 - currentDay);
						calTgTo.add(Calendar.DATE, 7 - currentDay);
					} else if (i > currentDay) {
						// If the day is later this week, add the correct
						// number of days
						calTgFrom.add(Calendar.DATE, i - currentDay);
						calTgTo.add(Calendar.DATE, i - currentDay);
					} else {

						if (calTgFrom.compareTo(calNow) <= 0
								|| calTgTo.compareTo(calNow) <= 0) {
							calTgFrom.add(Calendar.DATE, 7);
							calTgTo.add(Calendar.DATE, 7);
						}

					}

					if (calTgTo.compareTo(calTgFrom) <= 0) {
						calTgTo.add(Calendar.DATE, 1);
					}

					Intent disableIntent =
							new Intent(context, Schedule3GDisableReceiver.class);

					PendingIntent disableSender =
							PendingIntent.getBroadcast(context, i + 16,
									disableIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP,
							calTgFrom.getTimeInMillis(), disableSender);
					// /Disable related
					// ----------------------------------------------------- //
					//

					//
					// Enable related
					// ----------------------------------------------------- //
					Intent enableIntent =
							new Intent(context, Schedule3GEnableReceiver.class);

					PendingIntent enableSender =
							PendingIntent.getBroadcast(context, i + 24,
									enableIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP, calTgTo.getTimeInMillis(),
							enableSender);

					// /Enable related
					// ---------------------------------------------------- //
				}
			}
		}
	}
}
