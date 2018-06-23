package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BWTCodeLines {
	private static List<SourceCode> codeSnippets = new ArrayList<SourceCode>();
	private static List<List<TokenList>> FirstLastRow = new ArrayList<List<TokenList>>();
	private static File[] insFiles;

	public static void main(String[] args) {

		//readCode("F:\\data\\jarFiles\\Top100000N\\methodbody\\");
		//System.out.println("read in process finished");
		// for(SourceCode lls : codeSnippets){
		// System.out.println("File name: " + lls.getId() + " " +
		// lls.getName());
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// for(TokenList ls : lls.getCodes()){
		// for(String s : ls.getTokens()){
		// System.out.print(s + " ");
		// }
		// System.out.println();
		// }
		// }

		// getFirstLastRow(codeSnippets.get(0));
		// List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		// List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
		// BurrowsWheelerTransform(firstRowMap,lastRowMap,0,FirstLastRow.get(1).size()-1);
		// System.out.println(getSimilarity(codeSnippets.get(1),codeSnippets.get(1)));

		//BWTSearch(codeSnippets.get(0));

		runningDemo();
		
		
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// List<TokenList> ltl = FirstLastRow.get(1);
		// for(TokenList tl : ltl){
		// for(String s : tl.getTokens())
		// System.out.print(s + " ");
		// System.out.println();
		// }

		//System.out.println(codeSnippets.get(0).getCodes().get(0).getTokens());
		//System.out.println(codeSnippets.get(0).getName());
		//System.out.println(getSimilarityBetweenLine2(codeSnippets.get(0).getCodes().get(0),codeSnippets.get(1).getCodes().get(0)));
		
	}

	/**
	 * 运行demo;
	 * */
	public static void runningDemo(){
		long startTime = System.currentTimeMillis();//记录读取数据开始时间;
		readCode("F:\\data\\jarFiles\\Top100000N\\methodbody\\");
		System.out.println("read in process finished");
		long endTime=System.currentTimeMillis();//记录读取数据结束时间;
		System.out.println("read in time："+ (endTime - startTime));
		while(true){
			startTime = System.currentTimeMillis();//记录查询开始时间;
			
			List<TokenList> linecode = readCodeFromFile("F:\\data\\jarFiles\\Top100000N\\methodbody\\0.txt");
			SourceCode sc = new SourceCode();

			
			List<List<TokenList>> cutCode = SourceCodeCutting(linecode);
			if(cutCode.get(0).isEmpty() && cutCode.get(1).isEmpty()){
				System.out.println("Seed code format error");
			}
			if(cutCode.get(0).isEmpty()){
				sc.setCodes(cutCode.get(1));
				sc.setId(-1);
				sc.setName("seed code");
				BWTSearch(sc);
				//System.out.println("1111111111111111111111111111111111");
			}else if(cutCode.get(1).isEmpty()){
				sc.setCodes(cutCode.get(0));
				sc.setId(-1);
				sc.setName("seed code");
				BWTSearch(sc);
				//System.out.println("2222222222222222222222222222222222");
			}else{
				SourceCode fsc = new SourceCode();
				fsc.setCodes(cutCode.get(0));
				fsc.setId(-1);
				fsc.setName("seed code");
				
				SourceCode bsc = new SourceCode();
				bsc.setCodes(cutCode.get(1));
				bsc.setId(-1);
				bsc.setName("seed code");
				
				List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
				for (int i = 0; i < codeSnippets.size(); i++) {
					SourceCode is = new SourceCode(codeSnippets.get(i));
					Similarity2ClassIndex s2c = new Similarity2ClassIndex();
					s2c.setClassId(i);
					s2c.setSimilarity((0.5 * getSimilarity(fsc, is)) + (0.5 * getSimilarity(bsc, is)));
					simiList.add(s2c);

					FirstLastRow.clear();
				}
				Collections.sort(simiList);
				int i = 0;
				for (Similarity2ClassIndex s2c : simiList) {
					System.out.println(s2c.getClassId() + "  " + s2c.getSimilarity() + "   " + insFiles[s2c.getClassId()]);
					i++;
					if (i > 11) {
						break;
					}
				}
				//System.out.println("333333333333333333333333333333333333333333");
			}

			endTime=System.currentTimeMillis();//记录查询结束时间;
			System.out.println("search time："+ (endTime - startTime));
			
	        @SuppressWarnings("resource")
			Scanner sb = new Scanner(System.in);  
	        System.out.print("Continue or Not: ");  
	        String com = sb.nextLine();
	        if(com.equals("y") || com.equals("Y")){
	        	continue;
	        }else{
	        	break;
	        }
		}
	}
	
	/**
	 * 对需要查询的代码进行切割;
	 * 取前5行和后5行;
	 * */
	public static List<List<TokenList>> SourceCodeCutting(List<TokenList> code){
		
		//System.out.println(code.get(4).getTokens());
		
		
		List<List<TokenList>> cutCode = new ArrayList<List<TokenList>>();
		
		List<TokenList> frontCode = new ArrayList<TokenList>();
		List<TokenList> backCode = new ArrayList<TokenList>();
		int i = 0;
		for(i = 0;i < code.size();i++){
			if(code.get(i).getTokens().contains("mycursorposition")){
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				break;
			}
		}
		int front = (i - 5) >= 0 ? (i - 5) : 0;
		int back = (i + 5) < code.size() ? (i + 5) : (code.size()-1);
		
		for(int j = front;j < i;j++){
			frontCode.add(code.get(j));
		}
		for(int j = i + 1;j <= back;j++){
			backCode.add(code.get(j));
		}
		
		cutCode.add(frontCode);
		cutCode.add(backCode);
		
		return cutCode;
	}
	
	
	/**
	 * 在计算的过程中计算LC;
	 */
	public static void BWTSearch(SourceCode seed) {
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < codeSnippets.size(); i++) {
			SourceCode is = new SourceCode(codeSnippets.get(i));
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(getSimilarity(seed, is));
			simiList.add(s2c);

			FirstLastRow.clear();
		}
		Collections.sort(simiList);
		int i = 0;
		for (Similarity2ClassIndex s2c : simiList) {
			System.out.println(s2c.getClassId() + "  " + s2c.getSimilarity() + "   " + insFiles[s2c.getClassId()]);
			i++;
			if (i > 11) {
				break;
			}
		}
	}

	/**
	 * 通过第一种方法来计算两个InstructionSequence 之间的相似度; 即在计算的过程中来计算LC Sequence;
	 */
	public static double getSimilarity(SourceCode seed, SourceCode freq) {
		// System.out.println(seed.getFileName());
		// 正序查找;
		getFirstLastRow(new SourceCode(freq));
		List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));

		double s1 = (BWTSimilarity(firstRowMap, lastRowMap, seed.getCodes()) + 1) / seed.getCodes().size();
		firstRowMap.clear();
		lastRowMap.clear();
		FirstLastRow.clear();

		return (s1);
	}

	/**
	 * BWT 字串匹配相似度计算算法实现;
	 */
	public static double BWTSimilarity(List<Integer> firstRowMap, List<Integer> lastRowMap, List<TokenList> seedList) {
		double similarScore = 0.0;
		double threshold = 0.0;
		List<Integer> startPointList = null;
		for (int i = 0; i < 3; i++) {
			if (seedList.size() - 1 - i < 0) {
				break;
			}
			startPointList = getStartPoint(seedList.get(seedList.size() - 1 - i));
			if (startPointList != null && !startPointList.isEmpty()) {
				break;
			}
		}
		if (startPointList == null || startPointList.isEmpty()) {
			return 0.0;
		}
		if (startPointList.contains(-1)) {
			return 0.0;
		}

		// System.out.println("startPointList: " + startPointList);
		for (int i = 0; i < startPointList.size(); i++) {
			int startPoint = startPointList.get(i);
			// System.out.println("startPointList: " + startPointList.get(i));
			double tempSimilarity = 0.0;
			TokenList freqOp = null;
			TokenList freqOp1 = null;
			threshold = 0;
			for (int j = seedList.size() - 2; (j > -1) && (threshold < 3); j--) {
				// System.out.println(i + " " + j);
				double dtemp = -1.0;
				TokenList seedOp = seedList.get(j);
				List<TokenList> freqTokenList = BurrowsWheelerTransform(firstRowMap, lastRowMap, startPoint, 2);
				if (freqTokenList == null) {
					break;
				} else if (freqTokenList.size() > 1) {
					freqOp = freqTokenList.get(0);
					freqOp1 = freqTokenList.get(1);
					// System.out.println("freqOpList: " + freqOpList.size());
				} else if (freqTokenList.size() > 0) {
					freqOp = freqTokenList.get(0);
					// System.out.println("freqOpList: " + freqOpList.size());
				} else {
					// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					break;
				}

				double s1 = 0.0;
				double s2 = 0.0;
				if (isEquals(freqOp, seedOp)) {
					tempSimilarity += getSimilarityBetweenLine2(seedOp, freqOp);
				} else if (freqOp1 != null) {
					s1 = getSimilarityBetweenLine2(seedOp, freqOp);
					s2 = getSimilarityBetweenLine2(seedOp, freqOp1);
					s2 -= 0.2;
					dtemp = s1 > s2 ? s1 : s2;
					tempSimilarity += dtemp;
					// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				} else {
					tempSimilarity += getSimilarityBetweenLine2(seedOp, freqOp);
				}

				if (dtemp == s2) {
					threshold += 1;
					int temp = getFirstIndex(firstRowMap, freqOp.getTokens().get(0), lastRowMap.get(startPoint));
					startPoint = getFirstIndex(firstRowMap, freqOp1.getTokens().get(0), lastRowMap.get(temp));
					if (startPoint == -1) {
						break;
					}
				} else {
					startPoint = getFirstIndex(firstRowMap, freqOp.getTokens().get(0), lastRowMap.get(startPoint));
					if (startPoint == -1) {
						break;
					}
				}
			}
			// System.out.println(tempSimilarity);
			if (tempSimilarity > similarScore) {
				similarScore = tempSimilarity;
			}
		}
		return similarScore;
	}

	/**
	 * 判断两个TokenList 是否相等;
	 * */
	public static boolean isEquals(TokenList list1, TokenList list2) {
		if (getSimilarityBetweenLine2(list1, list2) > 0.8) {
			return true;
		}
		return false;
	}

	/**
	 * 计算两个TokenList 的相似度;
	 * */
	public static double getSimilarityBetweenLine(TokenList list1, TokenList list2) {
		Set<String> set1 = new HashSet<String>(list1.getTokens());
		Set<String> set2 = new HashSet<String>(list2.getTokens());

		Set<String> intersection = new HashSet<String>();
		intersection.addAll(set1);
		intersection.retainAll(set2);

		Set<String> union = new HashSet<String>();
		union.addAll(set1);
		union.addAll(set2);
		/*
		System.out.println("!!!!!!!!!!!!!!!!!" + 1.0 * intersection.size() / union.size() + " " + intersection.size() + " " + union.size());
		if(1.0 * intersection.size() / union.size() == 0){
			for(String s : set1){
				System.out.print(s + " ");
			}
			System.out.print(" +++++++++++++++++++++++++++++++++++++++++++ ");
			for(String s : list1.getTokens()){
				System.out.print(s + " ");
			}
			System.out.println("");
			for(String s : set2){
				System.out.print(s + " ");
			}
			System.out.print(" +++++++++++++++++++++++++++++++++++++++++++ ");
			for(String s : list2.getTokens()){
				System.out.print(s + " ");
			}
			System.out.println("");
		}
		*/
		return (1.0 * intersection.size() / union.size());
	}

	/**
	 * 计算两个TokenList 的相似度;
	 * */
	public static double getSimilarityBetweenLine2(TokenList list1, TokenList list2) {
		
		if(list2.getTokens().containsAll(list1.getTokens())){
			return 1.0;
		}
		
		int length1 = list1.getTokens().size();
		int length2 = list2.getTokens().size();
		Set<String> set1 = new HashSet<String>(list1.getTokens());
		int k1 =  2;
		double b = 0.75;
		double score = 0.0;
		int avgdl = 10;
		for(String s : set1){
			int count1 = Collections.frequency(list1.getTokens(), s);
			//double weight = Math.log(1.0*length1/count1);
			double weight = 1.0*count1/length1;
			int f1 = Collections.frequency(list2.getTokens(), s);
			double r = f1 * (k1 + 1) / (f1 * (k1 + 0.85) + (1 - b + 1.0 * b * length2 / avgdl));
			score += weight * r;
			//System.out.println("!!!!!!!!!!" + count1 + " " + weight + " " + f1 + " " + r + " " + score);
		}		
		return score;
	}
	
	
	/**
	 * 获取seed在freq中的开始位置列表;
	 */
	public static List<Integer> getStartPoint(TokenList seedLast) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < FirstLastRow.get(0).size(); i++) {
			if (FirstLastRow.get(0).get(i).getTokens().get(0).equals(seedLast.getTokens().get(0))) {
				list.add(i);
			}
		}
		return list;
	}

	/**
	 * BWT 算法实现; startPosition 表示LastList中开始的位置; 如果想要复现原串 startPosition = 0;N =
	 * FirstLastRow.get(1).size()-1;
	 */
	public static List<TokenList> BurrowsWheelerTransform(List<Integer> firstRowMap, List<Integer> lastRowMap,
			int startPosition, int N) {
		int Lindex = startPosition;
		int Findex = 0;
		String preToken = null;
		int preNum = 0;
		List<TokenList> list = new ArrayList<TokenList>();

		for (int i = 0; (i < N) && (FirstLastRow.get(1).size() > Lindex)
				&& !(FirstLastRow.get(1).get(Lindex).getTokens().get(0).equals(" ")); i++) {
			list.add(FirstLastRow.get(1).get(Lindex));
			// System.out.println("List add: " +
			// FirstLastRow.get(1).get(Lindex).getCodeId());
			preToken = FirstLastRow.get(1).get(Lindex).getTokens().get(0);
			preNum = lastRowMap.get(Lindex);
			Findex = getFirstIndex(firstRowMap, preToken, preNum);
			// System.out.println("Findex: " + Findex);
			if (Findex == -1) {
				System.out.println("索引错误");
				break;
			}
			Lindex = Findex;
		}
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// for(TokenList tl : list){
		// for(String s : tl.getTokens())
		// System.out.print(s + " ");
		// System.out.println();
		// }

		return list;
	}

	/**
	 * 计算FirstList和LastList 中的每个元素是第几次出现;
	 */
	public static List<Integer> mapRows(List<TokenList> row) {
		List<Integer> list = new ArrayList<Integer>();
		Map<String, Integer> RowMap = new HashMap<String, Integer>();
		// int i = 0;
		for (TokenList tl : row) {
			String token = tl.getTokens().get(0);

			if (RowMap.keySet().contains(token)) {
				int value = RowMap.get(token) + 1;
				list.add(value);
				RowMap.replace(token, value);
				// System.out.println("list add: " + value);
			} else {
				RowMap.put(token, 1);
				list.add(1);
				// System.out.println("list add: " + 1);
			}
		}

		return list;
	}

	/**
	 * 得到FirstRow 和 LastRow;
	 */
	public static void getFirstLastRow(SourceCode code) {
		List<SourceCode> scList = new ArrayList<SourceCode>();

		// 添加$符号;
		TokenList tl = new TokenList();
		List<String> templist = new ArrayList<String>();
		templist.add(" ");
		tl.setTokens(templist);
		List<TokenList> tll = code.getCodes();
		tll.add(0, tl);
		code.setCodes(tll);

		SourceCode locaCode = new SourceCode(code);
		for (int i = 0; i < code.getCodes().size(); i++) {
			SourceCode temp = new SourceCode(locaCode);
			List<TokenList> tempTokenList = temp.getCodes();
			TokenList tempToken = new TokenList(tempTokenList.get(tempTokenList.size() - 1));
			tempTokenList.remove(tempTokenList.size() - 1);
			tempTokenList.add(0, tempToken);
			temp.setCodes(tempTokenList);

			scList.add(temp);

			locaCode = temp;
		}

		// for(SourceCode sc : scList){
		// for(TokenList temptl : sc.getCodes()){
		// for(String s : temptl.getTokens()){
		// System.out.print(s + " ");
		// }
		// }
		// System.out.println();
		// }

		// 排序;
		Collections.sort(scList);
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// for(SourceCode sc : scList){
		// for(TokenList temptl : sc.getCodes()){
		// for(String s : temptl.getTokens()){
		// System.out.print(s + " ");
		// }
		// }
		// System.out.println();
		// }

		List<TokenList> firstRow = new ArrayList<TokenList>();
		List<TokenList> lastRow = new ArrayList<TokenList>();
		for (SourceCode ins : scList) {
			firstRow.add(ins.getCodes().get(0));
			lastRow.add(ins.getCodes().get(ins.getCodes().size() - 1));
		}
		FirstLastRow.add(firstRow);
		FirstLastRow.add(lastRow);
	}

	/**
	 * 根据LastList中的第几次出现的某个元素的位置,返回对应的FirstList中的索引;
	 */
	public static int getFirstIndex(List<Integer> firstRowMap, String preLine, int preNum) {
		for (int i = 0; i < firstRowMap.size(); i++) {
			if ((firstRowMap.get(i) == preNum) && (FirstLastRow.get(0).get(i).getTokens().get(0).equals(preLine))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 读取代码;格式化代码;
	 */
	private static void readCode(String filePath) {
		File directory = new File(filePath);
		insFiles = directory.listFiles();
		for (int i = 0; i < insFiles.length; i++) {
			List<TokenList> linecode = readCodeFromFile(filePath + insFiles[i].getName());
			SourceCode sc = new SourceCode();
			sc.setCodes(linecode);
			sc.setId(i);
			sc.setName(insFiles[i].getName());
			codeSnippets.add(sc);

			if(i > 2000000)
				break;
		}
	}

	/**
	 * 从文件中读取代码;
	 */
	private static List<TokenList> readCodeFromFile(String path) {
		List<TokenList> code = new ArrayList<TokenList>();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			fis = new FileInputStream(path);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				TokenList tl = new TokenList();
				tl.setTokens(getTokenList(str));
				if (!tl.getTokens().isEmpty())
					code.add(tl);
			}
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return code;
	}

	/**
	 * 将源码转为词的集合; 根据驼峰规则和下划线分词;
	 */
	private static List<String> getTokenList(String code) {
		List<String> list = new ArrayList<String>();
		String[] tokens = code.replaceAll("\\{|\\}|\\(|\\)|\\,|;|=|\\\"|\\.|!|&|\\|", " ").split(" ");
		for (String t : tokens) {
			if (t != null && !t.equals("") && !t.equals(" ")) {
				list.addAll(fotmatToken(t));
			}
		}
		return list;
	}

	/**
	 * 并将其同义转换为小写; 并去掉末尾的数字;
	 */
	private static List<String> fotmatToken(String code) {

		// 去掉末尾的数字;
		char[] codes = code.toCharArray();
		for (int i = codes.length - 1; i >= 0; i--) {
			if (codes[i] <= '9' && codes[i] >= '0') {
				codes[i] = '_';
			} else {
				break;
			}
		}
		String token = new String(codes);

		// 分词和大小写转换;
		List<String> list = new ArrayList<String>();
		if (token.contains("_")) {
			String[] words = token.split("_");
			Collections.addAll(list, words);
		} else {
			list.add(token);
		}

		// 分词;
		list = splitWord(list);

		return list;
	}

	/**
	 * 根据驼峰规则和下划线分词;
	 */
	private static List<String> splitWord(List<String> list) {
		List<String> addList = new ArrayList<String>();
		List<String> removeList = new ArrayList<String>();
		for (String word : list) {
			int location = 0;
			int flag = 0;
			for (int i = 0; i < word.length(); i++) {
				char c = word.charAt(i);
				if (Character.isUpperCase(c)) {
					// if((i+1 < word.length() &&
					// !Character.isUpperCase(word.charAt(i+1))) && (i-1 >= 0 &&
					// !Character.isUpperCase(word.charAt(i-1)))){
					if (i - 1 >= 0 && !Character.isUpperCase(word.charAt(i - 1))) {
						String word1 = word.substring(location, i);
						addList.add(word1.toLowerCase());
						location = i;
						removeList.add(word);
						flag = 1;
					}
				}
			}
			if (flag == 1) {
				String word2 = word.substring(location, word.length());
				addList.add(word2.toLowerCase());
			} else {
				addList.add(word.toLowerCase());
				removeList.add(word);
			}
		}
		list.removeAll(removeList);
		list.addAll(addList);
		return list;
	}
}
