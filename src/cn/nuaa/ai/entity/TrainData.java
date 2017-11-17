package cn.nuaa.ai.entity;

public class TrainData {
	private int Id;
	private int QueryNum;
	private String FqName;
	private int Score;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getQueryNum() {
		return QueryNum;
	}

	public void setQueryNum(int queryNum) {
		QueryNum = queryNum;
	}

	public String getFqName() {
		return FqName;
	}

	public void setFqName(String fqName) {
		FqName = fqName;
	}

	public int getScore() {
		return Score;
	}

	public void setScore(int score) {
		Score = score;
	}

}
