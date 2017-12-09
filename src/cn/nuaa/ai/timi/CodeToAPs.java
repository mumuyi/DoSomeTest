package cn.nuaa.ai.timi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CodeToAPs {
	// ep为key;id为value;
	private static Map<String, Integer> ep2id = new HashMap<String, Integer>();
	// ep的id为key;ep出现次数为value;
	private static Map<Integer, Integer> aps = new HashMap<Integer, Integer>();

	public static void main(String[] args) {
		// 获取文件;
		File file = new File("F:\\Java\\DoSomeTest\\FIMIData\\testcode.txt");
		// 获取方法体;
		List<String> content = getContent(file);
		System.out.println("方法体:");
		for (int i = 0; i < content.size(); i++)
			System.out.println(content.get(i));

		for (int i = 0; i < content.size(); i++) {
			// System.out.println(temps[i]);
			// 将code 转换为ep;
			String ep = code2ep(content.get(i));
			System.out.println(ep);
			// 维护ep表;
			if (!ep2id.containsKey(ep)) {
				int id = ep2id.size();
				ep2id.put(ep, id);
			}
			// 转为aps;
			int id = ep2id.get(ep);
			if (aps.containsKey(id)) {
				aps.replace(id, aps.get(id) + 1);
			} else {
				aps.put(id, 1);
			}
		}
		// 输出ep2id 表;
		System.out.println("\nep2id表:");
		for (String key : ep2id.keySet()) {
			System.out.println(key + "   " + ep2id.get(key));
		}
		// 输出一个代码片段的aps;
		System.out.println("\naps:");
		for (int key : aps.keySet()) {
			System.out.println(key + "   " + aps.get(key));
		}
	}

	private static String code2ep(String code) {
		// System.out.println(code);
		// 将代码中的关键字转换为 '#';
		String str = code.replaceAll("[^;.()=\\s*,\\r]", "#");
		// System.out.println(str);
		// 去除重复的 '#';
		String ep = str.replaceAll("[#]+", "#");
		// System.out.println(ep);
		return ep;
	}

	private static List<String> getContent(File file) {
		BufferedReader bufread = null;
		String temp = null;
		List<String> cleanList = new ArrayList<String>();
		try {
			bufread = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while ((temp = bufread.readLine()) != null) {
				/*
				 * // 去掉所有制表符和回车; String temp1 = temp.replaceAll("\t|\n|\r",
				 * ""); // 去掉所有多余的空格; String temp2 = temp1.replaceAll("[ ]+",
				 * " ").replaceAll("; ", ";").replaceAll(" = ", "="); // 去掉大括号;
				 * String temp3 = temp2.replaceAll("\\{|\\}", "");
				 * if(!temp3.equals(" "
				 * )&&!temp3.equals("\t")&&!temp3.equals('\n'))
				 * cleanList.add(temp3);
				 */
				// 去掉大括号;
				String temp1 = temp.replaceAll("\\{|\\}", " ");
				// 去掉所有制表符和回车;
				String temp2 = temp1.replaceAll("\t|\n|\r", " ");
				// 去掉所有多余的空格;
				String temp3 = temp2.replaceAll("[ ]+", " ").replaceAll("; ", ";").replaceAll(" = ", "=");
				String temp4 = temp3.replaceAll("[\t]+", "");

				if (!temp4.equals(" ") && !temp4.equals("\t") && !temp4.equals('\n'))
					cleanList.add(temp4);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bufread.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 去掉方法的声明;
		cleanList.remove(0);
		//去掉开始和结尾的空格;
		for(int i=0;i<cleanList.size();i++){
			if(cleanList.get(i).charAt(0)==' '){
				cleanList.set(i, cleanList.get(i).substring(1, cleanList.get(i).length()));
			}
			
			if(cleanList.get(i).charAt(cleanList.get(i).length()-1)==' '){
				cleanList.set(i, cleanList.get(i).substring(0, cleanList.get(i).length()-1));
			}
		}
		// 去掉除for循环中的分号之外的其余分号;
		for (int i = 0; i < cleanList.size(); i++) {
			if (!cleanList.get(i).startsWith("for")) {
				cleanList.set(i, cleanList.get(i).replaceAll(";", ""));
			}
		}
		
		return cleanList;
	}
}
