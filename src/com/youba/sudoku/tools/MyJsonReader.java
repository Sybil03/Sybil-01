package com.youba.sudoku.tools;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyJsonReader {
	String jsonData;

	public MyJsonReader(String jsonData) {
		this.jsonData = jsonData;
	}

	public ArrayList<GameRecord> getJsonData() {
		ArrayList<GameRecord> gameRecords = new ArrayList<GameRecord>();
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONArray jsonArray = jsonObject.getJSONArray("vallage");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				GameRecord gameRecord = new GameRecord();
				gameRecord.setGameDiff(json.optInt("gameDiff"));
				gameRecord.setFinishTime(json.optString("finishTime"));
				gameRecords.add(gameRecord);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return gameRecords;
	}

}