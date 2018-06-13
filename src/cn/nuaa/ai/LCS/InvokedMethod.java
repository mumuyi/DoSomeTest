package cn.nuaa.ai.LCS;

public class InvokedMethod {
	private String methodPath; // 0.15;
	private String methodName; // 0.4;
	private String methodParameters; // 0.2;
	private String methodReturnValue; // 0.25;

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

	public void showMethod() {
		System.out.println("MethodPath: " + this.getMethodPath());
		System.out.println("MethodName: " + this.getMethodName());
		System.out.println("MethodParameters: " + this.getMethodParameters());
		System.out.println("MethodReturnValue: " + this.getMethodReturnValue());
	}

	public boolean isEquals(InvokedMethod m) {
		if (!this.getMethodName().equals(m.getMethodName())) {
			return false;
		}
		if (!this.getMethodPath().equals(m.getMethodPath())) {
			return false;
		}
		if (!this.getMethodReturnValue().equals(m.getMethodReturnValue())) {
			return false;
		}
		if (!this.getMethodParameters().equals(m.getMethodParameters())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InvokedMethod) {
			InvokedMethod m = (InvokedMethod) obj;
			if (m.getMethodName().equals(this.getMethodName())
					&& m.getMethodParameters().equals(this.getMethodParameters())
					&& m.getMethodPath().equals(this.getMethodPath())
					&& m.getMethodReturnValue().equals(this.getMethodReturnValue())) {
				return true;
			}
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return (this.getMethodName() + this.getMethodParameters() + this.getMethodPath() + this.getMethodReturnValue())
				.hashCode();
	}
}
