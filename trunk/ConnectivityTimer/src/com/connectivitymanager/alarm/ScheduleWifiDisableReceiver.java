package com.connectivitymanager.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.connectivitymanager.core.SchedulerActivity;
import com.connectivitymanager.utility.Tools;

public class ScheduleWifiDisableReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// Get the Notification manager to be able to set alarms
		AlarmManager am =
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// Set the alarm for the same time next day
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);

		Intent newIntent =
				new Intent(context, ScheduleWifiDisableReceiver.class);
		PendingIntent sender =
				PendingIntent.getBroadcast(context, 0, newIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		// Cancel any conflicting alarms
		am.cancel(sender);
		// Set the alarm
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

		// Get the Wi-Fi manager
		WifiManager wfMgr =
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// Disable Wi-Fi
		wfMgr.setWifiEnabled(false);

		Tools.showNotification(
				context,
				SchedulerActivity.class,
				context.getString(com.connectivitymanager.R.string.wifi_disabled),
				context.getString(com.connectivitymanager.R.string.wf_disabled_schedule),
				3219);

	}
}
