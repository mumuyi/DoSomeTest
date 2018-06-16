package cn.nuaa.ai.LCS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodCollection {
	public static void main(String[] args) {
		//InvokedMethod m1 = methodParse("javax/activation/ActivationDataFlavor.init:(Ljava/lang/String;Ljava/lang/String;)V");
		//InvokedMethod m2 = methodParse("javax/activation111/ActivationDataFlavor.init111:(Ljava/lang/String;Ljava/lang/String;)V");
		//InvokedMethod m2 = methodParse("getSourceLocation:()Lorg/aspectj/bridge/ISourceLocation");
		//System.out.println(getSimilarityBetweenMethodInvoke(m1, m2));
		
		
		//System.out.println(getSimilarityBetweenMethodPath("1/2/3/4","1/2/3/4"));
		//System.out.println(getSimilarityBetweenMethodName("11111111fsdfsdfs","11111111fsdfsdfs"));
		
		
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile("F:\\data\\jarFiles\\Top100000\\instruction\\");
		System.out.println("read in process finished");
		
		Set<InvokedMethod> seed = instructions2methodset(TestLCS.getInstructions().get(1069));
		List<Similarity2ClassIndex> simiList = new ArrayList<Similarity2ClassIndex>();
		for (int i = 0; i < TestLCS.getInstructions().size(); i++) {
			Similarity2ClassIndex s2c = new Similarity2ClassIndex();
			s2c.setClassId(i);
			s2c.setSimilarity(1.0*Intersection(seed,instructions2methodset(TestLCS.getInstructions().get(i)))/union(seed,instructions2methodset(TestLCS.getInstructions().get(i))));
			simiList.add(s2c);
		}
		Collections.sort(simiList);
		int i = 0;
		for (Similarity2ClassIndex s2c : simiList) {
			System.out.println(s2c.getClassId() + "  " + s2c.getSimilarity() + "   " + TestLCS.getInsFiles()[s2c.getClassId()]);
			i++;
			if (i > 11) {
				break;
			}
		}
	}

	
	/**
	 * �󽻼�;
	 * */
	public static int Intersection(Set<InvokedMethod> set1,Set<InvokedMethod> set2){
		int counter = 0;
		for(InvokedMethod m1 : set1){
			for(InvokedMethod m2 : set2){
				if(m1.equals(m2) || getSimilarityBetweenMethodInvoke(m1,m2) > 0.95){
					counter++;
					break;
				}
			}
		}
		return counter;
	}
	
	/**
	 * �󲢼�;
	 * */
	public static int union(Set<InvokedMethod> set1,Set<InvokedMethod> set2){
		Set<InvokedMethod> union = new HashSet<InvokedMethod>();
		union.addAll(set1);
		union.addAll(set2);
		
		return removeDuplicates(union).size();
	}
	
	/**
	 * ȥ��;
	 * */
	public static Set<InvokedMethod> removeDuplicates(Set<InvokedMethod> set){
		List<InvokedMethod> list = new ArrayList<InvokedMethod>(set);
		List<InvokedMethod> removeList = new ArrayList<InvokedMethod>();
		for(int i = 0;i < list.size();i++){
			for(int j = i + 1;j < list.size();j++){
				if(getSimilarityBetweenMethodInvoke(list.get(i),list.get(j)) > 0.95){
					removeList.add(list.get(j));
				}
			}
		}
		list.removeAll(removeList);
		return new HashSet<InvokedMethod>(list);
	}
	
	
	/**
	 * ������������֮������ƶ�;
	 * */
	public static double getSimilarityBetweenMethodInvoke(InvokedMethod m1, InvokedMethod m2){
		double similarity = 0.0;
		//��ȫ���;
		if(m1.isEquals(m2)){
			return 1.0;
		}
		//�������Ƿ����;
		similarity += getSimilarityBetweenMethodName(m1.getMethodName(), m2.getMethodName()) * 0.4;

		//����ֵ�Ƿ����;
		if(m1.getMethodReturnValue().equals("EMPTY") && m2.getMethodReturnValue().equals("EMPTY")){
			similarity += 0.25;
		}else if(!m1.getMethodReturnValue().equals("EMPTY") && !m2.getMethodReturnValue().equals("EMPTY")){
			similarity += (getSimilarityBetweenMethodPath(m1.getMethodReturnValue(),m2.getMethodReturnValue()) * 0.25);
		}else{
			similarity += 0.0;
		}
		//·���Ƿ����;
		if(m1.getMethodPath().equals("EMPTY") && m2.getMethodPath().equals("EMPTY")){
			similarity += 0.15;
		}else if(!m1.getMethodPath().equals("EMPTY") && !m2.getMethodPath().equals("EMPTY")){
			similarity += (getSimilarityBetweenMethodPath(m1.getMethodPath(),m2.getMethodPath()) * 0.15);
		}else{
			similarity += 0.0;
		}
		//�����Ƿ����;
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
	 * ��������������֮������ƶ�;
	 * */
	public static double getSimilarityBetweenMethodName(String name1, String name2){
		//����;
		int counter = 0;
		for(int i = 0;i < name1.length() && i < name2.length();i++){
			if(name1.charAt(i) == name2.charAt(i)){
				counter++;
			}
		}
		double s1 = 1.0*counter/(name1.length() < name2.length() ? name1.length() : name2.length());
		//����;
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
	 * ����������������֮������ƶ�;
	 * �����"/" �и�������path�����һ��Token���;����Ϊ�����ƶ�Ϊ0.85;��ΪԽ�����Խ��Ҫ;
	 * ֮���0.15�����ƶ��ɺ���ǰ�ݼ��ķ����ʣ�µ�Token;
	 * ���е����ڶ���Token�Ƿ���ȵ�Ȩ����Weights = 0.45 / ( 2 * (length-1) ); ����ֵ����Difference = 0.15 / ((length-1) * (length-1) - (length-1));
	 * ����,���Ա�֤�ܺ�Ϊ1;
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
			if(ss1[i].equals(ss2[j])){
				Similarity += (Weights - (counter * Difference));
			}
		}
		
		return Similarity;
	}
	
	/**
	 * ���������������е����ƶ�;
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
	 * ����������������;���ı���ʽ����Ϣת��Ϊ��,���ڱȽ�;
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
	
	/**
	 * ָ������ת��������;
	 * */
	public static Set<InvokedMethod> instructions2methodset(InstructionSequence ins){
		Set<InvokedMethod> set = new HashSet<InvokedMethod>();
		for(OpCode op : ins.getIns()){
			if(op.getCodeId() == 182 || op.getCodeId() == 183 || op.getCodeId() == 184 || op.getCodeId() == 185 || op.getCodeId() == 186){
				set.add(methodParse(op.getInvokedMethod()));
			}
		}
		return set;
	}
}
