package com.youba.sudoku.tools;

import android.content.Context;
import android.media.MediaPlayer;

public class Music {
	
	private  MediaPlayer mp = null;
	Context context;
	public Music(Context context){
		this.context = context;
	}

	public  void play(int resid) {
		// ≤•∑≈“Ù¿÷
		if (mp == null) {
			mp = MediaPlayer.create(context, resid);
			mp.setLooping(true);
			mp.start();
		}
	}

	public  void stop() {
		// Õ£÷π“Ù¿÷
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
	}
}
