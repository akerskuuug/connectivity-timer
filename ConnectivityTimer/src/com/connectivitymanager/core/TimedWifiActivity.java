package com.connectivitymanager.core;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.connectivitymanager.R;
import com.connectivitymanager.alarm.DisconnectReceiver;
import com.connectivitymanager.alarm.TimedWifiReceiver;
import com.connectivitymanager.utility.Constants;

public class TimedWifiActivity extends Activity {

	private TextView tooltip;
	private int durationHours = 0, durationMinutes = 0;
	private AlarmManager am;
	private Button startButton;
	private SharedPreferences settings;
	private Editor editor;
	private Spinner durationSpinner;
	private RadioButton enableRadio, disableRadio;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timed_wifi);

		durationSpinner = (Spinner) findViewById(R.id.duration_input);
		tooltip = (TextView) findViewById(R.id.timed_wf_tooltip);
		enableRadio = (RadioButton) findViewById(R.id.connect_check);
		disableRadio = (RadioButton) findViewById(R.id.disconnect_check);

		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		ArrayAdapter<CharSequence> adapter =
				ArrayAdapter
						.createFromResource(
								this,
								com.connectivitymanager.R.array.timed_wf_durations_array,
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
				String durationText =
						durationSpinner.getSelectedItem().toString();

				durationHours =
						Integer.parseInt(durationText.substring(0,
								durationText.indexOf(':')));

				durationMinutes =
						Integer.parseInt(durationText.substring(durationText
								.indexOf(':') + 1));

				tooltip.setText(getString(R.string.timed_wf_tooltip_text));
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing
			}

		});

		startButton = (Button) findViewById(R.id.startbutton);

		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				boolean checked = enableRadio.isChecked();
				Calendar cal = Calendar.getInstance();

				cal.add(Calendar.HOUR, durationHours);
				cal.add(Constants.DURATION, durationMinutes);

				Intent intent =
						new Intent(TimedWifiActivity.this,
								TimedWifiReceiver.class);

				intent.putExtra(Constants.TIMED_WF_ENABLE_WIFI, !checked);

				WifiManager wfMgr =
						(WifiManager) getSystemService(Context.WIFI_SERVICE);
				wfMgr.setWifiEnabled(checked);

				PendingIntent sender =
						PendingIntent.getBroadcast(TimedWifiActivity.this, 0,
								intent, PendingIntent.FLAG_UPDATE_CURRENT);

				Toast.makeText(getApplicationContext(),
						getString(R.string.service_started), Toast.LENGTH_SHORT)
						.show();

				// Cancel any conflicting alarms
				am.cancel(sender);

				// Set the alarm
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

				Toast.makeText(getApplicationContext(),
						getString(R.string.service_started), Toast.LENGTH_SHORT)
						.show();

				// Save the current state of this activity in shared preferences
				startButton.setEnabled(false);
				editor.putBoolean(Constants.TIMED_WF_ENABLED, true);
				editor.putBoolean(Constants.TIMED_WF_ENABLE_WIFI,
						enableRadio.isChecked());
				editor.putBoolean(Constants.TIMED_WF_DISABLE_WIFI,
						disableRadio.isChecked());
				editor.putInt(Constants.TIMED_WF_DURATION,
						durationSpinner.getSelectedItemPosition());
				editor.commit();
			}
		});

		Button stopButton = (Button) findViewById(R.id.stopbutton);

		stopButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent tempIntent =
						new Intent(TimedWifiActivity.this,
								DisconnectReceiver.class);
				PendingIntent tempSender =
						PendingIntent.getBroadcast(TimedWifiActivity.this, 0,
								tempIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				am.cancel(tempSender);

				Toast.makeText(getApplicationContext(),
						getString(R.string.service_stopped), Toast.LENGTH_SHORT)
						.show();
				// Save the current state of this activity in shared preferences
				startButton.setEnabled(true);
				editor.putBoolean(Constants.TIMED_WF_ENABLED, false);
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
				settings.getBoolean(Constants.TIMED_WF_ENABLED, false);
		startButton.setEnabled(!isEnabled);

		durationSpinner.setSelection(settings.getInt(
				Constants.TIMED_WF_DURATION, 0));

		enableRadio.setChecked(settings.getBoolean(
				Constants.TIMED_WF_ENABLE_WIFI, true));

		disableRadio.setChecked(settings.getBoolean(
				Constants.TIMED_WF_DISABLE_WIFI, false));
	}
}
