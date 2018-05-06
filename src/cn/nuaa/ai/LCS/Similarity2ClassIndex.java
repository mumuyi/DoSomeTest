package cn.nuaa.ai.LCS;

public class Similarity2ClassIndex implements Comparable<Similarity2ClassIndex>{
	int classId;
	double similarity;

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	@Override
	public int compareTo(Similarity2ClassIndex s2c) {
		if((s2c.getSimilarity() - this.getSimilarity()) > 0)
			return 1;
		else if((s2c.getSimilarity() - this.getSimilarity()) == 0)
			return 0;
		else
			return -1;
	}

}
