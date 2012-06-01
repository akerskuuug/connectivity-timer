package com.connectivitymanager.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class Tools {

	/**
	 * Takes an integer as parameter and returns a string that is the integer
	 * padded with a zero if the value is below 10
	 * 
	 * @param input
	 *            the integer to examine
	 * @return a padded string
	 */
	public static String getPaddedString(int input) {

		return input < 10 ? "0" + input : "" + input;

	}

	public static PendingIntent getDistinctPendingIntent(Context context,
			Intent intent, int requestId) {
		PendingIntent pi =
				PendingIntent.getBroadcast(context, requestId, intent, 0);

		return pi;
	}

	public static void set3GEnabled(Context context, boolean enabled) {
		ConnectivityManager cnMgr =
				(ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
		Method dataMtd;
		try {

			// Set mobile data connection according to the
			// user's request
			dataMtd =
					ConnectivityManager.class.getDeclaredMethod(
							"setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(cnMgr, enabled);

		} catch (SecurityException e) {
			Log.e("Tools.set3GEnabled() SE", e.getLocalizedMessage());
		} catch (NoSuchMethodException e) {
			Log.e("Tools.set3GEnabled() NSME", e.getLocalizedMessage());
		} catch (IllegalArgumentException e) {
			Log.e("Tools.set3GEnabled() IArE", e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			Log.e("Tools.set3GEnabled() IAcE", e.getLocalizedMessage());
		} catch (InvocationTargetException e) {
			if (e != null && e.getLocalizedMessage() != null) {
				Log.e("Tools.set3GEnabled() ITE", e.getLocalizedMessage());
			}
		}

	}

	public static boolean get3GDataEnabled(Context context) {
		ConnectivityManager conManager =
				(ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);

		try {

			Method dataMtd =
					ConnectivityManager.class
							.getDeclaredMethod("getMobileDataEnabled");
			dataMtd.setAccessible(true);
			return (Boolean) dataMtd.invoke(conManager);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Shows a notification
	 * 
	 * @param context
	 *            The context to use to show the notification
	 * @param nextActivityClass
	 *            The activity to launch when this notification is clicked
	 * @param tickerText
	 *            The text to show in the notification bar
	 * @param description
	 *            The text to show when the notification is expanded
	 * @param id
	 *            The notification id
	 */
	public static void showNotification(Context context,
			Class nextActivityClass, String tickerText, String description,
			int id) {

		// Get the Notification manager
		NotificationManager nm =
				(NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = android.R.drawable.star_on;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// Decide what will be displayed in the notification bar
		// and what will happen when the notification is clicked
		Intent notificationIntent = new Intent(context, nextActivityClass);
		PendingIntent contentIntent =
				PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, tickerText, description,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Show the notification
		nm.notify(id, notification);

	}

}
