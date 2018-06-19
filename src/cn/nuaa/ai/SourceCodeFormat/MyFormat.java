package cn.nuaa.ai.SourceCodeFormat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public static List<String> VariableNameList = new ArrayList<String>();
	public static List<String> VariableTypeList = new ArrayList<String>();

	public static void main(String[] args) {
		//codeFormat(
		//		"public class TestFormatter{public static void main(String[] args){int i = 0;List list = new ArrayList();if(!Sysss.out()){i+=1;i+=1;i--;int j = 10;}System.out.println(\"Hello World\" + i);TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);}}");
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		//SingleFileTest("public class TestFormatter{public static void main(String[] args){int i = 0;List<String> list = new ArrarList<String>();list.add(\"12333\");}}");
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		//for (String i : VariableDeclarationList) {
		//	System.out.print(i);
		//}
		//removeVariableDeclaration("public class TestFormatter{public static void main(String[] args){int i = 0;List<String> list = new ArrarList<String>();list.add(\"12333\");}}");
		
		//���ļ��л�ȡԴ��;��������и�ʽ��;
		String code = codeFormat(addClassHead(readCodeFromFile("F:\\data\\jarFiles\\Top100000N\\methodbody\\1.txt")));
		//System.out.println(code);
		//��������;��ȡ����������Ϣ;
		SingleFileTest(code);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		for (String i : VariableDeclarationList) {
			System.out.print(i);
		}
		//ȥ��Դ�������еı�������;
		String nCode = removeVariableDeclaration(code);
		String oCode = nCode;
		//�������滻Ϊ��ԭ������;
		for(int i = 0;i < VariableTypeList.size();i++){
			nCode = oCode.replaceAll(VariableNameList.get(i),VariableTypeList.get(i));
		}
		System.out.println(nCode);
	}

	/**
	 * ȥ��������е���������;
	 * ����˵�������ͷ�����ֿ�;
	 * */
	private static String removeVariableDeclaration(String code){
		String oCode = code;
		String nCode = null;
		for (String i : VariableDeclarationList) {
			nCode = oCode.replace(i.replace("\n", "").replaceAll("=", " = "), "");
			oCode = nCode;
		}
		//System.out.println(nCode);
		return nCode;
	}
	
	
	/**
	 * ��������; ������Ҫ���������ķ����������һ����Ľṹ����;
	 */
	private static void SingleFileTest(String code) {
		CompilationUnit comp = getCompilationUnit(
				//"public class TestFormatter{public static void main(String[] args){int i = 0;List<String> list = new ArrarList<String>();list.add(\"12333\");if(!Sysss.out()){i+=1;i+=1;i--;int j = 10;}System.out.println(\"Hello World\" + i);TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);}}");
				//"public class TestFormatter{public static void main(String[] args){List<String> list = new ArrarList<String>();list.add(\"12333\");}}"
				code);
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
			//System.out.println(doc.get());
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		//System.out.println(doc.get());
		return doc.get();
	}
	
	/**
	 * �����ṹ;
	 * */
	private static String addClassHead(String code){
		//System.out.println("public class test{" + code + "}");
		return "public class test{" + code + "}";
	}
	
	
	/**
	 * ���ļ��ж�ȡ����;
	 */
	private static String readCodeFromFile(String path) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû�С�
		String code = "";
		try {
			String str = "";
			fis = new FileInputStream(path);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
			while ((str = br.readLine()) != null) {
				//��ΪFormat�������ܴ���List<String> list = new ArrarList<String>(); �е�<String>����д��;
				//���������ȥ���ⲿ������;
				code += (str.replaceAll("\\<.*?\\>", "") + "\n");
			}
		} catch (FileNotFoundException e) {
			System.out.println("�Ҳ���ָ���ļ�");
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ��");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// �رյ�ʱ����ð����Ⱥ�˳��ر���󿪵��ȹر������ȹ�s,�ٹ�n,����m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(code);
		return code;
	}
}
