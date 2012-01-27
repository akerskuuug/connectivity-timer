package com.connectivitymanager.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.connectivitymanager.utility.Tools;

public class Timed3GReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Tools.set3GEnabled(context,
				intent.getExtras().getBoolean("tg_enable", false));

	}

}
