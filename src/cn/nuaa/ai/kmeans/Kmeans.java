package cn.nuaa.ai.kmeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nuaa.ai.LCS.TestLCS;

public class Kmeans extends KmeansUtils<InstructionSequence> {
	
	Map<String, Double> simimap = new HashMap<String, Double>();
	Map<String, Integer> idmap = new HashMap<String, Integer>();
	
	public static void main(String[] args) {
		
		Kmeans km = new Kmeans();
		
		TestLCS.getOpCodeFromFile();
		TestLCS.getInstructionsFromFile();
		System.out.println("!!!!!!!!!!!!! readin process finished !!!!!!!!!!!!!!!!!!");
		
		for(InstructionSequence ins : TestLCS.getInstructions()){
			km.addRecord(ins);	
		}
		km.setK(100);   
	    List<List<InstructionSequence>> cresult = km.clustering();  
	    List<InstructionSequence> center = km.getClusteringCenterT();  
 
	    
	    
	    if(center == null){
	    	System.out.println("1111111111111111111111111111111111111111");
	    }
	    if(cresult == null){
	    	System.out.println("2222222222222222222222222222222222222222");
	    }
	    //System.out.println(center.get(0).getFileName());
		for(InstructionSequence i : cresult.get(0)){
			System.out.println(i.getFileName());
		}
		
	}
	
	public double getsimi(InstructionSequence i,InstructionSequence j){
		int id1 = idmap.get(i.getFileName());
		int id2 = idmap.get(j.getFileName());
		if(id1 < id2){
			return simimap.get(i.getFileName()+j.getFileName());
		}else{
			return simimap.get(j.getFileName()+i.getFileName());
		}
	}
	
	
	@Override
	public double similarScore(InstructionSequence o1, InstructionSequence o2) {
		if(o1 == null || o2 == null){
			return 0;
		}
		double s1 = TestLCS.LCSequence(o1, o2);
		double s2 = TestLCS.LCSString(o1, o2);
		double s3 = TestLCS.SetComputing(o1, o2);
		return s1 * 0.5 + s2 * 0.3 + s3 * 0.2;
	}

	@Override
	public boolean equals(InstructionSequence o1, InstructionSequence o2) {
		if(o1 == null || o2 == null){
			return true;
		}
		for (int i = 0; i < o1.getIns().size() && i < o2.getIns().size(); i++) {
			if (o1.getIns().get(i).getCodeId() != o2.getIns().get(i).getCodeId()) {
				return false;
			} else {
				if (o1.getIns().get(i).getCodeId() == 182 || o1.getIns().get(i).getCodeId() == 183
						|| o1.getIns().get(i).getCodeId() == 184 || o1.getIns().get(i).getCodeId() == 185
						|| o1.getIns().get(i).getCodeId() == 186) {
					if (o1.getIns().get(i).getName().equals(o2.getIns().get(i).getName())) {
						continue;
					} else {
						return false;
					}
				} else {
					continue;
				}
			}
		}
		return true;
	}

	@Override
	public InstructionSequence getCenterT(List<InstructionSequence> list) {
		InstructionSequence tempCenter = null;
		double similarity = 0.0;
		double tempsimi = 0.0;
		for(int i = 0;i < list.size();i++){
			for(int j = 0;j < list.size();j++){
				if(i == j){
					continue;
				}
				tempsimi += similarScore(list.get(i),list.get(j));
			}
			if(tempsimi > similarity){
				tempCenter = list.get(i);
				similarity = tempsimi;
			}
			tempsimi = 0.0;
		}
		
		
		//if(tempCenter == null){
		//	tempCenter = list.get(0);
		//}
		return tempCenter;
	}
}
