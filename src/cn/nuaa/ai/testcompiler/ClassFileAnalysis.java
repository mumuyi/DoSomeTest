package cn.nuaa.ai.testcompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ClassFileAnalysis {

	private static List<String> list = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		fileAnalysis();
	}

	// �ӷ�������ļ��г�ȡ��Ҫ����Ϣ;
	private static void fileAnalysis() {

		File directory = new File("F:\\data\\decompile\\");
		File[] files = directory.listFiles();
		for (File file : files) {
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			// String filename =
			// "Activiti-develop@AbstractActivitiTestCase$1.class";
			String filename = file.getName();
			try {
				String str = "";
				String str1 = "";
				fis = new FileInputStream("F:\\data\\decompile\\" + filename);
				isr = new InputStreamReader(fis);
				br = new BufferedReader(isr);
				while ((str = br.readLine()) != null) {
					// System.out.println(str);
					if (!"".equals(str) && !"\n".equals(str) && !"{".equals(str) && !"}".equals(str)) {
						str1 += (str + "\n");
					} else {
						list.add(str1);
						str1 = "";
					}
				}
				list.add(str1);
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

			// System.out.println(list.size());

			// ȥ����һ������һ��;
			list.remove(0);
			list.remove(list.size() - 1);
			// ȥ��û��"Code:"��"LineNumberTable:"�Ŀ�;
			for (int i = 0; i < list.size(); i++) {
				if (!list.get(i).contains("Code:") || !list.get(i).contains("LineNumberTable:")) {
					list.remove(i);
					i--;
				}
			}

			// д���ļ�;
			for (int i = 0; i < list.size(); i++) {
				String name = NameExtraction(list.get(i));
				List<String> ins = FeatureExtraction(list.get(i));
				StringBuffer buffer = new StringBuffer();
				for (String str : ins) {
					buffer.append(str + "\n");
				}
				// ȥ�����һ���س�;
				buffer.delete(buffer.length() - 1, buffer.length());
				try {
					writeFileContent(
							"F:\\data\\instruction\\" + filename.replaceAll(".class", "") + "#" + name + ".txt",
							buffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			list.clear();
		}
	}

	// ��ȡ�����������;
	private static String NameExtraction(String block) {
		// System.out.println(block);
		String firstLine = block.split("\n")[0];
		// System.out.println(firstLine);
		String[] strs = firstLine.split(" |\\.");
		String fuzzyName = "";
		for (String str : strs) {
			// System.out.println(str);
			if (str.contains("(")) {
				fuzzyName = str;
			}
		}
		String Name = "";
		if(fuzzyName.indexOf("(") >= 0){
			Name = fuzzyName.substring(0, fuzzyName.indexOf("("));			
		}else{
			Name = "static";
		}
		System.out.println("Method Name: " + Name);

		return Name;
	}

	// ��һ���������г�ȡ����;
	private static List<String> FeatureExtraction(String block) {
		// for(int i=0;i<list.size();i++)
		// �и��"Code:"��:"LineNumberTable:֮���"
		//System.out.println(block);
		//System.out.println(block.indexOf("Code:") + " " + block.indexOf("LineNumberTable:"));
		// System.out.println(block.charAt(block.indexOf("Code:")));
		// System.out.println(block.charAt(block.indexOf("LineNumberTable:")));
		String str1 = block.substring(block.indexOf("Code:") + 5, block.indexOf("LineNumberTable:"));
		// ȥ������Ĳ���;
		String[] strs = str1.split("\n");
		List<String> strlist = new ArrayList<String>();
		for (String str : strs) {
			if (!"".equals(str) && !"\n".equals(str) && !"      ".equals(str)) {
				strlist.add(str);
			}
		}
		strlist.remove(0);

		// ��ȡָ��;
		List<String> inslist = new ArrayList<String>();
		// System.out.println("strlist's size: " + strlist.size());
		for (String str : strlist) {
			// System.out.println(str);
			String[] strs1 = str.split(" ");
			int i = 0;
			for (String sstr : strs1) {
				if (!"".equals(sstr) && !"\n".equals(sstr) && null != sstr) {
					if (i == 0) {
						i++;
					} else if (i == 1) {
						inslist.add(sstr);
						i++;
					} else {
						break;
					}
				}
			}
		}
		// System.out.println("Instructions:");
		// for (String str : inslist) {
		// System.out.println(str);
		// }

		return inslist;
	}

	// ������.class �ļ�;
	private static void decompile() throws IOException {
		Runtime run = Runtime.getRuntime();

		File directory = new File(".\\data");
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			Process process = run.exec("javap -verbose .\\data\\" + files[i].getName());
			InputStream in = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String info = "";
			StringBuffer buffer = new StringBuffer();
			while ((info = reader.readLine()) != null) {
				buffer.append(info);
				buffer.append("\n");
			}
			writeFileContent("F:\\data\\decompile\\" + files[i].getName(), buffer);

			System.out.println(files[i].getName() + "               done");
		}
	}

	// д���ļ�;
	private static boolean writeFileContent(String filepath, StringBuffer buffer) throws IOException {
		Boolean bool = false;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);// �ļ�·��(�����ļ�����)

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			bool = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			// ��Ҫ���ǹر�
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return bool;
	}
}
