package cn.nuaa.ai.ast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTAnalysis {
	public static void main(String[] args){
        //CompilationUnit comp = getCompilationUnit("F:\\Java\\DoSomeTest\\LDAData\\LdaOriginalDocs\\aarddict.android_26_src.tar.gz@Article#Article.txt");  
        CompilationUnit comp = getCompilationUnit("F:\\Java\\DoSomeTest\\LDAData\\LdaOriginalDocs\\Program.java");  
        MyVisitor visitor = new MyVisitor();  
        comp.accept(visitor);
	}

	public static CompilationUnit getCompilationUnit(String javaFilePath) {
		byte[] input = null;
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));
			input = new byte[bufferedInputStream.available()];
			bufferedInputStream.read(input);
			bufferedInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ASTParser astParser = ASTParser.newParser(AST.JLS3);
		astParser.setSource(new String(input).toCharArray());
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));
		
		return result;
	}
}
