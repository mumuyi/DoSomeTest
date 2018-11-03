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
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			fis = new FileInputStream("F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclaration\\" + fileName);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				VariableDeclarationList.add(str);
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

		

		try {
			String str = "";
			fis = new FileInputStream(
					"F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclarationInformation\\" + fileName);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				String[] strs = str.split(" ");
				TypeMap.put(strs[0], Integer.parseInt(strs[1]));
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

		// 信息输出;

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
