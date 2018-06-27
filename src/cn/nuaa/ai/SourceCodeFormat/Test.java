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

import org.eclipse.jdt.core.JavaCore;
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

public class Test {
	public static void main(String[] args) {
		// take default Eclipse formatting options
		Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		for (String s : options.keySet()) {
			System.out.println(s + "          " + options.get(s));
		}

		// initialize the compiler settings to be able to format 1.5 code
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);

		// change the option to wrap each enum constant on a new line
		// options.replace("org.eclipse.jdt.core.formatter.comment.line_length",
		// "2000");
		options.replace("org.eclipse.jdt.core.formatter.lineSplit", "2000");
		// options.replace("org.eclipse.jdt.core.formatter.indentation.size",
		// "8");
		options.replace("org.eclipse.jdt.core.formatter.tabulation.char", "space");

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		for (String s : options.keySet()) {
			System.out.println(s + "          " + options.get(s));
		}

		// instantiate the default code formatter with the given options
		final CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

		// retrieve the source to format
		String source = "public class TestFormatter{public static void main(String[] args){int i = 0;List list = new ArrarList(dasdasdsadasdas).dsadsa().dasdsadasdas().dasdasdasdsa().dasdasdasdasdasdsad().dasdasdasdasdas();list.add(\"12333\");if(i==0){i++;j++;asdasdas.asdhgd().dasdas();}}}";

		TextEdit edit = codeFormatter.format(CodeFormatter.F_INCLUDE_COMMENTS, source, 0, source.length(), 0, null);

		IDocument doc = new Document(source);
		try {
			edit.apply(doc);
			// System.out.println(doc.get());
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// display the formatted string on the System out
		System.out.println(doc.get());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");

		// retrieve the source to format
		String source1 = "public class TestFormatter{public static void main(String[] args){int i = 0;List<String> list = new ArrarList<String>(dasdasdsadasdas).dsadsa().dasdsadasdas().dasdasdasdsa().dasdasdasdasdasdsad().dasdasdasdasdas();list.add(\"12333\");if(i==0){i++;j++;asdasdas.asdhgd().dasdas();}}}";

		TextEdit edit1 = codeFormatter.format(CodeFormatter.F_INCLUDE_COMMENTS, source1, 0, source1.length(), 0, null);

		IDocument doc1 = new Document(source1);
		try {
			edit1.apply(doc1);
			// System.out.println(doc.get());
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// display the formatted string on the System out
		System.out.println(doc1.get());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
}
