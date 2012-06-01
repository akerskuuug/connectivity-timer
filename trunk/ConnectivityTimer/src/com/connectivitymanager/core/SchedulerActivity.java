package com.connectivitymanager.core;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.connectivitymanager.R;
import com.connectivitymanager.alarm.SchedulerReceiver;
import com.connectivitymanager.database.AlarmDbAdapter;
import com.connectivitymanager.utility.Constants;
import com.connectivitymanager.utility.MyTimePicker;
import com.connectivitymanager.utility.Tools;

public class SchedulerActivity extends Activity implements OnClickListener {

	private AlarmManager am;
	private CheckBox notificationCheck;
	private SharedPreferences settings;
	private Editor editor;
	private ListView alarmList;

	private final ArrayList<Alarm> alarm_data = new ArrayList<Alarm>();
	private final ArrayList<Integer> alarmIDs = new ArrayList<Integer>();

	private AlarmDbAdapter dbAdapter;

	public static int SDK_VERSION = 8;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler);
		SDK_VERSION = Integer.valueOf(android.os.Build.VERSION.SDK);
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		settings =
				getSharedPreferences(Constants.SHARED_PREFS_NAME,
						Activity.MODE_PRIVATE);
		editor = settings.edit();

		CheckBox notificationCheck =
				(CheckBox) findViewById(R.id.scheduler_notification_check);

		notificationCheck
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						editor.putBoolean(
								Constants.SCHEDULER_SHOW_NOTIFICATION,
								isChecked);

						editor.commit();

					}
				});

		dbAdapter = new AlarmDbAdapter(SchedulerActivity.this);
		dbAdapter.open();

		alarmList = (ListView) findViewById(R.id.alarm_list);

		View footer =
				getLayoutInflater().inflate(R.layout.listview_header_row, null);

		alarmList.addFooterView(footer);

		alarmList.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final int pos = position;

				AlertDialog.Builder aDialog =
						new AlertDialog.Builder(SchedulerActivity.this);

				aDialog.setCancelable(true);
				aDialog.setTitle(R.string.confirm_delete_short);
				aDialog.setMessage(R.string.confirm_delete);

				aDialog.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								int tempId = alarmIDs.get(pos);

								dbAdapter.deleteAlarm(tempId);

								// Needed to cancel the alarms properly
								Intent tempIntent =
										new Intent(SchedulerActivity.this,
												SchedulerReceiver.class);

								PendingIntent sender =
										getDistinctPendingIntent(tempIntent,
												tempId);
								// Cancel ongoing alarms related to scheduled
								// Wi-Fi disabling
								am.cancel(sender);

								PendingIntent newSender =
										getDistinctPendingIntent(tempIntent,
												tempId + 1);

								// Cancel ongoing alarms related to scheduled
								// Wi-Fi enabling
								am.cancel(newSender);

								alarm_data.remove(pos);
								alarmIDs.remove(pos);

								Alarm[] alarmArray =
										new Alarm[alarm_data.size()];

								alarmList.setAdapter(new AlarmAdapter(
										SchedulerActivity.this,
										R.layout.alarm_list_item, alarm_data
												.toArray(alarmArray)));
								dialog.dismiss();
							}
						}).setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});

				aDialog.create().show();

				return true;
			}

		});

		Button addButton = (Button) footer.findViewById(R.id.add_button);
		addButton.setOnClickListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();

		Cursor alarmCursor = dbAdapter.fetchAllAlarms();
		alarmCursor.moveToFirst();

		// Go through all alarms and their IDs, adding them to the lists
		while (!alarmCursor.isAfterLast()) {
			alarmIDs.add(alarmCursor.getInt(0));

			alarm_data.add(new Alarm(alarmCursor.getInt(1), alarmCursor
					.getInt(2), alarmCursor.getInt(10) == 1, alarmCursor
					.getInt(11) == 1, new boolean[] {
					alarmCursor.getInt(3) == 1, alarmCursor.getInt(4) == 1,
					alarmCursor.getInt(5) == 1, alarmCursor.getInt(6) == 1,
					alarmCursor.getInt(7) == 1, alarmCursor.getInt(8) == 1,
					alarmCursor.getInt(9) == 1, }));

			alarmCursor.moveToNext();

		}

		Alarm[] alarmArray = new Alarm[alarm_data.size()];

		alarmList.setAdapter(new AlarmAdapter(SchedulerActivity.this,
				R.layout.alarm_list_item, alarm_data.toArray(alarmArray)));

		notificationCheck =
				(CheckBox) findViewById(R.id.scheduler_notification_check);
		notificationCheck.setChecked(settings.getBoolean(
				Constants.SCHEDULER_SHOW_NOTIFICATION, true));

	}

	@Override
	public void onPause() {
		super.onPause();

		alarmIDs.clear();
		alarm_data.clear();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		dbAdapter.close();
	}

	public void onClick(View parent) {
		if (parent.equals(findViewById(R.id.add_button))) {
			final Dialog dialog = new Dialog(SchedulerActivity.this);
			dialog.setContentView(R.layout.alarm_popup);
			dialog.setTitle(R.string.add);
			dialog.setCancelable(true);

			final Button fromButton =
					(Button) dialog.findViewById(R.id.from_button);
			final Button toButton =
					(Button) dialog.findViewById(R.id.to_button);

			Calendar cal = Calendar.getInstance();

			String temp =
					Tools.getPaddedString(cal.getTime().getHours())
							+ ":"
							+ Tools.getPaddedString(cal.getTime().getMinutes() / 10 * 10 + 5);

			fromButton.setText(temp);
			toButton.setText(temp);

			OnClickListener listener = new OnClickListener() {

				public void onClick(final View parent) {
					final Dialog nDialog = new Dialog(SchedulerActivity.this);
					nDialog.setContentView(R.layout.time_dialog);
					nDialog.setTitle(R.string.pick_time);
					nDialog.setCancelable(true);

					Button doneButton =
							(Button) nDialog.findViewById(R.id.done_button);
					final MyTimePicker picker =
							(MyTimePicker) nDialog.findViewById(R.id.picker);

					String timeString = ((Button) parent).getText().toString();

					picker.setHour(Integer.parseInt(timeString.substring(0, 2)));
					picker.setMinute(Integer.parseInt(timeString.substring(3)));

					doneButton.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							((Button) parent).setText(Tools
									.getPaddedString(picker.getHour())
									+ ":"
									+ Tools.getPaddedString(picker.getMinute()));

							nDialog.dismiss();
						}

					});

					nDialog.show();
				}
			};

			fromButton.setOnClickListener(listener);
			toButton.setOnClickListener(listener);

			final CheckBox wifiCheck =
					(CheckBox) dialog.findViewById(R.id.disable_wifi_check);
			final CheckBox threeGCheck =
					(CheckBox) dialog.findViewById(R.id.disable_3g_check);

			// Hide 3G related views for devices with SDK version 8 or lower
			if (SDK_VERSION <= 8) {
				threeGCheck.setVisibility(View.GONE);
			}

			final CheckBox[] weekdayChecks =
					{
							(CheckBox) dialog.findViewById(R.id.sunday_check),
							(CheckBox) dialog.findViewById(R.id.monday_check),
							(CheckBox) dialog.findViewById(R.id.tuesday_check),
							(CheckBox) dialog
									.findViewById(R.id.wednesday_check),
							(CheckBox) dialog.findViewById(R.id.thursday_check),
							(CheckBox) dialog.findViewById(R.id.friday_check),
							(CheckBox) dialog.findViewById(R.id.saturday_check) };

			Button alarmDoneButton =
					(Button) dialog.findViewById(R.id.done_button);

			alarmDoneButton.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					String fromString = fromButton.getText().toString();
					String untilString = toButton.getText().toString();

					int fromHours =
							Integer.parseInt(fromString.substring(0, 2));
					int fromMinutes = Integer.parseInt(fromString.substring(3));

					int toHours = Integer.parseInt(untilString.substring(0, 2));
					int toMinutes = Integer.parseInt(untilString.substring(3));

					boolean wfChecked = wifiCheck.isChecked();
					boolean tgChecked = threeGCheck.isChecked();

					boolean[] wdays = new boolean[7];

					for (int i = 0; i < 7; i++) {
						wdays[i] = weekdayChecks[i].isChecked();
					}

					alarm_data.add(new Alarm(fromHours * 60 + fromMinutes,
							toHours * 60 + toMinutes, wfChecked, tgChecked,
							wdays));

					Alarm[] alarmArray = new Alarm[alarm_data.size()];

					alarmList.setAdapter(new AlarmAdapter(
							SchedulerActivity.this, R.layout.alarm_list_item,
							alarm_data.toArray(alarmArray)));

					int newMaxId =
							settings.getInt(Constants.SCHEDULER_MAX_ALARM_ID, 0) + 2;

					dbAdapter.createAlarm(newMaxId, fromHours * 60
							+ fromMinutes, toHours * 60 + toMinutes, wdays,
							wfChecked, tgChecked);

					editor.putInt(Constants.SCHEDULER_MAX_ALARM_ID, newMaxId);
					editor.commit();

					alarmIDs.add(newMaxId);

					// Calendars used to set all of the alarms
					Calendar calNow = Calendar.getInstance(), calFrom =
							Calendar.getInstance(), calTo =
							Calendar.getInstance();

					// Get the current day of the week (starting with Sunday
					// as 0)
					int currentDay = calNow.get(Calendar.DAY_OF_WEEK) - 1;

					// Set the Wi-Fi related times according to their
					// spinners
					calFrom.set(Calendar.HOUR_OF_DAY, fromHours);
					calFrom.set(Calendar.MINUTE, fromMinutes);
					calFrom.set(Calendar.SECOND, 0);

					calTo.set(Calendar.HOUR_OF_DAY, toHours);
					calTo.set(Calendar.MINUTE, toMinutes);
					calTo.set(Calendar.SECOND, 0);

					// If the alarm is set to be enabled before it is disabled,
					// set
					// it for 24 hours later
					if (calTo.compareTo(calFrom) <= 0) {
						calTo.add(Calendar.DATE, 1);
					}

					// Number of days until the alarm is enabled the next time
					int daysUntil = dbAdapter.daysUntilNextEnabled(newMaxId);

					// If the alarm is disabled or both of the times have
					// already passed, set the alarms for the next enabled day
					if (!wdays[currentDay] || calFrom.compareTo(calNow) <= 0
							&& calTo.compareTo(calNow) <= 0) {
						calFrom.add(Calendar.DATE, daysUntil);
						calTo.add(Calendar.DATE, daysUntil);

					}

					// If the alarm is set to be disabled before now, set it for
					// the next enabled day
					if (calFrom.compareTo(calNow) <= 0) {

						calFrom.add(Calendar.DATE, daysUntil);
					}

					Intent intent =
							new Intent(SchedulerActivity.this,
									SchedulerReceiver.class);

					intent.putExtra(Constants.SCHEDULER_ALARM_ID, newMaxId);
					intent.putExtra(Constants.SCHEDULER_ENABLE, false);
					intent.putExtra(Constants.SCHEDULER_DISABLE_WIFI, wfChecked);
					intent.putExtra(Constants.SCHEDULER_DISABLE_3G, tgChecked);

					Log.d("",
							""
									+ (calFrom.getTimeInMillis() - calNow
											.getTimeInMillis()) / 60000);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP, calFrom.getTimeInMillis(),
							getDistinctPendingIntent(intent, newMaxId));

					// Change the second alarm to enable connections
					intent.putExtra(Constants.SCHEDULER_ALARM_ID, newMaxId + 1);
					intent.putExtra(Constants.SCHEDULER_ENABLE, true);

					// Set the alarm
					am.set(AlarmManager.RTC_WAKEUP, calTo.getTimeInMillis(),
							getDistinctPendingIntent(intent, newMaxId + 1));

					dialog.dismiss();

				}
			});

			dialog.show();

		}

	}

	private PendingIntent getDistinctPendingIntent(Intent intent, int requestId) {
		return Tools.getDistinctPendingIntent(SchedulerActivity.this, intent,
				requestId);
	}

}
