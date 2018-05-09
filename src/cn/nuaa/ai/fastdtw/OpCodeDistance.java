package cn.nuaa.ai.fastdtw;

import cn.nuaa.ai.LCS.OpCode;

public class OpCodeDistance implements DistanceFunction {

	public OpCodeDistance() {
	}

	@Override
	public double calcDistance(double[] vector1, double[] vector2) {
		if(vector1.length != vector2.length){
			System.out.println("ERROR: Vector1 & Vector2 have different length");
			return 0;
		}
		double distance = 0.0;
		for(int i = 0;i < vector1.length && i < vector2.length;i++ ){
			OpCode opc1 = FastDTWTest.getOpCode(1,vector1[i]);
			OpCode opc2 = FastDTWTest.getOpCode(2,vector2[i]);
			if(opc1 != null && opc2 != null){
				if ((opc1.getLevle1() == opc2.getLevle1())
						&& (opc1.getLevle2() == opc2.getLevle2())) {
					distance += 0.0;
				} else if ((opc1.getLevle1() == opc2.getLevle1())
						&& (opc1.getLevle2() != opc2.getLevle2())) {
					distance += 0.3;
				} else if ((opc1.getLevle1() != opc2.getLevle1())) {
					distance += 1;
				}
			}
			else{
				System.out.println("ERROR");
			}
		}
		return distance;
	}

}
