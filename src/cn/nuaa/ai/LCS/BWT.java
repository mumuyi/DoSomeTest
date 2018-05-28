package cn.nuaa.ai.LCS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.runtime.Inner;

public class BWT {
	private static List<List<OpCode>> FirstLastRow = new ArrayList<List<OpCode>>();

	private static final Set<Integer> invSet = new HashSet<Integer>(){
		private static final long serialVersionUID = -8723339838495980395L;
	{add(182);add(183);add(184);add(185);add(186);}};

	private static int controlGroupNum = 19042;
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();//记录开始时间
		
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile("F:\\data\\jarFiles\\Top10000\\instruction\\");
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");

		long readTime=System.currentTimeMillis();//记录结束时间
		
		//InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(0));
		//getFirstLastRow(is);
		
		//List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		//List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
		
		
		//BurrowsWheelerTransform(firstRowMap, lastRowMap, 0, FirstLastRow.get(0).size()-1);
		//for(OpCode op : TestLCS.getInstructions().get(0).getIns()){
		//	System.out.print(op.getCodeId() + " ");
		//}
		//System.out.println();
		
		
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
		
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(i));
			getFirstLastRow(is);
			
			List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
			List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity((BWTSimilarity(firstRowMap, lastRowMap,TestLCS.getInstructions().get(controlGroupNum).getIns())+1)/TestLCS.getInstructions().get(controlGroupNum).getIns().size());
			simiList.add(s2c);
			
			FirstLastRow.clear();
		}
		Collections.sort(simiList);
		int i = 0;
		for (Similarity2ClassIndex s2c : simiList) {
			System.out.println(s2c.getClassId() + "  " + s2c.getSimilarity() + "   " + TestLCS.getInsFiles()[s2c.getClassId()]);
			i++;
			if (i > 11) {
				break;
			}
		}
		
		long endTime=System.currentTimeMillis();//记录结束时间 
		
		float readinTime=(float)(readTime - startTime)/1000;  
		float excTime=(float)(endTime - readTime)/1000;  
		
		System.out.println("read in time："+readinTime);
		System.out.println("process time："+excTime); 
	}
	
	
	
	/**
	 * BWT 算法实现;
	 * startPosition 表示LastList中开始的位置;
	 * 如果想要复现原串 startPosition = 0;N = FirstLastRow.get(1).size()-1;
	 * */
	public static List<OpCode> BurrowsWheelerTransform(List<Integer> firstRowMap,List<Integer> lastRowMap,int startPosition,int N){
		int Lindex = startPosition;
		int Findex = 0;
		int preId = 0;
		int preNum = 0;
		List<OpCode> list = new ArrayList<OpCode>();
		
		for(int i = 0;(i < N) && (FirstLastRow.get(1).get(Lindex).getCodeId() != -1);i++){
			list.add(FirstLastRow.get(1).get(Lindex));
			//System.out.println("List add: " + FirstLastRow.get(1).get(Lindex).getCodeId());
			preId  = FirstLastRow.get(1).get(Lindex).getCodeId();
			preNum = lastRowMap.get(Lindex);
			Findex = getFirstIndex(firstRowMap, preId, preNum);
			//System.out.println("Findex: " + Findex);
			if(Findex == -1){
				System.out.println("索引错误");
				break;
			}
			Lindex = Findex;
		}
		
		//for(int i = (list.size() - 1);i >= 0;i--){
		//	System.out.print(list.get(i).getCodeId() + " ");
		//}
		//System.out.println();
		
		return list;
	}
	
	
	
	
	/**
	 * BWT 字串匹配相似度计算算法实现;
	 * */
	public static double BWTSimilarity(List<Integer> firstRowMap,List<Integer> lastRowMap,List<OpCode> seedList){
		//计算相似度的,现在没用;
		double similarScore = 0.0;
		double threshold = 0.0;
		List<Integer> startPointList = null;
		for(int i = 0;i < 3;i++){
			startPointList = getStartPoint(seedList.get(seedList.size()-1));
			if(startPointList != null){
				break;
			}
		}
		if(startPointList == null){
			return 0.0;
		}
		
		//System.out.println("startPointList: " + startPointList);
		for(int i = 0;i < startPointList.size();i++){
			int startPoint = startPointList.get(i);
			//System.out.println("startPointList: " + startPointList.get(i));
			double tempSimilarity = 0.0;
			OpCode freqOp = null;
			OpCode freqOp1 = null;
			threshold = 0;
			for(int j = seedList.size()-2;(j > -1) && (threshold < 3);j--){
				//System.out.println(i + " " + j);
				double dtemp = -1.0;
				OpCode seedOp = seedList.get(j);
				List<OpCode> freqOpList = BurrowsWheelerTransform(firstRowMap,lastRowMap,startPoint,2);
				if(freqOpList == null){
					break;
				}else if(freqOpList.size() > 1){
					freqOp = freqOpList.get(0);
					freqOp1 = freqOpList.get(1);
					//System.out.println("freqOpList: " + freqOpList.size());
				}else if(freqOpList.size() > 0){
					freqOp = freqOpList.get(0);
					//System.out.println("freqOpList: " + freqOpList.size());
				}else{
					//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					break;
				}
				
				double s1 = 0.0;
				double s2 = 0.0;
				if((freqOp.getCodeId() == seedOp.getCodeId()) || (freqOp.getLevle1() == seedOp.getLevle1())){
					tempSimilarity += getSimilarityBetweenOpCodes(seedOp,freqOp);
				}else if(freqOp1 != null){
					s1 = getSimilarityBetweenOpCodes(seedOp,freqOp);
					s2 = getSimilarityBetweenOpCodes(seedOp,freqOp1);
					s2 -= 0.2;
					dtemp = s1 > s2 ? s1 : s2;
					tempSimilarity += dtemp;
					//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}else{
					tempSimilarity += getSimilarityBetweenOpCodes(seedOp,freqOp);
				}
				
				if(dtemp == s2){
					threshold += 1;
					int temp = getFirstIndex(firstRowMap,freqOp.getCodeId(),lastRowMap.get(startPoint));
					startPoint = getFirstIndex(firstRowMap,freqOp1.getCodeId(),lastRowMap.get(temp));
				}else{
					startPoint = getFirstIndex(firstRowMap,freqOp.getCodeId(),lastRowMap.get(startPoint));
				}
			}
			//System.out.println(tempSimilarity);
			if(tempSimilarity > similarScore){
				similarScore = tempSimilarity;
			}
		}


		
		return similarScore;
	}
	
	/**
	 * 计算两个OpCode 之间的相似度;
	 * */
	public static double getSimilarityBetweenOpCodes(OpCode seedOp,OpCode freqOp){
		if((seedOp.getCodeId() == freqOp.getCodeId()) && !(invSet.contains(seedOp.getCodeId()))){
			return 1.0;
		}else if((seedOp.getCodeId() == freqOp.getCodeId()) && (invSet.contains(seedOp.getCodeId()))){
			if(seedOp.getInvokedMethod().equals(freqOp.getInvokedMethod())){
				return 1.0;
			}else{
				return TestLCS.getAPISimilarity(seedOp.getInvokedMethod(), freqOp.getInvokedMethod());
			}
		}else if((invSet.contains(seedOp.getCodeId())) && (invSet.contains(freqOp.getCodeId()))){
			return TestLCS.getAPISimilarity(seedOp.getInvokedMethod(), freqOp.getInvokedMethod());
		}else if((seedOp.getLevle1() == freqOp.getLevle1()) && (seedOp.getLevle2() == freqOp.getLevle2())){
			return 1.0;
		}else if((seedOp.getLevle1() == freqOp.getLevle1()) && (seedOp.getLevle2() != freqOp.getLevle2())){
			return 0.8;
		}
		return 0.0;
	}
	
	/**
	 * 获取seed在freq中的开始位置列表;
	 * */
	public static List<Integer> getStartPoint(OpCode seedLast){
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0;i < FirstLastRow.get(0).size();i++){
			if(FirstLastRow.get(0).get(i).getCodeId() == seedLast.getCodeId()){
				list.add(i);
			}
		}
		return list;
	}
	
	
	
	/**
	 * 根据LastList中的第几次出现的某个元素的位置,返回对应的FirstList中的索引;
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
	 * 计算FirstList和LastList 中的每个元素是第几次出现; 
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
	 * 得到FirstRow 和 LastRow;
	 * */
	public static void getFirstLastRow(InstructionSequence is) {
		List<InstructionSequence> isList = new ArrayList<InstructionSequence>();
		//System.out.println(is.getIns().size());

		// 添加$符号;
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
		
		// 排序;
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
