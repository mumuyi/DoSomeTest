package cn.nuaa.ai.kmeans;

import java.util.List;

import cn.nuaa.ai.LCS.OpCode;

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
