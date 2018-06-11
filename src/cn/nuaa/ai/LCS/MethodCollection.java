package cn.nuaa.ai.LCS;

public class MethodCollection {
	public static void main(String[] args) {
		InvokedMethod m1 = methodParse("javax/activation/ActivationDataFlavor.\"<init>\":(Ljava/lang/String;Ljava/lang/String;)V");
		InvokedMethod m2 = methodParse("javax/activation.\"<init>\":(Ljava/lang/String;Ljava/lang/String;)V");
		//InvokedMethod m2 = methodParse("getSourceLocation:()Lorg/aspectj/bridge/ISourceLocation");
		System.out.println(getSimilarityBetweenMethodInvoke(m1, m2));
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
		if(m1.getMethodName().equals(m2.getMethodName())){
			similarity += 0.4;
		}
		//返回值是否相等;
		if(m1.getMethodReturnValue().equals("EMPTY") && m2.getMethodReturnValue().equals("EMPTY")){
			similarity += 0.25;
		}else if(!m1.getMethodReturnValue().equals("EMPTY") && !m2.getMethodReturnValue().equals("EMPTY")){
			similarity += (getSimilarityBetweenMethodRootPath(m1.getMethodReturnValue(),m2.getMethodReturnValue()) * 0.25);
		}else{
			similarity += 0.0;
		}
		//路径是否相等;
		if(m1.getMethodPath().equals("EMPTY") && m2.getMethodPath().equals("EMPTY")){
			similarity += 0.15;
		}else if(!m1.getMethodPath().equals("EMPTY") && !m2.getMethodPath().equals("EMPTY")){
			similarity += (getSimilarityBetweenMethodRootPath(m1.getMethodPath(),m2.getMethodPath()) * 0.15);
		}else{
			similarity += 0.0;
		}
		//参数是否相等;
		if(m1.getMethodParameters().equals("EMPTY") && m2.getMethodParameters().equals("EMPTY")){
			similarity += 0.20;
		}else if(!m1.getMethodParameters().equals("EMPTY") && !m2.getMethodParameters().equals("EMPTY")){
			//similarity += (getSimilarityBetweenMethodRootPath(m1.getMethodParameters(),m2.getMethodParameters()) * 0.20);
			String[] ss1 = m1.getMethodParameters().split(";");
			String[] ss2 = m2.getMethodParameters().split(";");
			double tempS = 0.0;
			for(int i = 0; i < ss1.length; i++){
				double tempS1 = 0.0;
				for(int j = 0; j < ss2.length; j++){
					double tempS2 = getSimilarityBetweenMethodRootPath(m1.getMethodParameters(),m2.getMethodParameters());
					tempS1 = (tempS1 > tempS2 ? tempS1 : tempS2);
				}
				tempS += tempS1;
			}
			tempS /= ss1.length;
			similarity += tempS * 0.20;
		}else{
			similarity += 0.0;
		}
		
		
		return similarity;
	}
	
	/**
	 * 计算两个方法序列之间的相似度;
	 * */
	public static double getSimilarityBetweenMethodRootPath(String path1, String path2){
		int counter = 0;
		String[] ss1 = path1.split("/");
		String[] ss2 = path2.split("/");
		for(int i = 0;i < ss1.length && i < ss2.length;i++){
			if(ss1[i].equals(ss2[i])){
				counter++;
			}
		}
		
		return 1.0*counter/(ss1.length > ss2.length ? ss1.length : ss2.length);
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
