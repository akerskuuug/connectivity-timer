package com.connectivitymanager.core;

public class Alarm {

	public int from, to;

	public boolean disableWifi, disable3g;

	public boolean monday, tuesday, wednesday, thursday, friday, saturday,
			sunday;

	/**
	 * 
	 * @param from
	 * @param to
	 * @param disableWifi
	 * @param disable3g
	 * @param weekdays
	 *            days of the week beginning with Sunday
	 */
	public Alarm(int from, int to, boolean disableWifi, boolean disable3g,
			boolean[] weekdays) {

		this.from = from;
		this.to = to;

		this.disableWifi = disableWifi;
		this.disable3g = disable3g;

		sunday = weekdays[0];
		monday = weekdays[1];
		tuesday = weekdays[2];
		wednesday = weekdays[3];
		thursday = weekdays[4];
		friday = weekdays[5];
		saturday = weekdays[6];

	}

}
