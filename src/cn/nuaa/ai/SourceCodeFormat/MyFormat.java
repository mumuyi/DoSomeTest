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
		
		//从文件中获取源码;并对其进行格式化;
		String code = codeFormat(addClassHead(readCodeFromFile("F:\\data\\jarFiles\\Top100000N\\methodbody\\1.txt")));
		//System.out.println(code);
		//解析代码;获取变量声明信息;
		SingleFileTest(code);
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		for (String i : VariableDeclarationList) {
			System.out.print(i);
		}
		//去掉源码中所有的变量声明;
		String nCode = removeVariableDeclaration(code);
		String oCode = nCode;
		//将变量替换为其原有类型;
		for(int i = 0;i < VariableTypeList.size();i++){
			nCode = oCode.replaceAll(VariableNameList.get(i),VariableTypeList.get(i));
		}
		System.out.println(nCode);
	}

	/**
	 * 去掉代码段中的所有声明;
	 * 或者说将声明和方法体分开;
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
	 * 解析代码; 现在需要将带解析的方法体包含在一个类的结构里面;
	 */
	private static void SingleFileTest(String code) {
		CompilationUnit comp = getCompilationUnit(
				//"public class TestFormatter{public static void main(String[] args){int i = 0;List<String> list = new ArrarList<String>();list.add(\"12333\");if(!Sysss.out()){i+=1;i+=1;i--;int j = 10;}System.out.println(\"Hello World\" + i);TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);}}");
				//"public class TestFormatter{public static void main(String[] args){List<String> list = new ArrarList<String>();list.add(\"12333\");}}"
				code);
		MyCodeVisitor visitor = new MyCodeVisitor();
		comp.accept(visitor);
		// 获取import数据;
		// System.out.println(comp.imports());
		// 返回了整个类,暂时不知道干嘛的;
		// System.out.println(comp.types());
	}

	/**
	 * 由代码字符串直接生成CompilationUnit;
	 */
	public static CompilationUnit getCompilationUnit(String code) {
		ASTParser astParser = ASTParser.newParser(AST.JLS3);
		astParser.setSource(code.toCharArray());
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit result = (CompilationUnit) (astParser.createAST(null));

		return result;
	}

	/**
	 * 从文件中直接读取数据,并返回CompilationUnit;
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
	 * 格式化源代码;
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
	 * 添加类结构;
	 * */
	private static String addClassHead(String code){
		//System.out.println("public class test{" + code + "}");
		return "public class test{" + code + "}";
	}
	
	
	/**
	 * 从文件中读取代码;
	 */
	private static String readCodeFromFile(String path) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		String code = "";
		try {
			String str = "";
			fis = new FileInputStream(path);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				//因为Format方法不能处理List<String> list = new ArrarList<String>(); 中的<String>这种写法;
				//因此在这里去掉这部分内容;
				code += (str.replaceAll("\\<.*?\\>", "") + "\n");
			}
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(code);
		return code;
	}
}
