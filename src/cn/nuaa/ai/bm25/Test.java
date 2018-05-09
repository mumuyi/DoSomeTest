package cn.nuaa.ai.bm25;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;


public class Test {
	public static void main(String[] args) throws Exception {

		//BufferedReader stopwordsReader = new BufferedReader(new FileReader("F:\\Java\\BM25Test\\data\\d1.dat"));
		//Analyzer analyzer = new StandardAnalyzer(stopwordsReader);
		//CharArraySet stopWords = new CharArraySet(0, false);
		//stopWords.add("this");
		//stopWords.add("that");
		//Analyzer analyzer = new StandardAnalyzer(stopWords);
		Analyzer analyzer = new StandardAnalyzer();

		Path file = Paths.get("F:\\Java\\BM25Test\\data\\d3.dat");
		InputStream stream = Files.newInputStream(file);
		BufferedReader fileReader = null;
		fileReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

		List<String> result = new ArrayList<String>();

		TokenStream ts = analyzer.tokenStream(null, fileReader);
		ts = new PorterStemFilter(ts);
		//OffsetAttribute offsetAttribute = ts.addAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

		ts.reset();// ±ØÐëµÄ
		while (ts.incrementToken()) {
			//int startOffset = offsetAttribute.startOffset();
			//int endOffset = offsetAttribute.endOffset();
			String term = charTermAttribute.toString();
			result.add(term);
			System.out.println(term);
		}
		System.out.println(result.size());
		ts.end();
		ts.close();
	}
}
