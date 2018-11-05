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
	 * ���ļ��ж�ȡ����;
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
	 * �洢����������;
	 */
	private static void storeData(String code, String fileName) {
		// �洢��ʽ����ķ�����;
		StringBuffer str = new StringBuffer(code);
		try {
			writeFileContent("F:\\data\\github\\methodCleaned\\" + fileName, new StringBuffer(str));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * д���ļ�;
	 */
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
