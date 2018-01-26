package cn.nuaa.ai.rosf;
/**
 * 获取代码片段的文本特征信息。
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetSnippetsFeature {
	public static void main(String[] arg) {
		// System.out.println("Let's have some fun");
		traverseFolder("C:\\Users\\ai\\Desktop\\ROSF\\methodCodeSnippets1564\\1564methodCodeSnippets");
	}

	private static void traverseFolder(String path) {
		File folder = new File(path);
		if (folder.exists()) {
			File[] files = folder.listFiles();
			int i = 0;
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.println("文件夹:" + file.getAbsolutePath());
				} else {
					System.out.println("文件: " + file.getAbsolutePath());
					// 获取方法的完全限定名称;
					System.out.println("方法完全限定名: " + file.getName().split(".txt")[0]);
					// 获取方法名;
					System.out.println("方法名: " + file.getName().split("#|.txt")[1]);
					// 获取代码行数;
					System.out.println("代码行数: " + getLineNumber(file));
					// 获取兄弟方法名称;
					List<String> sibMethods = getSiblingMethodNames(files, file);
					for (int jj = 0; jj < sibMethods.size(); jj++)
						System.out.println("sibling Method " + jj + ":  " + sibMethods.get(i));
					// 获取代码片段内容;
					System.out.println(getContent(file));
				}
				if (i > -1)
					break;
				else
					i++;
			}
		}
	}

	private static int getLineNumber(File file) {
		int lines = 0;
		long fileLength = file.length();
		LineNumberReader rf = null;
		try {
			rf = new LineNumberReader(new FileReader(file));
			if (rf != null) {

				rf.skip(fileLength);
				lines = rf.getLineNumber();
				rf.close();
			}
		} catch (IOException e) {
			if (rf != null) {
				try {
					rf.close();
				} catch (IOException ee) {
				}
			}
		}
		if (lines != 1 && lines != 0)
			lines++;
		return lines;
	}

	private static List<String> getSiblingMethodNames(File[] files, File file) {
		List<String> sbn = new ArrayList<String>();
		int index = Arrays.binarySearch(files, file);
		// System.out.println(index+" "+files.length);
		int begin = 0, end = 0;
		if (index - 20 < 0) {
			begin = 0;
		} else {
			begin = index - 20;
		}
		if (index + 20 > files.length - 1) {
			end = files.length - 1;
		} else {
			end = index + 20;
		}
		String[] fileName = file.getName().split("@|#|.txt");
		for (int i = begin; i < end; i++) {
			if (i == index)
				continue;

			File tempFile = files[i];
			String[] names = tempFile.getName().split("@|#|.txt");
			// System.out.println(names[0]+" "+names[1]+" "+names[2]);
			if (fileName[0].equals(names[0]) && fileName[1].equals(names[1])) {
				sbn.add(tempFile.getName().split(".txt")[0]);
			}
		}

		// for(int i=0;i<sbn.size();i++)
		// System.out.println("sibling Method "+i+": "+sbn.get(i));

		return sbn;
	}

	private static String getContent(File file) {
		BufferedReader bufread = null;
		String temp = null;
		String read = "";
		String ans = "";
		try {
			bufread = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while ((temp = bufread.readLine()) != null) {
				read += temp;
				// System.out.println(read);
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
		//System.out.println(read);
		String[] temps = read.split("\\{|\\}");
		if (temps.length == 1) {
			ans += read;
		} else if (temps.length == 2) {
			ans += temps[1];
		} else if (temps.length > 2) {
			for (int i = 1; i < temps.length; i++) {
				ans += temps[i];
			}
		}
		//System.out.println(temps.length);
		//System.out.println(ans);
		return ans;
	}
}
