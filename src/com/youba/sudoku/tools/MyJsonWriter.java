package com.youba.sudoku.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class MyJsonWriter {
	ArrayList<GameRecord> gameRecords;
	File saveFile;

	public MyJsonWriter() {
	}

	public MyJsonWriter(ArrayList<GameRecord> gameRecords) {
		this.gameRecords = gameRecords;
	}

	public void setFilePath(String filepath) {
		saveFile = new File(filepath);
	}

	/**
	 * 从文件获取json数据
	 * 
	 * @return
	 */
	public String getJsonData() {
		String jsonData = null;
		try {
			ArrayList<String> gameRecordsData = new ArrayList<String>();
			JSONArray array = new JSONArray();
			for (int i = 0; i < gameRecords.size(); i++) {
				GameRecord gameRecord = gameRecords.get(i);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("gameDiff", gameRecord.getGameDiff());
				jsonObject.put("finishTime", gameRecord.getFinishTime());
				gameRecordsData.add(jsonObject.toString());
				array.put(jsonObject);
			}
			jsonData = new JSONStringer().object().key("vallage").value(array).endObject().toString();
			System.out.println(jsonData);
			writeData(jsonData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonData;
	}

	/**
	 * 将数据写入文件
	 * 
	 * @param jsonData
	 */
	private void writeData(String jsonData) {
		try {
			BufferedReader reader = new BufferedReader(new StringReader(jsonData));
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
			int len = 0;
			char[] buffer = new char[1024];
			while ((len = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, len);
			}
			writer.flush();
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}