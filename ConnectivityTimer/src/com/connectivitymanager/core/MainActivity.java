package com.connectivitymanager.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.connectivitymanager.R;
import com.connectivitymanager.utility.Tools;

public class MainActivity extends Activity {

	private CheckBox check3G, checkWifi;
	private WifiManager wfMgr;

	public static int SDK_VERSION = 8;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button wifiWatcherButton = (Button) findViewById(R.id.wfwatcher_button);
		Button timedWifiButton = (Button) findViewById(R.id.timed_wf_button);
		Button timed3GButton = (Button) findViewById(R.id.timed_tg_button);
		check3G = (CheckBox) findViewById(R.id.main_threeg_enable_check);
		checkWifi = (CheckBox) findViewById(R.id.main_wifi_enable_check);

		wfMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		SDK_VERSION = Integer.valueOf(android.os.Build.VERSION.SDK);

		if (SDK_VERSION <= 8) {
			timed3GButton.setVisibility(View.GONE);
			check3G.setVisibility(View.GONE);
		}

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

		timed3GButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent =
						new Intent(MainActivity.this, Timed3GActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});

		check3G.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Tools.set3GEnabled(MainActivity.this, isChecked);

			}
		});

		checkWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				wfMgr.setWifiEnabled(isChecked);

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		checkWifi = (CheckBox) findViewById(R.id.main_wifi_enable_check);

		// Set the checkboxes' status according to the network status
		if (SDK_VERSION > 8) {
			check3G = (CheckBox) findViewById(R.id.main_threeg_enable_check);
			check3G.setChecked(Tools.get3GDataEnabled(this));
		}
		checkWifi.setChecked(wfMgr.isWifiEnabled());

	}
}
