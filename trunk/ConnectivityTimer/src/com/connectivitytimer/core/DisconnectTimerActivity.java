package com.connectivitytimer.core;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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

import com.connectivitytimer.R;
import com.connectivitytimer.alarm.DisconnectReceiver;
import com.connectivitytimer.alarm.RetryReceiver;
import com.connectivitytimer.utility.Constants;

public class DisconnectTimerActivity extends Activity {

	private int duration;
	private PendingIntent sender;
	private AlarmManager am;
	private TextView tooltip;
	private CheckBox retry_check, exit_check, three_g_enable_check,
			three_g_disable_check;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.disconnect_watcher);

		// Get the AlarmManager service
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		final Spinner durationSpinner = (Spinner) findViewById(R.id.duration_input);
		tooltip = (TextView) findViewById(R.id.watcher_tooltip);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, com.connectivitytimer.R.array.durations_array,
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
				duration = Integer.parseInt(durationSpinner.getSelectedItem()
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
		three_g_enable_check = (CheckBox) findViewById(R.id.threeg_enable_check);
		three_g_disable_check = (CheckBox) findViewById(R.id.threeg_disable_check);

		setOnCheckedListener(retry_check);
		setOnCheckedListener(exit_check);
		setOnCheckedListener(three_g_enable_check);
		setOnCheckedListener(three_g_disable_check);

		Button startButton = (Button) findViewById(R.id.startbutton);

		// Upon click this starts the service
		startButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// Enable Wi-Fi
				WifiManager wfMan = (WifiManager) getSystemService(WIFI_SERVICE);
				wfMan.setWifiEnabled(true);

				// Get a Calendar object with current time (to be used in an
				// alarm)
				Calendar cal = Calendar.getInstance();
				// Add 15 second to the calendar object
				cal.add(Constants.DURATION, 15);

				cancelAllAlarms();

				Intent intent = new Intent(DisconnectTimerActivity.this,
						DisconnectReceiver.class);
				intent.putExtra("alarm_repeats", 0);
				intent.putExtra("max_repeats", duration / 15);

				// "On disconnect" options
				intent.putExtra("retry", retry_check.isChecked());
				intent.putExtra("exit", exit_check.isChecked());
				intent.putExtra("tgenable", three_g_enable_check.isChecked());
				intent.putExtra("tgdisable", three_g_disable_check.isChecked());

				sender = PendingIntent.getBroadcast(
						DisconnectTimerActivity.this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

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

			}
		});

	}

	public void setOnCheckedListener(CheckBox check) {
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
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
		Intent tempIntent = new Intent(DisconnectTimerActivity.this,
				DisconnectReceiver.class);
		sender = PendingIntent.getBroadcast(DisconnectTimerActivity.this, 0,
				tempIntent, 0);

		// Cancel ongoing alarms related to Wi-Fi disconnecting
		am.cancel(sender);

		tempIntent = new Intent(DisconnectTimerActivity.this,
				RetryReceiver.class);

		sender = PendingIntent.getBroadcast(DisconnectTimerActivity.this, 0,
				tempIntent, 0);

		// Cancel ongoing alarms related to Wi-Fi retries
		am.cancel(sender);
	}

}