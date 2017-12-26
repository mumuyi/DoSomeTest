package cn.nuaa.ai.timi;

import java.util.ArrayList;
import java.util.List;

import cn.nuaa.ai.dao.MyHibernate;
import cn.nuaa.ai.entity.Aps;

public class CosineSimilarity {
	private static List<Integer> epIds;
	private static int TotalSnippetsNum = 0;
	private static List<Aps> apsList;
	
	public static void main(String[] args) {

		//这里加了一个10;是因为现在数据库里面只有一个数据,会造成计算结果为0;当数据多了之后就可以删掉了;
		int totalnum = getTotalSnippetNum(); 

		//System.out.println(getLxi(aps1, 0));
		//System.out.println(getGx(111));
		//初始化了一个epId表,即确定了整个系统中一共有多少个ep;实际使用时,需要按照ep2id表中的实际数据来初始化;
		List<Integer> epids = new ArrayList<Integer>();
		int TotalEpNum = (int) MyHibernate.sqlGetRecordNum("select count(*) from Ep2Id");
		for(int i=0;i<TotalEpNum;i++){
			epids.add(i);
		}
		//System.out.println("TotalEpNum   "+TotalEpNum);
		//获取aps表;
		@SuppressWarnings("unchecked")
		List<Aps> apslist = (List<Aps>) MyHibernate.sqlQuery(0, 100, "from Aps");
		
		Aps aps1 = apslist.get(0);
		Aps aps2 = apslist.get(0);
		
		CosineSimilarity cos = new CosineSimilarity(epids, totalnum, apslist);
		System.out.println(cos.getCosineSimilarity(aps1, aps2));
	}

	public CosineSimilarity(List<Integer> epids,int totalnum,List<Aps> apslist){
		epIds = epids;
		TotalSnippetsNum = totalnum;
		apsList = apslist;
	}
	
	private static int getLxi(Aps aps, int id) {
		int ans = 0;
		List<Integer> EpIds = String2List(aps.getEpIds());
		List<Integer> Freqs = String2List(aps.getFreqs());

		// for(int i=0;i<EpIds.size();i++){
		// System.out.println(EpIds.get(i)+" "+Freqs.get(i));
		// }

		if (EpIds.contains(id)) {
			ans = Freqs.get(EpIds.indexOf(id));
		}
		//System.out.println("Lxi:"+ans);
		return ans;
	}

	private static List<Integer> String2List(String str) {
		List<String> tempList = java.util.Arrays.asList(str.split(", "));
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < tempList.size(); i++) {
			list.add(Integer.parseInt(tempList.get(i)));
		}
		return list;
	}

	private static int getTotalSnippetNum() {
		return (int) MyHibernate.sqlGetRecordNum("select count(*) from Aps");
	}

	private static int getGx(int id) {
		int sum = 0;
		for (int i = 0; i < apsList.size(); i++) {
			List<Integer> EpIds = String2List(apsList.get(i).getEpIds());
			if (EpIds.contains(id)) {
				sum++;
			}
		}
		//System.out.println("Gx:"+sum);
		return sum;
	}

	private static double getHxi(Aps aps, int id) {
		double hxi = 1.0*getLxi(aps, id);
		double gx = 1.0*getGx(id);
		double N=1.0*TotalSnippetsNum;
		//System.out.println("hxi:"+ hxi);
		//System.out.println("gx:"+ gx);
		//System.out.println("N:"+N);
		//System.out.println("TF:"+(1.0+Math.log(hxi+1.0)));
		//System.out.println("IDF:"+Math.log(N/(gx+1.0)));
		return ((1.0+Math.log(hxi+1.0))*Math.log(N/gx));
	}

	private static List<Double> getVector(Aps aps) {
		List<Double> vec = new ArrayList<Double>();
		for (int i = 0; i < epIds.size(); i++) {
			vec.add(getHxi(aps, epIds.get(i)));
		}
		return vec;
	}

	public double getCosineSimilarity(Aps aps1, Aps aps2) {
		double similarity = 0.0;
		List<Double> vec1 = getVector(aps1);
		List<Double> vec2 = getVector(aps2);

		//System.out.println(vec1);
		//System.out.println(vec2);
		
		double vecmulti = 0.0;
		for (int i = 0; i < vec1.size(); i++) {
			vecmulti += vec1.get(i) * vec2.get(i);
		}
		double mold1 = getMoldLength(vec1);
		double mold2 = getMoldLength(vec2);
		
		similarity = vecmulti/(mold1*mold2);
		
		//System.out.println(vecmulti);
		//System.out.println(mold1);
		//System.out.println(mold2);
		
		return similarity;
	}

	private static double getMoldLength(List<Double> vec) {
		double ans = 0.0;
		for (int i = 0; i < vec.size(); i++) {
			ans += vec.get(i) * vec.get(i);
		}
		return Math.sqrt(ans);
	}
}
