package cn.nuaa.ai.LCS;

import java.util.Stack;

public class TestLCS {
	public static void main(String[] args) {
		String x = "213789217389127389";
		String y = "1298748912738912739";
		LCSequence(x, y);
		System.out.println();
		LCSString(x,y);
	}

	public static void LCSequence(String x, String y) {

		char[] s1 = x.toCharArray();
		char[] s2 = y.toCharArray();
		int[][] array = new int[x.length() + 1][y.length() + 1];// 此处的棋盘长度要比字符串长度多加1，需要多存储一行0和一列0

		for (int j = 0; j < array[0].length; j++) {// 第0行第j列全部赋值为0
			array[0][j] = 0;
		}
		for (int i = 0; i < array.length; i++) {// 第i行，第0列全部为0
			array[i][0] = 0;
		}

		for (int m = 1; m < array.length; m++) {// 利用动态规划将数组赋满值
			for (int n = 1; n < array[m].length; n++) {
				if (s1[m - 1] == s2[n - 1]) {
					array[m][n] = array[m - 1][n - 1] + 1;// 动态规划公式一
				} else {
					array[m][n] = max(array[m - 1][n], array[m][n - 1]);// 动态规划公式二
				}
			}
		}
		
		
		for (int m = 1; m < array.length; m++) {// 利用动态规划将数组赋满值
			for (int n = 1; n < array[m].length; n++) {
				System.out.print(array[m][n]+" ");
			}
			System.out.println();
		}
		
		
		Stack<Character> stack = new Stack<Character>();
		int i = x.length() - 1;
		int j = y.length() - 1;

		int cost = 0; 
		while ((i >= 0) && (j >= 0)) {
			if (s1[i] == s2[j]) {// 字符串从后开始遍历，如若相等，则存入栈中
				stack.push(s1[i]);
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
			System.out.print(stack.pop());
		}
		System.out.println();
		System.out.println("Cost: " + cost);
	}

	public static int max(int a, int b) {// 比较(a,b)，输出大的值
		return (a > b) ? a : b;
	}

	public static void LCSString(String x, String y) {
		int len1, len2;
		len1 = x.length();
		len2 = y.length();
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// 保存最长子串长度的数组
		int[] maxIndex = new int[maxLen];// 保存最长子串长度最大索引的数组
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (y.charAt(i) == x.charAt(j)) {
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
					System.out.print(x.charAt(i));
				System.out.println(" ");
			}
		}
	}
}
