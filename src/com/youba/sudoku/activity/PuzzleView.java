package com.youba.sudoku.activity;

import com.youba.sudoku.R;
import com.youba.sudoku.activity.GameActivity;
import com.youba.sudoku.tools.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku";
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final GameActivity game;
	private final Rect selRect = new Rect();
	private float x;
	private float y;
	private Paint gameNum;
	private Paint foreground;
	private Paint selectedNumBackground;

	public PuzzleView(Context context) {
		super(context);
		this.game = (GameActivity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		Log.e(TAG, "width" + width + "height" + height);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		// 游戏初始数字背景设置
		Paint num_background = new Paint();
		num_background.setColor(getResources().getColor(R.color.game_num_color));
		// 游戏空白位置设置
		Paint zero_background = new Paint();
		zero_background.setColor(getResources().getColor(R.color.selected_zero_none));
		Rect r = new Rect();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (this.game.getTileString(i, j).equals("")) {
					getRect(i, j, r);
					canvas.drawRect(r, zero_background);
				} else {
					getRect(i, j, r);
					canvas.drawRect(r, num_background);
				}
			}
		}
		gameNum = new Paint(Paint.ANTI_ALIAS_FLAG);
		gameNum.setColor(getResources().getColor(R.color.selected_zero));
		gameNum.setStyle(Style.FILL);
		gameNum.setFakeBoldText(true);
		gameNum.setAntiAlias(true);
		gameNum.setTextSize(height * 0.5f);
		gameNum.setTextScaleX(width / height);
		gameNum.setTextAlign(Paint.Align.CENTER);

		foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setFakeBoldText(true);
		foreground.setAntiAlias(true);
		foreground.setTextSize(height * 0.5f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);
		FontMetrics fm = foreground.getFontMetrics();
		x = width / 2;
		y = height / 2 - (fm.ascent + fm.descent) / 2;
		selectedNumBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
		selectedNumBackground.setStyle(Style.FILL);
		selectedNumBackground.setColor(getResources().getColor(R.color.options_menu));
		obviousNumbers(canvas, r);
		// 数字键是否可用,剩余的数字按键
		game.setNumButtonFailed();
		// 将游戏的数字背景绘制到界面上,三种状态，初始数字，空白，已经填了
		if (game.isTouch) {
			if (this.game.getTileString(selX, selY).equals("")) {
				selectedFont(canvas);
				game.GameNumberClickableTrue(true);
			} else {
				if (game.puzzle[selX + selY * 9] == game.puzzleAnswer[selX + selY * 9]) {
					selectedSameFont(canvas);
					game.GameNumberClickableTrue(false);
				} else {
					selectedFont(canvas);
					game.GameNumberClickableTrue(true);
					if (mThread == null) {
						mThread = new MyThread();
						mThread.start();
					} else {
						mThread.run();
					}
					if (isWrong) {
						 game.GameNumberClickableTrue(false);
						if (game.isOpenSound) {
							game.playNumWrongSound();
						}
						RectF rectF = new RectF(selX * width, selY * height, (selX + 1) * width, (selY + 1) * height);
						float centerX = rectF.centerX();
						float centerY = rectF.centerY();
						float left = rectF.left / 2 + centerX / 2;
						float top = rectF.top / 2 + centerY / 2;
						float right = rectF.right / 2 + centerX / 2;
						float bottom = rectF.bottom / 2 + centerY / 2;
						rectF.set(left, top, right, bottom);
						canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_error), null, rectF, null);
						// ///////////////////////////////////////////////////此处的打叉代码无法适应各个屏幕的手机///////////////////////////////////////////////////////////
						// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
						// R.drawable.ic_error), selX * width + width/5, selY *
						// height + height/5, foreground);
						isWrong = false;
					}
				}
			}
		}
		LineKuang(canvas);
		// 是否已经完成游戏
		if (game.isShowing) {
			game.succeedThisGame();
		}
		game._numCountOne = 0;
		game._numCountTwo = 0;
		game._numCountThree = 0;
		game._numCountFour = 0;
		game._numCountFive = 0;
		game._numCountSix = 0;
		game._numCountSeven = 0;
		game._numCountEight = 0;
		game._numCountNine = 0;
	}

	MyThread mThread;

	class MyThread extends Thread {
		@Override
		public synchronized void run() {
			// 子线程中通过handler发送消息给handler接收，由handler去更新TextView的值
			try {
				Thread.sleep(sleep);
				Message msg = new Message();
				msg.what = UPDATE;
				handler.sendMessage(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	int sleep = 0;
	boolean isWrong = false;
	int UPDATE = 0;//
	int UPDATE1 = 1;// 设置打叉效果
	int UPDATE2 = 2;// 清楚错误数字
	private Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				UPDATE = 1;
				invalidate();
				sleep = 50;
				break;
			case 1:
				UPDATE = 2;
				isWrong = true;
				sleep = 50;
				invalidate();
				break;
			case 2:
				UPDATE = 0;
				setSelectedZero();
				sleep = 0;
				invalidate();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 显示数字
	 * 
	 * @param canvas
	 * @param r
	 */
	private void obviousNumbers(final Canvas canvas, Rect r) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// 如果是游戏初始数字，那么显示灰黑色，否则显示橙色
				if (this.game.getTileString(i, j).equals(this.game.getGamePuzzleTileString(i, j))) {
					// 初始数字设置，背景设置
					canvas.drawText(this.game.getGamePuzzleTileString(i, j), i * width + x, j * height + y, gameNum);
				} else {
					// 填入的数字
					getRect(i, j, r);
					canvas.drawRect(r, selectedNumBackground);
					canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
				}
			}
		}
	}

	/**
	 * 九宫格以及九条边框线
	 * 
	 * @param canvas
	 */
	private void LineKuang(final Canvas canvas) {
		// 添加九宫格以及边框边沿线背景
		// 边框
		Paint puzzle_kuang = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		puzzle_kuang.setStyle(Style.FILL_AND_STROKE);// 设置非填充
		puzzle_kuang.setStrokeWidth(1);// 笔宽1像素
		puzzle_kuang.setColor(getResources().getColor(R.color.puzzle_nine));// 设置为红笔
		puzzle_kuang.setAntiAlias(true);// 锯齿不显示
		// 三横线
		Paint puzzle_three = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		puzzle_three.setStyle(Style.FILL_AND_STROKE);// 设置非填充
		puzzle_three.setStrokeWidth(1);// 笔宽1像素
		puzzle_three.setColor(getResources().getColor(R.color.puzzle_three));// 设置为红笔
		puzzle_three.setAntiAlias(true);// 锯齿不显示
		// 九横线
		Paint puzzle_nine = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
		puzzle_nine.setStyle(Style.FILL_AND_STROKE);// 设置非填充
		puzzle_nine.setStrokeWidth(1);// 笔宽1像素
		puzzle_nine.setColor(getResources().getColor(R.color.puzzle_nine));// 设置为红笔
		puzzle_nine.setAntiAlias(true);// 锯齿不显示
		for (int i = 0; i < 10; i++) {
			if (i == 0) {
				canvas.drawLine(0, i * height, getWidth(), i * height, puzzle_kuang);
				canvas.drawLine(i * width, 0, i * width, getHeight(), puzzle_kuang);
			} else if (i == 9) {
				canvas.drawLine(0, i * height, getWidth(), i * height, puzzle_kuang);
				canvas.drawLine(i * width, 0, i * width, getHeight(), puzzle_kuang);
			} else {
				if (i % 3 != 0) {
					if (i == 4) {
						canvas.drawLine(0, i * height, getWidth(), i * height, puzzle_nine);
						canvas.drawLine(i * width, 0, i * width, getHeight(), puzzle_nine);
					} else {
						canvas.drawLine(0, i * height, getWidth(), i * height, puzzle_nine);
						canvas.drawLine(i * width, 0, i * width, getHeight(), puzzle_nine);
					}
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				if (i == 0 || i == 9)
					continue;
				canvas.drawLine(0, i * height, getWidth(), i * height, puzzle_three);
				canvas.drawLine(i * width, 0, i * width, getHeight(), puzzle_three);
			}
		}
	}

	/**
	 * 修改单元格中的数字,算法要改变，只有唯一数才正确
	 * 
	 * @param tile
	 */
	public void setSelectedTile(int tile) {
		game.setTile(selX, selY, tile);
		if (game.puzzle[selX + selY * 9] == game.puzzleAnswer[selX + selY * 9]) {
			if (game.isOpenSound) {
				game.playDingSound();
			}
		}
		invalidate();
	}

	/**
	 * 擦除单元格中的数字
	 * 
	 */
	public void setSelectedZero() {
		game.setTile(selX, selY, 0);
		invalidate();
	}

	/**
	 * 设置空白位置白色背景
	 * 
	 * @param canvas
	 */
	private void setZeroNumWhiteBackground(Canvas canvas) {
		canvas.drawRect(selRect, selectedNumBackground);
	}

	/**
	 * 设置蓝色空心边框
	 * 
	 * @param canvas
	 */
	// private void setZeroNumBlueFrame(Canvas canvas) {
	// Paint mPaint = new Paint();
	// mPaint.setStyle(Paint.Style.STROKE);
	// mPaint.setStrokeWidth(2);
	// mPaint.setColor(getResources().getColor(R.color.selected_zero_kuang));
	// canvas.drawRect(selRect, mPaint);
	// }
	private void setZeroNumBlueFrame(Canvas canvas) {
		Paint mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setColor(getResources().getColor(R.color.selected_zero_kuang));
		canvas.drawRect(selRect, mPaint);
		getRect2(selX, selY, selRect);
		setZeroNumWhiteBackground(canvas);
	}

	/**
	 * 设置界面为点击空白状态
	 * 
	 * @param canvas
	 */
	private void selectedFont(Canvas canvas) {
		Log.d(TAG, "selRect=" + selRect);
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_zero_others));
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (i == selX || j == selY) {
					getRect3(i, j, selRect);
					if (!(i == selX && j == selY)) {
						canvas.drawRect(selRect, selected);
					} else {
						setZeroNumWhiteBackground(canvas);
						setZeroNumBlueFrame(canvas);
					}
				} else {
					continue;
				}
			}
		}
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// 如果是游戏初始数字，那么显示灰黑色，否则显示橙色
				if (this.game.getTileString(i, j).equals(this.game.getGamePuzzleTileString(i, j))) {
					// 初始数字设置，背景设置
					canvas.drawText(this.game.getGamePuzzleTileString(i, j), i * width + x, j * height + y, gameNum);
				} else {
					// 填入的数字
					canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
				}
			}
		}
	}

	/**
	 * 获取rect位置
	 * 
	 * @param x
	 * @param y
	 * @param rect
	 */
	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height), (int) (x * width + width), (int) (y * height + height));
	}

	private void getRect2(int x, int y, Rect rect) {
		rect.set((int) (x * width) + 3, (int) (y * height) + 3, (int) (x * width + width) - 2, (int) (y * height + height) - 2);
	}

	private void getRect3(int x, int y, Rect rect) {
		rect.set((int) (x * width) + 1, (int) (y * height) + 1, (int) (x * width + width), (int) (y * height + height));
	}

	/**
	 * 设置界面为点击数字状态
	 * 
	 * @param canvas
	 */
	private void selectedSameFont(Canvas canvas) {
		Log.d(TAG, "selRect=" + selRect);
		Paint selectedNum = new Paint();
		selectedNum.setColor(getResources().getColor(R.color.puzzle_selected_same_num));
		Paint gameNum = new Paint();
		gameNum.setColor(getResources().getColor(android.R.color.white));
		gameNum.setStyle(Style.FILL);
		gameNum.setFakeBoldText(true);
		gameNum.setAntiAlias(true);
		gameNum.setTextSize(height * 0.5f);
		gameNum.setTextScaleX(width / height);
		gameNum.setTextAlign(Paint.Align.CENTER);
		// 计算相同数字的位置并且都凸显出蓝色
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (game.puzzle[i + j * 9] == game.puzzle[selX + 9 * selY]) {
					// Log.d(TAG, "onTouchEvent: x" + i + ", y" + j);
					getRect3(i, j, selRect);
					canvas.drawRect(selRect, selectedNum);
					canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, gameNum);
				}
			}
		}
	}

	/**
	 * 触摸事件触发
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		game.isTouch = true;
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		if (!game.isSucceed) {
			select((int) (event.getX() / width), (int) (event.getY() / height));
			game.showKyepadOrError(selX, selY);
			Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
			invalidate();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 键盘输入，重新定位位置，刷新界面，这里可以判定下位置是数字还是空白
	 * 
	 * @param x
	 * @param y
	 */
	private void select(int x, int y) {
		invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		invalidate(selRect);
	}

}
