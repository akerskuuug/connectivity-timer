package com.connectivitymanager.core;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.connectivitymanager.R;
import com.connectivitymanager.alarm.Schedule3GDisableReceiver;
import com.connectivitymanager.alarm.Schedule3GEnableReceiver;
import com.connectivitymanager.alarm.ScheduleWifiDisableReceiver;
import com.connectivitymanager.alarm.ScheduleWifiEnableReceiver;
import com.connectivitymanager.utility.Constants;

public class SchedulerActivity extends Activity implements
		OnItemSelectedListener {

	private int wfFromHours, wfToHours, tgFromHours, tgToHours;
	private int wfFromMinutes, wfToMinutes, tgFromMinutes, tgToMinutes;
	private AlarmManager am;
	private CheckBox wfCheck, tgCheck;
	private Button startButton;
	private SharedPreferences settings;
	private Editor editor;
	private Spinner[] spinners;

	public static int SDK_VERSION = 8;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler);
		SDK_VERSION = Integer.valueOf(android.os.Build.VERSION.SDK);
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		wfCheck = (CheckBox) findViewById(R.id.wf_check);
		tgCheck = (CheckBox) findViewById(R.id.tg_check);

		spinners = new Spinner[4];
		// Spinner to set time at which to disable Wi-Fi
		spinners[0] = (Spinner) findViewById(R.id.wf_from_spinner);
		// Spinner to set time at which to enable Wi-Fi
		spinners[1] = (Spinner) findViewById(R.id.wf_to_spinner);

		// Spinner to set time at which to disable mobile data
		spinners[2] = (Spinner) findViewById(R.id.tg_from_spinner);
		// Spinner to set time at which to enable mobile data
		spinners[3] = (Spinner) findViewById(R.id.tg_to_spinner);

		// Hide 3G related views for devices with SDK version 8 or lower
		if (SDK_VERSION <= 8) {
			TextView tv = (TextView) findViewById(R.id.scheduler_and_label);
			tv.setVisibility(View.GONE);

			spinners[2].setVisibility(View.GONE);
			spinners[3].setVisibility(View.GONE);

			tgCheck.setVisibility(View.GONE);
			tgCheck.setChecked(false);

		}

		for (Spinner spin : spinners) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(
							this,
							com.connectivitymanager.R.array.scheduler_time_array,
							R.layout.my_simple_spinner_item);
			spin.setAdapter(adapter);
			spin.setSelection(0);
			spin.setOnItemSelectedListener(this);
		}

		startButton = (Button) findViewById(R.id.startbutton);

		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Calendars used to set all of the alarms
				Calendar calNow = Calendar.getInstance();
				Calendar calWfFrom = Calendar.getInstance();
				Calendar calWfTo = Calendar.getInstance();
				Calendar calTgFrom = Calendar.getInstance();
				Calendar calTgTo = Calendar.getInstance();

				if (wfCheck.isChecked()) {

					// Set the Wi-Fi related times according to their spinners
					calWfFrom.set(Calendar.HOUR_OF_DAY, wfFromHours);
					calWfFrom.set(Calendar.MINUTE, wfFromMinutes);
					calWfFrom.set(Calendar.SECOND, 0);
					calWfTo.set(Calendar.HOUR_OF_DAY, wfToHours);
					calWfTo.set(Calendar.MINUTE, wfToMinutes);
					calWfTo.set(Calendar.SECOND, 0);

					// If Wi-Fi is set to be disabled or enabled before now, do
					// it 24 hours later.
					if (calWfFrom.compareTo(calNow) <= 0) {
						calWfFrom.add(Calendar.DATE, 1);
					}
					if (calWfTo.compareTo(calNow) <= 0) {
						calWfTo.add(Calendar.DATE, 1);
					}

					Intent disableIntent = new Intent(SchedulerActivity.this,
							ScheduleWifiDisableReceiver.class);

					PendingIntent disableSender = PendingIntent.getBroadcast(
							SchedulerActivity.this, 0, disableIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					// Cancel any conflicting alarms
					am.cancel(disableSender);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP,
							calWfFrom.getTimeInMillis(), disableSender);

					Intent enableIntent = new Intent(SchedulerActivity.this,
							ScheduleWifiEnableReceiver.class);
					PendingIntent enableSender = PendingIntent.getBroadcast(
							SchedulerActivity.this, 0, enableIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					// Cancel any conflicting alarms
					am.cancel(enableSender);
					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP, calWfTo.getTimeInMillis(),
							enableSender);

				}

				if (tgCheck.isChecked()) {

					// Disable related
					// ---------------------------------------------------- //
					// Set the mobile data related times according to their
					// spinners
					calTgFrom.set(Calendar.HOUR_OF_DAY, tgFromHours);
					calTgFrom.set(Calendar.MINUTE, tgFromMinutes);
					calTgFrom.set(Calendar.SECOND, 0);
					calTgTo.set(Calendar.HOUR_OF_DAY, tgToHours);
					calTgTo.set(Calendar.MINUTE, tgToMinutes);
					calTgTo.set(Calendar.SECOND, 0);

					// If mobile data is set to be disabled or enabled before
					// now, do it 24
					// hours later.
					if (calTgFrom.compareTo(calNow) <= 0) {
						calTgFrom.add(Calendar.DATE, 1);

					}
					if (calTgTo.compareTo(calNow) <= 0) {
						calTgTo.add(Calendar.DATE, 1);
					}
					Intent disableIntent = new Intent(SchedulerActivity.this,
							Schedule3GDisableReceiver.class);

					PendingIntent disableSender = PendingIntent.getBroadcast(
							SchedulerActivity.this, 0, disableIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					// Cancel any conflicting alarms
					am.cancel(disableSender);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP,
							calTgFrom.getTimeInMillis(), disableSender);
					// /Disable related
					// ----------------------------------------------------- //
					//

					//
					// Enable related
					// ----------------------------------------------------- //
					Intent enableIntent = new Intent(SchedulerActivity.this,
							Schedule3GEnableReceiver.class);

					PendingIntent enableSender = PendingIntent.getBroadcast(
							SchedulerActivity.this, 0, enableIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					// Cancel any conflicting alarms
					am.cancel(enableSender);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP, calTgTo.getTimeInMillis(),
							enableSender);
					// /Enable related
					// ---------------------------------------------------- //

				}
				Toast.makeText(getApplicationContext(),
						getString(R.string.service_started), Toast.LENGTH_SHORT)
						.show();

				// Save the current state of this activity in shared preferences
				startButton.setEnabled(false);
				editor.putBoolean(Constants.SCHEDULER_ENABLED, true);
				editor.putBoolean(Constants.SCHEDULER_DISABLE_WIFI,
						wfCheck.isChecked());
				editor.putBoolean(Constants.SCHEDULER_DISABLE_3G,
						tgCheck.isChecked());

				editor.putInt(Constants.SCHEDULER_WIFI_FROM_SELECTION,
						spinners[0].getSelectedItemPosition());
				editor.putInt(Constants.SCHEDULER_WIFI_TO_SELECTION,
						spinners[1].getSelectedItemPosition());
				editor.putInt(Constants.SCHEDULER_3G_FROM_SELECTION,
						spinners[2].getSelectedItemPosition());
				editor.putInt(Constants.SCHEDULER_3G_TO_SELECTION,
						spinners[3].getSelectedItemPosition());

				editor.commit();

			}

		});

		Button stopButton = (Button) findViewById(R.id.stopbutton);

		stopButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				cancelAllAlarms();

				Toast.makeText(getApplicationContext(),
						getString(R.string.service_stopped), Toast.LENGTH_SHORT)
						.show();

				// Save the current state of this activity in shared preferences
				startButton.setEnabled(true);
				editor.putBoolean(Constants.SCHEDULER_ENABLED, false);
				editor.commit();
			}
		});

	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		String s = parent.getSelectedItem().toString();

		if (parent.equals(findViewById(R.id.wf_from_spinner))) {
			wfFromHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			wfFromMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));

		} else if (parent.equals(findViewById(R.id.wf_to_spinner))) {
			wfToHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			wfToMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));
		} else if (parent.equals(findViewById(R.id.tg_from_spinner))) {
			tgFromHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			tgFromMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));

		} else if (parent.equals(findViewById(R.id.tg_to_spinner))) {
			tgToHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			tgToMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));
		}

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing

	}

	@Override
	public void onResume() {
		super.onResume();
		settings = getSharedPreferences(Constants.SHARED_PREFS_NAME,
				Activity.MODE_PRIVATE);
		editor = settings.edit();

		// See if this service is enabled. If it is, disable the
		// "enable service" button
		boolean isEnabled = settings.getBoolean(Constants.SCHEDULER_ENABLED,
				false);
		startButton.setEnabled(!isEnabled);

		spinners[0].setSelection(settings.getInt(
				Constants.SCHEDULER_WIFI_FROM_SELECTION, 0));
		spinners[1].setSelection(settings.getInt(
				Constants.SCHEDULER_WIFI_TO_SELECTION, 0));
		spinners[2].setSelection(settings.getInt(
				Constants.SCHEDULER_3G_FROM_SELECTION, 0));
		spinners[3].setSelection(settings.getInt(
				Constants.SCHEDULER_3G_TO_SELECTION, 0));

		wfCheck.setChecked(settings.getBoolean(
				Constants.SCHEDULER_DISABLE_WIFI, false));
		tgCheck.setChecked(settings.getBoolean(Constants.SCHEDULER_DISABLE_3G,
				false));
	}

	/**
	 * Disables all alarms related to this Activity
	 * 
	 */
	public void cancelAllAlarms() {

		// Needed to cancel the alarms properly
		Intent tempIntent = new Intent(SchedulerActivity.this,
				ScheduleWifiDisableReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(
				SchedulerActivity.this, 0, tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi disabling
		am.cancel(sender);

		tempIntent = new Intent(SchedulerActivity.this,
				ScheduleWifiEnableReceiver.class);

		sender = PendingIntent.getBroadcast(SchedulerActivity.this, 0,
				tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi enabling
		am.cancel(sender);

		tempIntent = new Intent(SchedulerActivity.this,
				Schedule3GDisableReceiver.class);
		sender = PendingIntent.getBroadcast(SchedulerActivity.this, 0,
				tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi disabling
		am.cancel(sender);

		tempIntent = new Intent(SchedulerActivity.this,
				Schedule3GEnableReceiver.class);

		sender = PendingIntent.getBroadcast(SchedulerActivity.this, 0,
				tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi enabling
		am.cancel(sender);

	}
}
