package com.connectivitymanager.utility;

import java.util.Calendar;

import com.connectivitymanager.R;

public class Constants {
	public final static int DURATION = Calendar.MINUTE;

	public final static String SHARED_PREFS_NAME = "cm_prefs";

	public final static String WATCHER_ENABLED = "watcher_enabled";
	public final static String WATCHER_DURATION = "watcher_duration";
	public final static String WATCHER_DURATION_POSITION =
			"watcher_duration_position";
	public final static String WATCHER_RETRY = "watcher_retry";
	public final static String WATCHER_ALARM_REPEATS = "alarm_repeats";
	public final static String WATCHER_MAX_REPEATS = "max_repeats";
	public final static String WATCHER_EXIT = "exit";
	public final static String WATCHER_3G_ENABLE = "watcher_3g_enable";
	public final static String WATCHER_3G_DISABLE = "watcher_3g_disable";
	public final static String WATCHER_DISABLED_BY_THIS = "disabled_by_this";

	public final static String TIMED_WF_ENABLED = "timedwf_enabled";
	public final static String TIMED_WF_ENABLE_WIFI = "wifi_enable";
	public final static String TIMED_WF_DISABLE_WIFI = "wifi_disable";
	public final static String TIMED_WF_DURATION = "timedwf_duration";

	public final static String TIMED_3G_ENABLED = "timedtg_enabled";
	public final static String TIMED_3G_ENABLE_3G = "tg_enable";
	public final static String TIMED_3G_DISABLE_3G = "tg_disable";
	public final static String TIMED_3G_DURATION = "timedtg_duration";

	public final static String SCHEDULER_ENABLED = "scheduler_enabled";
	public final static String SCHEDULER_DISABLE_WIFI =
			"scheduler_disable_wifi";
	public final static String SCHEDULER_DISABLE_3G = "scheduler_disable_3g";
	public final static String SCHEDULER_WIFI_FROM_SELECTION =
			"scheduler_wffrom";
	public final static String SCHEDULER_WIFI_TO_SELECTION = "scheduler_wfto";
	public final static String SCHEDULER_3G_FROM_SELECTION = "scheduler_tgfrom";
	public final static String SCHEDULER_3G_TO_SELECTION = "scheduler_tgto";
	public final static String SCHEDULER_MODE = "scheduler_mode";
	public final static String SCHEDULER_SHOW_NOTIFICATION =
			"scheduler_show_notification";

	/*
	 * Scheduler weekday/weekend
	 */
	public final static String SCHEDULER_WD_WIFI_FROM_SELECTION =
			"scheduler_wd_wffrom";
	public final static String SCHEDULER_WD_WIFI_TO_SELECTION =
			"scheduler_wd_wfto";
	public final static String SCHEDULER_WD_3G_FROM_SELECTION =
			"scheduler_wd_tgfrom";
	public final static String SCHEDULER_WD_3G_TO_SELECTION =
			"scheduler_wd_tgto";

	public final static String SCHEDULER_WE_WIFI_FROM_SELECTION =
			"scheduler_we_wffrom";
	public final static String SCHEDULER_WE_WIFI_TO_SELECTION =
			"scheduler_we_wfto";
	public final static String SCHEDULER_WE_3G_FROM_SELECTION =
			"scheduler_we_tgfrom";
	public final static String SCHEDULER_WE_3G_TO_SELECTION =
			"scheduler_we_tgto";

	/*
	 * Scheduler weekdays 
	 */

	// MONDAY
	public final static String SCHEDULER_MON_WIFI_FROM_SELECTION =
			"scheduler_mon_wffrom";
	public final static String SCHEDULER_MON_WIFI_TO_SELECTION =
			"scheduler_mon_wfto";
	public final static String SCHEDULER_MON_3G_FROM_SELECTION =
			"scheduler_mon_tgfrom";
	public final static String SCHEDULER_MON_3G_TO_SELECTION =
			"scheduler_mon_tgto";

	// TUESDAY
	public final static String SCHEDULER_TUE_WIFI_FROM_SELECTION =
			"scheduler_tue_wffrom";
	public final static String SCHEDULER_TUE_WIFI_TO_SELECTION =
			"scheduler_tue_wfto";
	public final static String SCHEDULER_TUE_3G_FROM_SELECTION =
			"scheduler_tue_tgfrom";
	public final static String SCHEDULER_TUE_3G_TO_SELECTION =
			"scheduler_tue_tgto";

	// WEDNESDAY
	public final static String SCHEDULER_WED_WIFI_FROM_SELECTION =
			"scheduler_wed_wffrom";
	public final static String SCHEDULER_WED_WIFI_TO_SELECTION =
			"scheduler_wed_wfto";
	public final static String SCHEDULER_WED_3G_FROM_SELECTION =
			"scheduler_wed_tgfrom";
	public final static String SCHEDULER_WED_3G_TO_SELECTION =
			"scheduler_wed_tgto";

	// THURSDAY
	public final static String SCHEDULER_THU_WIFI_FROM_SELECTION =
			"scheduler_thu_wffrom";
	public final static String SCHEDULER_THU_WIFI_TO_SELECTION =
			"scheduler_thu_wfto";
	public final static String SCHEDULER_THU_3G_FROM_SELECTION =
			"scheduler_thu_tgfrom";
	public final static String SCHEDULER_THU_3G_TO_SELECTION =
			"scheduler_thu_tgto";

	// FRIDAY
	public final static String SCHEDULER_FRI_WIFI_FROM_SELECTION =
			"scheduler_fri_wffrom";
	public final static String SCHEDULER_FRI_WIFI_TO_SELECTION =
			"scheduler_fri_wfto";
	public final static String SCHEDULER_FRI_3G_FROM_SELECTION =
			"scheduler_fri_tgfrom";
	public final static String SCHEDULER_FRI_3G_TO_SELECTION =
			"scheduler_fri_tgto";

