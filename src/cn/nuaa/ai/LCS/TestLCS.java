package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
		//LCSequence(instructions.get(0),instructions.get(1));
		//LCSString(instructions.get(0),instructions.get(1));
		SetComputing(instructions.get(0),instructions.get(1));
	}

	public static void LCSequence(List<OpCode> x, List<OpCode> y) {

		int[][] array = new int[x.size() + 1][y.size() + 1];// 此处的棋盘长度要比字符串长度多加1，需要多存储一行0和一列0

		for (int j = 0; j < array[0].length; j++) {// 第0行第j列全部赋值为0
			array[0][j] = 0;
		}
		for (int i = 0; i < array.length; i++) {// 第i行，第0列全部为0
			array[i][0] = 0;
		}

		for (int m = 1; m < array.length; m++) {// 利用动态规划将数组赋满值
			for (int n = 1; n < array[m].length; n++) {
				//if (s1[m - 1] == s2[n - 1]) {
				if (x.get(m - 1).getCodeId() == y.get(n - 1).getCodeId()) {
					array[m][n] = array[m - 1][n - 1] + 1;// 动态规划公式一
				} else {
					array[m][n] = max(array[m - 1][n], array[m][n - 1]);// 动态规划公式二
				}
			}
		}
		
		
		//for (int m = 1; m < array.length; m++) {// 利用动态规划将数组赋满值
		//	for (int n = 1; n < array[m].length; n++) {
		//		System.out.print(array[m][n]+" ");
		//	}
		//	System.out.println();
		//}
		
		
		Stack<OpCode> stack = new Stack<OpCode>();
		int i = x.size() - 1;
		int j = y.size() - 1;

		int cost = 0; 
		while ((i >= 0) && (j >= 0)) {
			if (x.get(i).getCodeId() == y.get(j).getCodeId()) {// 字符串从后开始遍历，如若相等，则存入栈中
				stack.push(x.get(i));
				i--;
				j--;
			} else {
				if (array[i + 1][j] > array[i][j + 1]) {// 如果字符串的字符不同，则在数组中找相同的字符，注意：数组的行列要比字符串中字符的个数大1，因此i和j要各加1
					j--;
				} else {
					i--;
				}
				cost++;
			}
		}

		System.out.println("最长公共子序列:");
		while (!stack.isEmpty()) {// 打印输出栈正好是正向输出最大的公共子序列
			System.out.println(stack.pop().getName());
		}
		System.out.println();
		System.out.println("Cost: " + cost);
	}

	public static int max(int a, int b) {// 比较(a,b)，输出大的值
		return (a > b) ? a : b;
	}

	public static void LCSString(List<OpCode> x, List<OpCode> y) {
		int len1, len2;
		len1 = x.size();
		len2 = y.size();
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// 保存最长子串长度的数组
		int[] maxIndex = new int[maxLen];// 保存最长子串长度最大索引的数组
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (y.get(i).getCodeId() == x.get(j).getCodeId()) {
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
			//for (int temp : c) {
			//	  System.out.print(temp);
			//}
			//System.out.println();
		}
		// 打印最长子字符串
		for (j = 0; j < maxLen; j++) {
			if (max[j] > 0) {
				System.out.println("最长公共公共子串 "+(j+1)+":");
				for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++)
					System.out.println(x.get(i).getName());
				System.out.println(" ");
			}
		}
	}
	
	
	public static void getOpCodeFromFile(){
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			fis = new FileInputStream("C:\\Users\\ai\\Desktop\\opcode.txt");// FileInputStream
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
			//for(OpCode op : oplist){
			//	System.out.println(op.getCodeId() + " " + op.getCode() + " " + op.getName() + " " + op.getLevle1() + " " + op.getLevle2());
			//}
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
	
	public static int getOpCodeID(String opc){
		for(OpCode op : oplist){
			if(opc.equals(op.getName())){
				return op.getCodeId();
			}
		}
		return -1;
	}
	
	
	// 从反编译的文件中抽取需要的信息;
	private static void getInstructions() {

		int i = 0;
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
				while ((str = br.readLine()) != null) {
					//System.out.println(str);
					//System.out.println(getOpCodeID(str));
					list.add(oplist.get(getOpCodeID(str)));
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
			i++;
			if(i==2)
				break;
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
		
		
		
		System.out.println(xSet);
		System.out.println(ySet);
		System.out.println(intersection);
		System.out.println(union);
		
		System.out.println(1.0*intersection.size()/union.size());
		
		return 1.0*intersection.size()/union.size();
	}
}
