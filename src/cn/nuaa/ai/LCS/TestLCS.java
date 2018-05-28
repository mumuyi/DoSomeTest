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
			//��һ�ּ������ķ���;
			
			double s1 = LCSequence(instructions.get(1), instructions.get(i));
			double s2 = LCSString(instructions.get(1), instructions.get(i));
			double s3 = SetComputing(instructions.get(1), instructions.get(i));
			System.out.println();
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(s1 * 0.5 + s2 * 0.3 + s3 * 0.2);
			simiList.add(s2c);
			
			
			//�ڶ��ּ������ķ���;
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
	 * ͨ������ָ�����м��������������������ƶ�;
	 */
	public static double LCSequence(InstructionSequence x, InstructionSequence y) {

		int[][] array = new int[x.getIns().size() + 1][y.getIns().size() + 1];// �˴������̳���Ҫ���ַ������ȶ��1����Ҫ��洢һ��0��һ��0

		for (int j = 0; j < array[0].length; j++) {// ��0�е�j��ȫ����ֵΪ0
			array[0][j] = 0;
		}
		for (int i = 0; i < array.length; i++) {// ��i�У���0��ȫ��Ϊ0
			array[i][0] = 0;
		}

		for (int m = 1; m < array.length; m++) {// ���ö�̬�滮�����鸳��ֵ
			for (int n = 1; n < array[m].length; n++) {
				// if (s1[m - 1] == s2[n - 1]) {
				if (x.getIns().get(m - 1).getCodeId() == y.getIns().get(n - 1).getCodeId()) {
					array[m][n] = array[m - 1][n - 1] + 1;// ��̬�滮��ʽһ
				} else {
					array[m][n] = max(array[m - 1][n], array[m][n - 1]);// ��̬�滮��ʽ��
				}
			}
		}

		// for (int m = 1; m < array.length; m++) {// ���ö�̬�滮�����鸳��ֵ
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
			if (x.getIns().get(i).getCodeId() == y.getIns().get(j).getCodeId()) {// �ַ����Ӻ�ʼ������������ȣ������ջ��
				stack.push(x.getIns().get(i));
				i--;
				j--;
			} else {
				if (array[i + 1][j] > array[i][j + 1]) {// ����ַ������ַ���ͬ����������������ͬ���ַ���ע�⣺���������Ҫ���ַ������ַ��ĸ�����1�����i��jҪ����1
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

		// System.out.println("�����������:");
		// while (!stack.isEmpty()) {// ��ӡ���ջ����������������Ĺ���������
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
	 * ȡ�ϴ���;
	 */
	public static int max(int a, int b) {
		return (a > b) ? a : b;
	}

	/**
	 * ͨ������ָ�����м����ִ������������ƶ�;
	 */
	public static double LCSString(InstructionSequence x, InstructionSequence y) {
		int len1, len2;
		len1 = x.getIns().size();
		len2 = y.getIns().size();
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// ������Ӵ����ȵ�����
		int[] maxIndex = new int[maxLen];// ������Ӵ������������������
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (y.getIns().get(i).getCodeId() == x.getIns().get(j).getCodeId()) {
					if ((i == 0) || (j == 0))
						c[j] = 1;
					else
						c[j] = c[j - 1] + 1;// ��ʱC[j-1]�����ϴ�ѭ���е�ֵ����Ϊ��û�����¸�ֵ
				} else {
					c[j] = 0;
				}

				// ����Ǵ�������ʱֻ��һ�������,����Ҫ�Ѻ������0;
				if (c[j] > max[0]) {
					max[0] = c[j];
					maxIndex[0] = j;

					for (int k = 1; k < maxLen; k++) {
						max[k] = 0;
						maxIndex[k] = 0;
					}
				}
				// �ж������ͬ���ȵ��Ӵ�
				else if (c[j] == max[0]) {
					for (int k = 1; k < maxLen; k++) {
						if (max[k] == 0) {
							max[k] = c[j];
							maxIndex[k] = j;
							break; // �ں����һ����Ҫ�˳�ѭ����
						}
					}
				}
			}
			// for (int temp : c) {
			// System.out.print(temp);
			// }
			// System.out.println();
		}
		// ��ӡ����ַ���
		int amount = 0;
		for (j = 0; j < maxLen; j++) {
			int amount1 = 0;
			if (max[j] > 0) {
				// System.out.println("����������Ӵ� "+(j+1)+":");
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
	 * ���ļ��ж�ȡOpCode;
	 */
	public static void getOpCodeFromFile() {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû�С�
		try {
			String str = "";
			fis = new FileInputStream("F:\\Java\\DoSomeTest\\OpCode\\opcode.txt");// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
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
	}

	/**
	 * ͨ��OpCode��Name��ȡ����ID;
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
	 * �ӷ������ļ�/ָ���ļ��л�ȡ����;
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
	 * ���㼯�ϼ�����ƶ�;
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
	 * ����API�������ü�����ƶ�;
	 */
	public static double getAPISimilarity(String s1, String s2) {
		List<String> list1 = getAPIMethod(s1);
		List<String> list2 = getAPIMethod(s2);
		// �ж��Ƿ�Ϊ��;
		if (list1.isEmpty() || list2.isEmpty()) {
			return -1.0;
		}

		// �жϷ�������·���Ƿ���ͬ;
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
	 * NOM ��ʾ���ݲɼ�����,�ⲻ��һ����ȷ�ĵ���; NoTrace ��ʾû�з�������·��,���е��õķ���; "<init>"
	 * ������Ϊ�����ʾ�˷���Ϊ��ʼ������;
	 * 
	 * ���ص�List��ֻ����2��: 0���ʾ�˷����ĵ���·��;1���ʾ�˷����ķ�����;
	 * 
	 * ��ʵ���ܳ����֮���,������ʱ�����ȡ;
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
	 * ȡ�������е���С��;
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
	 * ��ȡ����֮���DTW����;
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
