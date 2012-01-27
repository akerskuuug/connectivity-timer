package com.connectivitymanager.alarm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.connectivitymanager.core.DisconnectTimerActivity;
import com.connectivitymanager.utility.Constants;

public class DisconnectReceiver extends BroadcastReceiver {
	private WifiManager wfMgr;
	private ConnectivityManager cnMgr;

	@Override
	public void onReceive(Context context, Intent intent) {

		Calendar cal = Calendar.getInstance();
		AlarmManager am =
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		// Get the WifiManager service to read and control Wi-Fi
		wfMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		cnMgr =
				(ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Get the maximum number of repeats, to turn off Wi-Fi at the right
		// time
		Bundle extras = intent.getExtras();
		int repeats = extras.getInt("alarm_repeats", 0);
		int maxRepeats = extras.getInt("max_repeats", 3);
		boolean retry = extras.getBoolean("retry", false);
		boolean exit = extras.getBoolean("exit", true);
		boolean t_g_enable = extras.getBoolean("tgenable", false);
		boolean t_g_disable = extras.getBoolean("tgdisable", false);

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

						Method dataMtd;
						try {

							// Set mobile data connection according to the
							// user's request
							dataMtd =
									ConnectivityManager.class
											.getDeclaredMethod(
													"setMobileDataEnabled",
													boolean.class);
							dataMtd.setAccessible(true);
							dataMtd.invoke(cnMgr, t_g_enable && !t_g_disable);

						} catch (SecurityException e) {
							Log.e("DisconnectReceiver SE",
									e.getLocalizedMessage());
						} catch (NoSuchMethodException e) {
							Log.e("DisconnectReceiver NSME",
									e.getLocalizedMessage());
						} catch (IllegalArgumentException e) {
							Log.e("DisconnectReceiver IArE",
									e.getLocalizedMessage());
						} catch (IllegalAccessException e) {
							Log.e("DisconnectReceiver IAcE",
									e.getLocalizedMessage());
						} catch (InvocationTargetException e) {
							Log.e("DisconnectReceiver ITE",
									e.getLocalizedMessage());
						}

					}

					NotificationManager nm =
							(NotificationManager) context
									.getSystemService(Context.NOTIFICATION_SERVICE);

					// Create the visuals for the notification
					int icon = android.R.drawable.star_on;
					CharSequence tickerText =
							context.getString(com.connectivitymanager.R.string.wifi_disabled);
					long when = System.currentTimeMillis();
					Notification notification =
							new Notification(icon, tickerText, when);

					// Decide what will be displayed in the notification bar
					// and what will happen when the notification is clicked
					Intent notificationIntent =
							new Intent(context, DisconnectTimerActivity.class);
					PendingIntent contentIntent =
							PendingIntent.getActivity(context, 0,
									notificationIntent, 0);
					notification
							.setLatestEventInfo(
									context,
									context.getString(com.connectivitymanager.R.string.wifi_disabled),
									context.getString(com.connectivitymanager.R.string.wifi_disabled_lost_conn),
									contentIntent);
					notification.flags |= Notification.FLAG_AUTO_CANCEL;

					// Show the notification
					nm.notify(3216, notification);

					repeats = 0;
				} else {

					// Wait 15 minutes before checking next time
					cal.add(Constants.DURATION, 15);

				}
			} else {

				// If Wi-Fi is connected, wait 15 minutes before checking
				// next time
				cal.add(Constants.DURATION, 15);
				repeats = 0;
				Log.d("AlarmReceiver onReceive", "Connected");
			}

		} else if (!exit) {

			cal.add(Constants.DURATION, 15);

		}

		if (!wfMgr.isWifiEnabled() && retry) {

			cal.add(Constants.DURATION, 30);

			Log.d("RetryReceiver", "send");

			// Construct the next alarm
			Intent retryIntent = new Intent(context, RetryReceiver.class);
			retryIntent.putExtra("alarm_repeats", repeats);
			retryIntent.putExtra("max_repeats", maxRepeats);
			retryIntent.putExtra("retry", retry);
			retryIntent.putExtra("exit", exit);
			retryIntent.putExtra("tgenable", t_g_enable);
			retryIntent.putExtra("tgdisable", t_g_disable);

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
			nextAlarmIntent.putExtra("alarm_repeats", repeats);
			nextAlarmIntent.putExtra("max_repeats", maxRepeats);
			nextAlarmIntent.putExtra("retry", retry);
			nextAlarmIntent.putExtra("exit", exit);
			nextAlarmIntent.putExtra("tgenable", t_g_enable);
			nextAlarmIntent.putExtra("tgdisable", t_g_disable);

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