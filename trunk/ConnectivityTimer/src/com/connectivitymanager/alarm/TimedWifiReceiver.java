package com.connectivitymanager.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.connectivitymanager.core.TimedWifiActivity;
import com.connectivitymanager.utility.Tools;

public class TimedWifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		WifiManager wfMgr =
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		String tickerText = "";
		String description = "";

		boolean setWifiTo = intent.getExtras().getBoolean("wifi_enable", false);

		wfMgr.setWifiEnabled(setWifiTo);

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

		Tools.showNotification(context, TimedWifiActivity.class, tickerText,
				description, 3218);

	}

}
