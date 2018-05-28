package cn.nuaa.ai.LCS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NgramOverlap {
	public static void main(String[] args) {
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile("F:\\data\\instruction\\");
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");
		
		Map<Integer,Integer> map = OverLap(TestLCS.getInstructions().get(1),TestLCS.getInstructions().get(2));
		
		for(int i : map.keySet()){
			System.out.println(i + ": " + map.get(i));
		}
	}

	/**
	 * 计算Overlap;
	 * */
	public static Map<Integer,Integer> OverLap(InstructionSequence x,InstructionSequence y){
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		
		for(int i = x.getIns().size();i > x.getIns().size()/2;i--){
			List<List<OpCode>> NgramList = getNgram(x, i);
			int tempoverlap = 0;
			for(List<OpCode> list : NgramList){
				if(isApartof(list, y.getIns())){
					tempoverlap++;
				}
			}
			map.put(i, tempoverlap);
		}
		
		return map;
	}
	
	/**
	 * 获取x的Ngram切割;
	 * */
	public static List<List<OpCode>> getNgram(InstructionSequence x, int gram) {
		List<List<OpCode>> list = new ArrayList<List<OpCode>>();
		List<OpCode> listx = x.getIns();

		for (int i = 0; i <= listx.size() - gram; i++) {
			List<OpCode> templist = new ArrayList<OpCode>();
			for (int j = 0; j < gram; j++) {
				templist.add(listx.get(i + j));
			}
			list.add(templist);
		}

		return list;
	}

	/**
	 * 判断x是否为y中的一部分;
	 * */
	public static boolean isApartof(List<OpCode> listx, List<OpCode> listy) {
		if (listx.size() > listy.size()) {
			return false;
		}
		int flag = 0;
		for (int i = 0; i <= listy.size() - listx.size(); i++) {
			for (int j = 0; j < listx.size(); j++) {
				if (!equals(listx.get(j), listx.get(j))) {
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				return true;
			}
			flag = 0;
		}
		return false;
	}

	/**
	 * 判断两个OpCode 是否相等;
	 * */
	public static boolean equals(OpCode o1, OpCode o2) {
		if (o1 == null || o2 == null) {
			return true;
		}

		if (o1.getCodeId() != o2.getCodeId()) {
			return false;
		} else {
			if (o1.getCodeId() == 182 || o1.getCodeId() == 183 || o1.getCodeId() == 184 || o1.getCodeId() == 185
					|| o1.getCodeId() == 186) {
				if (o1.getInvokedMethod().equals(o2.getInvokedMethod())) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
	}
}
