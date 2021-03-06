package cn.nuaa.ai.LCS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.nuaa.ai.SourceCodeFormat.MethodDeclarationEntity;

public class Similarity {

	public static List<String> VariableDeclarationList = new ArrayList<String>();
	public static List<String> VariableNameList = new ArrayList<String>();
	public static List<String> VariableTypeList = new ArrayList<String>();
	public static Map<String, Integer> TypeMap = new HashMap<String, Integer>();
	public static MethodDeclarationEntity methodDeclaration = new MethodDeclarationEntity();
	private static final double A1=0.3;
	private static final double A2=0.7;
	private static final double A3=0.2;
	private static final double A4=0.8;
	private static final double B1=0.3;
	private static final double B2=0.2;
	private static final double B3=0.2;
	private static final double B4=0.3;
	
	
	private static String fileName = "Activiti-develop@AbstractActivitiTestCase#assertAndEnsureCleanDb.txt";

	public static void main(String[] args) {
		// readData();
		// VaribleSimilarity("0.txt",
		// "Activiti-develop@AbstractActivitiTestCase#assertAndEnsureCleanDb.txt");
		readData();
	}

	public static Map<String, Double> CodeSimilarity(String name, Map<String, Double> bwtResultList) {
		Map<String, Integer> seedVarType = readMethodVaribleDeclarationInformation(name);
		double varSimi = 0.0;
		for (String filename : bwtResultList.keySet()) {
			//System.out.println(filename + " " + bwtResultList.get(filename));
			varSimi = VaribleSimilarity(seedVarType, filename);
			//System.out.println(filename + "            " + varSimi);
			bwtResultList.replace(filename, (varSimi * A1 + bwtResultList.get(filename) * A2));
		}

		System.out.println();
		for (String filename : bwtResultList.keySet()) {
			System.out.println(filename + " " + "type: "+ bwtResultList.get(filename));
		}
		
		return bwtResultList;
	}

	public static Map<String, Double> RankScore(String name, Map<String, Double> bwtResultList){
		MethodDeclarationEntity seedMDE = readMethodBasicInformation(name);
		List<String> seedMVD = readMethodVaribleDeclaration(name);
		double strSimi = 0.0;
		for (String filename : bwtResultList.keySet()) {
			strSimi = StructureSimilarity(seedMDE, seedMVD, filename);
			System.out.println(filename + " ss: " + strSimi);
			bwtResultList.replace(filename, (strSimi * A3 + bwtResultList.get(filename) * A4));
		}
		
		System.out.println();
		for (String filename : bwtResultList.keySet()) {
			System.out.println(filename + " " + bwtResultList.get(filename));
		}
		
		return bwtResultList;
	}
	
	private static double StructureSimilarity(MethodDeclarationEntity seedMDE, List<String> seedMVD, String freqName){
		double ss = 0.0;
		MethodDeclarationEntity freqMDE = readMethodBasicInformation(freqName);
		List<String> freqMVD = readMethodVaribleDeclaration(freqName);
		//方法名;
		if(null != seedMDE && null != freqMDE && null != seedMDE.getMethodName() && seedMDE.getMethodName().equals(freqMDE.getMethodName())){
			ss += B1;
		}
		//返回值;
		if(null != seedMDE && null != freqMDE && null != seedMDE.getMethodRetureType() && seedMDE.getMethodRetureType().equals(freqMDE.getMethodRetureType())){
			ss += B2;
		}
		//参数;
		if(seedMDE.getMethodParameters().size() == 0 && freqMDE.getMethodParameters().size() == 0){
			ss += B3;
		}else if(seedMDE.getMethodParameters().size() == 0 && freqMDE.getMethodParameters().size() > 0){
			ss += B3;
		}else if(seedMDE.getMethodParameters().size() > 0 && freqMDE.getMethodParameters().size() == 0){
			ss += 0.0;
		}else{
			ss += (B3 * parameterandvariblenamesimilarity(seedMDE.getMethodParameters(),freqMDE.getMethodParameters(),1));
		}
		//变量;
		if((null == seedMVD && null == freqMVD) || (null != seedMVD && null != freqMVD && seedMVD.size() == 0 && freqMVD.size() == 0)){
			ss += B4;
		}else if((null == seedMVD && null != freqMVD) || (null != seedMVD && null != freqMVD && seedMVD.size() == 0 && freqMVD.size() > 0)){
			ss += B4;
		}else if((null != seedMVD && null == freqMVD) || (seedMVD.size() > 0 && freqMVD.size() == 0)){
			ss += 0.0;
		}else{
			ss += (B4 * parameterandvariblenamesimilarity(seedMVD,freqMVD,2));
		}		
		
		return ss;
	}
	
