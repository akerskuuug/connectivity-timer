package com.connectivitymanager.core;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.connectivitymanager.R;
import com.connectivitymanager.alarm.DisconnectReceiver;
import com.connectivitymanager.alarm.RetryReceiver;
import com.connectivitymanager.utility.Constants;

public class DisconnectTimerActivity extends Activity {

	private int duration;
	private PendingIntent sender;
	private AlarmManager am;
	private TextView tooltip;
	private CheckBox retry_check, exit_check, three_g_enable_check,
			three_g_disable_check;
	private Button startButton;
	private SharedPreferences settings;
	private Editor editor;
	private Spinner durationSpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.disconnect_watcher);

		three_g_enable_check =
				(CheckBox) findViewById(R.id.threeg_enable_check);
		three_g_disable_check =
				(CheckBox) findViewById(R.id.threeg_disable_check);

		// If device is running Android 2.2 or below, do not show 3G
		// functionality
		if (Integer.valueOf(android.os.Build.VERSION.SDK) <= 8) {
			three_g_enable_check.setVisibility(View.GONE);
			three_g_disable_check.setVisibility(View.GONE);
			three_g_enable_check.setChecked(false);
			three_g_disable_check.setChecked(false);
		}

		// Get the AlarmManager service
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		durationSpinner = (Spinner) findViewById(R.id.duration_input);
		tooltip = (TextView) findViewById(R.id.watcher_tooltip);

		ArrayAdapter<CharSequence> adapter =
				ArrayAdapter
						.createFromResource(
								this,
								com.connectivitymanager.R.array.watcher_durations_array,
								R.layout.my_simple_spinner_item);

		durationSpinner.setAdapter(adapter);
		// Default delay is 30 minutes
		durationSpinner.setSelection(1);

		// When a duration is selected in the durationSpinner, the duration
		// variable should be updated
		durationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// Read the desired delay
				duration =
						Integer.parseInt(durationSpinner.getSelectedItem()
								.toString());
				tooltip.setText(getString(R.string.watcher_tooltip_text) + " "
						+ duration + " "
						+ getString(R.string.watcher_tooltip_text_end));
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing
			}

		});

		retry_check = (CheckBox) findViewById(R.id.retry_check);
		exit_check = (CheckBox) findViewById(R.id.exit_service_check);

		setOnCheckedListener(retry_check);
		setOnCheckedListener(exit_check);
		setOnCheckedListener(three_g_enable_check);
		setOnCheckedListener(three_g_disable_check);

		startButton = (Button) findViewById(R.id.startbutton);

		// Upon click this starts the service
		startButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// Enable Wi-Fi
				WifiManager wfMan =
						(WifiManager) getSystemService(WIFI_SERVICE);
				wfMan.setWifiEnabled(true);

				// Get a Calendar object with current time (to be used in an
				// alarm)
				Calendar cal = Calendar.getInstance();
				// Add 15 second to the calendar object
				cal.add(Constants.DURATION, 15);

				cancelAllAlarms();

				Intent intent =
						new Intent(DisconnectTimerActivity.this,
								DisconnectReceiver.class);

				intent.putExtra(Constants.WATCHER_ALARM_REPEATS, 0);
				intent.putExtra(Constants.WATCHER_MAX_REPEATS, duration / 15);

				// "On disconnect" options
				intent.putExtra(Constants.WATCHER_RETRY,
						retry_check.isChecked());
				intent.putExtra(Constants.WATCHER_EXIT, exit_check.isChecked());
				intent.putExtra(Constants.WATCHER_3G_ENABLE,
						three_g_enable_check.isChecked());
				intent.putExtra(Constants.WATCHER_3G_DISABLE,
						three_g_disable_check.isChecked());

				sender =
						PendingIntent.getBroadcast(
								DisconnectTimerActivity.this, 0, intent,
								PendingIntent.FLAG_UPDATE_CURRENT);

				// Save the current state of this activity in shared preferences
				startButton.setEnabled(false);
				editor.putBoolean(Constants.WATCHER_ENABLED, true);
				editor.putInt(Constants.WATCHER_DURATION,
						durationSpinner.getSelectedItemPosition());
				editor.putBoolean(Constants.WATCHER_RETRY,
						retry_check.isChecked());
				editor.putBoolean(Constants.WATCHER_EXIT,
						exit_check.isChecked());
				editor.putBoolean(Constants.WATCHER_3G_ENABLE,
						three_g_enable_check.isChecked());
				editor.putBoolean(Constants.WATCHER_3G_DISABLE,
						three_g_disable_check.isChecked());
				editor.commit();

				Toast.makeText(getApplicationContext(),
						getString(R.string.service_started), Toast.LENGTH_SHORT)
						.show();
				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
			}
		});

		Button stopButton = (Button) findViewById(R.id.stopbutton);
		stopButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Toast.makeText(getApplicationContext(),
						getString(R.string.service_stopped), Toast.LENGTH_SHORT)
						.show();
				cancelAllAlarms();
				// Save the current state of this activity in shared preferences
				startButton.setEnabled(true);
				editor.putBoolean(Constants.WATCHER_ENABLED, false);
				editor.commit();

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		settings =
				getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Activity.MODE_PRIVATE);
		editor = settings.edit();

		// See if this service is enabled. If it is, disable the
		// "enable service" button
		boolean isEnabled =
				settings.getBoolean(Constants.WATCHER_ENABLED, false);
		startButton.setEnabled(!isEnabled);

		// Set all Views according to the shared preferences (earlier sessions)
		durationSpinner.setSelection(settings.getInt(
				Constants.WATCHER_DURATION, 0));
		retry_check.setChecked(settings.getBoolean(Constants.WATCHER_RETRY,
				false));
		exit_check.setChecked(settings
				.getBoolean(Constants.WATCHER_EXIT, false));
		three_g_enable_check.setChecked(settings.getBoolean(
				Constants.WATCHER_3G_ENABLE, false));
		three_g_disable_check.setChecked(settings.getBoolean(
				Constants.WATCHER_3G_DISABLE, false));

	}

	public void setOnCheckedListener(CheckBox check) {
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// exit_check and retry_check cannot be checked at the same
					// time, neither can 3G be enabled and disabled at the same
					// time
					if (buttonView.equals(retry_check)) {
						exit_check.setChecked(false);
					} else if (buttonView.equals(exit_check)) {
						retry_check.setChecked(false);
					} else if (buttonView.equals(three_g_enable_check)) {
						three_g_disable_check.setChecked(false);
					} else if (buttonView.equals(three_g_disable_check)) {
						three_g_enable_check.setChecked(false);
					}
				}
			}
		});
	}

	/**
	 * Cancel all alarms related to this application
	 */
	public void cancelAllAlarms() {

		// Needed to cancel the alarms properly
		Intent tempIntent =
				new Intent(DisconnectTimerActivity.this,
						DisconnectReceiver.class);
		sender =
				PendingIntent.getBroadcast(DisconnectTimerActivity.this, 0,
						tempIntent, 0);

		// Cancel ongoing alarms related to Wi-Fi disconnecting
		am.cancel(sender);

		tempIntent =
				new Intent(DisconnectTimerActivity.this, RetryReceiver.class);

		sender =
				PendingIntent.getBroadcast(DisconnectTimerActivity.this, 0,
						tempIntent, 0);

		// Cancel ongoing alarms related to Wi-Fi retries
		am.cancel(sender);
	}

}