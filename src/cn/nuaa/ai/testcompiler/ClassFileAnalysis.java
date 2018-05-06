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

	// 从反编译的文件中抽取需要的信息;
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

			// 去掉第一块和最后一块;
			list.remove(0);
			list.remove(list.size() - 1);
			// 去掉没有"Code:"和"LineNumberTable:"的块;
			for (int i = 0; i < list.size(); i++) {
				if (!list.get(i).contains("Code:") || !list.get(i).contains("LineNumberTable:")) {
					list.remove(i);
					i--;
				}
			}

			// 写入文件;
			for (int i = 0; i < list.size(); i++) {
				String name = NameExtraction(list.get(i));
				List<String> ins = FeatureExtraction(list.get(i));
				StringBuffer buffer = new StringBuffer();
				for (String str : ins) {
					buffer.append(str + "\n");
				}
				// 去掉最后一个回车;
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

	// 获取方法体的名字;
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

	// 从一个方法块中抽取数据;
	private static List<String> FeatureExtraction(String block) {
		// for(int i=0;i<list.size();i++)
		// 切割出"Code:"和:"LineNumberTable:之间的"
		//System.out.println(block);
		//System.out.println(block.indexOf("Code:") + " " + block.indexOf("LineNumberTable:"));
		// System.out.println(block.charAt(block.indexOf("Code:")));
		// System.out.println(block.charAt(block.indexOf("LineNumberTable:")));
		String str1 = block.substring(block.indexOf("Code:") + 5, block.indexOf("LineNumberTable:"));
		// 去掉多余的部分;
		String[] strs = str1.split("\n");
		List<String> strlist = new ArrayList<String>();
		for (String str : strs) {
			if (!"".equals(str) && !"\n".equals(str) && !"      ".equals(str)) {
				strlist.add(str);
			}
		}
		strlist.remove(0);

		// 抽取指令;
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

	// 反编译.class 文件;
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

	// 写入文件;
	private static boolean writeFileContent(String filepath, StringBuffer buffer) throws IOException {
		Boolean bool = false;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);// 文件路径(包括文件名称)

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			bool = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			// 不要忘记关闭
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
