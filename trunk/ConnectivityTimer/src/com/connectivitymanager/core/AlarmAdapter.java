package com.connectivitymanager.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.connectivitymanager.R;
import com.connectivitymanager.utility.Tools;

public class AlarmAdapter extends ArrayAdapter<Alarm> {

	Context context;
	int layoutResourceId;
	Alarm data[] = null;

	public AlarmAdapter(Context context, int layoutResourceId, Alarm[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AlarmHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new AlarmHolder();
			holder.from = (TextView) row.findViewById(R.id.from_label);
			holder.to = (TextView) row.findViewById(R.id.to_label);

			holder.wifi = (TextView) row.findViewById(R.id.disable_wifi_label);
			holder.mdata = (TextView) row.findViewById(R.id.disable_3g_label);

			holder.monday = (TextView) row.findViewById(R.id.monday_label);
			holder.tuesday = (TextView) row.findViewById(R.id.tuesday_label);
			holder.wednesday =
					(TextView) row.findViewById(R.id.wednesday_label);
			holder.thursday = (TextView) row.findViewById(R.id.thursday_label);
			holder.friday = (TextView) row.findViewById(R.id.friday_label);
			holder.saturday = (TextView) row.findViewById(R.id.saturday_label);
			holder.sunday = (TextView) row.findViewById(R.id.sunday_label);

			row.setTag(holder);
		} else {
			holder = (AlarmHolder) row.getTag();
		}

		Alarm alarm = data[position];

		holder.from.setText(Tools.getPaddedString(alarm.from / 60) + ":"
				+ Tools.getPaddedString(alarm.from % 60));

		holder.to.setText(Tools.getPaddedString(alarm.to / 60) + ":"
				+ Tools.getPaddedString(alarm.to % 60));

		if (!alarm.disableWifi) {
			holder.wifi.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.wifi.setTextColor(Color.BLACK);
		}

		if (!alarm.disable3g) {
			holder.mdata.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.mdata.setTextColor(Color.BLACK);
		}

		//

		// Weekdays
		if (!alarm.monday) {
			holder.monday.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.monday.setTextColor(Color.BLACK);
		}

		if (!alarm.tuesday) {
			holder.tuesday.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.tuesday.setTextColor(Color.BLACK);
		}

		if (!alarm.wednesday) {
			holder.wednesday.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.wednesday.setTextColor(Color.BLACK);
		}

		if (!alarm.thursday) {
			holder.thursday.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.thursday.setTextColor(Color.BLACK);
		}

		if (!alarm.friday) {
			holder.friday.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.friday.setTextColor(Color.BLACK);
		}

		if (!alarm.saturday) {
			holder.saturday.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.saturday.setTextColor(Color.BLACK);
		}

		if (!alarm.sunday) {
			holder.sunday.setTextColor(Color.parseColor("#c0c0c0"));
		} else {
			holder.sunday.setTextColor(Color.BLACK);
		}

		return row;
	}

	static class AlarmHolder {
		TextView from, to;

		TextView wifi, mdata;

		TextView monday, tuesday, wednesday, thursday, friday, saturday,
				sunday;

	}

}
