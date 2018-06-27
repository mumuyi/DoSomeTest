package cn.nuaa.ai.SourceCodeFormat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;


public class MyFormat {
	public static List<String> VariableDeclarationList = new ArrayList<String>();
	public static List<String> VariableNameList = new ArrayList<String>();
	public static List<String> VariableTypeList = new ArrayList<String>();
	public static Map<String,Integer> TypeMap = new HashMap<String,Integer>();
	public static MethodDeclarationEntity methodDeclaration = new MethodDeclarationEntity();

	private static final Set<String> BasicTypeSet = new HashSet<String>(){
		private static final long serialVersionUID = -7711952209080770726L;
	{add("int");add("long");add("short");add("float");add("double");add("String");add("byte");add("char");add("boolean");
	add("int[]");add("long[]");add("short[]");add("float[]");add("double[]");add("String[]");add("byte[]");add("char[]");add("boolean[]");}};
	
	private static String fileName = "0.txt";
	
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
		String code = codeFormat(addClassHead(readCodeFromFile("F:\\data\\jarFiles\\Top100000N\\methodbody\\0.txt")));
		//System.out.println(code);
		//��������;��ȡ����������Ϣ;
		SingleFileTest(code);

		//ȥ��Դ�������еı�������;
		String nCode = removeVariableDeclaration(code);
		String oCode = nCode;
		//�������滻Ϊ��ԭ������;
		nCode = replaceVariableName(oCode);
		oCode = nCode;
		//ȥ����ṹ;
		nCode = removeClassHead(nCode);
		oCode = nCode;
		
		//�ڸ�ʽ��һ�δ���;ȥ������;
		nCode = codeFormat(oCode);
		
		
		//��Ϣ���;
		System.out.println("Format Code:");
		System.out.println(nCode);
		
		System.out.println("\nVariable Declaration:");
		for (String i : VariableDeclarationList) {
			System.out.println(i);
		}
		
		System.out.println("\nVariable Type Information:");
		for(String s : TypeMap.keySet()){
			System.out.println("type: " + s + "      num: " + TypeMap.get(s));
		}
		
		System.out.println("\nMethod Declaration:");
		System.out.println("method name: " + methodDeclaration.getMethodName());
		System.out.println("method return value: " + methodDeclaration.getMethodRetureType());
		for(String s : methodDeclaration.getMethodParameters()){
			System.out.println("parameter: " + s);
		}
		
		//�洢����;
		storeData(nCode);
		
		//��������;
		clearData();
		
