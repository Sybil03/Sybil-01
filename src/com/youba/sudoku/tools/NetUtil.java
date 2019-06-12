package com.youba.sudoku.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/***
 * 工具类，检查当前网络状态
 * 
 * @author shuimu
 * 
 */
public class NetUtil {

	public static boolean checkNet(Context context) {

		// 获取手机所以连接管理对象（包括wi-fi，net等连接的管理）

		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conn != null) {
			// 网络管理连接对象
			NetworkInfo info = conn.getActiveNetworkInfo();

			if (info != null && info.isConnected()) {
				// 判断当前网络是否连接
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}

		}

		return false;
	}
}