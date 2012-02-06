package com.connectivitymanager.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.connectivitymanager.core.Timed3GActivity;
import com.connectivitymanager.utility.Tools;

public class Timed3GReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean setEnabled = intent.getExtras().getBoolean("tg_enable", false);
		String tickerText = "";
		String description = "";

		Tools.set3GEnabled(context, setEnabled);

		if (setEnabled) {
			tickerText =
					context.getString(com.connectivitymanager.R.string.tg_enabled);

			description =
					context.getString(com.connectivitymanager.R.string.tg_enabled_set_time);
		} else {

			tickerText =
					context.getString(com.connectivitymanager.R.string.tg_disabled);

			description =
					context.getString(com.connectivitymanager.R.string.tg_disabled_set_time);

		}

		Tools.showNotification(context, Timed3GActivity.class, tickerText,
				description, 3217);

	}

}
