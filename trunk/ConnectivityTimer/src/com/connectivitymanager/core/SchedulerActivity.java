package com.connectivitymanager.core;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.connectivitymanager.R;
import com.connectivitymanager.alarm.Schedule3GDisableReceiver;
import com.connectivitymanager.alarm.Schedule3GEnableReceiver;
import com.connectivitymanager.alarm.ScheduleWifiDisableReceiver;
import com.connectivitymanager.alarm.ScheduleWifiEnableReceiver;

public class SchedulerActivity extends Activity implements
		OnItemSelectedListener {

	private int wfFromHours, wfToHours, tgFromHours, tgToHours;
	private int wfFromMinutes, wfToMinutes, tgFromMinutes, tgToMinutes;
	private AlarmManager am;
	private CheckBox wfCheck, tgCheck;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler);

		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		wfCheck = (CheckBox) findViewById(R.id.wf_check);
		tgCheck = (CheckBox) findViewById(R.id.tg_check);

		final Spinner[] spinners =
				{ (Spinner) findViewById(R.id.wf_from_spinner),
						(Spinner) findViewById(R.id.wf_to_spinner),
						(Spinner) findViewById(R.id.tg_from_spinner),
						(Spinner) findViewById(R.id.tg_to_spinner) };
		for (Spinner spin : spinners) {
			ArrayAdapter<CharSequence> adapter =
					ArrayAdapter
							.createFromResource(
									this,
									com.connectivitymanager.R.array.scheduler_time_array,
									R.layout.my_simple_spinner_item);
			spin.setAdapter(adapter);
			spin.setSelection(0);
			spin.setOnItemSelectedListener(this);
		}

		Button startButton = (Button) findViewById(R.id.startbutton);

		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Calendar calNow = Calendar.getInstance();
				Calendar calWfFrom = Calendar.getInstance();
				Calendar calWfTo = Calendar.getInstance();
				Calendar calTgFrom = Calendar.getInstance();
				Calendar calTgTo = Calendar.getInstance();

				if (wfCheck.isChecked()) {

					calWfFrom.set(Calendar.HOUR_OF_DAY, wfFromHours);
					calWfFrom.set(Calendar.MINUTE, wfFromMinutes);
					calWfFrom.set(Calendar.SECOND, 0);
					calWfTo.set(Calendar.HOUR_OF_DAY, wfToHours);
					calWfTo.set(Calendar.MINUTE, wfToMinutes);
					calWfTo.set(Calendar.SECOND, 0);

					if (calWfFrom.compareTo(calNow) <= 0) {
						calWfFrom.add(Calendar.DATE, 1);
						calWfTo.add(Calendar.DATE, 1);
						if (calWfTo.compareTo(calWfFrom) <= 0) {
							calWfTo.add(Calendar.DATE, 1);
						}
					} else if (calWfTo.compareTo(calWfFrom) <= 0) {
						calWfTo.add(Calendar.DATE, 1);
					}

					Intent disableIntent =
							new Intent(SchedulerActivity.this,
									ScheduleWifiDisableReceiver.class);

					PendingIntent disableSender =
							PendingIntent.getBroadcast(SchedulerActivity.this,
									0, disableIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);

					// Cancel any conflicting alarms
					am.cancel(disableSender);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP,
							calWfFrom.getTimeInMillis(), disableSender);

					Intent enableIntent =
							new Intent(SchedulerActivity.this,
									ScheduleWifiEnableReceiver.class);
					PendingIntent enableSender =
							PendingIntent.getBroadcast(SchedulerActivity.this,
									0, enableIntent,
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

					calTgFrom.set(Calendar.HOUR_OF_DAY, tgFromHours);
					calTgFrom.set(Calendar.MINUTE, tgFromMinutes);
					calTgFrom.set(Calendar.SECOND, 0);
					calTgTo.set(Calendar.HOUR_OF_DAY, tgToHours);
					calTgTo.set(Calendar.MINUTE, tgToMinutes);
					calTgTo.set(Calendar.SECOND, 0);

					int hours = tgFromHours;
					int minutes = tgFromMinutes;

					if (calTgFrom.compareTo(calNow) <= 0) {
						calTgFrom.add(Calendar.DATE, 1);
						calTgTo.add(Calendar.DATE, 1);
						if (calTgTo.compareTo(calTgFrom) <= 0) {
							calTgTo.add(Calendar.DATE, 1);
						}
					} else if (calWfTo.compareTo(calWfFrom) <= 0) {
						calWfTo.add(Calendar.DATE, 1);
					}
					Intent disableIntent =
							new Intent(SchedulerActivity.this,
									Schedule3GDisableReceiver.class);

					PendingIntent disableSender =
							PendingIntent.getBroadcast(SchedulerActivity.this,
									0, disableIntent,
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
					Intent enableIntent =
							new Intent(SchedulerActivity.this,
									Schedule3GEnableReceiver.class);

					PendingIntent enableSender =
							PendingIntent.getBroadcast(SchedulerActivity.this,
									0, enableIntent,
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

			}
		});

		Button stopButton = (Button) findViewById(R.id.stopbutton);

		stopButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				cancelAllAlarms();

				Toast.makeText(getApplicationContext(),
						getString(R.string.service_stopped), Toast.LENGTH_SHORT)
						.show();
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

	/**
	 * Disables all alarms related to this Activity
	 * 
	 */
	public void cancelAllAlarms() {

		// Needed to cancel the alarms properly
		Intent tempIntent =
				new Intent(SchedulerActivity.this,
						ScheduleWifiDisableReceiver.class);
		PendingIntent sender =
				PendingIntent.getBroadcast(SchedulerActivity.this, 0,
						tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi disabling
		am.cancel(sender);

		tempIntent =
				new Intent(SchedulerActivity.this,
						ScheduleWifiEnableReceiver.class);

		sender =
				PendingIntent.getBroadcast(SchedulerActivity.this, 0,
						tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi enabling
		am.cancel(sender);

		tempIntent =
				new Intent(SchedulerActivity.this,
						Schedule3GDisableReceiver.class);
		sender =
				PendingIntent.getBroadcast(SchedulerActivity.this, 0,
						tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi disabling
		am.cancel(sender);

		tempIntent =
				new Intent(SchedulerActivity.this,
						Schedule3GEnableReceiver.class);

		sender =
				PendingIntent.getBroadcast(SchedulerActivity.this, 0,
						tempIntent, 0);

		// Cancel ongoing alarms related to scheduled Wi-Fi enabling
		am.cancel(sender);

	}
}
