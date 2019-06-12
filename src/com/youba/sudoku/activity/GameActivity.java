package com.youba.sudoku.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.youba.sudoku.R;
import com.youba.sudoku.constants.Contant;
import com.youba.sudoku.tools.GameRecord;
import com.youba.sudoku.tools.Music;
import com.youba.sudoku.tools.MyJsonReader;
import com.youba.sudoku.tools.MyJsonWriter;
import com.youba.sudoku.tools.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class GameActivity extends Activity implements OnClickListener {
	private static final int intentValue = 0;
	// 关卡题目数字
	public int puzzle[];
	// 关卡答案
	public int puzzleAnswer[];
	// 关卡
	private int diff = 1;
	public boolean isOpenSound = true;
	private PuzzleView puzzleView;
	boolean isTouch = false;
	private TextView time;
	public int totalTime = 0;
	public int count = 0;
	int second = 0;
	int minute = 0;
	public Timer mTimer = null;
	public Button btn_game_num1;
	public Button btn_game_num2;
	public Button btn_game_num3;
	public Button btn_game_num4;
	public Button btn_game_num5;
	public Button btn_game_num6;
	public Button btn_game_num7;
	public Button btn_game_num8;
	public Button btn_game_num9;
	public TextView tv_game_num1_in_lefttext;
	public TextView tv_game_num2_in_lefttext;
	public TextView tv_game_num3_in_lefttext;
	public TextView tv_game_num4_in_lefttext;
	public TextView tv_game_num5_in_lefttext;
	public TextView tv_game_num6_in_lefttext;
	public TextView tv_game_num7_in_lefttext;
	public TextView tv_game_num8_in_lefttext;
	public TextView tv_game_num9_in_lefttext;
	public Button btn_game_options;
	int _numCountOne = 0;
	int _numCountTwo = 0;
	int _numCountThree = 0;
	int _numCountFour = 0;
	int _numCountFive = 0;
	int _numCountSix = 0;
	int _numCountSeven = 0;
	int _numCountEight = 0;
	int _numCountNine = 0;
	public FrameLayout game_main;
	public boolean isSucceed = true;
	private View view;
	private PopupWindow popupWindow;
	private Button restart;
	private Button resume;
	private Button setting;
	private Button cancel_return;
	// 音效
	SoundPool sp;
	private int soundId_wrong;
	private int soundId_ding;
	private int soundId_btnclick;
	private int soundId_submenu_click;
	private int soundId_click_digit_btn;
	private int soundId_unit_complete;
	ArrayList<GameRecord> village = new ArrayList<GameRecord>();
	private String filepath;
	private boolean sdCardExist;
	private View game_linear;
	Music music;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Tools.setVirtualMenudis(this);
		super.onCreate(savedInstanceState);
		music = new Music(this);
		playSound();
		setContentView(R.layout.game);
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundId_wrong = sp.load(this, R.raw.wrong_number, 1);
		soundId_ding = sp.load(this, R.raw.ding, 1);
		soundId_btnclick = sp.load(this, R.raw.btnclick, 1);
		soundId_submenu_click = sp.load(this, R.raw.submenu_click, 1);
		soundId_click_digit_btn = sp.load(this, R.raw.click_digit_btn, 1);
		soundId_unit_complete = sp.load(this, R.raw.unit_complete, 1);
		game_linear = findViewById(R.id.game);
		// 创建一个PopuWidow对象
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.popwindow, null);
		findViewById();
		setOnBtnClick();
		// 获取SD卡里gamerecord.txt的值
		sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		filepath = Environment.getExternalStorageDirectory() + File.separator
				+ "gamerecord.txt";
		// 提取gamerecord.txt的值赋值给village
		if (sdCardExist) {
			File file = new File(filepath);
			try {
				if (!file.exists()) {
					file.createNewFile();
				} else {
					/**
					 * 加载文本中diff和时间的值，如果用户卸载了再安装，不可能从头再来玩吧！
					 */
					String jsonData = Tools.ReadTxtFile(file);
					Log.e("gamerecord.txt:", jsonData);
					if (!jsonData.equals("")) {
						MyJsonReader jsonReader = new MyJsonReader(jsonData);
						ArrayList<GameRecord> gamerecords = jsonReader
								.getJsonData();
						for (GameRecord gamerecord : gamerecords) {
							village.add(gamerecord);
							diff = gamerecord.getGameDiff() + 1;
						}
					}
				}
			} catch (Exception e) {
				e.toString();
			}
		} else {
			Toast.makeText(this, "SD卡不存在，自动将文件存入内置储存器", Toast.LENGTH_SHORT)
					.show();
		}
		/**
		 * 下面这两句，如果对于卸载了游戏又重装的情况下，就会有问题,因为卸载了，sd卡里面保存的关卡进度照样保留下来，
		 * 而保存在Preferences的会被清除掉，关卡等于从头开始
		 */
		if (diff > 25) {
			gameAllPass();
		} else {
			if (getTime() > 0) {
				puzzle = getPuzzle(-1);
			} else {
				puzzle = getPuzzle(diff);
			}
			puzzleAnswer = getPuzzleAnswer(diff);
			puzzleView = new PuzzleView(this);
			game_main = (FrameLayout) findViewById(R.id.game_relay_bottom_main);
			WindowManager wm = this.getWindowManager();
			int width = wm.getDefaultDisplay().getWidth();
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) game_main
					.getLayoutParams();
			linearParams.height = width - 20;
			game_main.setLayoutParams(linearParams);
			game_main.addView(puzzleView);
			puzzleView.requestFocus();
			popupWindow = new PopupWindow(view, game_linear.getWidth(),
					game_linear.getHeight());
			popupWindow.setFocusable(true);
		}
	}

	/**
	 * 获取游戏设置返回的参数
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case  intentValue:
			if (data != null) {
				isOpenSound = data.getBooleanExtra("isOpenSound", isOpenSound);
				getPreferences(MODE_PRIVATE).edit().putBoolean(Contant.ISOPENMUSIC, isOpenSound).commit();
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		Log.e(Contant.TAG, "onResume()");
		Tools.setVirtualMenudis(this);
		super.onResume();
		if (isShowing) {
			if (popupWindow != null) {
				if (!popupWindow.isShowing()) {
					int time = getTime();
					countTime(time);
					popupWindow = null;
				}
			} else {
				showPopupWindow();
			}
		} else {
		}
	}

	private void returnToGame() {
		playSound();
		int time = getTime();
		countTime(time);
		// Tools.setVirtualMenudis(this);
	}

	private void playSound() {
		isOpenSound = getPreferences(MODE_PRIVATE).getBoolean(
				Contant.ISOPENMUSIC, isOpenSound);
		if (isOpenSound) {
			music.play(R.raw.sudoku);
		} else {
			music.stop();
		}
	}

	@Override
	protected void onPause() {
		Log.e(Contant.TAG, "onPause()");
		super.onPause();
		stopTime();
		music.stop();
		getPreferences(MODE_PRIVATE).edit()
				.putBoolean(Contant.ISOPENMUSIC, isOpenSound).commit();
		getPreferences(MODE_PRIVATE).edit()
				.putInt(Contant.PREF_TIME, totalTime).commit();
		if (puzzle != null) {
			getPreferences(MODE_PRIVATE).edit()
					.putString(Contant.PREF_PUZZLE, toPuzzleString(puzzle))
					.commit();
		}
		isTouch = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_game_num1:
			puzzleView.setSelectedTile(1);
			break;
		case R.id.btn_game_num2:
			puzzleView.setSelectedTile(2);
			break;
		case R.id.btn_game_num3:
			puzzleView.setSelectedTile(3);
			break;
		case R.id.btn_game_num4:
			puzzleView.setSelectedTile(4);
			break;
		case R.id.btn_game_num5:
			puzzleView.setSelectedTile(5);
			break;
		case R.id.btn_game_num6:
			puzzleView.setSelectedTile(6);
			break;
		case R.id.btn_game_num7:
			puzzleView.setSelectedTile(7);
			break;
		case R.id.btn_game_num8:
			puzzleView.setSelectedTile(8);
			break;
		case R.id.btn_game_num9:
			puzzleView.setSelectedTile(9);
			break;
		case R.id.btn_game_options:
			if (isOpenSound) {
				playBtnClickSound();
			}
			stopTime();
			music.stop();
			getPreferences(MODE_PRIVATE).edit()
					.putBoolean(Contant.ISOPENMUSIC, isOpenSound).commit();
			getPreferences(MODE_PRIVATE).edit()
					.putInt(Contant.PREF_TIME, totalTime).commit();
			getPreferences(MODE_PRIVATE).edit()
					.putString(Contant.PREF_PUZZLE, toPuzzleString(puzzle))
					.commit();
			showPopupWindow();
			isTouch = false;
			break;
		case R.id.cancel_return:
			playSound();
			if (isOpenSound) {
				playBtnClickSound();
			}
			popupWindow.dismiss();
			popupWindow = null;
			int time = getTime();
			countTime(time);
			break;
		case R.id.resume:
			if (isOpenSound) {
				playBtnClickSound();
			}
			popupWindow.dismiss();
			popupWindow = null;
			returnToGame();
			break;
		case R.id.setting:
			if (isOpenSound) {
				playBtnClickSound();
			}
			Intent intent = new Intent(this, SettingsActivity.class);
			intent.putExtra("isOpenSound", isOpenSound);
			startActivityForResult(intent, intentValue);
			break;
		case R.id.restart:
			setNumButtonRestart();
			if (isOpenSound) {
				playBtnClickSound();
			}
			if (isOpenSound) {
				music.play(R.raw.sudoku);
			} else {
				music.stop();
			}
			puzzle = getGameNumPuzzle(diff);
			isTouch = false;
			count = 0;
			countTime(0);
			puzzleView.invalidate();
			popupWindow.dismiss();
			popupWindow = null;
			break;
		}
	}

	boolean isShowing = true;

	@Override
	public void onBackPressed() {
		if (popupWindow != null && popupWindow.isShowing()) {
			playSound();
			if (isOpenSound) {
				playBtnClickSound();
			}
			popupWindow.dismiss();
			popupWindow = null;
			int time = getTime();
			countTime(time);
		}else {
			//弹出窗口是否退出界面
			onPause();
			exitGame();
		}
	}

	private void showPopupWindow() {
		popupWindow = new PopupWindow(view, game_linear.getWidth(),
				game_linear.getHeight());
//		popupWindow.setFocusable(true);
		if (!popupWindow.isShowing()) {
			popupWindow.showAtLocation(game_main, Gravity.CENTER, 0, -1);
		}
	}

	private void setOnBtnClick() {
		btn_game_num1.setOnClickListener(this);
		btn_game_num2.setOnClickListener(this);
		btn_game_num3.setOnClickListener(this);
		btn_game_num4.setOnClickListener(this);
		btn_game_num5.setOnClickListener(this);
		btn_game_num6.setOnClickListener(this);
		btn_game_num7.setOnClickListener(this);
		btn_game_num8.setOnClickListener(this);
		btn_game_num9.setOnClickListener(this);
		btn_game_options.setOnClickListener(this);
		resume.setOnClickListener(this);
		restart.setOnClickListener(this);
		setting.setOnClickListener(this);
		cancel_return.setOnClickListener(this);
		GameNumberClickableTrue(false);
	}

	/**
	 * 设置按键效果
	 * 
	 * @param b
	 */
	public void GameNumberClickableTrue(boolean b) {
		btn_game_num1.setClickable(b);
		btn_game_num2.setClickable(b);
		btn_game_num3.setClickable(b);
		btn_game_num4.setClickable(b);
		btn_game_num5.setClickable(b);
		btn_game_num6.setClickable(b);
		btn_game_num7.setClickable(b);
		btn_game_num8.setClickable(b);
		btn_game_num9.setClickable(b);
	}

	private void findViewById() {
		time = (TextView) findViewById(R.id.tv_time_count);
		btn_game_num1 = (Button) findViewById(R.id.btn_game_num1);
		btn_game_num2 = (Button) findViewById(R.id.btn_game_num2);
		btn_game_num3 = (Button) findViewById(R.id.btn_game_num3);
		btn_game_num4 = (Button) findViewById(R.id.btn_game_num4);
		btn_game_num5 = (Button) findViewById(R.id.btn_game_num5);
		btn_game_num6 = (Button) findViewById(R.id.btn_game_num6);
		btn_game_num7 = (Button) findViewById(R.id.btn_game_num7);
		btn_game_num8 = (Button) findViewById(R.id.btn_game_num8);
		btn_game_num9 = (Button) findViewById(R.id.btn_game_num9);

		tv_game_num1_in_lefttext = (TextView) findViewById(R.id.game_num1_in_lefttext);
		tv_game_num2_in_lefttext = (TextView) findViewById(R.id.game_num2_in_lefttext);
		tv_game_num3_in_lefttext = (TextView) findViewById(R.id.game_num3_in_lefttext);
		tv_game_num4_in_lefttext = (TextView) findViewById(R.id.game_num4_in_lefttext);
		tv_game_num5_in_lefttext = (TextView) findViewById(R.id.game_num5_in_lefttext);
		tv_game_num6_in_lefttext = (TextView) findViewById(R.id.game_num6_in_lefttext);
		tv_game_num7_in_lefttext = (TextView) findViewById(R.id.game_num7_in_lefttext);
		tv_game_num8_in_lefttext = (TextView) findViewById(R.id.game_num8_in_lefttext);
		tv_game_num9_in_lefttext = (TextView) findViewById(R.id.game_num9_in_lefttext);
		btn_game_options = (Button) findViewById(R.id.btn_game_options);
		cancel_return = (Button) view.findViewById(R.id.cancel_return);
		resume = (Button) view.findViewById(R.id.resume);
		restart = (Button) view.findViewById(R.id.restart);
		setting = (Button) view.findViewById(R.id.setting);
	}

	/**
	 * 当前游戏过关的函数
	 */
	public void succeedThisGame() {
		isSucceed = true;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (puzzle[i + j * 9] == 0) {
					isSucceed = false;
					break;
				}
			}
		}
		if (isSucceed) {
			// 停止计时，将所有信息保存记录下来，并列出当前级别当前关卡的最好记录，将记录保存
			if (isOpenSound) {
				playUnitCompleteSound();
			}
			stopTime();
			getPreferences(MODE_PRIVATE).edit()
					.putInt(Contant.PREF_TIME, totalTime).commit();
			saveCurrentProgress(totalTime, diff);
			if (diff == Contant.PUZZLEGAME) {
				gameAllPass();
			} else if (diff < Contant.PUZZLEGAME) {
				nextGame();
				isShowing = false;
				diff++;
			}
		}
	}

	/**
	 * 保存当前游戏进度
	 * 
	 * @param diff
	 * @param totalTime
	 */
	private void saveCurrentProgress(int totalTime, int diff) {
		String timeClock = getTimeClock(totalTime);
		GameRecord gamerecord = new GameRecord();
		gamerecord.setGameDiff(diff);
		gamerecord.setFinishTime(timeClock);
		village.add(gamerecord);
		MyJsonWriter jsonWriter = new MyJsonWriter(village);
		jsonWriter.setFilePath(filepath);
		jsonWriter.getJsonData();
	}

	/**
	 * 退出游戏
	 */
	private void exitGame() {
		final Dialog dlg = new Dialog(this, R.style.FullScreenDialog);
		dlg.setCancelable(false);
		Window window = dlg.getWindow();
		dlg.show();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.exit_game_dialog);
		// 为确认按钮添加事件,执行退出应用操作
		Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.cancel();
				returnToGame();
				Tools.setVirtualMenudis(GameActivity.this);
			}
			
		});
		// 退出游戏
		Button btn_exit_game = (Button) window.findViewById(R.id.btn_exit_game);
		btn_exit_game.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dlg.cancel();
				finish();
			}
		});
	}
	
	/**
	 * 通关
	 */
	private void gameAllPass() {
		final Dialog dlg = new Dialog(this, R.style.FullScreenDialog);
		dlg.setCancelable(false);
		Window window = dlg.getWindow();
		dlg.show();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.game_allpass_dialog);
		// 为确认按钮添加事件,执行退出应用操作
		Button btn_rate = (Button) window.findViewById(R.id.btn_rate);
		btn_rate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (isOpenSound) {
					playBtnClickSound();
				}
				// rateGame();
				// // 文件清除txt文件的内容,读取txt的值，并
				Tools.deleteSDFile(filepath);
				village.clear();
				diff = 1;
				setNumButtonRestart();
				// getPreferences(MODE_PRIVATE).edit().putInt(Contant.KEY_DIFFICULTY,diff).commit();
				puzzle = getPuzzle(1);
				puzzleAnswer = getPuzzleAnswer(1);
				// calculateUsedTiles();
				isTouch = false;
				isShowing = true;
				// isHelp = false;
				count = 0;
				countTime(0);
				dlg.cancel();
			}
		});
		// 退出游戏
		Button btn_more = (Button) window.findViewById(R.id.btn_exit);
		btn_more.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (isOpenSound) {
					playBtnClickSound();
				}
				diff = 1;
				System.exit(0);
			}
		});
	}

	/**
	 * 过关
	 */
	private void nextGame() {
		final Dialog dlg = new Dialog(this, R.style.FullScreenDialog);
		dlg.setCancelable(false);
		Window window = dlg.getWindow();
		dlg.show();
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.game_next_dialog);
		TextView game_next_time = (TextView) window
				.findViewById(R.id.game_next_time);
		game_next_time.setText("完成时间" + getTimeClock(totalTime));
		Button btn_continue = (Button) window
				.findViewById(R.id.button_game_next);
		btn_continue.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (isOpenSound) {
					playBtnClickSound();
				}
				setNumButtonRestart();
				puzzle = getPuzzle(diff);
				puzzleAnswer = getPuzzleAnswer(diff);
				isTouch = false;
				isShowing = true;
				count = 0;
				countTime(0);
				puzzleView.invalidate();
				dlg.cancel();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void setNumButtonRestart() {
		btn_game_num1.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num2.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num3.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num4.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num5.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num6.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num7.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num8.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num9.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.selector_num_button));
		btn_game_num1.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num2.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num3.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num4.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num5.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num6.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num7.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num8.setTextColor(getResources()
				.getColor(R.color.options_menu));
		btn_game_num9.setTextColor(getResources()
				.getColor(R.color.options_menu));
	}

	/**
	 * 如果界面上已经有7个数字了，那么此数字按键消失
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "ResourceAsColor" })
	public void setNumButtonFailed() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				switch (puzzle[i + j * 9]) {
				case 1:
					_numCountOne++;
					if (_numCountOne == 9) {
						btn_game_num1.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num1.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num1.setClickable(false);
						tv_game_num1_in_lefttext.setText("");
					} else {
						btn_game_num1.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num1_in_lefttext.setText("x"
								+ (9 - _numCountOne));
						btn_game_num1.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num1.setText("1");
					}
					// setCleanButtonfailed();
					break;
				case 2:
					_numCountTwo++;
					if (_numCountTwo == 9) {
						btn_game_num2.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num2.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num2.setClickable(false);
						tv_game_num2_in_lefttext.setText("");

					} else {
						btn_game_num2.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num2_in_lefttext.setText("x"
								+ (9 - _numCountTwo));
						btn_game_num2.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num2.setText("2");
					}
					// setCleanButtonfailed();
					break;
				case 3:
					_numCountThree++;
					if (_numCountThree == 9) {
						btn_game_num3.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num3.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num3.setClickable(false);
						tv_game_num3_in_lefttext.setText("");
					} else {
						btn_game_num3.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num3_in_lefttext.setText("x"
								+ (9 - _numCountThree));
						btn_game_num3.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num3.setText("3");
					}
					// setCleanButtonfailed();
					break;
				case 4:
					_numCountFour++;
					if (_numCountFour == 9) {
						btn_game_num4.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num4.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num4.setClickable(false);
						tv_game_num4_in_lefttext.setText("");
					} else {
						btn_game_num4.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num4_in_lefttext.setText("x"
								+ (9 - _numCountFour));
						btn_game_num4.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num4.setText("4");
					}
					// setCleanButtonfailed();
					break;
				case 5:
					_numCountFive++;
					if (_numCountFive == 9) {
						btn_game_num5.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num5.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num5.setClickable(false);
						tv_game_num5_in_lefttext.setText("");
					} else {
						btn_game_num5.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num5_in_lefttext.setText("x"
								+ (9 - _numCountFive));
						btn_game_num5.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num5.setText("5");
					}
					// setCleanButtonfailed();
					break;
				case 6:
					_numCountSix++;
					if (_numCountSix == 9) {
						btn_game_num6.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num6.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num6.setClickable(false);
						tv_game_num6_in_lefttext.setText("");
					} else {
						btn_game_num6.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num6_in_lefttext.setText("x"
								+ (9 - _numCountSix));
						btn_game_num6.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num6.setText("6");
					}
					// setCleanButtonfailed();
					break;
				case 7:
					_numCountSeven++;
					if (_numCountSeven == 9) {
						btn_game_num7.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num7.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num7.setClickable(false);
						tv_game_num7_in_lefttext.setText("");
					} else {
						btn_game_num7.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num7_in_lefttext.setText("x"
								+ (9 - _numCountSeven));
						btn_game_num7.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num7.setText("7");
					}
					// setCleanButtonfailed();
					break;
				case 8:
					_numCountEight++;
					if (_numCountEight == 9) {
						btn_game_num8.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num8.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num8.setClickable(false);
						tv_game_num8_in_lefttext.setText("");
					} else {
						btn_game_num8.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num8_in_lefttext.setText("x"
								+ (9 - _numCountEight));
						btn_game_num8.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num8.setText("8");
					}
					// setCleanButtonfailed();
					break;
				case 9:
					_numCountNine++;
					if (_numCountNine == 9) {
						btn_game_num9.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.bg_bm0));
						btn_game_num9.setTextColor(getResources().getColor(
								R.color.number_failed));
						btn_game_num9.setClickable(false);
						tv_game_num9_in_lefttext.setText("");
					} else {
						btn_game_num9.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.selector_num_button));
						tv_game_num9_in_lefttext.setText("x"
								+ (9 - _numCountNine));
						btn_game_num9.setTextColor(getResources().getColor(
								android.R.color.white));
						btn_game_num9.setText("9");
					}
					// setCleanButtonfailed();
					break;
				}
			}
		}
	}

	/**
	 * 计时
	 */
	private void countTime(final int time) {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				totalTime = time + count++;
				if (totalTime < 10) {
					runOnUI("00:0" + totalTime);
				} else if (totalTime < 60) {
					runOnUI("00:" + totalTime);
				} else if (totalTime == 60) {
					runOnUI("01:00");
				} else if (60 < totalTime) {
					minute = totalTime / 60;
					second = totalTime % 60;
					if (second < 10 && minute < 10) {
						runOnUI("0" + minute + ":0" + second);
					} else if (second < 60 && minute < 10) {
						runOnUI("0" + minute + ":" + second);
					} else if (second < 10 && minute == 10) {
						runOnUI(minute + ":0" + second);
					} else if (second == 10 || second > 10 && minute == 10) {
						runOnUI(minute + ":" + second);
					} else if (second < 10 && minute > 10) {
						runOnUI(minute + ":0" + second);
					} else if (second == 10 || second > 10 && minute > 10) {
						runOnUI(minute + ":" + second + "");
					} else if (minute == 60 || minute > 60) {
						runOnUI("60:00");
						mTimer.cancel();
					}
				}
				Log.e(Contant.TAG + ":Game", totalTime + "");
			}

		}, 0, 1000);
	}

	/**
	 * int类型时间改为String类型时间
	 * 
	 * @param totalTime
	 * @return
	 */
	private String getTimeClock(int totalTime) {
		if (totalTime < 10) {
			return "00:0" + totalTime;
		} else if (totalTime < 60) {
			return "00:" + totalTime;
		} else if (totalTime == 60) {
			return "01:00";
		} else if (60 < totalTime) {
			minute = totalTime / 60;
			second = totalTime % 60;
			if (second < 10 && minute < 10) {
				return "0" + minute + ":0" + second;
			} else if (second < 60 && minute < 10) {
				return "0" + minute + ":" + second;
			} else if (second < 10 && minute == 10) {
				return minute + ":0" + second;
			} else if (second == 10 || second > 10 && minute == 10) {
				return minute + ":" + second;
			} else if (second < 10 && minute > 10) {
				return minute + ":0" + second;
			} else if (second == 10 || second > 10 && minute > 10) {
				return minute + ":" + second + "";
			}
		}
		return "00:00";
	}

	private void runOnUI(final String str) {
		runOnUiThread(new Runnable() {
			public void run() {
				time.setText(str);
			}
		});
	}

	/**
	 * 停止计时
	 */
	private void stopTime() {
		if (mTimer != null) {
			mTimer.cancel();
			count = 0;
			mTimer = null;
		}
	}

	/**
	 * 获取时间
	 * 
	 * @return
	 */
	private int getTime() {
		int timeInt = getPreferences(MODE_PRIVATE).getInt(Contant.PREF_TIME,
				totalTime);
		return timeInt;
	}

	/**
	 * 根据不同的关卡值，获取界面上的关卡数据
	 * 
	 * @param in
	 * @return
	 */
	private int[] getPuzzle(int in) {
		String puz;
		switch (in) {
		case Contant.DIFFICULTY_CONTINUE:// 第一关或者继续
			puz = getPreferences(MODE_PRIVATE).getString(Contant.PREF_PUZZLE,
					Contant.PUZZLE1);
			break;
		case Contant.DIFFICULTY_1:// 第四关
			puz = Contant.PUZZLE1;
			break;
		case Contant.DIFFICULTY_2:// 第四关
			puz = Contant.PUZZLE2;
			break;
		case Contant.DIFFICULTY_3:// 第四关
			puz = Contant.PUZZLE3;
			break;
		case Contant.DIFFICULTY_4:// 第四关
			puz = Contant.PUZZLE4;
			break;
		case Contant.DIFFICULTY_5:// 第四关
			puz = Contant.PUZZLE5;
			break;
		case Contant.DIFFICULTY_6:// 第四关
			puz = Contant.PUZZLE6;
			break;
		case Contant.DIFFICULTY_7:// 第四关
			puz = Contant.PUZZLE7;
			break;
		case Contant.DIFFICULTY_8:// 第四关
			puz = Contant.PUZZLE8;
			break;
		case Contant.DIFFICULTY_9:// 第四关
			puz = Contant.PUZZLE9;
			break;
		case Contant.DIFFICULTY_10:// 第四关
			puz = Contant.PUZZLE10;
			break;
		case Contant.DIFFICULTY_11:// 第四关
			puz = Contant.PUZZLE11;
			break;
		case Contant.DIFFICULTY_12:// 第四关
			puz = Contant.PUZZLE12;
			break;
		case Contant.DIFFICULTY_13:// 第四关
			puz = Contant.PUZZLE13;
			break;
		case Contant.DIFFICULTY_14:// 第四关
			puz = Contant.PUZZLE14;
			break;
		case Contant.DIFFICULTY_15:// 第四关
			puz = Contant.PUZZLE15;
			break;
		case Contant.DIFFICULTY_16:// 第四关
			puz = Contant.PUZZLE16;
			break;
		case Contant.DIFFICULTY_17:// 第四关
			puz = Contant.PUZZLE17;
			break;
		case Contant.DIFFICULTY_18:// 第四关
			puz = Contant.PUZZLE18;
			break;
		case Contant.DIFFICULTY_19:// 第四关
			puz = Contant.PUZZLE19;
			break;
		case Contant.DIFFICULTY_20:// 第四关
			puz = Contant.PUZZLE20;
			break;
		case Contant.DIFFICULTY_21:// 第四关
			puz = Contant.PUZZLE21;
			break;
		case Contant.DIFFICULTY_22:// 第四关
			puz = Contant.PUZZLE22;
			break;
		case Contant.DIFFICULTY_23:// 第四关
			puz = Contant.PUZZLE23;
			break;
		case Contant.DIFFICULTY_24:// 第四关
			puz = Contant.PUZZLE24;
			break;
		case Contant.DIFFICULTY_25:// 第四关
			puz = Contant.PUZZLE25;
			break;
		default:// 默认第一关
			puz = Contant.PUZZLE1;
			break;
		}
		return fromPuzzleString(puz);
	}

	/**
	 * 根据不同的关卡值，获取界面上的关卡数据
	 * 
	 * @param in
	 * @return
	 */
	private int[] getGameNumPuzzle(int in) {
		String puz;
		switch (in) {
		case Contant.DIFFICULTY_1:// 第四关
			puz = Contant.PUZZLE1;
			break;
		case Contant.DIFFICULTY_2:// 第四关
			puz = Contant.PUZZLE2;
			break;
		case Contant.DIFFICULTY_3:// 第四关
			puz = Contant.PUZZLE3;
			break;
		case Contant.DIFFICULTY_4:// 第四关
			puz = Contant.PUZZLE4;
			break;
		case Contant.DIFFICULTY_5:// 第四关
			puz = Contant.PUZZLE5;
			break;
		case Contant.DIFFICULTY_6:// 第四关
			puz = Contant.PUZZLE6;
			break;
		case Contant.DIFFICULTY_7:// 第四关
			puz = Contant.PUZZLE7;
			break;
		case Contant.DIFFICULTY_8:// 第四关
			puz = Contant.PUZZLE8;
			break;
		case Contant.DIFFICULTY_9:// 第四关
			puz = Contant.PUZZLE9;
			break;
		case Contant.DIFFICULTY_10:// 第四关
			puz = Contant.PUZZLE10;
			break;
		case Contant.DIFFICULTY_11:// 第四关
			puz = Contant.PUZZLE11;
			break;
		case Contant.DIFFICULTY_12:// 第四关
			puz = Contant.PUZZLE12;
			break;
		case Contant.DIFFICULTY_13:// 第四关
			puz = Contant.PUZZLE13;
			break;
		case Contant.DIFFICULTY_14:// 第四关
			puz = Contant.PUZZLE14;
			break;
		case Contant.DIFFICULTY_15:// 第四关
			puz = Contant.PUZZLE15;
			break;
		case Contant.DIFFICULTY_16:// 第四关
			puz = Contant.PUZZLE16;
			break;
		case Contant.DIFFICULTY_17:// 第四关
			puz = Contant.PUZZLE17;
			break;
		case Contant.DIFFICULTY_18:// 第四关
			puz = Contant.PUZZLE18;
			break;
		case Contant.DIFFICULTY_19:// 第四关
			puz = Contant.PUZZLE19;
			break;
		case Contant.DIFFICULTY_20:// 第四关
			puz = Contant.PUZZLE20;
			break;
		case Contant.DIFFICULTY_21:// 第四关
			puz = Contant.PUZZLE21;
			break;
		case Contant.DIFFICULTY_22:// 第四关
			puz = Contant.PUZZLE22;
			break;
		case Contant.DIFFICULTY_23:// 第四关
			puz = Contant.PUZZLE23;
			break;
		case Contant.DIFFICULTY_24:// 第四关
			puz = Contant.PUZZLE24;
			break;
		case Contant.DIFFICULTY_25:// 第四关
			puz = Contant.PUZZLE25;
			break;
		default:// 默认第一关
			puz = Contant.PUZZLE1;
			break;
		}
		return fromPuzzleString(puz);
	}

	/**
	 * 根据不同的关卡值，获取界面上的关卡数据答案
	 * 
	 * @param in
	 * @return
	 */
	private int[] getPuzzleAnswer(int in) {
		String puz;
		switch (in) {
		case Contant.DIFFICULTY_1:// 第四关
			puz = Contant.PUZZLE_ANSERW1;
			break;
		case Contant.DIFFICULTY_2:// 第四关
			puz = Contant.PUZZLE_ANSERW2;
			break;
		case Contant.DIFFICULTY_3:// 第四关
			puz = Contant.PUZZLE_ANSERW3;
			break;
		case Contant.DIFFICULTY_4:// 第四关
			puz = Contant.PUZZLE_ANSERW4;
			break;
		case Contant.DIFFICULTY_5:// 第四关
			puz = Contant.PUZZLE_ANSERW5;
			break;
		case Contant.DIFFICULTY_6:// 第四关
			puz = Contant.PUZZLE_ANSERW6;
			break;
		case Contant.DIFFICULTY_7:// 第四关
			puz = Contant.PUZZLE_ANSERW7;
			break;
		case Contant.DIFFICULTY_8:// 第四关
			puz = Contant.PUZZLE_ANSERW8;
			break;
		case Contant.DIFFICULTY_9:// 第四关
			puz = Contant.PUZZLE_ANSERW9;
			break;
		case Contant.DIFFICULTY_10:// 第四关
			puz = Contant.PUZZLE_ANSERW10;
			break;
		case Contant.DIFFICULTY_11:// 第四关
			puz = Contant.PUZZLE_ANSERW11;
			break;
		case Contant.DIFFICULTY_12:// 第四关
			puz = Contant.PUZZLE_ANSERW12;
			break;
		case Contant.DIFFICULTY_13:// 第四关
			puz = Contant.PUZZLE_ANSERW13;
			break;
		case Contant.DIFFICULTY_14:// 第四关
			puz = Contant.PUZZLE_ANSERW14;
			break;
		case Contant.DIFFICULTY_15:// 第四关
			puz = Contant.PUZZLE_ANSERW15;
			break;
		case Contant.DIFFICULTY_16:// 第四关
			puz = Contant.PUZZLE_ANSERW16;
			break;
		case Contant.DIFFICULTY_17:// 第四关
			puz = Contant.PUZZLE_ANSERW17;
			break;
		case Contant.DIFFICULTY_18:// 第四关
			puz = Contant.PUZZLE_ANSERW18;
			break;
		case Contant.DIFFICULTY_19:// 第四关
			puz = Contant.PUZZLE_ANSERW19;
			break;
		case Contant.DIFFICULTY_20:// 第四关
			puz = Contant.PUZZLE_ANSERW20;
			break;
		case Contant.DIFFICULTY_21:// 第四关
			puz = Contant.PUZZLE_ANSERW21;
			break;
		case Contant.DIFFICULTY_22:// 第四关
			puz = Contant.PUZZLE_ANSERW22;
			break;
		case Contant.DIFFICULTY_23:// 第四关
			puz = Contant.PUZZLE_ANSERW23;
			break;
		case Contant.DIFFICULTY_24:// 第四关
			puz = Contant.PUZZLE_ANSERW24;
			break;
		case Contant.DIFFICULTY_25:// 第四关
			puz = Contant.PUZZLE_ANSERW25;
			break;
		default:// 默认第一关
			puz = Contant.PUZZLE_ANSERW1;
			break;
		}
		return fromPuzzleString(puz);
	}

	/**
	 * 错误提示或者数字输入，需要判定
	 * 
	 * @param x
	 * @param y
	 */
	public void showKyepadOrError(int x, int y) {
		if (!getTileString(x, y).equals("")
				&& getGamePuzzleTileString(x, y).equals(getTileString(x, y))
				&& isOpenSound) {
			playSubMenuClickSound();
		} else {
			if (isOpenSound) {
				playClickDigitBtnSound();
			}
		}
	}

	/**
	 * 数字填写错误音效
	 */
	public void playNumWrongSound() {
		sp.play(soundId_wrong, 2, 2, 0, 0, 1);
	}

	/**
	 * 数字填写正确音效
	 */
	public void playDingSound() {
		sp.play(soundId_ding, 2, 2, 0, 0, 1);
	}


	/**
	 * 按键点击效果音效
	 */
	public void playBtnClickSound() {
		sp.play(soundId_btnclick, 2, 2, 0, 0, 1);
	}

	/**
	 * 点击被背景效果音效
	 */
	public void playSubMenuClickSound() {
		sp.play(soundId_submenu_click, 2, 2, 0, 0, 1);
	}

	/**
	 * 屏幕触摸音效
	 */
	public void playClickDigitBtnSound() {
		sp.play(soundId_click_digit_btn, 2, 2, 0, 0, 1);
	}

	/**
	 * 过关音效
	 */
	public void playUnitCompleteSound() {
		sp.play(soundId_unit_complete, 2, 2, 0, 0, 1);
	}

	/**
	 * 字符串转换成数组
	 * 
	 * @param string
	 * @return
	 */
	protected int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}

	/**
	 * 数组转换成字符串
	 * 
	 * @param puz
	 * @return
	 */
	protected String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}

	/**
	 * 根据x,y来循迹puzzle的值，x代表行，y代表列
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public String getTileString(int x, int y) {
		int v = getTile(x, y);
		if (v == 0)
			return "";
		else
			return String.valueOf(v);
	}

	public int getTile(int x, int y) {
		return puzzle[y * 9 + x];
	}

	public void setTile(int x, int y, int value) {
		puzzle[y * 9 + x] = value;
	}

	/**
	 * 游戏默认的数字
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public String getGamePuzzleTileString(int x, int y) {
		int v = getGamePuzzleTile(x, y);
		if (v == 0)
			return "";
		else
			return String.valueOf(v);
	}

	private int getGamePuzzleTile(int x, int y) {
		diff = getPreferences(MODE_PRIVATE)
				.getInt(Contant.KEY_DIFFICULTY, diff);
		int[] gamePuzzle = getGameNumPuzzle(diff);
		return gamePuzzle[y * 9 + x];
	}

}
