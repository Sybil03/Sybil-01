package com.youba.sudoku.activity;

import java.io.File;
import java.util.ArrayList;

import com.youba.sudoku.R;
import com.youba.sudoku.tools.GameRecord;
import com.youba.sudoku.tools.MyJsonReader;
import com.youba.sudoku.tools.Tools;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecordActivity extends Activity {
	SoundPool sp;
	private boolean isOpenSound = true;
	private int soundId_btnclick;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.setVirtualMenudis(this);
		setContentView(R.layout.activity_record);
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundId_btnclick = sp.load(this, R.raw.btnclick, 1);
		isOpenSound = getIntent().getBooleanExtra("isOpenSound", isOpenSound);
		TextView btn_record = (TextView) findViewById(R.id.game_record);
		String filepath = Environment.getExternalStorageDirectory()
				+ File.separator + "gamerecord.txt";
		File file = new File(filepath);
		String records = Tools.ReadTxtFile(file);
		Log.e("gamerecord.txt:", records);
		if (records.equals("")) {
			btn_record.setText(R.string.first_of_25);
		} else {
			MyJsonReader jsonReader = new MyJsonReader(records);
			ArrayList<GameRecord> gamerecords = jsonReader.getJsonData();
			String record = "";
			for (GameRecord gamerecord : gamerecords) {
				record += gamerecord.toString() + "\n";
			}
			btn_record.setText(record);
		}
		// 返回
		Button btn_game_return = (Button) findViewById(R.id.game_return);
		btn_game_return.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (isOpenSound) {
					playBtnClickSound();
				}
				finish();
			}
		});

	}

	/**
	 * 按键点击效果音效
	 */
	public void playBtnClickSound() {
		sp.play(soundId_btnclick, 2, 2, 0, 0, 1);
	}
}
