package com.youba.sudoku.activity;

import com.youba.sudoku.R;
import com.youba.sudoku.tools.Tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {

	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.setVirtualMenudis(this);
		setContentView(R.layout.welcome);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, GameActivity.class));
				WelcomeActivity.this.finish();
			}
		}, 1000);
	}
}
