package com.youba.sudoku.activity;


import com.youba.sudoku.R;
import com.youba.sudoku.constants.Contant;
import com.youba.sudoku.tools.Music;
import com.youba.sudoku.tools.Tools;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends Activity implements OnClickListener {
	public static String TAG = "SettingsActivity";
	public boolean isOpenSound = true;
	private Button sound;
	private Button share;
	private Button record;
	private Button _return;
	SoundPool sp;
	private int soundId_btnclick;
	Music music;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		Tools.setVirtualMenudis(this);
		music = new Music(this);
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundId_btnclick = sp.load(this, R.raw.btnclick, 1);
		isOpenSound = getPreferences(MODE_PRIVATE).getBoolean(
				Contant.ISOPENMUSIC, isOpenSound);
		sound = (Button) findViewById(R.id.sound);
		share = (Button) findViewById(R.id.share);
		record = (Button) findViewById(R.id.record);
		_return = (Button) findViewById(R.id._return);
		sound.setOnClickListener(this);
		share.setOnClickListener(this);
		record.setOnClickListener(this);
		_return.setOnClickListener(this);
		if (isOpenSound) {
			sound.setText(getResources().getString(R.string.Sound_On));
		} else {
			sound.setText(getResources().getString(R.string.Sound_Off));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e(TAG, "onpause()");
		Tools.setVirtualMenudis(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "onresume()");
		Tools.setVirtualMenudis(this);
	}

	@Override
	public void onBackPressed() {
		if (isOpenSound) {
			playBtnClickSound();
		}
		isOpenSound = getPreferences(MODE_PRIVATE).getBoolean(
				Contant.ISOPENMUSIC, isOpenSound);
		Intent it2 = new Intent(SettingsActivity.this, GameActivity.class);
		it2.putExtra("isOpenSound", isOpenSound);
		setResult(Activity.RESULT_OK, it2);
		SettingsActivity.this.finish();
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sound:
			if (isOpenSound) {
				playBtnClickSound();
			}
			if (isOpenSound) {
				music.stop();
				sound.setText(getResources().getString(R.string.Sound_Off));
				isOpenSound = false;
			} else {
				sound.setText(getResources().getString(R.string.Sound_On));
				isOpenSound = true;
			}
			getPreferences(MODE_PRIVATE).edit()
					.putBoolean(Contant.ISOPENMUSIC, isOpenSound).commit();
			break;
		case R.id.record:
			if (isOpenSound) {
				playBtnClickSound();
			}
			Intent intent = new Intent(this, RecordActivity.class);
			intent.putExtra("isOpenSound", isOpenSound);
			startActivity(intent);
			break;
		case R.id.share:
			if (isOpenSound) {
				playBtnClickSound();
			}
			shareGame();
			break;
		case R.id._return:
			if (isOpenSound) {
				playBtnClickSound();
			}
			isOpenSound = getPreferences(MODE_PRIVATE).getBoolean(
					Contant.ISOPENMUSIC, isOpenSound);
			Intent it2 = new Intent(SettingsActivity.this, GameActivity.class);
			it2.putExtra("isOpenSound", isOpenSound);
			setResult(Activity.RESULT_OK, it2);
			SettingsActivity.this.finish();
			break;
		}
	}

	private void shareGame() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, "谢谢支持和分享我们的数独游戏哦");
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	/**
	 * 按键点击效果音效
	 */
	public void playBtnClickSound() {
		sp.play(soundId_btnclick, 2, 2, 0, 0, 1);
	}
}
