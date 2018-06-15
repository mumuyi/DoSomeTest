package cn.nuaa.ai.LCS;

public class OpCode implements Comparable<OpCode> {
	private int codeId;
	private String code;
	private String name;
	private int levle1;
	private int levle2;
	private String invokedMethod;
	private String newType;

	public OpCode() {
	}

	public OpCode(OpCode op) {
		this.codeId = op.getCodeId();
		this.code = op.getCode();
		this.name = op.getName();
		this.levle1 = op.getLevle1();
		this.levle2 = op.getLevle2();
		if (op.getInvokedMethod() != null)
			this.invokedMethod = new String(op.getInvokedMethod());
		else
			this.invokedMethod = null;
	}

	public int getCodeId() {
		return codeId;
	}

	public void setCodeId(int codeId) {
		this.codeId = codeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevle1() {
		return levle1;
	}

	public void setLevle1(int levle1) {
		this.levle1 = levle1;
	}

	public int getLevle2() {
		return levle2;
	}

	public void setLevle2(int levle2) {
		this.levle2 = levle2;
	}

	public String getInvokedMethod() {
		return invokedMethod;
	}

	public void setInvokedMethod(String invokedMethod) {
		this.invokedMethod = invokedMethod;
	}

	public String getNewType() {
		return newType;
	}

	public void setNewType(String newType) {
		this.newType = newType;
	}

	@Override
	public int compareTo(OpCode op) {
		if (this.getCodeId() < op.getCodeId())
			return -1;
		else if (this.getCodeId() > op.getCodeId())
			return 1;
		else
			return 0;
	}

}
