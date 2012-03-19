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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.connectivitymanager.R;
import com.connectivitymanager.alarm.Schedule3GDisableReceiver;
import com.connectivitymanager.alarm.Schedule3GEnableReceiver;
import com.connectivitymanager.alarm.ScheduleWifiDisableReceiver;
import com.connectivitymanager.alarm.ScheduleWifiEnableReceiver;
import com.connectivitymanager.utility.Constants;

public class SchedulerActivity extends Activity implements
		OnItemSelectedListener {

	private final int[] wfFromHours = new int[7], wfToHours = new int[7],
			tgFromHours = new int[7], tgToHours = new int[7];
	private final int[] wfFromMinutes = new int[7], wfToMinutes = new int[7],
			tgFromMinutes = new int[7], tgToMinutes = new int[7];
	private AlarmManager am;
	private CheckBox wfCheck, tgCheck, notificationCheck;
	private Button startButton;
	private SharedPreferences settings;
	private Editor editor;
	private Spinner[] spinners;
	private Button[] modeButtons;
	private String mode;

	public static int SDK_VERSION = 8;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler);
		SDK_VERSION = Integer.valueOf(android.os.Build.VERSION.SDK);
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		// Set the mode to simple for first launch
		mode = "simple";
		wfCheck = (CheckBox) findViewById(R.id.simple_wf_check);
		tgCheck = (CheckBox) findViewById(R.id.simple_tg_check);

		// Hide 3G related views for devices with SDK version 8 or lower
		if (SDK_VERSION <= 8) {

			LinearLayout tgLayout =
					(LinearLayout) findViewById(R.id.scheduler_s_tg_layout);
			tgLayout.setVisibility(View.GONE);

			tgLayout = (LinearLayout) findViewById(R.id.scheduler_m_tg_layout);
			tgLayout.setVisibility(View.GONE);

			tgLayout = (LinearLayout) findViewById(R.id.scheduler_a_tg_layout);
			tgLayout.setVisibility(View.GONE);

		}

		modeButtons = new Button[3];
		// Initialize all the buttons
		modeButtons[0] = (Button) findViewById(R.id.simple_button);
		modeButtons[1] = (Button) findViewById(R.id.medium_button);
		modeButtons[2] = (Button) findViewById(R.id.advanced_button);

		// Since we are in simple mode in the beginning, disable the button for
		// switching to it
		modeButtons[0].setEnabled(false);

		// Set the onclickListener for all of the buttons
		for (Button button : modeButtons) {
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					modeChanged(v);
				}
			});
		}

		startButton = (Button) findViewById(R.id.startbutton);

		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				// Cancel any conflicting alarms
				cancelAllAlarms();

				// Since friday night is often counted as weekend and sunday
				// night is counted as a weekday night, if Wi-Fi or mobile data
				// is set to be disabled during the night in medium mode we will
				// set the times accordingly
				if (mode.equals("medium")) {
					if (wfFromHours[6] >= wfToHours[6]
							&& wfFromMinutes[6] >= wfToMinutes[6]
							&& wfFromHours[1] >= wfToHours[1]
							&& wfFromMinutes[1] >= wfToMinutes[1]) {

						wfFromHours[5] = wfFromHours[6];
						wfToHours[5] = wfToHours[6];

						wfFromHours[0] = wfFromHours[1];
						wfToHours[0] = wfToHours[1];
					}
					if (tgFromHours[6] >= tgToHours[6]
							&& tgFromMinutes[6] >= tgToMinutes[6]
							&& tgFromHours[1] >= tgToHours[1]
							&& tgFromMinutes[1] >= tgToMinutes[1]) {

						tgFromHours[5] = tgFromHours[6];
						tgToHours[5] = tgToHours[6];

						tgFromHours[0] = tgFromHours[1];
						tgToHours[0] = tgToHours[1];
					}

				}

				// Calendars used to set all of the alarms
				Calendar calNow = Calendar.getInstance();
				Calendar calWfFrom, calWfTo, calTgFrom, calTgTo;

				// Get the current day of the week (starting with Sunday as 0)
				int currentDay = calNow.get(Calendar.DAY_OF_WEEK) - 1;

				if (wfCheck.isChecked()) {
					for (int i = 0; i < wfFromHours.length; i++) {

						calWfFrom = Calendar.getInstance();
						calWfTo = Calendar.getInstance();

						// Set the Wi-Fi related times according to their
						// spinners
						calWfFrom.set(Calendar.HOUR_OF_DAY, wfFromHours[i]);
						calWfFrom.set(Calendar.MINUTE, wfFromMinutes[i]);
						calWfFrom.set(Calendar.SECOND, 0);

						calWfTo.set(Calendar.HOUR_OF_DAY, wfToHours[i]);
						calWfTo.set(Calendar.MINUTE, wfToMinutes[i]);
						calWfTo.set(Calendar.SECOND, 0);

						if (calWfTo.compareTo(calWfFrom) <= 0) {
							calWfTo.add(Calendar.DATE, 1);
						}

						if (i < currentDay) {
							// If the day we are setting the alarm for has
							// already passed this week, set it for the same day
							// next week
							calWfFrom.add(Calendar.DATE, 7 - currentDay);
							calWfTo.add(Calendar.DATE, 7 - currentDay);

						} else if (i > currentDay) {
							// If the day is later this week, add the correct
							// number of days
							calWfFrom.add(Calendar.DATE, i - currentDay);
							calWfTo.add(Calendar.DATE, i - currentDay);

						} else {

							if (calWfFrom.compareTo(calNow) <= 0) {
								calWfFrom.add(Calendar.DATE, 7);
							}
							if (calWfTo.compareTo(calNow) <= 0) {
								calWfTo.add(Calendar.DATE, 7);
							}

						}

						Intent disableIntent =
								new Intent(SchedulerActivity.this,
										ScheduleWifiDisableReceiver.class);

						PendingIntent disableSender =
								PendingIntent.getBroadcast(
										SchedulerActivity.this, i,
										disableIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);

						// Set the alarm
						am.set(AlarmManager.RTC_WAKEUP,
								calWfFrom.getTimeInMillis(), disableSender);

						Intent enableIntent =
								new Intent(SchedulerActivity.this,
										ScheduleWifiEnableReceiver.class);
						PendingIntent enableSender =
								PendingIntent.getBroadcast(
										SchedulerActivity.this, i + 8,
										enableIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);

						// Set the alarm
						am.set(AlarmManager.RTC_WAKEUP,
								calWfTo.getTimeInMillis(), enableSender);

					}
				}

				if (tgCheck.isChecked()) {
					for (int i = 0; i < tgFromHours.length; i++) {

						calTgFrom = Calendar.getInstance();
						calTgTo = Calendar.getInstance();

						// Disable related
						// ----------------------------------------------------
						// //
						// Set the mobile data related times according to their
						// spinners
						calTgFrom.set(Calendar.HOUR_OF_DAY, tgFromHours[i]);
						calTgFrom.set(Calendar.MINUTE, tgFromMinutes[i]);
						calTgFrom.set(Calendar.SECOND, 0);

						calTgTo.set(Calendar.HOUR_OF_DAY, tgToHours[i]);
						calTgTo.set(Calendar.MINUTE, tgToMinutes[i]);
						calTgTo.set(Calendar.SECOND, 0);

						if (calTgTo.compareTo(calTgFrom) <= 0) {
							calTgTo.add(Calendar.DATE, 1);
						}

						if (i < currentDay) {
							// If the day we are setting the alarm for has
							// already passed this week, set it for the same day
							// next week
							calTgFrom.add(Calendar.DATE, 7 - currentDay);
							calTgTo.add(Calendar.DATE, 7 - currentDay);
						} else if (i > currentDay) {
							// If the day is later this week, add the correct
							// number of days
							calTgFrom.add(Calendar.DATE, i - currentDay);
							calTgTo.add(Calendar.DATE, i - currentDay);
						} else {

							if (calTgFrom.compareTo(calNow) <= 0) {
								calTgFrom.add(Calendar.DATE, 7);
							}
							if (calTgTo.compareTo(calNow) <= 0) {
								calTgTo.add(Calendar.DATE, 7);
							}
						}

						Intent disableIntent =
								new Intent(SchedulerActivity.this,
										Schedule3GDisableReceiver.class);

						PendingIntent disableSender =
								PendingIntent.getBroadcast(
										SchedulerActivity.this, i + 16,
										disableIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);

						// Set the alarm
						am.set(AlarmManager.RTC_WAKEUP,
								calTgFrom.getTimeInMillis(), disableSender);

						// Enable related
						// -----------------------------------------------------
						// //
						Intent enableIntent =
								new Intent(SchedulerActivity.this,
										Schedule3GEnableReceiver.class);

						PendingIntent enableSender =
								PendingIntent.getBroadcast(
										SchedulerActivity.this, i + 24,
										enableIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						// Set the alarm
						am.set(AlarmManager.RTC_WAKEUP,
								calTgTo.getTimeInMillis(), enableSender);

					}
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
				editor.putBoolean(Constants.SCHEDULER_SHOW_NOTIFICATION,
						notificationCheck.isChecked());

				saveSpinnerSelections();

				editor.putString(Constants.SCHEDULER_MODE, mode);

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

		if (mode.equals("simple")) {

			// Since the same time is set for all days, loop through all days
			// and set them
			for (int i = 0; i < wfFromHours.length; i++) {
				if (parent.equals(findViewById(R.id.wf_from_spinner))) {

					// Parse the time at which to disable Wi-Fi
					wfFromHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					wfFromMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));

				} else if (parent.equals(findViewById(R.id.wf_to_spinner))) {

					// Parse the time at which to enable Wi-Fi
					wfToHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					wfToMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));
				} else if (parent.equals(findViewById(R.id.tg_from_spinner))) {
					// Parse the time at which to disable mobile data
					tgFromHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					tgFromMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));

				} else if (parent.equals(findViewById(R.id.tg_to_spinner))) {
					// Parse the time at which to enable mobile data
					tgToHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					tgToMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));
				}
			}
		} else if (mode.equals("medium")) {
			if (parent.equals(findViewById(R.id.weekday_wf_from_spinner))) {
				// Loop through all weekdays
				for (int i = 1; i < 6; i++) {

					// Parse the time at which to disable Wi-Fi
					wfFromHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					wfFromMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));
				}
			} else if (parent.equals(findViewById(R.id.weekday_wf_to_spinner))) {
				// Loop through all weekdays
				for (int i = 1; i < 6; i++) {

					// Parse the time at which to enable Wi-Fi
					wfToHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					wfToMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));
				}
			} else if (parent
					.equals(findViewById(R.id.weekday_tg_from_spinner))) {
				// Loop through all weekdays
				for (int i = 1; i < 6; i++) {

					// Parse the time at which to disable mobile data
					tgFromHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					tgFromMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));
				}
			} else if (parent.equals(findViewById(R.id.weekday_tg_to_spinner))) {
				// Loop through all weekdays
				for (int i = 1; i < 6; i++) {

					// Parse the time at which to enable mobile data
					tgToHours[i] =
							Integer.parseInt(s.substring(0, s.indexOf(':')));
					tgToMinutes[i] =
							Integer.parseInt(s.substring(s.indexOf(':') + 1));
				}
			} else if (parent
					.equals(findViewById(R.id.weekend_wf_from_spinner))) {

				// Parse the time at which to disable Wi-Fi
				wfFromHours[0] =
						Integer.parseInt(s.substring(0, s.indexOf(':')));
				wfFromMinutes[0] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));

				// Parse the time at which to disable Wi-Fi
				wfFromHours[6] =
						Integer.parseInt(s.substring(0, s.indexOf(':')));
				wfFromMinutes[6] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));

			} else if (parent.equals(findViewById(R.id.weekend_wf_to_spinner))) {

				// Parse the time at which to enable Wi-Fi
				wfToHours[0] = Integer.parseInt(s.substring(0, s.indexOf(':')));
				wfToMinutes[0] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));

				// Parse the time at which to enable Wi-Fi
				wfToHours[6] = Integer.parseInt(s.substring(0, s.indexOf(':')));
				wfToMinutes[6] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));

			} else if (parent
					.equals(findViewById(R.id.weekend_tg_from_spinner))) {

				// Parse the time at which to disable mobile data
				tgFromHours[0] =
						Integer.parseInt(s.substring(0, s.indexOf(':')));
				tgFromMinutes[0] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));
				// Parse the time at which to disable mobile data
				tgFromHours[6] =
						Integer.parseInt(s.substring(0, s.indexOf(':')));
				tgFromMinutes[6] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));

			} else if (parent.equals(findViewById(R.id.weekend_tg_to_spinner))) {

				// Parse the time at which to enable mobile data
				tgToHours[0] = Integer.parseInt(s.substring(0, s.indexOf(':')));
				tgToMinutes[0] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));

				// Parse the time at which to enable mobile data
				tgToHours[6] = Integer.parseInt(s.substring(0, s.indexOf(':')));
				tgToMinutes[6] =
						Integer.parseInt(s.substring(s.indexOf(':') + 1));
			}

		} else if (mode.equals("advanced")) {
			for (int i = 0; i < 28; i++) {
				if (parent.equals(spinners[i])) {
					int mod = i % 2;
					// Spinners 0-13 are Wi-Fi spinners
					if (i < 14) {
						// Spinners with even numbers are disable spinners, odd
						// numbers are enable spinners
						if (mod == 0) {
							wfFromHours[i / 2] =
									Integer.parseInt(s.substring(0,
											s.indexOf(':')));
							wfFromMinutes[i / 2] =
									Integer.parseInt(s.substring(s.indexOf(':') + 1));
						} else {
							wfToHours[i / 2] =
									Integer.parseInt(s.substring(0,
											s.indexOf(':')));
							wfToMinutes[i / 2] =
									Integer.parseInt(s.substring(s.indexOf(':') + 1));
						}
						// Spinners 14-27 are mobile data spinners
					} else {
						// Spinners with even numbers are disable spinners, odd
						// numbers are enable spinners
						if (mod == 0) {
							tgFromHours[(i - 14) / 2] =
									Integer.parseInt(s.substring(0,
											s.indexOf(':')));
							tgFromMinutes[(i - 14) / 2] =
									Integer.parseInt(s.substring(s.indexOf(':') + 1));
						} else {
							tgToHours[(i - 14) / 2] =
									Integer.parseInt(s.substring(0,
											s.indexOf(':')));
							tgToMinutes[(i - 14) / 2] =
									Integer.parseInt(s.substring(s.indexOf(':') + 1));
						}
					}

				}

			}

		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing

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
		startButton.setEnabled(!settings.getBoolean(
				Constants.SCHEDULER_ENABLED, false));

		notificationCheck =
				(CheckBox) findViewById(R.id.schedulder_notification_check);
		notificationCheck.setChecked(settings.getBoolean(
				Constants.SCHEDULER_SHOW_NOTIFICATION, true));

		CheckBox[] wfChecks =
				{ (CheckBox) findViewById(R.id.simple_wf_check),
						(CheckBox) findViewById(R.id.medium_wf_check),
						(CheckBox) findViewById(R.id.advanced_wf_check) };
		for (CheckBox c : wfChecks) {
			c.setChecked(settings.getBoolean(Constants.SCHEDULER_DISABLE_WIFI,
					false));
		}
		CheckBox[] tgChecks =
				{ (CheckBox) findViewById(R.id.simple_tg_check),
						(CheckBox) findViewById(R.id.medium_tg_check),
						(CheckBox) findViewById(R.id.advanced_tg_check) };
		for (CheckBox c : tgChecks) {
			c.setChecked(settings.getBoolean(Constants.SCHEDULER_DISABLE_3G,
					false));
		}

		mode = settings.getString(Constants.SCHEDULER_MODE, "simple");

		if (mode.equals("simple")) {
			modeChanged(modeButtons[0]);
		} else if (mode.equals("medium")) {
			modeChanged(modeButtons[1]);
		} else {
			modeChanged(modeButtons[2]);
		}

		initializeSpinners();

	}

	/**
	 * Disables all alarms related to this Activity
	 * 
	 */
	public void cancelAllAlarms() {

		for (int i = 0; i < 8; i++) {

			// Needed to cancel the alarms properly
			Intent tempIntent =
					new Intent(SchedulerActivity.this,
							ScheduleWifiDisableReceiver.class);
			PendingIntent sender =
					PendingIntent.getBroadcast(SchedulerActivity.this, i,
							tempIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			// Cancel ongoing alarms related to scheduled Wi-Fi disabling
			am.cancel(sender);

			tempIntent =
					new Intent(SchedulerActivity.this,
							ScheduleWifiEnableReceiver.class);

			sender =
					PendingIntent.getBroadcast(SchedulerActivity.this, i + 8,
							tempIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			// Cancel ongoing alarms related to scheduled Wi-Fi enabling
			am.cancel(sender);

			tempIntent =
					new Intent(SchedulerActivity.this,
							Schedule3GDisableReceiver.class);
			sender =
					PendingIntent.getBroadcast(SchedulerActivity.this, i + 16,
							tempIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			// Cancel ongoing alarms related to scheduled Wi-Fi disabling
			am.cancel(sender);

			tempIntent =
					new Intent(SchedulerActivity.this,
							Schedule3GEnableReceiver.class);

			sender =
					PendingIntent.getBroadcast(SchedulerActivity.this, i + 24,
							tempIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			// Cancel ongoing alarms related to scheduled Wi-Fi enabling
			am.cancel(sender);
		}
	}

	public void modeChanged(View modeButton) {

		for (Button button : modeButtons) {
			if (button.equals(modeButton)) {
				// Disable the clicked button
				button.setEnabled(false);
			} else {
				// Enable all other buttons
				button.setEnabled(true);
			}
		}

		if (modeButton.equals(modeButtons[0])) {

			findViewById(R.id.simple_mode).setVisibility(View.VISIBLE);
			findViewById(R.id.medium_mode).setVisibility(View.GONE);
			findViewById(R.id.advanced_mode).setVisibility(View.GONE);
			mode = "simple";

			wfCheck = (CheckBox) findViewById(R.id.simple_wf_check);
			tgCheck = (CheckBox) findViewById(R.id.simple_tg_check);

		} else if (modeButton.equals(modeButtons[1])) {

			findViewById(R.id.simple_mode).setVisibility(View.GONE);
			findViewById(R.id.medium_mode).setVisibility(View.VISIBLE);
			findViewById(R.id.advanced_mode).setVisibility(View.GONE);
			mode = "medium";

			wfCheck = (CheckBox) findViewById(R.id.medium_wf_check);
			tgCheck = (CheckBox) findViewById(R.id.medium_tg_check);
		} else if (modeButton.equals(modeButtons[2])) {

			findViewById(R.id.simple_mode).setVisibility(View.GONE);
			findViewById(R.id.medium_mode).setVisibility(View.GONE);
			findViewById(R.id.advanced_mode).setVisibility(View.VISIBLE);
			mode = "advanced";

			wfCheck = (CheckBox) findViewById(R.id.advanced_wf_check);
			tgCheck = (CheckBox) findViewById(R.id.advanced_tg_check);
		}

		initializeSpinners();

	}

	public void initializeSpinners() {
		if (mode.equals("simple")) {
			spinners = new Spinner[4];

			for (int i = 0; i < spinners.length; i++) {
				spinners[i] =
						(Spinner) findViewById(Constants.SCHEDULER_SIMPLE_SPINNER_IDS[i]);
			}
		} else if (mode.equals("medium")) {
			spinners = new Spinner[8];
			for (int i = 0; i < spinners.length; i++) {
				spinners[i] =
						(Spinner) findViewById(Constants.SCHEDULER_MEDIUM_SPINNER_IDS[i]);
			}
		} else if (mode.equals("advanced")) {
			spinners = new Spinner[28];

			for (int i = 0; i < spinners.length; i++) {
				spinners[i] =
						(Spinner) findViewById(Constants.SCHEDULER_ADVANCED_SPINNER_IDS[i]);
			}

		}
		for (Spinner spin : spinners) {
			ArrayAdapter<CharSequence> adapter =
					ArrayAdapter
							.createFromResource(
									this,
									com.connectivitymanager.R.array.scheduler_time_array,
									R.layout.my_simple_spinner_item);
			spin.setAdapter(adapter);
			spin.setOnItemSelectedListener(this);
		}

		loadSpinnerSelections();
	}

	public void saveSpinnerSelections() {
		if (mode.equals("simple")) {

			/*
			 ****************************************************
			 * Wi-Fi spinners
			 ****************************************************
			 */
			editor.putInt(Constants.SCHEDULER_WIFI_FROM_SELECTION,
					spinners[0].getSelectedItemPosition());
			editor.putInt(Constants.SCHEDULER_WIFI_TO_SELECTION,
					spinners[1].getSelectedItemPosition());
			/*
			 ****************************************************
			 * 3G spinners
			 ****************************************************
			 */
			editor.putInt(Constants.SCHEDULER_3G_FROM_SELECTION,
					spinners[2].getSelectedItemPosition());
			editor.putInt(Constants.SCHEDULER_3G_TO_SELECTION,
					spinners[3].getSelectedItemPosition());
		} else if (mode.equals("medium")) {

			for (int i = 0; i < 8; i++) {
				editor.putInt(Constants.SCHEDULER_MEDIUM_WEEK[i],
						spinners[i].getSelectedItemPosition());
			}

		} else if (mode.equals("advanced")) {

			for (int i = 0; i < 28; i++) {
				editor.putInt(Constants.SCHEDULER_ADVANCED_WEEKDAYS[i],
						spinners[i].getSelectedItemPosition());
			}

		}

		editor.commit();
	}

	public void loadSpinnerSelections() {

		if (mode.equals("simple")) {

			spinners[0].setSelection(settings.getInt(
					Constants.SCHEDULER_WIFI_FROM_SELECTION, 0));
			spinners[1].setSelection(settings.getInt(
					Constants.SCHEDULER_WIFI_TO_SELECTION, 0));
			spinners[2].setSelection(settings.getInt(
					Constants.SCHEDULER_3G_FROM_SELECTION, 0));
			spinners[3].setSelection(settings.getInt(
					Constants.SCHEDULER_3G_TO_SELECTION, 0));
		} else if (mode.equals("medium")) {

			for (int i = 0; i < 8; i++) {
				spinners[i].setSelection(settings.getInt(
						Constants.SCHEDULER_MEDIUM_WEEK[i], 0));
			}

		} else if (mode.equals("advanced")) {
			for (int i = 0; i < 28; i++) {
				spinners[i].setSelection(settings.getInt(
						Constants.SCHEDULER_ADVANCED_WEEKDAYS[i], 0));
			}

		}
	}
}
