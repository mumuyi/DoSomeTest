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
	 * �ڼ���Ĺ����м���LC;
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
	 * ͨ����һ�ַ�������������InstructionSequence ֮������ƶ�; ���ڼ���Ĺ�����������LC Sequence;
	 */
	public static double getSimilarity(TokenList seed, TokenList freq) {
		// System.out.println(seed.getFileName());
		// �������;
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
	 * BWT �ִ�ƥ�����ƶȼ����㷨ʵ��;
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
	 * ��������������֮������ƶ�;
	 */
	public static double getSimilarityBetweenTokens(String name1, String name2) {
		// ����;
		int counter = 0;
		for (int i = 0; i < name1.length() && i < name2.length(); i++) {
			if (name1.charAt(i) == name2.charAt(i)) {
				counter++;
			}
		}
		double s1 = 1.0 * counter / (name1.length() < name2.length() ? name1.length() : name2.length());
		// ����;
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
	 * ��ȡseed��freq�еĿ�ʼλ���б�;
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
	 * BWT �㷨ʵ��; startPosition ��ʾLastList�п�ʼ��λ��; �����Ҫ����ԭ�� startPosition = 0;N =
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
				System.out.println("��������");
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
	 * ����FirstList��LastList �е�ÿ��Ԫ���ǵڼ��γ���;
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
	 * �õ�FirstRow �� LastRow;
	 */
	public static void getFirstLastRow(TokenList code) {
		List<TokenList> scList = new ArrayList<TokenList>();

		// ���$����;
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

		// ����;
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
	 * ����LastList�еĵڼ��γ��ֵ�ĳ��Ԫ�ص�λ��,���ض�Ӧ��FirstList�е�����;
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
	 * ��ȡ����;��ʽ������;
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
	 * ���ļ��ж�ȡ����;
	 */
	private static List<String> readCodeFromFile(String path) {
		List<String> list = null;
		String buffer = "";
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
				buffer += (str + " ");
			}

			list = getTokenList(buffer);

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
		return list;
	}

	/**
	 * ��Դ��תΪ�ʵļ���; �����շ������»��߷ִ�;
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
	 * ������ͬ��ת��ΪСд; ��ȥ��ĩβ������;
	 */
	private static List<String> fotmatToken(String code) {

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

		// �ִʺʹ�Сдת��;
		List<String> list = new ArrayList<String>();
		if (token.contains("_")) {
			String[] words = token.split("_");
			Collections.addAll(list, words);
		} else {
			list.add(token);
		}

		// �ִ�;
		list = splitWord(list);

		return list;
	}

	/**
	 * �����շ������»��߷ִ�;
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
