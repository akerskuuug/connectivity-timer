package com.connectivitymanager.utility;

import java.util.Calendar;

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

}