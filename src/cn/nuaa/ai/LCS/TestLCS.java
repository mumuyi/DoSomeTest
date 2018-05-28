package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cn.nuaa.ai.fastdtw.FastDTWTest;

public class TestLCS {

	private static List<OpCode> oplist = new ArrayList<OpCode>();

	private static List<InstructionSequence> instructions = new ArrayList<InstructionSequence>();
	
	private static File[] insFiles;

	public static void main(String[] args) throws Exception {

		
		getOpCodeFromFile();
		getInstructionsFromFile("F:\\data\\instruction\\");
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");
		
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < instructions.size(); i++) {
			//第一种计算距离的方法;
			
			double s1 = LCSequence(instructions.get(1), instructions.get(i));
			double s2 = LCSString(instructions.get(1), instructions.get(i));
			double s3 = SetComputing(instructions.get(1), instructions.get(i));
			System.out.println();
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(s1 * 0.5 + s2 * 0.3 + s3 * 0.2);
			simiList.add(s2c);
			
			
			//第二种计算距离的方法;
			/*
			double distance = FastDTWTest.getMyDTWDistance(instructions.get(1).getIns(),instructions.get(i).getIns());
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(-1.0 * distance);
			simiList.add(s2c);
			*/
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
		 
		// getAPIMethod("org/activiti/engine/impl/test/AbstractTestCase.\"<init>\":()V");
		// System.out.println(getAPISimilarity("org/activiti/engine/impl/test/AbstractTestCase.\"<init>\":()V","java/util/ArrayList.\"<init>\":()V"));
		
		
		//double a[] = new double[] { 10, 11, 30, 11, 30, 11, 10, 10, 10, 10 };
		//double b[] = new double[] { 10, 11, 30, 11, 30, 11, 10, 10, 10, 20};
		//System.out.println(getDTWDistance(a,b));
		
		/*
		try {
			FastDTWTest.getMyDTWDistance(instructions.get(0),instructions.get(1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	/**
	 * 通过两个指令序列间的最长子序列来计算其相似度;
	 */
	public static double LCSequence(InstructionSequence x, InstructionSequence y) {

		int[][] array = new int[x.getIns().size() + 1][y.getIns().size() + 1];// 此处的棋盘长度要比字符串长度多加1，需要多存储一行0和一列0

		for (int j = 0; j < array[0].length; j++) {// 第0行第j列全部赋值为0
			array[0][j] = 0;
		}
		for (int i = 0; i < array.length; i++) {// 第i行，第0列全部为0
			array[i][0] = 0;
		}

		for (int m = 1; m < array.length; m++) {// 利用动态规划将数组赋满值
			for (int n = 1; n < array[m].length; n++) {
				// if (s1[m - 1] == s2[n - 1]) {
				if (x.getIns().get(m - 1).getCodeId() == y.getIns().get(n - 1).getCodeId()) {
					array[m][n] = array[m - 1][n - 1] + 1;// 动态规划公式一
				} else {
					array[m][n] = max(array[m - 1][n], array[m][n - 1]);// 动态规划公式二
				}
			}
		}

		// for (int m = 1; m < array.length; m++) {// 利用动态规划将数组赋满值
		// for (int n = 1; n < array[m].length; n++) {
		// System.out.print(array[m][n]+" ");
		// }
		// System.out.println();
		// }

		Stack<OpCode> stack = new Stack<OpCode>();
		int i = x.getIns().size() - 1;
		int j = y.getIns().size() - 1;

		double cost = 0.0;
		int amount = 0;
		while ((i >= 0) && (j >= 0)) {
			if (x.getIns().get(i).getCodeId() == y.getIns().get(j).getCodeId()) {// 字符串从后开始遍历，如若相等，则存入栈中
				stack.push(x.getIns().get(i));
				i--;
				j--;
			} else {
				if (array[i + 1][j] > array[i][j + 1]) {// 如果字符串的字符不同，则在数组中找相同的字符，注意：数组的行列要比字符串中字符的个数大1，因此i和j要各加1
					j--;
				} else {
					i--;
				}
				if ((i >= 0) && (j >= 0)) {
					amount++;
					if ((x.getIns().get(i).getLevle1() == y.getIns().get(j).getLevle1())
							&& (x.getIns().get(i).getLevle2() == y.getIns().get(j).getLevle2())) {
						cost += 0.1;
					} else if ((x.getIns().get(i).getLevle1() == y.getIns().get(j).getLevle1())
							&& (x.getIns().get(i).getLevle2() != y.getIns().get(j).getLevle2())) {
						cost += 0.3;
					} else if ((x.getIns().get(i).getLevle1() != y.getIns().get(j).getLevle1())) {
						cost += 1;
					}
				}
			}
		}

		// System.out.println("最长公共子序列:");
		// while (!stack.isEmpty()) {// 打印输出栈正好是正向输出最大的公共子序列
		// System.out.println(stack.pop().getName());
		// }
		// System.out.println();
		if (amount == 0) {
			System.out.println("LCSequence Similarity: " + 0);
			return 1.0;
		} else {
			System.out.println("LCSequence Similarity: " + (1 - cost / amount));
			return (1 - cost / amount);
		}
	}

	/**
	 * 取较大数;
	 */
	public static int max(int a, int b) {
		return (a > b) ? a : b;
	}

	/**
	 * 通过两个指令序列间的最长字串来计算其相似度;
	 */
	public static double LCSString(InstructionSequence x, InstructionSequence y) {
		int len1, len2;
		len1 = x.getIns().size();
		len2 = y.getIns().size();
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// 保存最长子串长度的数组
		int[] maxIndex = new int[maxLen];// 保存最长子串长度最大索引的数组
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (y.getIns().get(i).getCodeId() == x.getIns().get(j).getCodeId()) {
					if ((i == 0) || (j == 0))
						c[j] = 1;
					else
						c[j] = c[j - 1] + 1;// 此时C[j-1]还是上次循环中的值，因为还没被重新赋值
				} else {
					c[j] = 0;
				}

				// 如果是大于那暂时只有一个是最长的,而且要把后面的清0;
				if (c[j] > max[0]) {
					max[0] = c[j];
					maxIndex[0] = j;

					for (int k = 1; k < maxLen; k++) {
						max[k] = 0;
						maxIndex[k] = 0;
					}
				}
				// 有多个是相同长度的子串
				else if (c[j] == max[0]) {
					for (int k = 1; k < maxLen; k++) {
						if (max[k] == 0) {
							max[k] = c[j];
							maxIndex[k] = j;
							break; // 在后面加一个就要退出循环了
						}
					}
				}
			}
			// for (int temp : c) {
			// System.out.print(temp);
			// }
			// System.out.println();
		}
		// 打印最长子字符串
		int amount = 0;
		for (j = 0; j < maxLen; j++) {
			int amount1 = 0;
			if (max[j] > 0) {
				// System.out.println("最长公共公共子串 "+(j+1)+":");
				for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++) {
					// System.out.println(x.get(i).getName());
					amount1++;
				}
				if (amount1 > amount) {
					amount = amount1;
				}
				// System.out.println(" ");
			}
		}
		// System.out.println(amount);
		System.out.println("LCSString Similarity: " + 1.0 * amount / x.getIns().size());
		return 1.0 * amount / x.getIns().size();
	}

	/**
	 * 从文件中读取OpCode;
	 */
	public static void getOpCodeFromFile() {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			fis = new FileInputStream("F:\\Java\\DoSomeTest\\OpCode\\opcode.txt");// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				OpCode opc = new OpCode();
				String str1[] = str.split(" ");
				opc.setCodeId(Integer.parseInt(str1[0]));
				opc.setCode(str1[1].replaceAll("\\(|\\)", ""));
				opc.setName(str1[2]);
				opc.setLevle1(Integer.parseInt(str1[3]));
				opc.setLevle2(Integer.parseInt(str1[4]));
				oplist.add(opc);
			}
			// for(OpCode op : oplist){
			// System.out.println(op.getCodeId() + " " + op.getCode() + " " +
			// op.getName() + " " + op.getLevle1() + " " + op.getLevle2());
			// }
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
	}

	/**
	 * 通过OpCode的Name获取他的ID;
	 */
	public static int getOpCodeID(String opc) {
		for (OpCode op : oplist) {
			if (opc.equals(op.getName())) {
				return op.getCodeId();
			}
		}
		return -1;
	}
	
	public static OpCode getOpCodeFromID(int id){
		for(OpCode op : oplist){
			if(op.getCodeId() == id)
				return op;
		}
		return null;
	}
	

	/**
	 * 从反编译文件/指令文件中获取数据;
	 */
	public static void getInstructionsFromFile(String filePath) {

		//int i = 0;
		File directory = new File(filePath);
		insFiles = directory.listFiles();
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
					if (getOpCodeID(str) != -1) {
						OpCode op = new OpCode(oplist.get(getOpCodeID(str)));
						list.add(op);
					} else {
						String[] strs = str.split(" ");
						if (strs.length > 1) {
							if (strs[0].equals("invokevirtual") || strs[0].equals("invokespecial")
									|| strs[0].equals("invokestatic") || strs[0].equals("invokeinterface")
									|| strs[0].equals("invokedynamic")) {
								OpCode op = new OpCode(oplist.get(getOpCodeID(strs[0])));
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
			instructions.add(instrs);
			//i++;
			//if(i==2)
			//  break;
		}

		//for (OpCode op : instructions.get(0)) {
		//	System.out.println(op.getName() + " " + op.getInvokedMethod());
		//}
	}

	/**
	 * 计算集合间的相似度;
	 */
	public static double SetComputing(InstructionSequence x, InstructionSequence y) {
		Set<Integer> xSet = new HashSet<Integer>();
		Set<Integer> ySet = new HashSet<Integer>();
		for (OpCode op : x.getIns()) {
			xSet.add(op.getCodeId());
		}
		for (OpCode op : y.getIns()) {
			ySet.add(op.getCodeId());
		}

		Set<Integer> intersection = new HashSet<Integer>();
		intersection.addAll(xSet);
		intersection.retainAll(ySet);

		Set<Integer> union = new HashSet<Integer>();
		union.addAll(xSet);
		union.addAll(ySet);

		// System.out.println(xSet);
		// System.out.println(ySet);
		// System.out.println(intersection);
		// System.out.println(union);

		System.out.println("Set Similarity: " + 1.0 * intersection.size() / union.size());

		return 1.0 * intersection.size() / union.size();
	}

	/**
	 * 计算API方法调用间的相似度;
	 */
	public static double getAPISimilarity(String s1, String s2) {
		List<String> list1 = getAPIMethod(s1);
		List<String> list2 = getAPIMethod(s2);
		// 判断是否为空;
		if (list1.isEmpty() || list2.isEmpty()) {
			return -1.0;
		}

		// 判断方法调用路径是否相同;
		if (list1.get(0).equals(list2.get(0)) && !list1.get(0).equals("NoTrace")) {
			if (list1.get(1).equals(list2.get(1))) {
				return 1.0;
			} else {
				return 0.8;
			}
		} else if (list1.get(0).equals(list2.get(0)) && list1.get(0).equals("NoTrace")) {
			if (list1.get(1).equals(list2.get(1))) {
				return 0.8;
			} else {
				return 0.0;
			}
		} else {
			String[] strs1 = list1.get(0).split("/");
			String[] strs2 = list2.get(0).split("/");
			Set<String> set1 = new HashSet<String>(Arrays.asList(strs1));
			Set<String> set2 = new HashSet<String>(Arrays.asList(strs2));

			Set<String> intersection = new HashSet<String>();
			intersection.addAll(set1);
			intersection.retainAll(set2);

			Set<String> union = new HashSet<String>();
			union.addAll(set1);
			union.addAll(set2);

			double simi = 0.6 * intersection.size() / union.size();

			if (list1.get(1).equals(list2.get(1)) && !list1.get(1).equals("\"<init>\"")) {
				return simi + 0.4;
			} else {
				return simi;
			}
		}
	}

	/**
	 * NOM 表示数据采集错误,这不是一个正确的调用; NoTrace 表示没有方法调用路径,但有调用的方法; "<init>"
	 * 方法名为这个表示此方法为初始化方法;
	 * 
	 * 返回的List中只会有2项: 0项表示此方法的调用路径;1项表示此方法的方法名;
	 * 
	 * 其实是能抽参数之类的,但是暂时不想抽取;
	 */
	public static List<String> getAPIMethod(String str) {
		List<String> list = new ArrayList<String>();
		if ("".equals(str) || str.isEmpty()) {
			return list;
		}
		String[] strs1 = str.split(":");
		if (strs1.length > 1) {
			String[] strs2 = strs1[0].split("\\.");
			if (strs2.length > 1) {
				list.add(strs2[0]);
				list.add(strs2[1]);
			} else {
				list.add("NoTrace");
				list.add(strs2[0]);
			}
		} else {
			list.add("NOM");
			list.add("NOM");
		}
		// for(String s : list){
		// System.out.println(s);
		// }
		return list;
	}

	/**
	 * 取三个数中的最小数;
	 * */
	public static double getMin(double a, double b, double c) {
		double min = a;
		if (b > a)
			min = a;
		else if (c > b) {
			min = b;
		} else {
			min = c;
		}
		return min;
	}

	/**
	 * 获取序列之间的DTW距离;
	 * */
	public static double getDTWDistance(double[] seqa, double[] seqb) {
		double distance = 0;
		int lena = seqa.length;
		int lenb = seqb.length;
		double[][] c = new double[lena][lenb];
		for (int i = 0; i < lena; i++) {
			for (int j = 0; j < lenb; j++) {
				c[i][j] = 1;
			}
		}
		for (int i = 0; i < lena; i++) {
			for (int j = 0; j < lenb; j++) {
				double tmp = (seqa[i] - seqb[j]) * (seqa[i] - seqb[j]);
				if (j == 0 && i == 0)
					c[i][j] = tmp;
				else if (j > 0)
					c[i][j] = c[i][j - 1] + tmp;
				if (i > 0) {
					if (j == 0)
						c[i][j] = tmp + c[i - 1][j];
					else
						c[i][j] = tmp + getMin(c[i][j - 1], c[i - 1][j - 1], c[i - 1][j]);
				}
			}
		}
		distance = c[lena - 1][lenb - 1];
		return distance;
	}

	public static List<OpCode> getOplist() {
		return oplist;
	}

	public static void setOplist(List<OpCode> oplist) {
		TestLCS.oplist = oplist;
	}

	public static List<InstructionSequence> getInstructions() {
		return instructions;
	}

	public static void setInstructions(List<InstructionSequence> instructions) {
		TestLCS.instructions = instructions;
	}

	public static File[] getInsFiles() {
		return insFiles;
	}

	public static void setInsFiles(File[] insFiles) {
		TestLCS.insFiles = insFiles;
	}
	
}
