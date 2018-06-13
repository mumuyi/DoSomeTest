package cn.nuaa.ai.LCS;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MethodCollection {
	public static void main(String[] args) {
		InvokedMethod m1 = methodParse("javax/activation/ActivationDataFlavor.init:(Ljava/lang/String;Ljava/lang/String;)V");
		InvokedMethod m2 = methodParse("javax/activation/ActivationDataFlavor.init:(Ljava/lang/String;Ljava/lang/String;)V");
		//InvokedMethod m2 = methodParse("getSourceLocation:()Lorg/aspectj/bridge/ISourceLocation");
		System.out.println(getSimilarityBetweenMethodInvoke(m1, m2));
		
		
		//System.out.println(getSimilarityBetweenMethodPath("1/2/3/4","1/2/3/4"));
		//System.out.println(getSimilarityBetweenMethodName("11111111fsdfsdfs","11111111fsdfsdfs"));
		
		/*
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile("F:\\data\\jarFiles\\Top100000\\instruction\\");
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			System.out.println(i + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + TestLCS.getInstructions().get(i).getFileName());
			for(OpCode op : TestLCS.getInstructions().get(i).getIns()){
				if(op.getCodeId() == 182 || op.getCodeId() == 183 || op.getCodeId() == 184 || op.getCodeId() == 185 || op.getCodeId() == 186){
					methodParse(op.getInvokedMethod());
				}
			}
		}
		*/
	}

	/**
	 * 计算两个方法之间的相似度;
	 * */
	public static double getSimilarityBetweenMethodInvoke(InvokedMethod m1, InvokedMethod m2){
		double similarity = 0.0;
		//完全相等;
		if(m1.isEquals(m2)){
			return 1.0;
		}
		//方法名是否相等;
		similarity += getSimilarityBetweenMethodName(m1.getMethodName(), m2.getMethodName()) * 0.4;

		//返回值是否相等;
		if(m1.getMethodReturnValue().equals("EMPTY") && m2.getMethodReturnValue().equals("EMPTY")){
			similarity += 0.25;
		}else if(!m1.getMethodReturnValue().equals("EMPTY") && !m2.getMethodReturnValue().equals("EMPTY")){
			similarity += (getSimilarityBetweenMethodPath(m1.getMethodReturnValue(),m2.getMethodReturnValue()) * 0.25);
		}else{
			similarity += 0.0;
		}
		//路径是否相等;
		if(m1.getMethodPath().equals("EMPTY") && m2.getMethodPath().equals("EMPTY")){
			similarity += 0.15;
		}else if(!m1.getMethodPath().equals("EMPTY") && !m2.getMethodPath().equals("EMPTY")){
			similarity += (getSimilarityBetweenMethodPath(m1.getMethodPath(),m2.getMethodPath()) * 0.15);
		}else{
			similarity += 0.0;
		}
		//参数是否相等;
		if(m1.getMethodParameters().equals("EMPTY") && m2.getMethodParameters().equals("EMPTY")){
			similarity += 0.20;
		}else if(!m1.getMethodParameters().equals("EMPTY") && !m2.getMethodParameters().equals("EMPTY")){

			similarity += getSimilarityBetweenMethodParameters(m1.getMethodParameters(),m2.getMethodParameters()) * 0.20;
		}else{
			similarity += 0.0;
		}
		
		
		return similarity;
	}
	
	
	/**
	 * 计算两个方法名之间的相似度;
	 * */
	public static double getSimilarityBetweenMethodName(String name1, String name2){
		//正序;
		int counter = 0;
		for(int i = 0;i < name1.length() && i < name2.length();i++){
			if(name1.charAt(i) == name2.charAt(i)){
				counter++;
			}
		}
		double s1 = 1.0*counter/(name1.length() < name2.length() ? name1.length() : name2.length());
		//逆序;
		counter = 0;
		for(int i = name1.length() - 1,j = name2.length() - 1;i >= 0 && j >= 0;i--,j--){
			if(name1.charAt(i) == name2.charAt(j)){
				counter++;
			}
		}
		double s2 = 1.0*counter/(name1.length() < name2.length() ? name1.length() : name2.length());
		return s1 > s2 ? s1 : s2;
	}
	
	
	/**
	 * 计算两个方法序列之间的相似度;
	 * 如果以"/" 切割后的两个path的最后一个Token相等;则认为其相似度为0.85;因为越靠后的越重要;
	 * 之后的0.15的相似度由后至前递减的分配给剩下的Token;
	 * 其中倒数第二个Token是否相等的权重是Weights = 0.45 / ( 2 * (length-1) ); 而差值则是Difference = 0.15 / ((length-1) * (length-1) - (length-1));
	 * 最终,可以保证总和为1;
	 * */
	public static double getSimilarityBetweenMethodPath(String path1, String path2){
		double Similarity = 0;
		String[] ss1 = path1.split("/");
		String[] ss2 = path2.split("/");
		int length = (ss1.length < ss2.length ? ss1.length : ss2.length);
		if(length < 2){
			if(getSimilarityBetweenMethodName(ss1[ss1.length-1],(ss2[ss2.length-1])) > 0.9){
				return 0.85;
			}else{
				return 0.0;
			}
		}	
		
		if(getSimilarityBetweenMethodName(ss1[ss1.length-1],(ss2[ss2.length-1])) > 0.9){
			Similarity += 0.85;
		}
		double Weights = 0.45 / ( 2 * (length-1) );
		double Difference = 0.15 / ((length-1) * (length-1) - (length-1));
		int counter = 0;
		for(int i = ss1.length - 2,j = ss2.length - 2;i >= 0 && j >= 0;i--,j--,counter++){
			if(ss1[i].equals(ss2[i])){
				Similarity += (Weights - (counter * Difference));
			}
		}
		
		return Similarity;
	}
	
	/**
	 * 计算两个参数序列的相似度;
	 * */
	public static double getSimilarityBetweenMethodParameters(String path1, String path2){
		String[] ss1 = path1.split(";");
		String[] ss2 = path2.split(";");
		Set<String> set1 = new HashSet<String>(Arrays.asList(ss1));
		Set<String> set2 = new HashSet<String>(Arrays.asList(ss2));
		
		Set<String> intersection = new HashSet<String>();
		intersection.addAll(set1);
		intersection.retainAll(set2);

		Set<String> union = new HashSet<String>();
		union.addAll(set1);
		union.addAll(set2);

		return 1.0 * intersection.size() / union.size();
		
		
		/*
		//similarity += (getSimilarityBetweenMethodRootPath(m1.getMethodParameters(),m2.getMethodParameters()) * 0.20);
		String[] ss1 = m1.getMethodParameters().split(";");
		String[] ss2 = m2.getMethodParameters().split(";");
		double tempS = 0.0;
		for(int i = 0; i < ss1.length; i++){
			double tempS1 = 0.0;
			for(int j = 0; j < ss2.length; j++){
				double tempS2 = getSimilarityBetweenMethodPath(m1.getMethodParameters(),m2.getMethodParameters());
				tempS1 = (tempS1 > tempS2 ? tempS1 : tempS2);
			}
			tempS += tempS1;
		}
		tempS /= ss1.length;
		return tempS;
		*/
	}
	
	
	/**
	 * 解析方法调用序列;将文本形式的信息转换为类,便于比较;
	 * */
	public static InvokedMethod methodParse(String textMethod) {
		InvokedMethod method = new InvokedMethod();
		if(textMethod == null){
			//method.showMethod();
			return method;
		}
		
		String s1, s2, s3;
		if (textMethod.contains(".")) {
			String[] ss1 = textMethod.split("\\.");
			method.setMethodPath(ss1[0]);
			s1 = ss1[1];
		} else {
			method.setMethodPath("EMPTY");
			s1 = textMethod;
		}
		String[] ss2 = s1.split(":");
		method.setMethodName(ss2[0]);
		if(ss2.length > 1){
			s2 = ss2[1];
		}
		else{
			method.setMethodReturnValue("EMPTY");
			method.setMethodParameters("EMPTY");
			return method;
		}

		String[] ss3 = s2.split("\\)");
		if (!(ss3[1].equals("V") || ss3[1].equals("I"))) {
			method.setMethodReturnValue(ss3[1]);
		} else {
			method.setMethodReturnValue("EMPTY");
		}
		s3 = ss3[0].replaceAll("\\(", "");

		if (s3.equals("")) {
			method.setMethodParameters("EMPTY");
		} else {
			method.setMethodParameters(s3);
		}

		//method.showMethod();
		
		return method;
	}
}
