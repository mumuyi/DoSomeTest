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
		int[][] array = new int[x.length() + 1][y.length() + 1];// �˴������̳���Ҫ���ַ������ȶ��1����Ҫ��洢һ��0��һ��0

		for (int j = 0; j < array[0].length; j++) {// ��0�е�j��ȫ����ֵΪ0
			array[0][j] = 0;
		}
		for (int i = 0; i < array.length; i++) {// ��i�У���0��ȫ��Ϊ0
			array[i][0] = 0;
		}

		for (int m = 1; m < array.length; m++) {// ���ö�̬�滮�����鸳��ֵ
			for (int n = 1; n < array[m].length; n++) {
				if (s1[m - 1] == s2[n - 1]) {
					array[m][n] = array[m - 1][n - 1] + 1;// ��̬�滮��ʽһ
				} else {
					array[m][n] = max(array[m - 1][n], array[m][n - 1]);// ��̬�滮��ʽ��
				}
			}
		}
		
		
		for (int m = 1; m < array.length; m++) {// ���ö�̬�滮�����鸳��ֵ
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
			if (s1[i] == s2[j]) {// �ַ����Ӻ�ʼ������������ȣ������ջ��
				stack.push(s1[i]);
				i--;
				j--;
			} else {
				if (array[i + 1][j] > array[i][j + 1]) {// ����ַ������ַ���ͬ����������������ͬ���ַ���ע�⣺���������Ҫ���ַ������ַ��ĸ�����1�����i��jҪ����1
					j--;
				} else {
					i--;
				}
				cost++;
			}
		}

		System.out.println("�����������:");
		while (!stack.isEmpty()) {// ��ӡ���ջ����������������Ĺ���������
			System.out.print(stack.pop());
		}
		System.out.println();
		System.out.println("Cost: " + cost);
	}

	public static int max(int a, int b) {// �Ƚ�(a,b)��������ֵ
		return (a > b) ? a : b;
	}

	public static void LCSString(String x, String y) {
		int len1, len2;
		len1 = x.length();
		len2 = y.length();
		int maxLen = len1 > len2 ? len1 : len2;

		int[] max = new int[maxLen];// ������Ӵ����ȵ�����
		int[] maxIndex = new int[maxLen];// ������Ӵ������������������
		int[] c = new int[maxLen];

		int i, j;
		for (i = 0; i < len2; i++) {
			for (j = len1 - 1; j >= 0; j--) {
				if (y.charAt(i) == x.charAt(j)) {
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
		for (j = 0; j < maxLen; j++) {
			if (max[j] > 0) {
				System.out.println("����������Ӵ� "+(j+1)+":");
				for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++)
					System.out.print(x.charAt(i));
				System.out.println(" ");
			}
		}
	}
}
