package com.connectivitymanager.core;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

public class TimedWifiActivity extends Activity {

	private TextView tooltip;
	private int durationHours, durationMinutes;
	private AlarmManager am;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timed_wifi);

		final Spinner durationSpinner =
				(Spinner) findViewById(R.id.duration_input);
		tooltip = (TextView) findViewById(R.id.timed_wf_tooltip);

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

		// When a durationHours is selected in the durationSpinner, the
		// durationHours
		// variable should be updated
		durationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// Read the desired delay
				String duration = durationSpinner.getSelectedItem().toString();

				durationHours =
						Integer.parseInt(duration.substring(0,
								duration.indexOf(':')));
				durationMinutes =
						Integer.parseInt(duration.substring(
								duration.indexOf(':') + 1, duration.length()));

				tooltip.setText(getString(R.string.timed_wf_tooltip_text));
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing
			}

		});

		Button startButton = (Button) findViewById(R.id.timed_wf_startbutton);

		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				RadioButton radio =
						(RadioButton) findViewById(R.id.connect_check);

				// Get the current checked status of the "enable Wi-Fi" radio
				// button
				boolean checked = radio.isChecked();

				// Get the calendar instance and add the time selected by the
				// user
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.HOUR, durationHours);
				cal.add(Calendar.MINUTE, durationMinutes);

				Intent intent =
						new Intent(TimedWifiActivity.this,
								DisconnectReceiver.class);

				// Set if the alarm should enable or disable Wi-Fi when
				// triggered
				intent.putExtra("wifi_enable", !checked);

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

			}
		});

		Button stopButton = (Button) findViewById(R.id.timed_wf_stopbutton);

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
			}
		});

	}
}
