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

	private static List<InstructionSequence> LCsequence = new ArrayList<InstructionSequence>();
	private static List<InstructionSequence> ReverseNarrationLCSequence = new ArrayList<InstructionSequence>();
	private static List<String> insName2IndexList = new ArrayList<String>();
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();//记录开始时间
		
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile("F:\\data\\jarFiles\\Top100000N\\instruction\\");
		//从LC文件中读取LC序列;第一种计算方法不需要读取这个信息,第二种计算方法才需要;
		//getInstructionsFromLCFile("F:\\data\\jarFiles\\Top100000N\\LCsequence\\",0);
		//getInstructionsFromLCFile("F:\\data\\jarFiles\\Top100000N\\ReverseNarrationLCSequence\\",1);
		//读取聚类信息;
		//Map<String, List<String>> clusteringMap = loadClusteringResult("F:\\data\\jarFiles\\Top100000N\\ClusteringResult80\\");
		//for(InstructionSequence ins : TestLCS.getInstructions()){
		//	insName2IndexList.add(ins.getFileName());
		//}
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");

		long readTime=System.currentTimeMillis();//记录结束时间
		
		//第一种查找方法,在计算的同时去计算LC Sequence;
		BWTSearch(TestLCS.getInstructions().get(973));
		//第二种查找方法,预先计算LC Sequence;		
		//BWTSearch2(TestLCS.getInstructions().get(666));
		//在聚类的基础上进行查找;
		//BWTSearchWithClustering(TestLCS.getInstructions().get(1069), clusteringMap);
		
		//用来为第二种计算方法预先计算LCsequence的;
		//storeLCSequence();
		//storeReverseNarrationLCSequence();
		
		//聚类;
		//ISClustering();
		
		//去掉数据库中Instruction Sequence长度小于20的;
		//removeLessThan20();
		
		long endTime=System.currentTimeMillis();//记录结束时间 
		float readinTime=(float)(readTime - startTime)/1000;  
		float excTime=(float)(endTime - readTime)/1000;  
		System.out.println("read in time："+readinTime);
		System.out.println("process time："+excTime); 
		
	}

	/**
	 * 使用了聚类结果进行查询;
	 * */
	public static void BWTSearchWithClustering(InstructionSequence seed, Map<String, List<String>> clusteringMap){
		List<Similarity2ClassName> clusteringGroupResultsList = new ArrayList<Similarity2ClassName>();
		List<Similarity2ClassName> fineGrainedResultsList = new ArrayList<Similarity2ClassName>();
		Map<String,Double> coarseGrainedResultsList = new HashMap<String,Double>();
		
		for(String s : clusteringMap.keySet()){
			//InstructionSequence is = getInstructionFromFileName(clusteringMap.get(s).get(0));
			Similarity2ClassName s2c = new Similarity2ClassName();
			s2c.setClassName(s);
			s2c.setSimilarity(getSimilarity2(seed,insName2IndexList.indexOf(clusteringMap.get(s).get(0))));
			//在clusteringGroupResultList 中加入相似度计算结果;以此判断相似度较高的类别; 
			clusteringGroupResultsList.add(s2c);
			//在coarseGrainedResultsList 中加入相似度计算结果;避免某些结果的重复计算;
			coarseGrainedResultsList.put(clusteringMap.get(s).get(0), s2c.getSimilarity());
			
		}
		Collections.sort(clusteringGroupResultsList);
		
		//for(Similarity2ClassName s2c : coarseGrainedResultsList){
		//	System.out.println(s2c.getClassName() + "  " + s2c.getSimilarity());
		//}
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		for (int i = 0;i < clusteringGroupResultsList.size() && i < 500;i++) {
			Similarity2ClassName s2c = clusteringGroupResultsList.get(i);
			for(String s : clusteringMap.get(s2c.getClassName())){			

				Similarity2ClassName temps2c = new Similarity2ClassName();
				temps2c.setClassName(s);
				//如果在第一步选择Clustering Group时就已经算过了,就不再重新算了;
				if(coarseGrainedResultsList.keySet().contains(s)){
					temps2c.setSimilarity(coarseGrainedResultsList.get(s));
				}else{
					temps2c.setSimilarity(getSimilarity2(seed,insName2IndexList.indexOf(s)));
				}
				
				fineGrainedResultsList.add(temps2c);
			}
		}
		Collections.sort(fineGrainedResultsList);
		int i = 0;
		for(Similarity2ClassName s2c : fineGrainedResultsList){
			System.out.println(s2c.getClassName() + "  " + s2c.getSimilarity());
			if(i > 10){
				break;
			}
			i++;
		}
	}
	
	/**
	 * 通过第一种方法来计算两个InstructionSequence 之间的相似度;
	 * 即在计算的过程中来计算LC Sequence;
	 * */
	public static double getSimilarity(InstructionSequence seed, InstructionSequence freq){
		//System.out.println(seed.getFileName());
		//正序查找;
		getFirstLastRow(freq);
		List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
		double s1 = (BWTSimilarity(firstRowMap, lastRowMap,seed.getIns())+1)/seed.getIns().size();
		firstRowMap.clear();
		lastRowMap.clear();
		FirstLastRow.clear();
		
		//逆序查找;
		InstructionSequence is1 = new InstructionSequence();
		is1.setFileName(freq.getFileName());
		is1.setIns(ReverseNarration(freq.getIns()));
		getFirstLastRow(is1);
		firstRowMap = mapRows(FirstLastRow.get(0));
		lastRowMap = mapRows(FirstLastRow.get(1));
		double s2 = (BWTSimilarity(firstRowMap, lastRowMap,ReverseNarration(seed.getIns()))+1)/seed.getIns().size();
		FirstLastRow.clear();
		//System.out.println(s1 + "  " + s2);
		
		return (s1 > s2 ? s1 : s2);
	}
	
	/**
	 * 从文件中读取聚类结果;
	 * */
	public static Map<String, List<String>> loadClusteringResult(String filePath){
		Map<String, List<String>> clusteringMap = new HashMap<String, List<String>>();
		File directory = new File(filePath);
		File[] cluFiles = directory.listFiles();
		for(File file : cluFiles){
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			List<String> list = new ArrayList<String>();
			try {
				String str = "";
				fis = new FileInputStream(filePath + file.getName());
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				while ((str = br.readLine()) != null) {
					list.add(str);
				}
			} catch (FileNotFoundException e) {
				System.out.println("Cann't find: " + file.getName());
			} catch (IOException e) {
				System.out.println("Cann't read: " + file.getName());
			} finally {
				try {
					br.close();
					isr.close();
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			clusteringMap.put(file.getName(), list);
		}
		
		//for(String s : clusteringMap.keySet()){
		//	System.out.println(s);
		//}
		//System.out.println(clusteringMap.size());		
		//for(String s : clusteringMap.get("clusteringResult0.txt")){
		//	System.out.println(s);
		//}
		
		return clusteringMap;
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
		
		for(String name : list){
			File file = new File("F:\\data\\jarFiles\\Top100000\\instruction\\" + name);
			file.delete();
			File file1 = new File("F:\\data\\jarFiles\\Top100000\\methodbody\\" + name);
			file1.delete();
		}
		
		System.out.println(TestLCS.getInstructions().size());
		System.out.println(counter + " " + list.size());
	}
	
	
	/**
	 * 对代码进行聚类;
	 * */
	public static void ISClustering(){
		List<List<String>> clusteringResultList = new ArrayList<List<String>>();
		Map<String,Boolean> map = new HashMap<String,Boolean>();
			
		for(int i =0; i < TestLCS.getInstructions().size(); i++){
			map.put(TestLCS.getInstructions().get(i).getFileName(), false);
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
			
				similarity = getSimilarity2(controlIns, j);
				
				//System.out.println(i + " " + j + " " + similarity);
				
				if(similarity >= 0.80){
					clusteringResult.add(is.getFileName());
					map.replace(is.getFileName(), true);
				}
			}
			clusteringResultList.add(clusteringResult);
			System.out.println(clusteringResultList.size());
		}
		//System.out.println(clusteringResultList.size());
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
				writeFileContent("F:\\data\\jarFiles\\Top100000\\ClusteringResult80\\clusteringResult" + i + ".txt",str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	/**
	 * 在计算的过程中计算LC;
	 * */
	public static void BWTSearch(InstructionSequence seed){
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(i));
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(getSimilarity(seed,is));
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
	 * 通过第二种方法来计算两个InstructionSequence 之间的相似度;
	 * 即在预先计算LC Sequence;
	 * */
	public static double getSimilarity2(InstructionSequence seed,int index){
		//正序;
		//InstructionSequence is = getLCSequence(freq);
		InstructionSequence is = LCsequence.get(index);
		getFCFromLC(is);
		List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
		double s1 = (BWTSimilarity(firstRowMap, lastRowMap,seed.getIns())+1)/seed.getIns().size();
		FirstLastRow.clear();
		
		//逆序;
		//InstructionSequence is2 = getReverseNarrationLCSequence(freq);
		InstructionSequence is2 = ReverseNarrationLCSequence.get(index);
		getFCFromLC(is2);
		firstRowMap = mapRows(FirstLastRow.get(0));
		lastRowMap = mapRows(FirstLastRow.get(1));
		double s2 = (BWTSimilarity(firstRowMap, lastRowMap,ReverseNarration(seed.getIns()))+1)/seed.getIns().size();
		FirstLastRow.clear();
		
		return (s1 > s2 ? s1 : s2);
	}
	
	
	/**
	 * LC在之前已经计算好了,在计算相似度时读取即可;
	 * */
	public static void BWTSearch2(InstructionSequence seed){
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(getSimilarity2(seed,i));
			simiList.add(s2c);
			
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
		}
		
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
		
		List<OpCode> firstRow = new ArrayList<OpCode>();
		List<OpCode> lastRow = new ArrayList<OpCode>();
		for (InstructionSequence ins : isList) {
			firstRow.add(ins.getIns().get(0));
			lastRow.add(ins.getIns().get(ins.getIns().size() - 1));
		}
		FirstLastRow.add(firstRow);
		FirstLastRow.add(lastRow);
	}

	/**
	 * 由Last Columns 得到 First Columns
	 * */
	public static void getFCFromLC(InstructionSequence is){
		InstructionSequence ins = new InstructionSequence(is);
		List<OpCode> lastRow = new ArrayList<OpCode>(ins.getIns());
		Collections.sort(ins.getIns());
		List<OpCode> firstRow = new ArrayList<OpCode>(ins.getIns());
		
		FirstLastRow.add(firstRow);
		FirstLastRow.add(lastRow);
	}
	
	/**
	 * 将逆序L-C Sequence 存入文件;
	 * 用以进行逆序的快速计算;
	 * */
	public static void storeReverseNarrationLCSequence(){
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			InstructionSequence is = new InstructionSequence(TestLCS.getInstructions().get(i));
			InstructionSequence is2 = new InstructionSequence();
			is2.setFileName(is.getFileName());
			is2.setIns(ReverseNarration(is.getIns()));
			getFirstLastRow(is2);
			
			StringBuffer buffer = new StringBuffer();
			for(int j = 0;j < FirstLastRow.get(1).size();j++){
				OpCode op = FirstLastRow.get(1).get(j);
				buffer.append(op.getName());
				if(op.getCodeId() == 182 || op.getCodeId() == 183 || op.getCodeId() == 184 || op.getCodeId() == 185 || op.getCodeId() == 186){
					buffer.append(" " + op.getInvokedMethod());
				}else if(op.getCodeId() == 187){
					buffer.append(" " + op.getNewType());
				}
				if(j < FirstLastRow.get(1).size()-1){
					buffer.append("\n");
				}
			}
			
			File file = TestLCS.getInsFiles()[i];
			System.out.println(file.getName());
			//System.out.println(buffer);
			
			try {
				writeFileContent("F:\\data\\jarFiles\\Top100000\\ReverseNarrationLCSequence\\" + file.getName(),buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}

			
			FirstLastRow.clear();
		}
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
				}else if(op.getCodeId() == 187){
					buffer.append(" " + op.getNewType());
				}
				if(j < FirstLastRow.get(1).size()-1){
					buffer.append("\n");
				}
			}
			
			File file = TestLCS.getInsFiles()[i];
			System.out.println(file.getName());
			//System.out.println(buffer);
			
			try {
				writeFileContent("F:\\data\\jarFiles\\Top100000\\LCsequence\\" + file.getName(),buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
	public static void getInstructionsFromLCFile(String filePath, int order) {
		File directory = new File(filePath);
		File[] insFiles = directory.listFiles();
		int i = 0;
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
				while ((str = br.readLine()) != null) {
					// System.out.println(str);
					// System.out.println(getOpCodeID(str));
					if(str.contains("SELFDEFINEDNULLTOKEN")){
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
			instrs.setId(i);
			instrs.setIns(list);
			instrs.setFileName(filename);
			if(order == 0)
				LCsequence.add(instrs);
			else if(order == 1)
				ReverseNarrationLCSequence.add(instrs);
			i++;
		}
	}
}
