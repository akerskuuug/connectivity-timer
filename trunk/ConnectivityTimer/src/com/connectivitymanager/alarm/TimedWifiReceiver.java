package com.connectivitymanager.alarm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;

import com.connectivitymanager.core.TimedWifiActivity;
import com.connectivitymanager.utility.Constants;
import com.connectivitymanager.utility.Tools;

public class TimedWifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		WifiManager wfMgr =
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		String tickerText = "";
		String description = "";

		boolean setWifiTo =
				intent.getExtras().getBoolean(Constants.TIMED_WF_ENABLE_WIFI,
						false);

		wfMgr.setWifiEnabled(setWifiTo);

		// Set the texts according to what the action is
		if (setWifiTo) {
			tickerText =
					context.getString(com.connectivitymanager.R.string.wifi_enabled);
			description =
					context.getString(com.connectivitymanager.R.string.wifi_enabled_set_time);
		} else {
			tickerText =
					context.getString(com.connectivitymanager.R.string.wifi_disabled);
			description =
					context.getString(com.connectivitymanager.R.string.wifi_disabled_set_time);
		}

		// Show the notification
		Tools.showNotification(context, TimedWifiActivity.class, tickerText,
				description, 3218);

		// Save the preferences so that the service does not look activated
		// when opening setup screen
		SharedPreferences settings =
				context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Constants.TIMED_WF_ENABLED, false);
		editor.commit();

	}

}
