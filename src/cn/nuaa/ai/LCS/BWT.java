package cn.nuaa.ai.LCS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BWT {
	private static List<List<OpCode>> FirstLastRow = new ArrayList<List<OpCode>>();

	public static void main(String[] args) {
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile();
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");

		InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(0));
		getFirstLastRow(is);
		
		List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
		
		
		BurrowsWheelerTransform(firstRowMap, lastRowMap, 0);
		for(OpCode op : TestLCS.getInstructions().get(0).getIns()){
			System.out.print(op.getCodeId() + " ");
		}
		System.out.println();
		
		
		/*
		System.out.println();
		for(int i : firstRowMap){
			System.out.print(i + " ");
		}
		
		System.out.println();
		for (OpCode ops : FirstLastRow.get(0)) {
			System.out.print(ops.getCodeId() + " ");
		}
		System.out.println();
		for (OpCode ops : FirstLastRow.get(1)) {
			System.out.print(ops.getCodeId() + " ");
		}
		*/
	}
 
	/**
	 * BWT �㷨ʵ��;
	 * startPosition ��ʾLastList�п�ʼ��λ��;
	 * �����Ҫ����ԭ�� startPosition = 0;
	 * �����Ҫƥ��,startPosition = Seed �еĵ�����һ��Ԫ����FirstList�еĶ�ӦԪ�ص�index;
	 * */
	public static double BurrowsWheelerTransform(List<Integer> firstRowMap,List<Integer> lastRowMap,int startPosition){
		int Lindex = startPosition;
		int Findex = 0;
		int preId = 0;
		int preNum = 0;
		//�������ƶȵ�,����û��;
		double similarScore = 0.0;
		
		List<Integer> list = new ArrayList<Integer>();
		while(FirstLastRow.get(1).get(Lindex).getCodeId() != -1){
			list.add(FirstLastRow.get(1).get(Lindex).getCodeId());
			//System.out.println("List add: " + FirstLastRow.get(1).get(Lindex).getCodeId());
			preId  = FirstLastRow.get(1).get(Lindex).getCodeId();
			preNum = lastRowMap.get(Lindex);
			Findex = getFirstIndex(firstRowMap, preId, preNum);
			//System.out.println("Findex: " + Findex);
			if(Findex == -1){
				System.out.println("��������");
				break;
			}
			Lindex = Findex;
		}
		
		for(int i = (list.size() - 1);i >= 0;i--){
			System.out.print(list.get(i) + " ");
		}
		System.out.println();
		
		return similarScore;
	}
	
	/**
	 * ����LastList�еĵڼ��γ��ֵ�ĳ��Ԫ�ص�λ��,���ض�Ӧ��FirstList�е�����;
	 * */
	public static int getFirstIndex(List<Integer> firstRowMap, int preId, int preNum){
		for(int i = 0;i < firstRowMap.size();i++){
			if((firstRowMap.get(i) == preNum) && (FirstLastRow.get(0).get(i).getCodeId() == preId)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * ����FirstList��LastList �е�ÿ��Ԫ���ǵڼ��γ���; 
	 * */
	public static List<Integer> mapRows(List<OpCode> row){
		List<Integer> list = new ArrayList<Integer>();
		Map<Integer,Integer> RowMap = new HashMap<Integer,Integer>();
		//int i = 0;
		for (OpCode ops : row) {
			int id = ops.getCodeId();
			
			//System.out.print(id + " ");
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + (i+1));
			
			if(RowMap.keySet().contains(id)){
				int value = RowMap.get(id)+1;
				list.add(value);
				RowMap.replace(id, value);
				//System.out.println("list add: " + value);
			}else{
				RowMap.put(id, 1);
				list.add(1);
				//System.out.println("list add: " + 1);
			}
			
			//for(int j : RowMap.keySet()){
			//	System.out.println(j + " " +RowMap.get(j));
			//}
			
			
			//i++;
			//if(i > 6)
			//	break;
		}
		
		//for(int k : list){
		//	System.out.print(k + " ");
		//}
		
		return list;
	}
	
	
	/**
	 * �õ�FirstRow �� LastRow;
	 * */
	public static void getFirstLastRow(InstructionSequence is) {
		List<InstructionSequence> isList = new ArrayList<InstructionSequence>();
		//System.out.println(is.getIns().size());

		// ���$����;
		OpCode op = new OpCode();
		op.setCodeId(-1);
		List<OpCode> opList = is.getIns();
		opList.add(0, op);
		is.setIns(opList);

		InstructionSequence localTemp = new InstructionSequence(is);
		for (int i = 0; i < is.getIns().size(); i++) {

			InstructionSequence temp = new InstructionSequence(localTemp);

			List<OpCode> tempOpList = temp.getIns();
			OpCode tempOp = tempOpList.get(tempOpList.size() - 1);
			tempOpList.remove(tempOpList.size() - 1);
			tempOpList.add(0, tempOp);
			temp.setIns(tempOpList);

			isList.add(temp);

			localTemp = temp;
		}
		
		// ����;
		Collections.sort(isList);
		//for (InstructionSequence ins : isList) {
		//	for (OpCode ops : ins.getIns()) {
		//		System.out.print(ops.getCodeId() + " ");
		//	}
		//	System.out.println();
		//}
		
		List<OpCode> firstRow = new ArrayList<OpCode>();
		List<OpCode> lastRow = new ArrayList<OpCode>();
		for (InstructionSequence ins : isList) {
			firstRow.add(ins.getIns().get(0));
			lastRow.add(ins.getIns().get(ins.getIns().size() - 1));
		}
		FirstLastRow.add(firstRow);
		FirstLastRow.add(lastRow);

		//System.out.println();
		//for (OpCode ops : firstRow) {
		//	System.out.print(ops.getCodeId() + " ");
		//}
		//System.out.println();
		//for (OpCode ops : lastRow) {
		//	System.out.print(ops.getCodeId() + " ");
		//}
	}

}
