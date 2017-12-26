package cn.nuaa.ai.timi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.nuaa.ai.dao.MyHibernate;
import cn.nuaa.ai.entity.Aps;
import cn.nuaa.ai.entity.Ep2Id;
import cn.nuaa.ai.entity.Ep2KeyWord;

public class RecommedationActivity {
	
	public static void main(String[] args) {
		// 数据准备;
		// dataPreparation();

		// 建立索引;
		// IndexFiles inf = new IndexFiles();
		// inf.doIndex();

		// 第一步查找;
		SearchFiles sf = new SearchFiles();
		List<String> list = null;
		try {
			list = sf.doSearch("redirected Id UUID");
		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println(list.size());
		//for (int i = 0; i < list.size(); i++) {
		//	System.out.println(list.get(i));
		//}
		
		//获取对应eps;
		List<Ep2Id> ep2idList = new ArrayList<Ep2Id>();
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).replace("F:\\Java\\DoSomeTest\\FIMIData\\keywords\\", "").replace(".txt", ""));
			System.out.println(list.get(i));
			ep2idList.add((Ep2Id)(MyHibernate.sqlQuery(0, 1, "from Ep2Id where Id = "+list.get(i)).get(0)));
		}
		
		//构成aps;
		Aps aps = new Aps();
		String EpIds = "";
		String Freqs = "";
		for(int i=0;i<ep2idList.size();i++){
			if(i==0){
				EpIds+=ep2idList.get(i).getId();
				Freqs+="1";
			}
			else{
				EpIds+=(", "+ep2idList.get(i).getId());
				Freqs+=", 1";
			}
		}
		aps.setEpIds(EpIds);
		aps.setFreqs(Freqs);
		System.out.println(aps.getEpIds());
		System.out.println(aps.getFreqs());
		
		
		//相似度计算;
		int totalnum = (int) MyHibernate.sqlGetRecordNum("select count(*) from Aps"); 

		List<Integer> epids = new ArrayList<Integer>();
		int TotalEpNum = (int) MyHibernate.sqlGetRecordNum("select count(*) from Ep2Id");
		for(int i=0;i<TotalEpNum;i++){
			epids.add(i);
		}
		@SuppressWarnings("unchecked")
		List<Aps> apslist = (List<Aps>) MyHibernate.sqlQuery(0, 100, "from Aps");
		
		CosineSimilarity cos = new CosineSimilarity(epids, totalnum, apslist);
		
		Map<Integer,Double> similarityMap = new HashMap<Integer,Double>();
		double simi = 0.0;
		for(int i=0;i<apslist.size();i++){
			simi = cos.getCosineSimilarity(aps, apslist.get(i));
			similarityMap.put(apslist.get(i).getId(), simi);
			//System.out.println(simi);
		}
		
		//排序;
		List<Entry<Integer,Double>> sortList = new ArrayList<Entry<Integer,Double>>(similarityMap.entrySet());
		Collections.sort(sortList, new Comparator<Map.Entry<Integer,Double>>() {  
		    public int compare(Map.Entry<Integer,Double> o1,  
		            Map.Entry<Integer,Double> o2) {  
		        return (int) ((o2.getValue() - o1.getValue())*10000000);  
		    }  
		}); 
		
		for(int i=0;i<sortList.size();i++){
			System.out.println(i+" "+sortList.get(i).getKey()+"  "+sortList.get(i).getValue());
		}
	}

	@SuppressWarnings("unchecked")
	private static void dataPreparation() {
		Map<String, Integer> ep2id = new HashMap<String, Integer>();
		Map<Integer, Set<String>> keywords = new HashMap<Integer, Set<String>>();

		List<Ep2Id> ep2idEntity = (List<Ep2Id>) MyHibernate.sqlQuery(0, 1000, "from Ep2Id");
		List<Ep2KeyWord> ep2keywordEntity = (List<Ep2KeyWord>) MyHibernate.sqlQuery(0, 1000, "from Ep2KeyWord");
		for (int i = 0; i < ep2idEntity.size(); i++) {
			ep2id.put(ep2idEntity.get(i).getEp(), ep2idEntity.get(i).getId());
			Set<String> keyw = new HashSet<String>();
			for (int j = 0; j < ep2keywordEntity.size(); j++) {
				if (ep2keywordEntity.get(j).getEpId() == ep2idEntity.get(i).getId()) {
					keyw.add(ep2keywordEntity.get(j).getKeyWord());
				}
			}
			keywords.put(ep2idEntity.get(i).getId(), keyw);
		}
		CodeToAPs c2a = new CodeToAPs(ep2id, keywords);

		File folder = new File("F:\\Java\\DoSomeTest\\FIMIData\\codesnippets");
		if (folder.exists()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.println("文件夹:" + file.getAbsolutePath());
				} else {
					System.out.println("文件: " + file.getAbsolutePath());
					c2a.TransformC2A(file, file.getAbsolutePath());
					System.out.println("done");
				}
			}
		}
		// c2a.saveToDataBase();
		// c2a.saveKeyWordsToFiles();
		c2a.saveEpToDataBase();
	}
}
