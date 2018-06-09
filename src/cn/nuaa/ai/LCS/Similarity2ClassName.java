package cn.nuaa.ai.LCS;

public class Similarity2ClassName implements Comparable<Similarity2ClassName>{
	String className;
	double similarity;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	@Override
	public int compareTo(Similarity2ClassName s2c) {
		if((s2c.getSimilarity() - this.getSimilarity()) > 0)
			return 1;
		else if((s2c.getSimilarity() - this.getSimilarity()) == 0)
			return 0;
		else
			return -1;
	}

}
