package com.youba.sudoku.tools;

public class GameRecord {
	int gameDiff;
	String finishTime;

	public static String[] getKeys() {
		String[] keys = null;
		return keys;
	}

	public int getGameDiff() {
		return gameDiff;
	}

	public void setGameDiff(int gameDiff) {
		this.gameDiff = gameDiff;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "GameRecord ("
	        + "gameDiff=" + this.gameDiff + TAB
	        + "finishTime=" + this.finishTime + TAB
	        + ")";
	
	    return retValue;
	}
}
