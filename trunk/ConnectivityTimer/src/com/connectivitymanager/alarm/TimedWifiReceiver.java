package com.connectivitymanager.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class TimedWifiReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		WifiManager wfMgr = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		boolean setWifiTo = intent.getExtras().getBoolean("wifi_enable", false);

		wfMgr.setWifiEnabled(setWifiTo);

	}

}
