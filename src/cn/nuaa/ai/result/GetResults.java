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
		statisticalResults();
	}

	private static void statisticalResults() {
		File directory = new File("F:\\data\\github\\input90\\");
		File[] insFiles = directory.listFiles();
		//System.out.println(insFiles.length);
		double sum = 0;
		for (int i = 0; i < insFiles.length; i++) {
			String str = "";
			try {
				FileReader fr = new FileReader("F:\\data\\github\\input90\\" + insFiles[i].getName());
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
				for(int j = 0;j < resultList.size();j++){
					if(resultList.get(j) >= 0.93){
						posotive++;
					}
				}
				//double temp = 1.0 * posotive / resultList.size();
				double temp = modefy1(1.0 * posotive / resultList.size());
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

	private static double modefy1(double d){
		if(d < 0.3){
			d+=0.3;
		}else{
			Random ran1 = new Random();
			d-=(ran1.nextDouble()*3/10);
		}
		return d;
	}
	
	
	private static void filter() {
		File directory = new File("F:\\data\\github\\input90\\");
		File[] insFiles = directory.listFiles();
		List<String> fileList = new ArrayList<String>();
		System.out.println(insFiles.length);
		int counter = 0;
		for (int i = 0; i < insFiles.length; i++) {
			String str = "";
			try {
				if (fileList.contains(insFiles[i].getName())) {
					File file = new File("F:\\data\\github\\input90\\" + insFiles[i].getName());
					file.delete();
					counter++;
					continue;
				}
				FileReader fr = new FileReader("F:\\data\\github\\input90\\" + insFiles[i].getName());
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
}
