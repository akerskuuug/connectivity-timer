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

			// Wi-Fi disable start:
			String str =
					timeArray[settings.getInt(
							Constants.SCHEDULER_WIFI_FROM_SELECTION, 0)];
			int wfFromHours =
					Integer.parseInt(str.substring(0, str.indexOf(':')));
			int wfFromMinutes =
					Integer.parseInt(str.substring(str.indexOf(':') + 1));

			// Wi-Fi disable end
			str =
					timeArray[settings.getInt(
							Constants.SCHEDULER_WIFI_TO_SELECTION, 0)];
			int wfToHours =
					Integer.parseInt(str.substring(0, str.indexOf(':')));
			int wfToMinutes =
					Integer.parseInt(str.substring(str.indexOf(':') + 1));

			// Mobile data disable start
			str =
					timeArray[settings.getInt(
							Constants.SCHEDULER_3G_FROM_SELECTION, 0)];
			int tgFromHours =
					Integer.parseInt(str.substring(0, str.indexOf(':')));
			int tgFromMinutes =
					Integer.parseInt(str.substring(str.indexOf(':') + 1));

			// Mobile data disable end
			str =
					timeArray[settings.getInt(
							Constants.SCHEDULER_3G_TO_SELECTION, 0)];
			int tgToHours =
					Integer.parseInt(str.substring(0, str.indexOf(':')));
			int tgToMinutes =
					Integer.parseInt(str.substring(str.indexOf(':') + 1));

			// Calendars used to set all of the alarms
			Calendar calNow = Calendar.getInstance();
			Calendar calWfFrom = Calendar.getInstance();
			Calendar calWfTo = Calendar.getInstance();
			Calendar calTgFrom = Calendar.getInstance();
			Calendar calTgTo = Calendar.getInstance();

			if (settings.getBoolean(Constants.SCHEDULER_DISABLE_WIFI, false)) {

				calWfFrom.set(Calendar.HOUR_OF_DAY, wfFromHours);
				calWfFrom.set(Calendar.MINUTE, wfFromMinutes);
				calWfFrom.set(Calendar.SECOND, 0);
				calWfTo.set(Calendar.HOUR_OF_DAY, wfToHours);
				calWfTo.set(Calendar.MINUTE, wfToMinutes);
				calWfTo.set(Calendar.SECOND, 0);

				// If Wi-Fi is set to be disabled or enabled before now, do it
				// 24 hours
				// later.
				if (calWfFrom.compareTo(calNow) <= 0) {
					calWfFrom.add(Calendar.DATE, 1);
				}
				if (calWfTo.compareTo(calNow) <= 0) {
					calWfTo.add(Calendar.DATE, 1);
				}

				Intent disableIntent =
						new Intent(context, ScheduleWifiDisableReceiver.class);

				PendingIntent disableSender =
						PendingIntent.getBroadcast(context, 0, disableIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);

				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, calWfFrom.getTimeInMillis(),
						disableSender);
				// /Disable related
				// ----------------------------------------------------- //
				//

				//
				// Enable related
				// ----------------------------------------------------- //
				Intent enableIntent =
						new Intent(context, ScheduleWifiEnableReceiver.class);

				PendingIntent enableSender =
						PendingIntent.getBroadcast(context, 0, enableIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);

				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, calWfTo.getTimeInMillis(),
						enableSender);
				// /Enable related
				// ---------------------------------------------------- //

			}
			// Only set a mobile data alarm if the SDK level is above 8
			if (Integer.valueOf(android.os.Build.VERSION.SDK) > 8
					&& settings.getBoolean(Constants.SCHEDULER_DISABLE_3G,
							false)) {
				calTgFrom.set(Calendar.HOUR_OF_DAY, tgFromHours);
				calTgFrom.set(Calendar.MINUTE, tgFromMinutes);
				calTgFrom.set(Calendar.SECOND, 0);
				calTgTo.set(Calendar.HOUR_OF_DAY, tgToHours);
				calTgTo.set(Calendar.MINUTE, tgToMinutes);
				calTgTo.set(Calendar.SECOND, 0);

				// If mobile data is set to be disabled or enabled before now,
				// do it 24 hours later.
				if (calTgFrom.compareTo(calNow) <= 0) {
					calTgFrom.add(Calendar.DATE, 1);
				}
				if (calTgTo.compareTo(calNow) <= 0) {
					calTgTo.add(Calendar.DATE, 1);
				}

				Intent disableIntent =
						new Intent(context, Schedule3GDisableReceiver.class);

				PendingIntent disableSender =
						PendingIntent.getBroadcast(context, 0, disableIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);

				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, calTgFrom.getTimeInMillis(),
						disableSender);
				// /Disable related
				// ----------------------------------------------------- //
				//

				//
				// Enable related
				// ----------------------------------------------------- //
				Intent enableIntent =
						new Intent(context, Schedule3GEnableReceiver.class);

				PendingIntent enableSender =
						PendingIntent.getBroadcast(context, 0, enableIntent,
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
