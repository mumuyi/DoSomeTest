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
	// epΪkey;idΪvalue;
	private static Map<String, Integer> ep2id = new HashMap<String, Integer>();
	// ep��idΪkey;ep���ִ���Ϊvalue;
	private static Map<Integer, Integer> aps = new HashMap<Integer, Integer>();

	public static void main(String[] args) {
		// ��ȡ�ļ�;
		File file = new File("F:\\Java\\DoSomeTest\\FIMIData\\testcode.txt");
		// ��ȡ������;
		List<String> content = getContent(file);
		System.out.println("������:");
		for (int i = 0; i < content.size(); i++)
			System.out.println(content.get(i));

		for (int i = 0; i < content.size(); i++) {
			// System.out.println(temps[i]);
			// ��code ת��Ϊep;
			String ep = code2ep(content.get(i));
			System.out.println(ep);
			// ά��ep��;
			if (!ep2id.containsKey(ep)) {
				int id = ep2id.size();
				ep2id.put(ep, id);
			}
			// תΪaps;
			int id = ep2id.get(ep);
			if (aps.containsKey(id)) {
				aps.replace(id, aps.get(id) + 1);
			} else {
				aps.put(id, 1);
			}
		}
		// ���ep2id ��;
		System.out.println("\nep2id��:");
		for (String key : ep2id.keySet()) {
			System.out.println(key + "   " + ep2id.get(key));
		}
		// ���һ������Ƭ�ε�aps;
		System.out.println("\naps:");
		for (int key : aps.keySet()) {
			System.out.println(key + "   " + aps.get(key));
		}
	}

	private static String code2ep(String code) {
		// System.out.println(code);
		// �������еĹؼ���ת��Ϊ '#';
		String str = code.replaceAll("[^;.()=\\s*,\\r]", "#");
		// System.out.println(str);
		// ȥ���ظ��� '#';
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
				 * // ȥ�������Ʊ���ͻس�; String temp1 = temp.replaceAll("\t|\n|\r",
				 * ""); // ȥ�����ж���Ŀո�; String temp2 = temp1.replaceAll("[ ]+",
				 * " ").replaceAll("; ", ";").replaceAll(" = ", "="); // ȥ��������;
				 * String temp3 = temp2.replaceAll("\\{|\\}", "");
				 * if(!temp3.equals(" "
				 * )&&!temp3.equals("\t")&&!temp3.equals('\n'))
				 * cleanList.add(temp3);
				 */
				// ȥ��������;
				String temp1 = temp.replaceAll("\\{|\\}", " ");
				// ȥ�������Ʊ���ͻس�;
				String temp2 = temp1.replaceAll("\t|\n|\r", " ");
				// ȥ�����ж���Ŀո�;
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
		// ȥ������������;
		cleanList.remove(0);
		//ȥ����ʼ�ͽ�β�Ŀո�;
		for(int i=0;i<cleanList.size();i++){
			if(cleanList.get(i).charAt(0)==' '){
				cleanList.set(i, cleanList.get(i).substring(1, cleanList.get(i).length()));
			}
			
			if(cleanList.get(i).charAt(cleanList.get(i).length()-1)==' '){
				cleanList.set(i, cleanList.get(i).substring(0, cleanList.get(i).length()-1));
			}
		}
		// ȥ����forѭ���еķֺ�֮�������ֺ�;
		for (int i = 0; i < cleanList.size(); i++) {
			if (!cleanList.get(i).startsWith("for")) {
				cleanList.set(i, cleanList.get(i).replaceAll(";", ""));
			}
		}
		
		return cleanList;
	}
}
