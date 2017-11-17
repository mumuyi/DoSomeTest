package cn.nuaa.ai;
/**
 * 由于直接使用LDA 进行主题提取的分词效果不好，所以在进行主题计算之前先使用lucene 的分词算法进行分词。
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class SplitWords {
	public static void main(String[] args) throws Exception {
		String path = "F:\\Java\\DoSomeTest\\LDAData\\LdaOriginalDocs";
		String writeBackPath = "F:\\Java\\DoSomeTest\\LDAData\\LdaSPlitWords";
		File folder = new File(path);
		if (folder.exists()) {
			File[] files = folder.listFiles();
			int i = 0;
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.println("文件夹:" + file.getAbsolutePath());
				} else {
					System.out.println(file.getName());
					List<String> terms = split(path + "\\" + file.getName());
					wirteBack(terms, writeBackPath + "\\" + file.getName() + ".spl");
				}
				if (i > 20)
					break;
				else
					i++;
			}
		}
	}

	private static List<String> split(String path) throws Exception {
		@SuppressWarnings("resource")
		Analyzer analyzer = new StandardAnalyzer();

		Path file = Paths.get(path);
		InputStream stream = Files.newInputStream(file);
		BufferedReader fileReader = null;
		fileReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

		List<String> result = new ArrayList<String>();

		TokenStream ts = analyzer.tokenStream(null, fileReader);
		ts = new PorterStemFilter(ts);
		CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

		ts.reset();// 必须的
		while (ts.incrementToken()) {
			String term = charTermAttribute.toString();
			if (term.contains(".")) {
				String[] temp = term.split("\\.");
				for (int i = 0; i < temp.length; i++) {
					if (!temp[i].equals("this") && !temp[i].equals("that")){				
						result.add(temp[i]);
					}
				}
			} else {
				result.add(term);
			}
			//System.out.println(term);
		}
		System.out.println(result.size());
		ts.end();
		ts.close();

		return result;
	}

	private static void wirteBack(List<String> result, String filepath) {
		File file = new File(filepath);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 创建FileOutputStream对应的PrintStream，方便操作。PrintStream的写入接口更便利
		PrintStream out = new PrintStream(fos);
		String tempResult = result.toString();
		// System.out.println(tempResult);
		String strResult = tempResult.substring(1, tempResult.length() - 1);
		// System.out.println(strResult);
		out.print(strResult);
		out.close();
	}
}
