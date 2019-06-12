package com.youba.sudoku.activity;

import com.youba.sudoku.R;
import com.youba.sudoku.tools.Tools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
/**
 * 此类暂时不需要，为选择关卡的界面实现关卡选择
 * @author LevelActivity
 *
 */
public class LevelActivity extends Activity implements OnClickListener {
	private Button dummy_button;

	/**
	 * 两次退出程序
	 */
	long firstTime = 0;

	private ImageView helpImageView;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 1000) {
				firstTime = secondTime;
				return true;
			} else {
				System.exit(0);
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.setVirtualMenudis(this);
		setContentView(R.layout.activity_level);

		dummy_button = (Button) findViewById(R.id.dummy_button);
		dummy_button.setOnClickListener(this);
		helpImageView = (ImageView) findViewById(R.id.helpTip);
		helpImageView.setOnClickListener(this);
		SharedPreferences sharedata = getSharedPreferences("fristrun", 0);
		Boolean isSoupon = sharedata.getBoolean("isSoupon", true);
		if (isSoupon) {
			helpImageView.setVisibility(View.VISIBLE);
		} else {
			helpImageView.setVisibility(View.GONE);
		}
	}

	private void startGame() {
		Intent intent = new Intent(LevelActivity.this, GameActivity.class);
		LevelActivity.this.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dummy_button:
			startGame();
			break;
		case R.id.helpTip:
			helpImageView.setVisibility(View.GONE);
			Editor sharedata = getSharedPreferences("fristrun", 0).edit();
			sharedata.putBoolean("isSoupon", false);
			sharedata.commit();
			break;

		default:
			break;
		}
	}
}
