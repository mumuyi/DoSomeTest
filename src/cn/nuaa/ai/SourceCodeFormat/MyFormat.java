package cn.nuaa.ai.SourceCodeFormat;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class MyFormat {
	public static List<String> VariableDeclarationList = new ArrayList<String>();

	public static void main(String[] args) {
		codeFormat(
				"public class TestFormatter{public static void main(String[] args){int i = 0;List list = new ArrayList();if(!Sysss.out()){i+=1;i+=1;i--;int j = 10;}System.out.println(\"Hello World\" + i);TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);}}");

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		SingleFileTest();

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		for (String i : VariableDeclarationList) {
			System.out.print(i);
		}
	}

	/**
	 * ��������; ������Ҫ���������ķ����������һ����Ľṹ����;
	 */
	private static void SingleFileTest() {
		CompilationUnit comp = getCompilationUnit(
				"public class TestFormatter{public static void main(String[] args){int i = 0;List<String> list = new ArrarList<String>();if(!Sysss.out()){i+=1;i+=1;i--;int j = 10;}System.out.println(\"Hello World\" + i);TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);}}");
		MyCodeVisitor visitor = new MyCodeVisitor();
		comp.accept(visitor);
		// ��ȡimport����;
		// System.out.println(comp.imports());
		// ������������,��ʱ��֪�������;
		// System.out.println(comp.types());
	}

	/**
	 * �ɴ����ַ���ֱ������CompilationUnit;
	 */
	public static CompilationUnit getCompilationUnit(String code) {
		ASTParser astParser = ASTParser.newParser(AST.JLS3);
		astParser.setSource(code.toCharArray());
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));

		return result;
	}

	/**
	 * ���ļ���ֱ�Ӷ�ȡ����,������CompilationUnit;
	 */
	public static CompilationUnit getCompilationUnitFromFile(String javaFilePath) {
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

	/**
	 * ��ʽ��Դ����;
	 */
	private static String codeFormat(String code) {
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);

		TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT | CodeFormatter.F_INCLUDE_COMMENTS,
				code, 0, code.length(), 0, null);
		IDocument doc = new Document(code);
		try {
			textEdit.apply(doc);
			System.out.println(doc.get());
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return doc.get();
	}
}
