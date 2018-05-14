package cn.nuaa.ai.LCS;

import java.util.List;

public class InstructionSequence {
	private List<OpCode> ins;
	private String FileName;

	public List<OpCode> getIns() {
		return ins;
	}

	public void setIns(List<OpCode> ins) {
		this.ins = ins;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

}
