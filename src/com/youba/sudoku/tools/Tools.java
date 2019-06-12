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
	// ��ȡ�ı��ļ��е�����
	public static String ReadTxtFile(File file) {
		String content = ""; // �ļ������ַ���
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
					// ���ж�ȡ
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
	 * ɾ��SD���ϵ��ļ�
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
	 * �汾��4.0���»������Ҫ�����жϣ�������,����ֶ���������������
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

	// ������Ϸ����ѡ��Ҫʹ�õ�Ӧ�ã�360�ֻ����֡��������֡��㶹��
	public static void rateGame(Context context) {
		try {
			Uri uri = Uri.parse("market://details?id="
					+ context.getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, "Ӧ���̵�δ��װ���޷����ۣ�sorry", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public static String getVersionName(Context context) throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		String version = packInfo.versionName;
		return version;
	}
}