		//��ȡ����;
		readData();
	}

	/**
	 * ��������;
	 * */
	private static void clearData(){
		VariableDeclarationList.clear();
		VariableNameList.clear();
		VariableTypeList.clear();
		TypeMap.clear();
		methodDeclaration.getMethodParameters().clear();
	}
	
	/**
	 * ȥ��������е���������;
	 * ����˵�������ͷ�����ֿ�;
	 * */
	private static String removeVariableDeclaration(String code){
		String oCode = code;
		String nCode = null;
		for (String i : VariableDeclarationList) {
			nCode = oCode.replace((i.replace("=", " = ").replace("\n", "")), "\n");
			oCode = nCode;
		}
		
		nCode = oCode.replaceAll(" +\n", "");
		
		//System.out.println(nCode);
		return nCode;
	}
	
	
	/**
	 * ��������еı����滻Ϊ��ԭ������;
	 * */
	private static String replaceVariableName(String code){
		String nCode = null;
		String oCode = code;
		for(int i = 0;i < VariableTypeList.size();i++){
			if(BasicTypeSet.contains(VariableTypeList.get(i))){
				continue;
			}
			
			//nCode = oCode.replaceAll(VariableNameList.get(i),VariableTypeList.get(i));
			//oCode = nCode;
			
			nCode = oCode.replaceAll(VariableNameList.get(i) + " ",VariableTypeList.get(i) + " ");
			oCode = nCode;
			nCode = oCode.replaceAll(VariableNameList.get(i) + "\\.",VariableTypeList.get(i) + "\\.");
			oCode = nCode;
			nCode = oCode.replaceAll(VariableNameList.get(i) + ",",VariableTypeList.get(i) + ",");
			oCode = nCode;
			nCode = oCode.replaceAll(VariableNameList.get(i) + ";",VariableTypeList.get(i) + ";");
			oCode = nCode;
		}
		
		for(int i = 0;i < methodDeclaration.getMethodParameters().size();i++){
			String[] types = methodDeclaration.getMethodParameters().get(i).split(" ");
			if(BasicTypeSet.contains(types[0])){
				continue;
			}
			
			//nCode = oCode.replaceAll(VariableNameList.get(i),VariableTypeList.get(i));
			//oCode = nCode;
			
			nCode = oCode.replaceAll(types[1] + " ",types[0] + " ");
			oCode = nCode;
			nCode = oCode.replaceAll(types[1] + "\\.",types[0] + "\\.");
			oCode = nCode;
			nCode = oCode.replaceAll(types[1] + ",",types[0] + ",");
			oCode = nCode;
			nCode = oCode.replaceAll(types[1] + ";",types[0] + ";");
			oCode = nCode;
		}
		
		
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
		MyCodeVisitor2 visitor = new MyCodeVisitor2();
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
		@SuppressWarnings("unchecked")
		Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
		//һ�ж��ٸ��ַ���ỻ��;
		options.replace("org.eclipse.jdt.core.formatter.lineSplit", "2000");
		//�Ʊ��;
		options.replace("org.eclipse.jdt.core.formatter.tabulation.char", "space");
		//ȥ������;
		options.replace("org.eclipse.jdt.core.formatter.comment.clear_blank_lines_in_block_comment", "true");
		options.replace("org.eclipse.jdt.core.formatter.comment.clear_blank_lines_in_javadoc_comment  ", "true");
		
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);
		TextEdit textEdit = codeFormatter.format(CodeFormatter.F_INCLUDE_COMMENTS,
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
		return doc.get().replaceAll("\t", "    ");
	}
	
	/**
	 * �����ṹ;
	 * */
	private static String addClassHead(String code){
		//System.out.println("public class test{" + code + "}");
		return "public class test{" + code + "}";
	}
	
	/**
	 * ɾ����ṹ;
	 * */
	private static String removeClassHead(String code){
		//System.out.println("public class test{" + code + "}");
		String nCode = code.replace("public class test {", "");
		return nCode.substring(0, nCode.length()-1);
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
				if(str == null || str.equals("\n") || str.equals("")){
					continue;
				}
				code += (str.replaceAll("\\<.*?\\>", ""));
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
	
	private static void readData(){
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû�С�
		try {
			String str = "";
			fis = new FileInputStream("F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclaration\\" + fileName);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
			while ((str = br.readLine()) != null) {
				VariableDeclarationList.add(str);
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
		
		String code = "";
		try {
			String str = "";
			fis = new FileInputStream("F:\\data\\jarFiles\\Top100000N\\methodFormatBody\\" + fileName);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
			while ((str = br.readLine()) != null) {
				if(str == null || str.equals("\n") || str.equals("")){
					continue;
				}
				code += (str + "\n");
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
		
		try {
			String str = "";
			fis = new FileInputStream("F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclarationInformation\\" + fileName);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
											// InputStreamReader�Ķ���
			while ((str = br.readLine()) != null) {
				String[] strs = str.split(" ");
				TypeMap.put(strs[0], Integer.parseInt(strs[1]));
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
		
		
        File file =new File("F:\\data\\jarFiles\\Top100000N\\methodBasicInformation\\" + fileName);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            methodDeclaration = (MethodDeclarationEntity) objIn.readObject();
            objIn.close();
            //System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        
		//��Ϣ���;
		System.out.println("\nFormat Code:");
		System.out.println(code);
		
		System.out.println("\nVariable Declaration:");
		for (String i : VariableDeclarationList) {
			System.out.println(i);
		}
		
		System.out.println("\nVariable Type Information:");
		for(String s : TypeMap.keySet()){
			System.out.println("type: " + s + "      num: " + TypeMap.get(s));
		}
		
		System.out.println("\nMethod Declaration:");
		System.out.println("method name: " + methodDeclaration.getMethodName());
		System.out.println("method return value: " + methodDeclaration.getMethodRetureType());
		for(String s : methodDeclaration.getMethodParameters()){
			System.out.println("parameter: " + s);
		}

	}
	
	
	/**
	 * �洢����������;
	 * */
	private static void storeData(String code){
		//�洢��������;
		StringBuffer str = new StringBuffer();
		for(int i = 0;i < VariableDeclarationList.size();i++){
			str.append(VariableDeclarationList.get(i));
		}
		try {
			writeFileContent("F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclaration\\" + fileName,new StringBuffer(str.substring(0, str.length()-1)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//�洢��ʽ����ķ�����;
		str = new StringBuffer(code);
		try {
			writeFileContent("F:\\data\\jarFiles\\Top100000N\\methodFormatBody\\" + fileName,new StringBuffer(str));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//�洢������Ϣ;
		str = new StringBuffer();
		for(String s : TypeMap.keySet()){
			str.append(s + " " + TypeMap.get(s) + "\n");
		}
		try {
			writeFileContent("F:\\data\\jarFiles\\Top100000N\\methodVaribleDeclarationInformation\\" + fileName,new StringBuffer(str.substring(0, str.length()-1)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//�洢������Ϣ;
        File file =new File("F:\\data\\jarFiles\\Top100000N\\methodBasicInformation\\" + fileName);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(methodDeclaration);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
	}
	
	/**
	 * д���ļ�;
	 * */
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
