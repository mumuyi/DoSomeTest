package cn.nuaa.ai.rosf;
/**
 * 将文本形式存储的训练集和测试集数据存入数据库。
 * */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cn.nuaa.ai.dao.MyHibernate;
import cn.nuaa.ai.entity.TestData;

public class File2DB {
	public static void main(String[] arg) throws Exception {
		
		for(int num=1,counter=1;num<21;num++){
			//List<String[]> data = getFileData(
			//		"C:\\Users\\ai\\Desktop\\ROSF\\labeled code snippets for 15 train queries\\pool_labled_"+num+".txt");
			List<String[]> data = getFileData(
					"C:\\Users\\ai\\Desktop\\ROSF\\labeled code snippets for 20 test queries\\pool_labled_"+num+".txt");
			TestData ted=new TestData();
			//TrainData trd=new TrainData();
			for(int i=0;i<data.size();i++){
				System.out.println((i+1)+"        "+data.get(i)[0] + "--------" + data.get(i)[1]);
				ted.setId(counter);
				ted.setQueryNum(num);
				ted.setFqName(data.get(i)[0]);
				ted.setScore(Integer.parseInt(data.get(i)[1]));
				MyHibernate.sqlSaveOrUpdate(ted);
				counter++;
			}
		}
		
	}

	private static List<String[]> getFileData(String filename) throws Exception {
		//System.out.println("Let's have some fun");
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		String[] arrs = null;
		List<String[]> data = new ArrayList<String[]>();
		while ((line = br.readLine()) != null) {
			arrs = line.split(": ");
			data.add(arrs);
			//System.out.println(arrs[0] + "--------" + arrs[1]);
		}
		br.close();
		fr.close();

		return data;
	}
}
