package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BWT {
	private static List<List<OpCode>> FirstLastRow = new ArrayList<List<OpCode>>();

	private static final Set<Integer> invSet = new HashSet<Integer>(){
		private static final long serialVersionUID = -8723339838495980395L;
	{add(182);add(183);add(184);add(185);add(186);}};

	private static int controlGroupNum = 2;

	private static List<InstructionSequence> LCsequence = new ArrayList<InstructionSequence>();
	
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();//记录开始时间
		
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile("F:\\data\\jarFiles\\Top10000\\instruction\\");
		//从LC文件中读取LC序列;第一种计算方法不需要读取这个信息,第二种计算方法才需要;
		//getInstructionsFromLCFile("F:\\data\\jarFiles\\Top10000\\LCsequence\\");
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");

		long readTime=System.currentTimeMillis();//记录结束时间
		
		//BWTSearch();
		//BWTSearch2();
		//BWTSearchWithReverseNarration();
		//用来为第二种计算方法预先计算LCsequence的;
		//storeLCSequence();
		//聚类;
		ISClustering();
	
		//removeLessThan20();
		
		long endTime=System.currentTimeMillis();//记录结束时间 
		float readinTime=(float)(readTime - startTime)/1000;  
		float excTime=(float)(endTime - readTime)/1000;  
		System.out.println("read in time："+readinTime);
		System.out.println("process time："+excTime); 
		
	}

	/**
	 * 删除Instruction Sequence 长度小于20的;
	 * */
	public static void removeLessThan20(){
		List<String> list = new ArrayList<String>();
		int counter = 0;
		for(int i =0; i < TestLCS.getInstructions().size(); i++){
			if(TestLCS.getInstructions().get(i).getIns().size() < 20){
				list.add(TestLCS.getInstructions().get(i).getFileName());
				counter ++;
			}
		}
		/*
		for(String name : list){
			File file = new File("F:\\data\\jarFiles\\Top10000\\instruction\\" + name);
			file.delete();
			File file1 = new File("F:\\data\\jarFiles\\Top10000\\methodbody\\" + name);
			file1.delete();
		}
		*/
		System.out.println(counter + " " + list.size());
	}
	
	
	/**
	 * 对代码进行聚类;
	 * */
	public static void ISClustering(){
		List<List<String>> clusteringResultList = new ArrayList<List<String>>();
		Map<String,Boolean> map = new HashMap<String,Boolean>();
			
		for(int i =0; i < TestLCS.getInstructions().size(); i++){
			if(TestLCS.getInstructions().get(i).getIns().size() > 20)
				map.put(TestLCS.getInstructions().get(i).getFileName(), false);
			else
				map.put(TestLCS.getInstructions().get(i).getFileName(), true);
		}
		
		System.out.println("!!!!!!!!!!!!!!! " + "begin to clustering" + " !!!!!!!!!!!!!!!!!!!!!!");
		
		for(int i =0; i < TestLCS.getInstructions().size(); i++){
			InstructionSequence controlIns = new InstructionSequence(TestLCS.getInstructions().get(i));
			if(map.get(controlIns.getFileName())){
				continue;
			}
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + i);
			List<String> clusteringResult = new ArrayList<String>();
			clusteringResult.add(controlIns.getFileName());
			map.replace(controlIns.getFileName(), true);
			for (int j = 0; j < TestLCS.getInstructions().size(); j++) {
				double similarity = 0;
				InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(j));
				if(i == j || map.get(is.getFileName())){
					continue;
				}
				
				//正序查找;
				getFirstLastRow(is);
				List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
				List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
				double s1 = (BWTSimilarity(firstRowMap, lastRowMap,controlIns.getIns())+1)/controlIns.getIns().size();
				firstRowMap.clear();
				lastRowMap.clear();
				FirstLastRow.clear();
				
				//逆序查找;
				InstructionSequence is1 = new InstructionSequence();
				is1.setFileName(is.getFileName());
				is1.setIns(ReverseNarration(is.getIns()));
				getFirstLastRow(is1);
				firstRowMap = mapRows(FirstLastRow.get(0));
				lastRowMap = mapRows(FirstLastRow.get(1));
				double s2 = (BWTSimilarity(firstRowMap, lastRowMap,ReverseNarration(controlIns.getIns()))+1)/controlIns.getIns().size();
				FirstLastRow.clear();
				
				//选择相似度大的;
				similarity = (s1 > s2 ? s1 : s2);
				if(similarity >= 0.80){
					clusteringResult.add(is.getFileName());
					map.replace(is.getFileName(), true);
				}
			}
			clusteringResultList.add(clusteringResult);
		}
		System.out.println(clusteringResultList.size());
		//for(int i = 0; i < clusteringResultList.size();i++){
		//	System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    " + i + "   " + clusteringResultList.get(i).size());
		//	for(String s : clusteringResultList.get(i)){
				//System.out.println(s);
		//	}
		//}
		storeClusteringResult(clusteringResultList);
	}
	
	/**
	 * 存储聚类结果;
	 * */
	public static void storeClusteringResult(List<List<String>> clusteringResultList){
		for(int i = 0; i < clusteringResultList.size();i++){
			//File file = new File("F:\\data\\jarFiles\\Top10000\\ClusteringResult\\clusteringResult" + i);
			//if(!file.exists()){  
			//    file.mkdirs();  
			//}
			
			StringBuffer str = new StringBuffer();
			for(int j = 0;j < clusteringResultList.get(i).size();j++){
				str.append(clusteringResultList.get(i).get(j));
				if(j < clusteringResultList.get(i).size() - 1){
					str.append("\n");
				}
			}
			
			try {
				writeFileContent("F:\\data\\jarFiles\\Top10000\\ClusteringResult75\\clusteringResult" + i + ".txt",str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	/**
	 * 在计算的过程中计算LC;
	 * */
	public static void BWTSearch(){
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(i));
			//正序查找;
			//getFirstLastRow(is);
			//逆序查找;
			InstructionSequence is1 = new InstructionSequence();
			is1.setFileName(is.getFileName());
			is1.setIns(ReverseNarration(is.getIns()));
			getFirstLastRow(is1);
			
			List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
			List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			//正序查找;
			//s2c.setSimilarity((BWTSimilarity(firstRowMap, lastRowMap,TestLCS.getInstructions().get(controlGroupNum).getIns())+1)/TestLCS.getInstructions().get(controlGroupNum).getIns().size());
			//逆序查找;
			s2c.setSimilarity((BWTSimilarity(firstRowMap, lastRowMap,ReverseNarration(TestLCS.getInstructions().get(controlGroupNum).getIns()))+1)/TestLCS.getInstructions().get(controlGroupNum).getIns().size());
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
	}
	
	/**
	 * 还是第一种计算方法,但是考虑了逆序的问题;
	 * */
	public static void BWTSearchWithReverseNarration(){
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(i));
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			//正序查找;
			getFirstLastRow(is);
			List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
			List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
			double s1 = (BWTSimilarity(firstRowMap, lastRowMap,TestLCS.getInstructions().get(controlGroupNum).getIns())+1)/TestLCS.getInstructions().get(controlGroupNum).getIns().size();
			firstRowMap.clear();
			lastRowMap.clear();
			FirstLastRow.clear();
			
			//逆序查找;
			InstructionSequence is1 = new InstructionSequence();
			is1.setFileName(is.getFileName());
			is1.setIns(ReverseNarration(is.getIns()));
			getFirstLastRow(is1);
			firstRowMap = mapRows(FirstLastRow.get(0));
			lastRowMap = mapRows(FirstLastRow.get(1));
			double s2 = (BWTSimilarity(firstRowMap, lastRowMap,ReverseNarration(TestLCS.getInstructions().get(controlGroupNum).getIns()))+1)/TestLCS.getInstructions().get(controlGroupNum).getIns().size();
			
			//选择相似度大的;
			s2c.setClassId(i);
			s2c.setSimilarity((s1 > s2 ? s1 : s2));
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
	}
	
	
	
	/**
	 * LC在之前已经计算好了,在计算相似度时读取即可;
	 * */
	public static void BWTSearch2(){
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			InstructionSequence is = LCsequence.get(i);
			getFCFromLC(is);
			
			List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
			List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity((BWTSimilarity(firstRowMap, lastRowMap,TestLCS.getInstructions().get(controlGroupNum).getIns())+1)/(TestLCS.getInstructions().get(controlGroupNum).getIns().size()));
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
	}
	
	
	
	/**
	 * 将seed Sequence逆叙;
	 * */
	public static List<OpCode> ReverseNarration(List<OpCode> ops){
		List<OpCode> list = new ArrayList<OpCode>();
		for(int i = ops.size()-1;i >= 0;i--){
			list.add(ops.get(i));
		}
		return list;
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
		
		for(int i = 0;(i < N) && (FirstLastRow.get(1).size() > Lindex) && (FirstLastRow.get(1).get(Lindex).getCodeId() != -1);i++){
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
		double similarScore = 0.0;
		double threshold = 0.0;
		List<Integer> startPointList = null;
		for(int i = 0;i < 3;i++){
			if(seedList.size()-1-i < 0){
				break;
			}
			startPointList = getStartPoint(seedList.get(seedList.size()-1-i));
			if(startPointList != null && !startPointList.isEmpty()){
				break;
			}
		}
		if(startPointList == null || startPointList.isEmpty()){
			return 0.0;
		}
		if(startPointList.contains(-1)){
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
					if(startPoint == -1){
						break;
					}
				}else{
					startPoint = getFirstIndex(firstRowMap,freqOp.getCodeId(),lastRowMap.get(startPoint));
					if(startPoint == -1){
						break;
					}
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
		op.setName("SELFDEFINEDNULLTOKEN");
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

	/**
	 * 由Last Columns 得到 First Columns
	 * */
	public static void getFCFromLC(InstructionSequence is){
		List<OpCode> firstRow = new ArrayList<OpCode>(is.getIns());
		Collections.sort(is.getIns());
		List<OpCode> lastRow = new ArrayList<OpCode>(is.getIns());
		
		FirstLastRow.add(lastRow);
		FirstLastRow.add(firstRow);
		
		
		//for(OpCode op : FirstLastRow.get(0)){
		//	System.out.println(op.getName() + " " + op.getCodeId());
		//}
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
		//for(OpCode op : FirstLastRow.get(1)){
		//	System.out.println(op.getName() + " " + op.getCodeId());
		//}
		
	}
	
	
	/**
	 * 将L-C Sequence 存入文件;
	 * */
	public static void storeLCSequence(){
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(i));
			getFirstLastRow(is);
			
			StringBuffer buffer = new StringBuffer();
			for(int j = 0;j < FirstLastRow.get(1).size();j++){
				OpCode op = FirstLastRow.get(1).get(j);
				buffer.append(op.getName());
				if(op.getCodeId() == 182 || op.getCodeId() == 183 || op.getCodeId() == 184 || op.getCodeId() == 185 || op.getCodeId() == 186){
					buffer.append(" " + op.getInvokedMethod());
				}
				if(j < FirstLastRow.get(1).size()-1){
					buffer.append("\n");
				}
			}
			
			File file = TestLCS.getInsFiles()[i];
			System.out.println(file.getName());
			//System.out.println(buffer);
			
			try {
				writeFileContent("F:\\data\\jarFiles\\Top10000\\LCsequence\\" + file.getName(),buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*
			for(OpCode op : FirstLastRow.get(0)){
				System.out.println(op.getName() + " " + op.getCodeId());
			}
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
			for(OpCode op : FirstLastRow.get(1)){
				System.out.println(op.getName() + " " + op.getCodeId());
			}
			*/
			
			FirstLastRow.clear();
		}
	}
	
	
	/**
	 * 写入文件;
	 * */
	private static boolean writeFileContent(String filepath, StringBuffer buffer) throws IOException {
		Boolean bool = false;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);// 文件路径(包括文件名称)

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			bool = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 不要忘记关闭
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return bool;
	}
	
	/**
	 * 从LCSequence文件中获取数据;
	 */
	public static void getInstructionsFromLCFile(String filePath) {
		File directory = new File(filePath);
		File[] insFiles = directory.listFiles();
		for (File file : insFiles) {
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			String filename = file.getName();
			List<OpCode> list = new ArrayList<OpCode>();
			InstructionSequence instrs = new InstructionSequence();
			try {
				String str = "";
				fis = new FileInputStream(filePath + filename);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
				// filename);
				while ((str = br.readLine()) != null) {
					// System.out.println(str);
					// System.out.println(getOpCodeID(str));
					if(str.equals("SELFDEFINEDNULLTOKEN")){
						OpCode op = new OpCode();
						op.setCodeId(-1);
						op.setName("SELFDEFINEDNULLTOKEN");
						list.add(op);
					}
					if (TestLCS.getOpCodeID(str) != -1) {
						OpCode op = new OpCode(TestLCS.getOplist().get(TestLCS.getOpCodeID(str)));
						list.add(op);
					} else {
						String[] strs = str.split(" ");
						if (strs.length > 1) {
							if (strs[0].equals("invokevirtual") || strs[0].equals("invokespecial")
									|| strs[0].equals("invokestatic") || strs[0].equals("invokeinterface")
									|| strs[0].equals("invokedynamic")) {
								OpCode op = new OpCode(TestLCS.getOplist().get(TestLCS.getOpCodeID(strs[0])));
								//System.out.println(strs[1]);
								op.setInvokedMethod(strs[1]);
								list.add(op);
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("Cann't find: " + filename);
			} catch (IOException e) {
				System.out.println("Cann't read: " + filename);
			} finally {
				try {
					br.close();
					isr.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			instrs.setIns(list);
			instrs.setFileName(filename);
			LCsequence.add(instrs);
			//for (OpCode op : instructions.get(0)) {
			//	System.out.println(op.getName() + " " + op.getInvokedMethod());
			//}
		}
	}
}
