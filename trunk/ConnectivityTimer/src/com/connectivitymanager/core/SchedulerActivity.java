package com.connectivitymanager.core;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.connectivitymanager.R;

public class SchedulerActivity extends Activity implements
		OnItemSelectedListener {

	private int wfFromHours, wfToHours, tgFromHours, tgToHours;
	private int wfFromMinutes, wfToMinutes, tgFromMinutes, tgToMinutes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scheduler);

		final Spinner[] spinners = {
				(Spinner) findViewById(R.id.wf_from_spinner),
				(Spinner) findViewById(R.id.wf_to_spinner),
				(Spinner) findViewById(R.id.tg_from_spinner),
				(Spinner) findViewById(R.id.tg_to_spinner) };
		for (Spinner spin : spinners) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(
							this,
							com.connectivitymanager.R.array.scheduler_time_array,
							R.layout.my_simple_spinner_item);

			spin.setAdapter(adapter);
			spin.setSelection(0);
		}

	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		String s = parent.getSelectedItem().toString();

		if (view.equals(findViewById(R.id.wf_from_spinner))) {
			wfFromHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			wfFromMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));

		} else if (view.equals(findViewById(R.id.wf_to_spinner))) {
			wfToHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			wfToMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));

		} else if (view.equals(findViewById(R.id.tg_from_spinner))) {
			wfToHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			wfToMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));

		} else if (view.equals(findViewById(R.id.tg_to_spinner))) {
			wfToHours = Integer.parseInt(s.substring(0, s.indexOf(':')));
			wfToMinutes = Integer.parseInt(s.substring(s.indexOf(':') + 1));

		}

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing

	}
}
