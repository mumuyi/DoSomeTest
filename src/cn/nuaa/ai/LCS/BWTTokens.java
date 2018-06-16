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
import java.util.List;
import java.util.Map;

public class BWTTokens {
	private static List<TokenList> codeSnippets = new ArrayList<TokenList>();
	private static List<List<String>> FirstLastRow = new ArrayList<List<String>>();
	private static File[] insFiles;

	public static void main(String[] args) {

		readCode("F:\\data\\jarFiles\\Top100000N\\methodbody\\");
		System.out.println("read in process finished");

		// for(TokenList ls : codeSnippets){
		// for(String s : ls.getTokens()){
		// System.out.print(s + " ");
		// }
		// System.out.println();
		// }

		// getFirstLastRow(codeSnippets.get(0));
		// List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		// List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));
		// BurrowsWheelerTransform(firstRowMap,lastRowMap,0,FirstLastRow.get(1).size()-1);

		// System.out.println(getSimilarity(codeSnippets.get(0),codeSnippets.get(0)));

		BWTSearch(codeSnippets.get(666));
	}

	/**
	 * 在计算的过程中计算LC;
	 */
	public static void BWTSearch(TokenList seed) {
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < codeSnippets.size(); i++) {
			TokenList is = new TokenList(codeSnippets.get(i));
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
	public static double getSimilarity(TokenList seed, TokenList freq) {
		// System.out.println(seed.getFileName());
		// 正序查找;
		getFirstLastRow(new TokenList(freq));
		List<Integer> firstRowMap = mapRows(FirstLastRow.get(0));
		List<Integer> lastRowMap = mapRows(FirstLastRow.get(1));

		double s1 = (BWTSimilarity(firstRowMap, lastRowMap, seed) + 1) / seed.getTokens().size();
		firstRowMap.clear();
		lastRowMap.clear();
		FirstLastRow.clear();

		return (s1);
	}

	/**
	 * BWT 字串匹配相似度计算算法实现;
	 */
	public static double BWTSimilarity(List<Integer> firstRowMap, List<Integer> lastRowMap, TokenList seedList) {
		double similarScore = 0.0;
		double threshold = 0.0;
		List<Integer> startPointList = null;
		for (int i = 0; i < 3; i++) {
			if (seedList.getTokens().size() - 1 - i < 0) {
				break;
			}
			startPointList = getStartPoint(seedList.getTokens().get(seedList.getTokens().size() - 1 - i));
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
			String freqOp = null;
			String freqOp1 = null;
			threshold = 0;
			for (int j = seedList.getTokens().size() - 2; (j > -1) && (threshold < 3); j--) {
				// System.out.println(i + " " + j);
				double dtemp = -1.0;
				String seedOp = seedList.getTokens().get(j);
				List<String> freqTokenList = BurrowsWheelerTransform(firstRowMap, lastRowMap, startPoint, 2);
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
				if (freqOp.equals(seedOp)) {
					tempSimilarity += 1;
				} else if (freqOp1 != null) {
					s1 = getSimilarityBetweenTokens(seedOp, freqOp);
					s2 = getSimilarityBetweenTokens(seedOp, freqOp1);
					s2 -= 0.2;
					dtemp = s1 > s2 ? s1 : s2;
					tempSimilarity += dtemp;
					// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				} else {
					tempSimilarity += getSimilarityBetweenTokens(seedOp, freqOp);
				}

				if (dtemp == s2) {
					threshold += 1;
					int temp = getFirstIndex(firstRowMap, freqOp, lastRowMap.get(startPoint));
					startPoint = getFirstIndex(firstRowMap, freqOp1, lastRowMap.get(temp));
					if (startPoint == -1) {
						break;
					}
				} else {
					startPoint = getFirstIndex(firstRowMap, freqOp, lastRowMap.get(startPoint));
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
	 * 计算两个方法名之间的相似度;
	 */
	public static double getSimilarityBetweenTokens(String name1, String name2) {
		// 正序;
		int counter = 0;
		for (int i = 0; i < name1.length() && i < name2.length(); i++) {
			if (name1.charAt(i) == name2.charAt(i)) {
				counter++;
			}
		}
		double s1 = 1.0 * counter / (name1.length() < name2.length() ? name1.length() : name2.length());
		// 逆序;
		counter = 0;
		for (int i = name1.length() - 1, j = name2.length() - 1; i >= 0 && j >= 0; i--, j--) {
			if (name1.charAt(i) == name2.charAt(j)) {
				counter++;
			}
		}
		double s2 = 1.0 * counter / (name1.length() < name2.length() ? name1.length() : name2.length());
		return s1 > s2 ? s1 : s2;
	}

	/**
	 * 获取seed在freq中的开始位置列表;
	 */
	public static List<Integer> getStartPoint(String seedLast) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < FirstLastRow.get(0).size(); i++) {
			if (FirstLastRow.get(0).get(i).equals(seedLast)) {
				list.add(i);
			}
		}
		return list;
	}

	/**
	 * BWT 算法实现; startPosition 表示LastList中开始的位置; 如果想要复现原串 startPosition = 0;N =
	 * FirstLastRow.get(1).size()-1;
	 */
	public static List<String> BurrowsWheelerTransform(List<Integer> firstRowMap, List<Integer> lastRowMap,
			int startPosition, int N) {
		int Lindex = startPosition;
		int Findex = 0;
		String preToken = null;
		int preNum = 0;
		List<String> list = new ArrayList<String>();

		for (int i = 0; (i < N) && (FirstLastRow.get(1).size() > Lindex)
				&& !(FirstLastRow.get(1).get(Lindex).equals("AA")); i++) {
			list.add(FirstLastRow.get(1).get(Lindex));
			// System.out.println("List add: " +
			// FirstLastRow.get(1).get(Lindex).getCodeId());
			preToken = FirstLastRow.get(1).get(Lindex);
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
		// System.out.println(list.size());
		// for(String tl : list){
		// System.out.print(tl + " ");
		// }

		return list;
	}

	/**
	 * 计算FirstList和LastList 中的每个元素是第几次出现;
	 */
	public static List<Integer> mapRows(List<String> row) {
		List<Integer> list = new ArrayList<Integer>();
		Map<String, Integer> RowMap = new HashMap<String, Integer>();
		// int i = 0;
		for (String tl : row) {
			String token = tl;

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
	public static void getFirstLastRow(TokenList code) {
		List<TokenList> scList = new ArrayList<TokenList>();

		// 添加$符号;
		List<String> templist = code.getTokens();
		templist.add("AA");
		code.setTokens(templist);

		TokenList locaCode = new TokenList(code);
		for (int i = 0; i < code.getTokens().size(); i++) {
			TokenList temp = new TokenList(locaCode);
			List<String> tempTokenList = temp.getTokens();
			String tempToken = tempTokenList.get(tempTokenList.size() - 1);
			tempTokenList.remove(tempTokenList.size() - 1);
			tempTokenList.add(0, tempToken);
			temp.setTokens(tempTokenList);

			scList.add(temp);

			locaCode = temp;
		}

		// 排序;
		Collections.sort(scList);

		List<String> firstRow = new ArrayList<String>();
		List<String> lastRow = new ArrayList<String>();
		for (TokenList ins : scList) {
			firstRow.add(ins.getTokens().get(0));
			lastRow.add(ins.getTokens().get(ins.getTokens().size() - 1));
		}
		FirstLastRow.add(firstRow);
		FirstLastRow.add(lastRow);
	}

	/**
	 * 根据LastList中的第几次出现的某个元素的位置,返回对应的FirstList中的索引;
	 */
	public static int getFirstIndex(List<Integer> firstRowMap, String preLine, int preNum) {
		for (int i = 0; i < firstRowMap.size(); i++) {
			if ((firstRowMap.get(i) == preNum) && (FirstLastRow.get(0).get(i).equals(preLine))) {
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
			List<String> list = readCodeFromFile(filePath + insFiles[i].getName());
			if (!list.isEmpty()) {
				TokenList code = new TokenList();
				code.setId(i);
				code.setName(insFiles[i].getName());
				code.setTokens(list);
				codeSnippets.add(code);
			}
			// break;
		}
	}

	/**
	 * 从文件中读取代码;
	 */
	private static List<String> readCodeFromFile(String path) {
		List<String> list = null;
		String buffer = "";
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
				buffer += (str + " ");
			}

			list = getTokenList(buffer);

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
		return list;
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
