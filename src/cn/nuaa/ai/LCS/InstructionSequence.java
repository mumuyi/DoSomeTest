package cn.nuaa.ai.LCS;

import java.util.ArrayList;
import java.util.List;

public class InstructionSequence implements Comparable<InstructionSequence>{
	private List<OpCode> ins;
	private String FileName;

	public InstructionSequence(){}
	
	public InstructionSequence(InstructionSequence is){
		this.setFileName(is.getFileName());
		List<OpCode> list = new ArrayList<OpCode>();
		if(is.getIns() != null){
			for(OpCode op : is.getIns()){
				list.add(op);
			}
		}
		this.setIns(list);
	}	
	
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

	@Override
	public int compareTo(InstructionSequence is) {
		for(int i = 0;i < is.getIns().size() && i < this.getIns().size();i++){
			if(is.getIns().get(i).getCodeId() < this.getIns().get(i).getCodeId()){
				return 1;
			}else if(is.getIns().get(i).getCodeId() > this.getIns().get(i).getCodeId()){
				return -1;
			}else{
				continue;
			}
		}
		return 0;
	}

}
