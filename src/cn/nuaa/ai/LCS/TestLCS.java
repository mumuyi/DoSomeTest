package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class TestLCS {
	
	private static List<OpCode> oplist = new ArrayList<OpCode>();
	
	private static List<List<OpCode>> instructions = new ArrayList<List<OpCode>>();
	
	public static void main(String[] args) {
		//String x = "213789217389127389";
		//String y = "1298748912738912739";
		//LCSequence(x, y);
		//System.out.println();
		//LCSString(x,y);
		
		getOpCodeFromFile();
		getInstructions();
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for(int i = 0;i<instructions.size();i++){
			double s1 = LCSequence(instructions.get(2),instructions.get(i));
			double s2 = LCSString(instructions.get(2),instructions.get(i));
			double s3 = SetComputing(instructions.get(2),instructions.get(i));
			System.out.println();
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(s1*0.5+s2*0.3+s3*0.2);
			simiList.add(s2c);
		}
		Collections.sort(simiList);
		int i = 0;
		for(Similarity2ClassIndex s2c : simiList){
			System.out.println(s2c.getClassId()+"  " + s2c.getSimilarity());
			i++;
			if(i > 11){
				break;
			}
		}
	}

	public static double LCSequence(List<OpCode> x, List<OpCode> y) {

		int[][] array = new int[x.size() + 1][y.size() + 1];// �˴������̳���Ҫ���ַ������ȶ��1����Ҫ��洢һ��0��һ��0

		for (int j = 0; j < array[0].length; j++) {// ��0�е�j��ȫ����ֵΪ0
			array[0][j] = 0;
		}
		for (int i = 0; i < array.length; i++) {// ��i�У���0��ȫ��Ϊ0
			array[i][0] = 0;
		}

		for (int m = 1; m < array.length; m++) {// ���ö�̬�滮�����鸳��ֵ
			for (int n = 1; n < array[m].length; n++) {
				//if (s1[m - 1] == s2[n - 1]) {
				if (x.get(m - 1).getCodeId() == y.get(n - 1).getCodeId()) {
					array[m][n] = array[m - 1][n - 1] + 1;// ��̬�滮��ʽһ
				} else {
					array[m][n] = max(array[m - 1][n], array[m][n - 1]);// ��̬�滮��ʽ��
				}
			}
		}
		
		
		//for (int m = 1; m < array.length; m++) {// ���ö�̬�滮�����鸳��ֵ
		//	for (int n = 1; n < array[m].length; n++) {
		//		System.out.print(array[m][n]+" ");
		//	}
		//	System.out.println();
		//}
		
		
		Stack<OpCode> stack = new Stack<OpCode>();
		int i = x.size() - 1;
		int j = y.size() - 1;

		double cost = 0.0; 
		int amount = 0;
		while ((i >= 0) && (j >= 0)) {
			if (x.get(i).getCodeId() == y.get(j).getCodeId()) {// �ַ����Ӻ�ʼ������������ȣ������ջ��
				stack.push(x.get(i));
				i--;
				j--;
			} else {
				if (array[i + 1][j] > array[i][j + 1]) {// ����ַ������ַ���ͬ����������������ͬ���ַ���ע�⣺���������Ҫ���ַ������ַ��ĸ�����1�����i��jҪ����1
					j--;
				} else {
					i--;
				}
				if((i >= 0) && (j >= 0)){
					amount++;
					if((x.get(i).getLevle1() == y.get(j).getLevle1())&&(x.get(i).getLevle2() == y.get(j).getLevle2())){
						cost += 0.1;
					}else if((x.get(i).getLevle1() == y.get(j).getLevle1())&&(x.get(i).getLevle2() != y.get(j).getLevle2())){
						cost += 0.3;
					}else if((x.get(i).getLevle1() != y.get(j).getLevle1())){
						cost += 1;
					}
				}
			}
		}

		//System.out.println("�����������:");
		//while (!stack.isEmpty()) {// ��ӡ���ջ����������������Ĺ���������
		//	System.out.println(stack.pop().getName());
		//}
		//System.out.println();
		if(amount == 0){
			System.out.println("LCSequence Similarity: " + 0);
			return 1.0;
		}else{
			System.out.println("LCSequence Similarity: " + (1-cost/amount));
			return (1-cost/amount);
		}
	}

	public static int max(int a, int b) {// �Ƚ�(a,b)��������ֵ
		return (a > b) ? a : b;
	}

	public static double LCSString(List<OpCode> x, List<OpCode> y) {
		int len1, len2;
		len1 = x.size();
		len2 = y.size();
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// ������Ӵ����ȵ�����
		int[] maxIndex = new int[maxLen];// ������Ӵ������������������
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (y.get(i).getCodeId() == x.get(j).getCodeId()) {
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
			//for (int temp : c) {
			//	  System.out.print(temp);
			//}
			//System.out.println();
		}
		// ��ӡ����ַ���
		int amount = 0;
		for (j = 0; j < maxLen; j++) {
			int amount1 = 0;
			if (max[j] > 0) {
				//System.out.println("����������Ӵ� "+(j+1)+":");
				for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++){
					//System.out.println(x.get(i).getName());
					amount1++;
				}
				if(amount1 > amount){
					amount = amount1;
				}
				//System.out.println(" ");
			}
		}
		//System.out.println(amount);
		System.out.println("LCSString Similarity: " + 1.0*amount/x.size());
		return 1.0*amount/x.size();
	}
	
	
	public static void getOpCodeFromFile(){
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû�С�
		try {
			String str = "";
			fis = new FileInputStream("C:\\Users\\ai\\Desktop\\opcode.txt");// FileInputStream
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
			//for(OpCode op : oplist){
			//	System.out.println(op.getCodeId() + " " + op.getCode() + " " + op.getName() + " " + op.getLevle1() + " " + op.getLevle2());
			//}
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
	
	public static int getOpCodeID(String opc){
		for(OpCode op : oplist){
			if(opc.equals(op.getName())){
				return op.getCodeId();
			}
		}
		return -1;
	}
	
	
	// �ӷ�������ļ��г�ȡ��Ҫ����Ϣ;
	private static void getInstructions() {

		//int i = 0;
		File directory = new File("F:\\data\\instruction\\");
		File[] files = directory.listFiles();
		for (File file : files) {
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			String filename = file.getName();
			List<OpCode> list = new ArrayList<OpCode>();
			try {
				String str = "";
				fis = new FileInputStream("F:\\data\\instruction\\" + filename);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + filename);
				while ((str = br.readLine()) != null) {
					//System.out.println(str);
					//System.out.println(getOpCodeID(str));
					if(getOpCodeID(str) != -1){
						list.add(oplist.get(getOpCodeID(str)));
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
			instructions.add(list);
			//i++;
			//if(i==2)
			//	break;
		}
		
		
		//for(OpCode op : instructions.get(0)){
		//	System.out.println(op.getName());
		//}
	}
	
	public static double SetComputing(List<OpCode> x, List<OpCode> y){
		Set<Integer> xSet = new HashSet<Integer>(); 
		Set<Integer> ySet = new HashSet<Integer>(); 
		for(OpCode op : x){
			xSet.add(op.getCodeId());
		}
		for(OpCode op : y){
			ySet.add(op.getCodeId());
		}

		Set<Integer> intersection = new HashSet<Integer>();
		intersection.addAll(xSet);
		intersection.retainAll(ySet);
		
		Set<Integer>  union = new HashSet<Integer>();
		union.addAll(xSet);
		union.addAll(ySet);
		
		
		
		//System.out.println(xSet);
		//System.out.println(ySet);
		//System.out.println(intersection);
		//System.out.println(union);
		
		System.out.println("Set Similarity: " + 1.0*intersection.size()/union.size());
		
		return 1.0*intersection.size()/union.size();
	}
}
