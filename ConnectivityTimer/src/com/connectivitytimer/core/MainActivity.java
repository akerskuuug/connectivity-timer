package com.connectivitytimer.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.connectivitytimer.R;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button wfTimerButton = (Button) findViewById(R.id.wftimer_button);

		wfTimerButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						DisconnectTimerActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}
}
