package cn.nuaa.ai.timi;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.nuaa.ai.dao.MyHibernate;
import cn.nuaa.ai.entity.Ep2Id;
import cn.nuaa.ai.entity.Ep2KeyWord;

public class RecommedationActivity {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
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
					c2a.TransformC2A(file,file.getAbsolutePath());
					System.out.println("done");
				}
			}
		}
		//c2a.saveToDataBase();
		//c2a.saveKeyWordsToFiles();
		c2a.saveEpToDataBase();
	}
}
