package cn.nuaa.ai.SourceCodeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class CleanCode {
	public static void main(String[] args) {
		File directory = new File("F:\\data\\github\\methodCleaned\\");
		File[] insFiles = directory.listFiles();
		for(int i = 0; i< insFiles.length;i++){
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			String s = readCodeFromFile(insFiles[i].getPath());
			//System.out.println(s.substring(0, s.length()-1));
			storeData(s,insFiles[i].getName());
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	public static void getCleanCode(File file){
		String s = readCodeFromFile(file.getPath());
		storeData(s,file.getName());
	}
	
	/**
	 * 从文件中读取代码;
	 */
	private static String readCodeFromFile(String path) {
		Scanner sc = null;
		try {
			sc = new Scanner(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line=null;
		String code = "";
		int i = 0;
		while((sc.hasNextLine()&&(line=sc.nextLine())!=null)){
			if (line == null || line.equals("\n") || line.equals("")) {
				continue;
			}
			if(!sc.hasNextLine()){
				continue;
			}
			if(i == 0){
				i++;
				continue;
			}
			code += (line.replaceAll("    ", "") + "\n");
		}
		return code;
	}

	/**
	 * 存储解析的数据;
	 */
	private static void storeData(String code, String fileName) {
		// 存储格式化后的方法体;
		StringBuffer str = new StringBuffer(code);
		try {
			writeFileContent("F:\\data\\github\\methodCleaned\\" + fileName, new StringBuffer(str));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 写入文件;
	 */
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
