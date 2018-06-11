package cn.nuaa.ai.LCS;

public class MethodCollection {
	public static void main(String[] args) {
		//methodParse("javax/activation/ActivationDataFlavor.\"<init>\":(Ljava/lang/String;Ljava/lang/String;)V");
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		//methodParse("getSourceLocation:()Lorg/aspectj/bridge/ISourceLocation");
		
		
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
	}

	public static Method methodParse(String textMethod) {
		Method method = new Method();
		if(textMethod == null){
			method.showMethod();
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

		method.showMethod();
		
		return method;
	}
}
