package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
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
import java.util.Set;

public class Rerank {
	public static void main(String[] args) {
		// splitWord("asdhjakshDksjahdkjwqhAdsfdsfdsfds1111111111111111111");
		List<String> tokens1 = getTokenList("F:\\data\\jarFiles\\Top10000\\methodbody\\1.txt");
		List<String> tokens2 = getTokenList("F:\\data\\jarFiles\\Top10000\\methodbody\\2.txt");
		
		
		getContentSimilarity(tokens1, tokens2);

		getWeightSimilarity(tokens1, tokens2);
		
		System.out.println("list2 " + isContains(tokens2, tokens1));
	}

	/**
	 * Does tokens2 contain tokens1;
	 * 判断token1中的所有元素是否包含于token2;
	 * 在实际用的时候seed 序列是tokens1; freq 序列是tokens2;
	 * */
	public static boolean isContains(List<String> tokens1, List<String> tokens2){
		return tokens2.containsAll(tokens1);
	}
	
	/**
	 * 计算带权重的content similarity;
	 * 这里不确定应该用哪一个做分母 union.size() 或是seed 序列的去重之后的集合的size();
	 * */
	public static double getWeightSimilarity(List<String> tokens1, List<String> tokens2) {
		int number = 0;
		Map<String, Integer> map1 = List2Map(tokens1);
		Map<String, Integer> map2 = List2Map(tokens2);
		for (String s : map1.keySet()) {
			if (map2.keySet().contains(s)) {
				number += (map1.get(s) < map2.get(s) ? map1.get(s) : map2.get(s));
			}
		}

		Set<String> union = new HashSet<String>();
		union.addAll(tokens1);
		union.addAll(tokens2);

		System.out.println("Weight Set Similarity: " + 1.0 * number / union.size());
		//System.out.println("Weight Set Similarity: " + union.size());
		//System.out.println("Weight Set Similarity: " + number);

		return 1.0 * number / union.size();
	}

	/**
	 * 将token list 转化为map; 其中key为list中的tokens, value为其在list中出现的次数;
	 * */
	public static Map<String, Integer> List2Map(List<String> tokens) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String s : tokens) {
			if (map.containsKey(s)) {
				map.replace(s, map.get(s) + 1);
			} else {
				map.put(s, 1);
			}
		}
		//for (String s : map.keySet()) {
		//	System.out.println(s + " " + map.get(s));
		//}

		return map;
	}

	/**
	 * 计算连个代码之间的词的相似度;
	 */
	public static double getContentSimilarity(List<String> tokens1, List<String> tokens2) {
		Set<String> set1 = new HashSet<String>(tokens1);
		Set<String> set2 = new HashSet<String>(tokens2);

		Set<String> intersection = new HashSet<String>();
		intersection.addAll(set1);
		intersection.retainAll(set2);

		Set<String> union = new HashSet<String>();
		union.addAll(set1);
		union.addAll(set2);

		// for(String s : intersection)
		// System.out.print(s + " ");
		// System.out.println();

		System.out.println("Set Similarity: " + 1.0 * intersection.size() / union.size());

		return 1.0 * intersection.size() / union.size();
	}

	/**
	 * 将源码转为词的集合;
	 */
	public static List<String> getTokenList(String path) {
		List<String> list = new ArrayList<String>();
		String code = readCodeFromFile(path).toString();
		String[] tokens = code.replaceAll("\\{|\\}|\\(|\\)|\\,|;|=|\\\"|\\.", " ").split(" ");
		for (String t : tokens) {
			if (t != null && !t.equals("") && !t.equals(" ")) {
				// System.out.print(t + " ");
				list.addAll(splitWord(t));
			}
		}
		// System.out.println();
		// for(String t : list){
		// System.out.print(t + " ");
		// }
		// System.out.println(list.size());
		return list;
	}

	/**
	 * 从文件中读取代码;
	 */
	public static StringBuffer readCodeFromFile(String path) {
		StringBuffer code = new StringBuffer();
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
				code.append(str + " ");
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
	 * 根据驼峰规则和下划线分词; 并将其同义转换为小写; 并去掉末尾的数字;
	 */
	public static List<String> splitWord(String code) {

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
		// System.out.println(token);

		// 分词和大小写转换;
		List<String> list = new ArrayList<String>();
		if (token.contains("_")) {
			String[] words = token.split("_");
			Collections.addAll(list, words);
		} else {
			list.add(token);
		}

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

		// for(String s : list){
		// System.out.println(s);
		// }

		return list;
	}
}
