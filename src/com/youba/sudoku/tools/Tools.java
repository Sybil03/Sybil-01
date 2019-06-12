package com.youba.sudoku.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Tools {
	// 读取文本文件中的内容
	public static String ReadTxtFile(File file) {
		String content = ""; // 文件内容字符串
		if (file.isDirectory()) {
			Log.d("TestFile", "The File doesn't not exist.");
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// 分行读取
					while ((line = buffreader.readLine()) != null) {
						content += line + "\n";
					}
					instream.close();
				}
			} catch (java.io.FileNotFoundException e) {
				Log.d("TestFile", "The File doesn't not exist.");
			} catch (IOException e) {
				Log.d("TestFile", e.getMessage());
			}
		}
		return content;
	}

	/**
	 * 删除SD卡上的文件
	 * 
	 * @param fileName
	 */
	public static void deleteSDFile(String fileName) {
		File file = new File(fileName);
		if (file == null || !file.exists() || file.isDirectory()) {
		} else {
			file.delete();
		}
	}

	/**
	 * 版本再4.0以下会出错，需要进行判断，导航栏,如果手动拉出导航栏？？
	 */
	public static void setVirtualMenudis(Activity activity) {
		if (Build.VERSION.SDK_INT < 19) { // 19 or above api
			View v = activity.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else {
			// for lower api versions.
			View decorView = activity.getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}

	}

	// 评价游戏，请选择要使用的应用：360手机助手、精灵助手、豌豆荚
	public static void rateGame(Context context) {
		try {
			Uri uri = Uri.parse("market://details?id="
					+ context.getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, "应用商店未安装，无法评价，sorry", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public static String getVersionName(Context context) throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}
}
