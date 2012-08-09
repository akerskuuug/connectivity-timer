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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class SchedulerActivity extends Activity implements OnClickListener,
		OnItemClickListener {

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

		alarmList.setOnItemClickListener(this);

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
			Calendar cal = Calendar.getInstance();
			int currentTime =
					cal.getTime().getHours() * 60 + cal.getTime().getMinutes()
							/ 10 * 10 + 5;

			final Dialog dialog =
					createAlarmDialog(currentTime, currentTime, false, false,
							new boolean[] { true, true, true, true, true, true,
									true }, true, 0);

			dialog.show();

		}

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.equals(findViewById(R.id.alarm_list))) {

			Alarm alarm = alarm_data.get(position);

			final Dialog dialog =
					createAlarmDialog(alarm.from, alarm.to, alarm.disableWifi,
							alarm.disable3g, new boolean[] { alarm.sunday,
									alarm.monday, alarm.tuesday,
									alarm.wednesday, alarm.thursday,
									alarm.friday, alarm.saturday }, false,
							alarmIDs.get(position));

			dialog.show();
		}

	}

	/**
	 * 
	 * Creates a new Dialog which contains the necessary items to create an
	 * alarm
	 * 
	 * @param from
	 * @param to
	 * @param disableWifi
	 * @param disable3g
	 * @param wdays
	 * 
	 * @param newAlarm
	 *            if the alarm is new or an updated one
	 * @param updateAlarmId
	 *            if the alarm is to be updated, this is its ID. This value can
	 *            be set to anything if the alarm is new
	 * @return the new Dialog
	 */
	private Dialog createAlarmDialog(final int from, final int to,
			final boolean disableWifi, final boolean disable3g,
			boolean[] wdays, final boolean newAlarm, final int updateAlarmId) {

		int fromHours = from / 60;
		int fromMinutes = from % 60;

		int toHours = to / 60;
		int toMinutes = to % 60;

		final Dialog dialog = new Dialog(SchedulerActivity.this);
		dialog.setContentView(R.layout.alarm_popup);
		if (newAlarm) {
			dialog.setTitle(R.string.add);
		} else {
			dialog.setTitle(R.string.edit);
		}
		dialog.setCancelable(true);

		final Button fromButton =
				(Button) dialog.findViewById(R.id.from_button);
		final Button toButton = (Button) dialog.findViewById(R.id.to_button);

		fromButton.setText(Tools.fixTimeFormatting(fromHours, fromMinutes));
		toButton.setText(Tools.fixTimeFormatting(toHours, toMinutes));

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
						((Button) parent).setText(Tools.getPaddedString(picker
								.getHour())
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
		wifiCheck.setChecked(disableWifi);

		final CheckBox tgCheck =
				(CheckBox) dialog.findViewById(R.id.disable_3g_check);
		tgCheck.setChecked(disable3g);

		final CheckBox[] weekdayChecks =
				{ (CheckBox) dialog.findViewById(R.id.sunday_check),
						(CheckBox) dialog.findViewById(R.id.monday_check),
						(CheckBox) dialog.findViewById(R.id.tuesday_check),
						(CheckBox) dialog.findViewById(R.id.wednesday_check),
						(CheckBox) dialog.findViewById(R.id.thursday_check),
						(CheckBox) dialog.findViewById(R.id.friday_check),
						(CheckBox) dialog.findViewById(R.id.saturday_check) };

		for (int i = 0; i < 7; i++) {
			weekdayChecks[i].setChecked(wdays[i]);
		}

		Button alarmDoneButton = (Button) dialog.findViewById(R.id.done_button);

		alarmDoneButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String fromString = fromButton.getText().toString();
				String untilString = toButton.getText().toString();

				int fromHours = Integer.parseInt(fromString.substring(0, 2));
				int fromMinutes = Integer.parseInt(fromString.substring(3));

				int toHours = Integer.parseInt(untilString.substring(0, 2));
				int toMinutes = Integer.parseInt(untilString.substring(3));

				boolean vibChecked = wifiCheck.isChecked();
				boolean mediaChecked = tgCheck.isChecked();

				boolean[] wdays = new boolean[7];

				for (int i = 0; i < 7; i++) {
					wdays[i] = weekdayChecks[i].isChecked();
				}

				if (!newAlarm) {
					for (int i = 0; i < alarmIDs.size(); i++) {

						if (alarmIDs.get(i) == updateAlarmId) {
							alarmIDs.remove(i);
							alarm_data.remove(i);
							break;
						}

					}

				}

				// Set the alarm.
				setAlarm(fromHours, fromMinutes, toHours, toMinutes,
						vibChecked, mediaChecked, wdays, newAlarm,
						updateAlarmId);

				dialog.dismiss();

			}
		});

		return dialog;
	}

	/**
	 * Sets an alarm pair and adds it to the list and database
	 * 
	 * @param fromHours
	 * @param fromMinutes
	 * @param toHours
	 * @param toMinutes
	 * @param disableWifi
	 * @param disable3g
	 * @param wdays
	 * @param newAlarm
	 *            if the alarm is new or an updated one
	 * @param updateAlarmId
	 *            if the alarm is to be updated, this is its ID. This value can
	 *            be set to anything if the alarm is new
	 */
	private void setAlarm(int fromHours, int fromMinutes, int toHours,
			int toMinutes, boolean disableWifi, boolean disable3g,
			boolean[] wdays, boolean newAlarm, int updateAlarmId) {

		int alarmId;
		alarm_data.add(new Alarm(fromHours * 60 + fromMinutes, toHours * 60
				+ toMinutes, disableWifi, disable3g, wdays));

		Alarm[] alarmArray = new Alarm[alarm_data.size()];

		alarmList.setAdapter(new AlarmAdapter(SchedulerActivity.this,
				R.layout.alarm_list_item, alarm_data.toArray(alarmArray)));

		if (newAlarm) {
			alarmId = settings.getInt(Constants.SCHEDULER_MAX_ALARM_ID, 0) + 2;
			dbAdapter.createAlarm(alarmId, fromHours * 60 + fromMinutes,
					toHours * 60 + toMinutes, wdays, disableWifi, disable3g);

			editor.putInt(Constants.SCHEDULER_MAX_ALARM_ID, alarmId);
			editor.commit();

		} else {

			alarmId = updateAlarmId;
			dbAdapter.updateAlarm(alarmId, fromHours * 60 + fromMinutes,
					toHours * 60 + toMinutes, wdays, disableWifi, disable3g);
		}

		alarmIDs.add(alarmId);
		// Calendars used to set all of the alarms
		Calendar calNow = Calendar.getInstance(), calFrom =
				Calendar.getInstance(), calTo = Calendar.getInstance();

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

		// Number of days until the alarm is enabled the next time
		int daysUntil = dbAdapter.daysUntilNextEnabled(alarmId);

		// If the alarm is set to be enabled before it is disabled, set
		// it for 24 hours later
		if (calTo.compareTo(calFrom) <= 0) {
			calTo.add(Calendar.DATE, 1);
		}

		// If the alarm is disabled today or both of the times have
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
				new Intent(SchedulerActivity.this, SchedulerReceiver.class);

		intent.putExtra(Constants.SCHEDULER_ALARM_ID, alarmId);
		intent.putExtra(Constants.SCHEDULER_ENABLE, false);
		intent.putExtra(Constants.SCHEDULER_DISABLE_WIFI, disableWifi);
		intent.putExtra(Constants.SCHEDULER_DISABLE_3G, disable3g);

		// Set the alarm
		am.set(AlarmManager.RTC_WAKEUP, calFrom.getTimeInMillis(),
				getDistinctPendingIntent(intent, alarmId));

		// Change the second alarm to enable connections
		intent.putExtra(Constants.SCHEDULER_ALARM_ID, alarmId + 1);
		intent.putExtra(Constants.SCHEDULER_ENABLE, true);

		// Set the alarm
		am.set(AlarmManager.RTC_WAKEUP, calTo.getTimeInMillis(),
				getDistinctPendingIntent(intent, alarmId + 1));

	}

	private PendingIntent getDistinctPendingIntent(Intent intent, int requestId) {
		return Tools.getDistinctPendingIntent(SchedulerActivity.this, intent,
				requestId);
	}

}
