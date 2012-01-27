package com.connectivitymanager.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.connectivitymanager.R;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button wifiWatcherButton = (Button) findViewById(R.id.wfwatcher_button);
		Button timedWifiButton = (Button) findViewById(R.id.timed_wf_button);

		wifiWatcherButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent =
						new Intent(MainActivity.this,
								DisconnectTimerActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});

		timedWifiButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent =
						new Intent(MainActivity.this, TimedWifiActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});

	}
}
