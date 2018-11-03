package cn.nuaa.ai.SourceCodeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodDeclarationEntity implements Serializable {
	private static final long serialVersionUID = -6109242689042043718L;
	private String methodName;
	private String methodRetureType;
	private List<String> methodParameters = new ArrayList<String>();

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodRetureType() {
		return methodRetureType;
	}

	public void setMethodRetureType(String methodRetureType) {
		this.methodRetureType = methodRetureType;
	}

	public List<String> getMethodParameters() {
		return methodParameters;
	}

	public void setMethodParameters(List<String> methodParameters) {
		this.methodParameters = methodParameters;
	}

	public static List<String> VariableDeclarationList = new ArrayList<String>();
	public static Map<String, Integer> TypeMap = new HashMap<String, Integer>();
	public static MethodDeclarationEntity methodDeclaration = new MethodDeclarationEntity();

	public static void readData(String fileName) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû�С�
		try {
			String str = "";
			fis = new FileInputStream("F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclaration\\" + fileName);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
			while ((str = br.readLine()) != null) {
				VariableDeclarationList.add(str);
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

		

		try {
			String str = "";
			fis = new FileInputStream(
					"F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclarationInformation\\" + fileName);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
			while ((str = br.readLine()) != null) {
				String[] strs = str.split(" ");
				TypeMap.put(strs[0], Integer.parseInt(strs[1]));
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

		File file = new File("F:\\data\\jarFiles\\Top100000N\\methodBasicInformation\\" + fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(in);
			methodDeclaration = (MethodDeclarationEntity) objIn.readObject();
			objIn.close();
			// System.out.println("read object success!");
		} catch (IOException e) {
			System.out.println("read object failed");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// ��Ϣ���;

		System.out.println("\nVariable Declaration:");
		for (String i : VariableDeclarationList) {
			System.out.println(i);
		}

		System.out.println("\nVariable Type Information:");
		for (String s : TypeMap.keySet()) {
			System.out.println("type: " + s + "      num: " + TypeMap.get(s));
		}

		System.out.println("\nMethod Declaration:");
		System.out.println("method name: " + methodDeclaration.getMethodName());
		System.out.println("method return value: " + methodDeclaration.getMethodRetureType());
		for (String s : methodDeclaration.getMethodParameters()) {
			System.out.println("parameter: " + s);
		}

	}
}