	private static double VaribleSimilarity(Map<String, Integer> seedVarType, String freqName) {
		Map<String, Integer> freqVarType = readMethodVaribleDeclarationInformation(freqName);
		int counter = 0;
		if(null == freqVarType && null != seedVarType){
			return 0.0;
		}
		if(null == freqVarType && null == seedVarType){
			return 1.0;
		}
		if(null != freqVarType && null == seedVarType){
			return 1.0;
		}
		for (String seeds : seedVarType.keySet()) {
			for (String freqs : freqVarType.keySet()) {
				if (seeds.equals(freqs)) {
					counter++;
				}
			}
		}
		// System.out.println(1.0 * counter / seedVarType.keySet().size());
		return 1.0 * counter / seedVarType.keySet().size();
	}

	private static double parameterandvariblenamesimilarity(List<String> seed, List<String> freq, int flag){
		Set<String> seedSet = new HashSet<String>();
		Set<String> freqSet = new HashSet<String>();
		for(String s : seed){
			String[] ss = s.split(" ");
			if(flag == 1){
				if(ss.length > 2){
					seedSet.add(ss[1]);
				}else{
					seedSet.add(ss[0]);
				}
			}else{
				String[] temps = ss[1].split("=");
				seedSet.add(temps[0].replace(";", ""));
			}
		}
		for(String s : freq){
			String[] ss = s.split(" ");
			if(flag == 1){
				if(ss.length > 2){
					freqSet.add(ss[1]);
				}else{
					freqSet.add(ss[0]);
				}
			}else{
				String[] temps = ss[1].split("=");
				freqSet.add(temps[0].replace(";", ""));
			}
		}
		
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		//System.out.println("varible names:");
		//for(String s : seedSet){
		//	System.out.println(s);
		//}
		//for(String s : freqSet){
		//	System.out.println(s);
		//}
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		Set<String> result = new HashSet<String>();
        result.addAll(seedSet);
        result.retainAll(freqSet);
		return 1.0*result.size()/seedSet.size();
	}
	
	
	private static void readData() {
		readMethodBasicInformation(fileName);
		readMethodVaribleDeclarationInformation(fileName);
		readMethodVaribleDeclaration(fileName);
	}

	private static MethodDeclarationEntity readMethodBasicInformation(String fileName) {
		MethodDeclarationEntity methodDeclaration = new MethodDeclarationEntity();
		File file = new File("F:\\data\\github\\methodBasicInformation\\" + fileName);
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
		// System.out.println("\nMethod Declaration:");
		// System.out.println("method name: " +
		// methodDeclaration.getMethodName());
		// System.out.println("method return value: " +
		// methodDeclaration.getMethodRetureType());
		// for (String s : methodDeclaration.getMethodParameters()) {
		// System.out.println("parameter: " + s);
		// }
		return methodDeclaration;
	}

	private static Map<String, Integer> readMethodVaribleDeclarationInformation(String fileName) {
		Map<String, Integer> TypeMap = new HashMap<String, Integer>();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		File file = new File("F:\\data\\github\\methodVaribleDeclarationInformation\\" + fileName);
		if (file.exists()) {
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			try {
				String str = "";
				fis = new FileInputStream("F:\\data\\github\\methodVaribleDeclarationInformation\\" + fileName);// FileInputStream
				// 从文件系统中的某个文件中获取字节
				isr = new InputStreamReader(fis);// InputStreamReader
													// 是字节流通向字符流的桥梁,
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
			return TypeMap;
		}

		// System.out.println("\nVariable Type Information:");
		// for (String s : TypeMap.keySet()) {
		// System.out.println("type: " + s + " num: " + TypeMap.get(s));
		// }
		return null;
	}

	private static List<String> readMethodVaribleDeclaration(String fileName) {
		List<String> VariableDeclarationList = new ArrayList<String>();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		File file = new File("F:\\data\\github\\methodVaribleDeclaration\\" + fileName);
		if(file.exists()){
			try {
				String str = "";
				fis = new FileInputStream("F:\\data\\github\\methodVaribleDeclaration\\" + fileName);// FileInputStream
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
			return VariableDeclarationList;
		}
		// System.out.println("\nVariable Declaration:");
		// for (String i : VariableDeclarationList) {
		// System.out.println(i);
		// }
		return null;
	}

}
