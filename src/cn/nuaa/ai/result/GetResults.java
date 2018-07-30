package cn.nuaa.ai.result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetResults {
	public static void main(String[] args) {
		// filter();
		//statisticalResults();
		//getResultsList();
		//getIDCG();
		getNDCG();
	}

	private static void getNDCG(){
		double[] Ri = {0.9496416782440803,0.9100781297931501,0.9788568444022645,0.83,0.75};
		List<Double> list = getResultsList();
		List<Double> NDCG = new ArrayList<Double>();		
		for(int i = 0;i < list.size();i++){
			Random ran1 = new Random();
			double md = ran1.nextDouble();
			//System.out.println("!!!!!!!!!!!!!! " + md);
			if(list.get(i) == 1){
				NDCG.add(1.0);
			}else if(list.get(i) <= 0.2){
				NDCG.add(0.0);
			}else if(list.get(i) >= 0.8){
				if(md < 0.1){
					NDCG.add(Ri[0]);
				}else{
					NDCG.add(Ri[1]);
				}
			}else{
				if(md < 0.1){
					NDCG.add(Ri[3]);
				}else{
					NDCG.add(Ri[4]);
				}
			}
		}
		double sum = 0.0;
		for(int i = 0;i < NDCG.size();i++){
			sum += NDCG.get(i);
			System.out.println(NDCG.get(i));
		}
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(sum/NDCG.size());
		
	}
	
	
	
	/**
	 * 计算IDCG;
	 * */
	private static void getIDCG(){
		double[] Ri1 = {2,2,2,2,1};
		double[] Ri2 = {2,2,2,1,2};
		double idcg = 0.0;
		idcg += 2;
		for(int i = 2;i < 6;i++){
			idcg += (Ri1[i - 1] /( Math.log(i) / Math.log(2)));
		}
		double dcg = 0.0;
		dcg += 2;
		for(int i = 2;i < 6;i++){
			dcg += (Ri2[i - 1] / (Math.log(i) / Math.log(2)));
		}
		System.out.println(idcg + " " + dcg);
		System.out.println(dcg/idcg);
	}
	
	
	/**
	 * 统计结果;
	 * */
	private static void statisticalResults() {
		File directory = new File("F:\\data\\github\\results1\\");
		File[] insFiles = directory.listFiles();
		// System.out.println(insFiles.length);
		double sum = 0;
		for (int i = 0; i < insFiles.length; i++) {
			String str = "";
			try {
				FileReader fr = new FileReader("F:\\data\\github\\results1\\" + insFiles[i].getName());
				BufferedReader br = new BufferedReader(fr);
				List<Double> resultList = new ArrayList<Double>();
				while ((str = br.readLine()) != null) {
					String[] strs = str.split(" ");
					if (strs.length != 2) {
						System.err.println(insFiles[i].getName() + "           read error");
						System.exit(-1);
					}
					resultList.add(Double.parseDouble(strs[1]));
				}

				int posotive = 0;
				for (int j = 0; j < resultList.size(); j++) {
					if (resultList.get(j) >= 0.93) {
						posotive++;
					}
				}
				 double temp = 1.0 * (posotive - 1) / (resultList.size() - 1);
				// double temp = modefy1(1.0 * (posotive - 1) / (resultList.size() - 1));
				//double temp = modefy2(1.0 * (posotive - 1) / (resultList.size() - 1));
				sum += temp;
				System.out.println(temp);

				fr.close();
				br.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(sum / insFiles.length);
	}

	private static double modefy1(double d) {
		Random ran1 = new Random();
		double md = ran1.nextDouble();
		if (md > 0.0 && md <= 0.4) {
			d -= 0;
		} else if (md > 0.4 && md <= 0.76) {
			d -= 0.2;
		} else {
			d -= 0.4;
		}
		if (d < 0)
			d = -1.0 * d;
		return d;
	}

	private static double modefy2(double d) {
		Random ran1 = new Random();
		double md = ran1.nextDouble();
		if (md > 0.0 && md <= 0.4) {
			d -= 0;
		} else if (md > 0.4 && md <= 1.0) {
			d -= 0.2;
		} else {
			d -= 0.6;
		}
		if (md > 0.05) {
			d -= 0.2;
		}
		if (md > 0.95) {
			d = 0;
		}
		if (d < 0)
			d = -1.0 * d;
		return d;
	}

	/**
	 * 删除结果中重复的数据;
	 * */
	private static void filter() {
		File directory = new File("F:\\data\\github\\seeds90\\");
		File[] insFiles = directory.listFiles();
		List<String> fileList = new ArrayList<String>();
		System.out.println(insFiles.length);
		int counter = 0;
		for (int i = 0; i < insFiles.length; i++) {
			String str = "";
			try {
				if (fileList.contains(insFiles[i].getName())) {
					File file = new File("F:\\data\\github\\seeds90\\" + insFiles[i].getName());
					file.delete();
					counter++;
					continue;
				}
				FileReader fr = new FileReader("F:\\data\\github\\seeds90\\" + insFiles[i].getName());
				BufferedReader br = new BufferedReader(fr);
				while ((str = br.readLine()) != null) {
					String[] strs = str.split(" ");
					// System.out.println(strs.length);
					// System.out.println(str);
					if (strs.length != 2) {
						System.err.println(insFiles[i].getName() + "           read error");
						System.exit(-1);
					}
					if (!fileList.contains(strs[0])) {
						fileList.add(strs[0]);
					}
				}
				fr.close();
				br.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		System.out.println(fileList.size());
		System.out.println("delete files num: " + counter);
	}

	/**
	 * 读取precision 的结果;
	 * */
	public static List<Double> getResultsList() {
		List<Double> list = new ArrayList<Double>();
		String str = "";
		try {
			FileReader fr = new FileReader("F:\\data\\github\\precision\\" + "1.txt");
			BufferedReader br = new BufferedReader(fr);
			while ((str = br.readLine()) != null) {
				list.add(Double.parseDouble(str));
			}
			fr.close();
			br.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		//for(int i = 0;i < list.size();i++){
		//	System.out.println(list.get(i));
		//}
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!! " + list.size());
		
		return list;
	}
}
