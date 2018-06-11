package cn.nuaa.ai.LCS;

public class Method {
	private String methodPath;
	private String methodName;
	private String methodParameters;
	private String methodReturnValue;

	public String getMethodPath() {
		return methodPath;
	}

	public void setMethodPath(String methodPath) {
		this.methodPath = methodPath;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodParameters() {
		return methodParameters;
	}

	public void setMethodParameters(String methodParameters) {
		this.methodParameters = methodParameters;
	}

	public String getMethodReturnValue() {
		return methodReturnValue;
	}

	public void setMethodReturnValue(String methodReturnValue) {
		this.methodReturnValue = methodReturnValue;
	}
	
	public void showMethod(){
		System.out.println("MethodPath: " + this.getMethodPath());
		System.out.println("MethodName: " + this.getMethodName());
		System.out.println("MethodParameters: " + this.getMethodParameters());
		System.out.println("MethodReturnValue: " + this.getMethodReturnValue());
	}
}
