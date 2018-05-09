package cn.nuaa.ai.fastdtw;

public class DistanceFunctionFactory {
	public static DistanceFunction EUCLIDEAN_DIST_FN = new EuclideanDistance();
	public static DistanceFunction MANHATTAN_DIST_FN = new ManhattanDistance();
	public static DistanceFunction BINARY_DIST_FN = new BinaryDistance();
	public static DistanceFunction OPCODE_DIST_FN = new OpCodeDistance();

	public static DistanceFunction getDistFnByName(String distFnName) {
		if (distFnName.equals("EuclideanDistance")) {
			return EUCLIDEAN_DIST_FN;
		} else if (distFnName.equals("ManhattanDistance")) {
			return MANHATTAN_DIST_FN;
		} else if (distFnName.equals("BinaryDistance")) {
			return BINARY_DIST_FN;
		} else if(distFnName.equals("OpCodeDistance")){
			return OPCODE_DIST_FN;
		}else {
			throw new IllegalArgumentException("There is no DistanceFunction for the name " + distFnName);
		} // end if
	}
}