	// SATURDAY
	public final static String SCHEDULER_SAT_WIFI_FROM_SELECTION =
			"scheduler_sat_wffrom";
	public final static String SCHEDULER_SAT_WIFI_TO_SELECTION =
			"scheduler_sat_wfto";
	public final static String SCHEDULER_SAT_3G_FROM_SELECTION =
			"scheduler_sat_tgfrom";
	public final static String SCHEDULER_SAT_3G_TO_SELECTION =
			"scheduler_sat_tgto";

	// SUNDAY
	public final static String SCHEDULER_SUN_WIFI_FROM_SELECTION =
			"scheduler_sun_wffrom";
	public final static String SCHEDULER_SUN_WIFI_TO_SELECTION =
			"scheduler_sun_wfto";
	public final static String SCHEDULER_SUN_3G_FROM_SELECTION =
			"scheduler_sun_tgfrom";
	public final static String SCHEDULER_SUN_3G_TO_SELECTION =
			"scheduler_sun_tgto";

	public final static String[] SCHEDULER_MEDIUM_WEEK = {
			SCHEDULER_WD_WIFI_FROM_SELECTION, SCHEDULER_WD_WIFI_TO_SELECTION,
			SCHEDULER_WE_WIFI_FROM_SELECTION, SCHEDULER_WE_WIFI_TO_SELECTION,
			SCHEDULER_WD_3G_FROM_SELECTION, SCHEDULER_WD_3G_TO_SELECTION,
			SCHEDULER_WE_3G_FROM_SELECTION, SCHEDULER_WE_3G_TO_SELECTION };

	// All weekdays (used to decrease amount of code)
	public final static String[] SCHEDULER_ADVANCED_WEEKDAYS = {
			SCHEDULER_SUN_WIFI_FROM_SELECTION, SCHEDULER_SUN_WIFI_TO_SELECTION,
			SCHEDULER_MON_WIFI_FROM_SELECTION, SCHEDULER_MON_WIFI_TO_SELECTION,
			SCHEDULER_TUE_WIFI_FROM_SELECTION, SCHEDULER_TUE_WIFI_TO_SELECTION,
			SCHEDULER_WED_WIFI_FROM_SELECTION, SCHEDULER_WED_WIFI_TO_SELECTION,
			SCHEDULER_THU_WIFI_FROM_SELECTION, SCHEDULER_THU_WIFI_TO_SELECTION,
			SCHEDULER_FRI_WIFI_FROM_SELECTION, SCHEDULER_FRI_WIFI_TO_SELECTION,
			SCHEDULER_SAT_WIFI_FROM_SELECTION, SCHEDULER_SAT_WIFI_TO_SELECTION,
			SCHEDULER_SUN_3G_FROM_SELECTION, SCHEDULER_SUN_3G_TO_SELECTION,
			SCHEDULER_MON_3G_FROM_SELECTION, SCHEDULER_MON_3G_TO_SELECTION,
			SCHEDULER_TUE_3G_FROM_SELECTION, SCHEDULER_TUE_3G_TO_SELECTION,
			SCHEDULER_WED_3G_FROM_SELECTION, SCHEDULER_WED_3G_TO_SELECTION,
			SCHEDULER_THU_3G_FROM_SELECTION, SCHEDULER_THU_3G_TO_SELECTION,
			SCHEDULER_FRI_3G_FROM_SELECTION, SCHEDULER_FRI_3G_TO_SELECTION,
			SCHEDULER_SAT_3G_FROM_SELECTION, SCHEDULER_SAT_3G_TO_SELECTION, };

	public final static int[] SCHEDULER_SIMPLE_SPINNER_IDS = {
			R.id.wf_from_spinner, R.id.wf_to_spinner, R.id.tg_from_spinner,
			R.id.tg_to_spinner, };

	public final static int[] SCHEDULER_MEDIUM_SPINNER_IDS = {
			R.id.weekday_wf_from_spinner, R.id.weekday_wf_to_spinner,
			R.id.weekend_wf_from_spinner, R.id.weekend_wf_to_spinner,
			R.id.weekday_tg_from_spinner, R.id.weekday_tg_to_spinner,
			R.id.weekend_tg_from_spinner, R.id.weekend_tg_to_spinner };

	public final static int[] SCHEDULER_ADVANCED_SPINNER_IDS = {
			R.id.sun_wf_from_spinner, R.id.sun_wf_to_spinner,
			R.id.mon_wf_from_spinner, R.id.mon_wf_to_spinner,
			R.id.tue_wf_from_spinner, R.id.tue_wf_to_spinner,
			R.id.wed_wf_from_spinner, R.id.wed_wf_to_spinner,
			R.id.thu_wf_from_spinner, R.id.thu_wf_to_spinner,
			R.id.fri_wf_from_spinner, R.id.fri_wf_to_spinner,
			R.id.sat_wf_from_spinner, R.id.sat_wf_to_spinner,
			R.id.sun_tg_from_spinner, R.id.sun_tg_to_spinner,
			R.id.mon_tg_from_spinner, R.id.mon_tg_to_spinner,
			R.id.tue_tg_from_spinner, R.id.tue_tg_to_spinner,
			R.id.wed_tg_from_spinner, R.id.wed_tg_to_spinner,
			R.id.thu_tg_from_spinner, R.id.thu_tg_to_spinner,
			R.id.fri_tg_from_spinner, R.id.fri_tg_to_spinner,
			R.id.sat_tg_from_spinner, R.id.sat_tg_to_spinner, };

}