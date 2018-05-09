package cn.nuaa.ai.fastdtw;

interface CostMatrix {
	public void put(int col, int row, double value);

	public double get(int col, int row);

	public int size();

} // end interface CostMatrix
