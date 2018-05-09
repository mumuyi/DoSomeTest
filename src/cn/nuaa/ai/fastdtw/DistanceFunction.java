package cn.nuaa.ai.fastdtw;

public interface DistanceFunction {
	public double calcDistance(double[] vector1, double[] vector2);
}