package com.connectivitymanager.database;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AlarmDbAdapter {

	// Keys
	public static final String KEY_ROWID = "_id";

	// Alarm related keys
	public static final String KEY_FROM = "timefrom";
	public static final String KEY_TO = "timeto";
	public static final String KEY_SUN = "sunday";
	public static final String KEY_MON = "monday";
	public static final String KEY_TUE = "tuesday";
	public static final String KEY_WED = "wednesday";
	public static final String KEY_THU = "thursday";
	public static final String KEY_FRI = "friday";
	public static final String KEY_SAT = "saturday";
	public static final String KEY_WIFI = "wifi";
	public static final String KEY_3G = "threeg";

	private static final String TAG = "AlarmDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	// Database properties
	private static final String DATABASE_NAME = "connectivity_manager_db";
	private static final String DATABASE_TABLE_ALARMS = "Alarms";
	private static final int DATABASE_VERSION = 1;

	// Tables
	private static final String CREATE_TABLE_ALARMS = "create table "
			+ DATABASE_TABLE_ALARMS + " (" + KEY_ROWID
			+ " INTEGER PRIMARY KEY, " + KEY_FROM + " INTEGER NOT NULL, "
			+ KEY_TO + " INTEGER NOT NULL, " + KEY_SUN + " BOOLEAN NOT NULL, "
			+ KEY_MON + " BOOLEAN NOT NULL, " + KEY_TUE + " BOOLEAN NOT NULL, "
			+ KEY_WED + " BOOLEAN NOT NULL, " + KEY_THU + " BOOLEAN NOT NULL, "
			+ KEY_FRI + " BOOLEAN NOT NULL, " + KEY_SAT + " BOOLEAN NOT NULL, "
			+ KEY_WIFI + " BOOLEAN NOT NULL, " + KEY_3G + " BOOLEAN NOT NULL);";

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(final Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {

			db.execSQL(CREATE_TABLE_ALARMS);

		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ALARMS);
			onCreate(db);

		}
	}

	public AlarmDbAdapter(final Context ctx) {
		mCtx = ctx;
	}

	/**
	 * Opens the database
	 * 
	 * @return
	 * @throws SQLException
	 */
	public AlarmDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();

		return this;
	}

	/**
	 * Closes the database.
	 */
	public void close() {
		mDbHelper.close();
	}

	// CREATE

	/**
	 * Creates a new Alarm entry in the database with the values provided.
	 * 
	 * @param from
	 *            the time to disable
	 * @param to
	 *            the time to enable
	 * @param enabledWeekdays
	 *            the days of the week, with their enabled status as a boolean
	 *            (7 boolean array starting with Sunday)
	 * @return
	 */
	public long createAlarm(final int id, final int from, final int to,
			final boolean[] enabledWeekdays, boolean disableWifi,
			boolean disable3g) {
		final ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, id);
		initialValues.put(KEY_FROM, from);
		initialValues.put(KEY_TO, to);
		initialValues.put(KEY_SUN, enabledWeekdays[0]);
		initialValues.put(KEY_MON, enabledWeekdays[1]);
		initialValues.put(KEY_TUE, enabledWeekdays[2]);
		initialValues.put(KEY_WED, enabledWeekdays[3]);
		initialValues.put(KEY_THU, enabledWeekdays[4]);
		initialValues.put(KEY_FRI, enabledWeekdays[5]);
		initialValues.put(KEY_SAT, enabledWeekdays[6]);
		initialValues.put(KEY_WIFI, disableWifi);
		initialValues.put(KEY_3G, disable3g);

		return mDb.insert(DATABASE_TABLE_ALARMS, null, initialValues);
	}

	// DELETE

	public boolean deleteAlarm(final int alarmId) {
		return mDb.delete(DATABASE_TABLE_ALARMS, KEY_ROWID + "=" + alarmId,
				null) > 0;
	}

	// FETCH
	public Cursor fetchAlarm(final int alarmId) {

		return mDb.query(DATABASE_TABLE_ALARMS, new String[] { KEY_ROWID,
				KEY_FROM, KEY_TO, KEY_SUN, KEY_MON, KEY_TUE, KEY_WED, KEY_THU,
				KEY_FRI, KEY_SAT, KEY_WIFI, KEY_3G },
				KEY_ROWID + "=" + alarmId, null, null, null, null);

	}

	public Cursor fetchAllAlarms() {

		return mDb.query(DATABASE_TABLE_ALARMS, new String[] { KEY_ROWID,
				KEY_FROM, KEY_TO, KEY_SUN, KEY_MON, KEY_TUE, KEY_WED, KEY_THU,
				KEY_FRI, KEY_SAT, KEY_WIFI, KEY_3G }, null, null, null, null,
				null);

	}

	/**
	 * Updates an alarm according to the parameters
	 * 
	 * @param id
	 *            the alarm to update
	 * @param from
	 * @param to
	 * @param enabledWeekdays
	 * @param disableWifi
	 * @param disable3g
	 * @return the success of the operation
	 */
	public boolean updateAlarm(final int id, final int from, final int to,
			final boolean[] enabledWeekdays, boolean disableWifi,
			boolean disable3g) {

		final ContentValues args = new ContentValues();
		args.put(KEY_FROM, from);
		args.put(KEY_TO, to);

		args.put(KEY_SUN, enabledWeekdays[0]);
		args.put(KEY_MON, enabledWeekdays[1]);
		args.put(KEY_TUE, enabledWeekdays[2]);
		args.put(KEY_WED, enabledWeekdays[3]);
		args.put(KEY_THU, enabledWeekdays[4]);
		args.put(KEY_FRI, enabledWeekdays[5]);
		args.put(KEY_SAT, enabledWeekdays[6]);

		args.put(KEY_WIFI, disableWifi);
		args.put(KEY_3G, disable3g);

		return mDb.update(DATABASE_TABLE_ALARMS, args, KEY_ROWID + "=" + id,
				null) > 0;
	}

	/**
	 * Queries the database for the given alarm and returns the enabled status
	 * of each day of the week (beginning with Sunday)
	 * 
	 * @param alarmId
	 *            the alarm ID to query for
	 * @return the enabled status of each day of the week (beginning with
	 *         Sunday)
	 */
	public boolean[] getEnabledWeekdays(final int alarmId) {
		Cursor alarmCursor = fetchAlarm(alarmId);

		alarmCursor.moveToFirst();

		boolean[] enabledWeekdays = new boolean[7];
		for (int i = 0; i < enabledWeekdays.length; i++) {

			// Since SQLite doesn't have a boolean datatype, but booleans are
			// stored as integers 0 (false) and 1 (true), compare the value to 1
			// to get the boolean value
			enabledWeekdays[i] = alarmCursor.getInt(i + 3) == 1;

		}
		return enabledWeekdays;

	}

	/**
	 * The number of days before the given alarm is enabled again
	 * 
	 * @param alarmId
	 *            the alarm to examine
	 * @return the number of days
	 */
	public int daysUntilNextEnabled(final int alarmId) {
		boolean[] enabledWeekdays;

		// All disable alarms have even IDs
		boolean disable = alarmId % 2 == 0;

		// There is only one database entry for each (enable/disable) alarm pair
		if (disable) {
			enabledWeekdays = getEnabledWeekdays(alarmId);
		} else {
			enabledWeekdays = getEnabledWeekdays(alarmId - 1);
		}

		Calendar cal = Calendar.getInstance();

		// Get the current day of the week -1 (to make comparisons easier)
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

		// Days until the alarm is enabled next time, default is 7 (1 week)
		int daysUntil = 7;

		for (int i = 0; i < 7; i++) {
			if (enabledWeekdays[i]) {
				// Set to the correct number of days
				if (i < dayOfWeek) {
					if (daysUntil == 7) {
						daysUntil = 7 - dayOfWeek + i;
					}
				} else if (i > dayOfWeek) {
					daysUntil = i - dayOfWeek;
					break;
				}
			}
		}

		if (!disable) {
			Cursor alarmCursor = fetchAlarm(alarmId - 1);
			alarmCursor.moveToFirst();

			if (alarmCursor.getInt(1) >= alarmCursor.getInt(2)) {
				if (enabledWeekdays[dayOfWeek]) {
					daysUntil = 1;
				} else {
					daysUntil++;
				}
			}

		}

		return daysUntil;

	}

}
