package com.connectivitymanager.alarm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.connectivitymanager.core.Timed3GActivity;
import com.connectivitymanager.utility.Constants;
import com.connectivitymanager.utility.Tools;

public class Timed3GReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean setEnabled =
				intent.getExtras().getBoolean(Constants.TIMED_3G_ENABLE_3G,
						false);
		String tickerText = "";
		String description = "";

		Tools.set3GEnabled(context, setEnabled);

		// Set the texts according to what the action is
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
		// Show the notification
		Tools.showNotification(context, Timed3GActivity.class, tickerText,
				description, 3217);

		// Save the preferences so that the service does not look activated
		// when opening setup screen
		SharedPreferences settings =
				context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Constants.TIMED_3G_ENABLED, false);
		editor.commit();
	}

}
