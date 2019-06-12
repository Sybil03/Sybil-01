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
		// ��Ϸ��ʼ���ֱ�������
		Paint num_background = new Paint();
		num_background.setColor(getResources().getColor(R.color.game_num_color));
		// ��Ϸ�հ�λ������
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
		// ���ּ��Ƿ����,ʣ������ְ���
		game.setNumButtonFailed();
		// ����Ϸ�����ֱ������Ƶ�������,����״̬����ʼ���֣��հף��Ѿ�����
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
						// ///////////////////////////////////////////////////�˴��Ĵ������޷���Ӧ������Ļ���ֻ�///////////////////////////////////////////////////////////
						// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
						// R.drawable.ic_error), selX * width + width/5, selY *
						// height + height/5, foreground);
						isWrong = false;
					}
				}
			}
		}
		LineKuang(canvas);
		// �Ƿ��Ѿ������Ϸ
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
			// ���߳���ͨ��handler������Ϣ��handler���գ���handlerȥ����TextView��ֵ
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
	int UPDATE1 = 1;// ���ô��Ч��
	int UPDATE2 = 2;// �����������
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
	 * ��ʾ����
	 * 
	 * @param canvas
	 * @param r
	 */
	private void obviousNumbers(final Canvas canvas, Rect r) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// �������Ϸ��ʼ���֣���ô��ʾ�Һ�ɫ��������ʾ��ɫ
				if (this.game.getTileString(i, j).equals(this.game.getGamePuzzleTileString(i, j))) {
					// ��ʼ�������ã���������
					canvas.drawText(this.game.getGamePuzzleTileString(i, j), i * width + x, j * height + y, gameNum);
				} else {
					// ���������
					getRect(i, j, r);
					canvas.drawRect(r, selectedNumBackground);
					canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
				}
			}
		}
	}

	/**
	 * �Ź����Լ������߿���
	 * 
	 * @param canvas
	 */
	private void LineKuang(final Canvas canvas) {
		// ��ӾŹ����Լ��߿�����߱���
		// �߿�
		Paint puzzle_kuang = new Paint(Paint.DITHER_FLAG);// ����һ������
		puzzle_kuang.setStyle(Style.FILL_AND_STROKE);// ���÷����
		puzzle_kuang.setStrokeWidth(1);// �ʿ�1����
		puzzle_kuang.setColor(getResources().getColor(R.color.puzzle_nine));// ����Ϊ���
		puzzle_kuang.setAntiAlias(true);// ��ݲ���ʾ
		// ������
		Paint puzzle_three = new Paint(Paint.DITHER_FLAG);// ����һ������
		puzzle_three.setStyle(Style.FILL_AND_STROKE);// ���÷����
		puzzle_three.setStrokeWidth(1);// �ʿ�1����
		puzzle_three.setColor(getResources().getColor(R.color.puzzle_three));// ����Ϊ���
		puzzle_three.setAntiAlias(true);// ��ݲ���ʾ
		// �ź���
		Paint puzzle_nine = new Paint(Paint.DITHER_FLAG);// ����һ������
		puzzle_nine.setStyle(Style.FILL_AND_STROKE);// ���÷����
		puzzle_nine.setStrokeWidth(1);// �ʿ�1����
		puzzle_nine.setColor(getResources().getColor(R.color.puzzle_nine));// ����Ϊ���
		puzzle_nine.setAntiAlias(true);// ��ݲ���ʾ
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
	 * �޸ĵ�Ԫ���е�����,�㷨Ҫ�ı䣬ֻ��Ψһ������ȷ
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
	 * ������Ԫ���е�����
	 * 
	 */
	public void setSelectedZero() {
		game.setTile(selX, selY, 0);
		invalidate();
	}

	/**
	 * ���ÿհ�λ�ð�ɫ����
	 * 
	 * @param canvas
	 */
	private void setZeroNumWhiteBackground(Canvas canvas) {
		canvas.drawRect(selRect, selectedNumBackground);
	}

	/**
	 * ������ɫ���ı߿�
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
	 * ���ý���Ϊ����հ�״̬
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
				// �������Ϸ��ʼ���֣���ô��ʾ�Һ�ɫ��������ʾ��ɫ
				if (this.game.getTileString(i, j).equals(this.game.getGamePuzzleTileString(i, j))) {
					// ��ʼ�������ã���������
					canvas.drawText(this.game.getGamePuzzleTileString(i, j), i * width + x, j * height + y, gameNum);
				} else {
					// ���������
					canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);
				}
			}
		}
	}

	/**
	 * ��ȡrectλ��
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
	 * ���ý���Ϊ�������״̬
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
		// ������ͬ���ֵ�λ�ò��Ҷ�͹�Գ���ɫ
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
	 * �����¼�����
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
	 * �������룬���¶�λλ�ã�ˢ�½��棬��������ж���λ�������ֻ��ǿհ�
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
