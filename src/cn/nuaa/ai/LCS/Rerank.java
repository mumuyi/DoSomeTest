package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rerank {
	public static void main(String[] args) {
		// splitWord("asdhjakshDksjahdkjwqhAdsfdsfdsfds1111111111111111111");
		List<String> tokens1 = getTokenList("F:\\data\\jarFiles\\Top10000\\methodbody\\1.txt");
		List<String> tokens2 = getTokenList("F:\\data\\jarFiles\\Top10000\\methodbody\\2.txt");
		getContentSimilarity(tokens1, tokens2);
	}

	public static void getContentSimilarity(List<String> tokens1, List<String> tokens2) {
		Set<String> set1 = new HashSet<String>(tokens1);
		Set<String> set2 = new HashSet<String>(tokens2);

		Set<String> intersection = new HashSet<String>();
		intersection.addAll(set1);
		intersection.retainAll(set2);

		Set<String> union = new HashSet<String>();
		union.addAll(set1);
		union.addAll(set2);

		for(String s : intersection)
			System.out.print(s + " ");
		System.out.println();
		
		System.out.println("Set Similarity: " + 1.0 * intersection.size() / union.size());
	}

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

	public static StringBuffer readCodeFromFile(String path) {
		StringBuffer code = new StringBuffer();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû�С�
		try {
			String str = "";
			fis = new FileInputStream(path);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
			while ((str = br.readLine()) != null) {
				code.append(str + " ");
			}
		} catch (FileNotFoundException e) {
			System.out.println("�Ҳ���ָ���ļ�");
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ��");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// �رյ�ʱ����ð����Ⱥ�˳��ر���󿪵��ȹر������ȹ�s,�ٹ�n,����m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return code;
	}

	/**
	 * �����շ������»��߷ִ�; ������ͬ��ת��ΪСд; ��ȥ��ĩβ������;
	 */
	public static List<String> splitWord(String code) {

		// ȥ��ĩβ������;
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

		// �ִʺʹ�Сдת��;
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
