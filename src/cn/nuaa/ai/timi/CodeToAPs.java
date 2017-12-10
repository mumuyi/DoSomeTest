package cn.nuaa.ai.timi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cn.nuaa.ai.dao.MyHibernate;
import cn.nuaa.ai.entity.Aps;
import cn.nuaa.ai.entity.Ep2Id;
import cn.nuaa.ai.entity.Ep2KeyWord;

public class CodeToAPs {
	// epΪkey;idΪvalue;
	private static Map<String, Integer> ep2id = new HashMap<String, Integer>();
	// ep��idΪkey;ep���ִ���Ϊvalue;
	private static Map<Integer, Integer> aps = new HashMap<Integer, Integer>();
	// keywords of ep;
	private static Map<Integer,Set<String>> keywords = new HashMap<Integer,Set<String>>();

	public static void main(String[] args) {
		
		/*
		String str = "dictionaryUUID=that.dictionaryUUID(asdsa,dasdsa,fsd,fd,fd,f,d,gf,g)";
		Set<String> set = getKeyWords(str);
		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
        */

		// ��ȡ�ļ�;
		File file = new File("F:\\Java\\DoSomeTest\\FIMIData\\testcode.txt");
		// ��ȡ������;
		List<String> content = getContent(file);
		System.out.println("������:");
		for (int i = 0; i < content.size(); i++)
			System.out.println(content.get(i));

		for (int i = 0; i < content.size(); i++) {
			// System.out.println(temps[i]);
			// ��code ת��Ϊep;
			String ep = code2ep(content.get(i));
			System.out.println(ep);
			// ά��ep��;
			if (!ep2id.containsKey(ep)) {
				// �����µ�ep;
				int id = ep2id.size();
				ep2id.put(ep, id);
				// �����µ�keywords set;
				keywords.put(id,getKeyWords(content.get(i)));
			}else{
				//����keywords set;
				int id = ep2id.get(ep);
				Set<String> set = getKeyWords(content.get(i));
				Iterator<String> iter = set.iterator();
				while (iter.hasNext()) {
					keywords.get(id).add(iter.next());
				}
			}
			
			// תΪaps;
			int id = ep2id.get(ep);
			if (aps.containsKey(id)) {
				aps.replace(id, aps.get(id) + 1);
			} else {
				aps.put(id, 1);
			}
		}
		// ���ep2id ��;
		System.out.println("\nep2id��:");
		for (String key : ep2id.keySet()) {
			System.out.print(key + "   " + ep2id.get(key)+"    ");
			Set<String> set = keywords.get(ep2id.get(key));
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()) {
				System.out.print(iter.next()+"  ");
			}
			System.out.println();
		}
		// ���һ������Ƭ�ε�aps;
		System.out.println("\naps:");
		for (int key : aps.keySet()) {
			System.out.println(key + "   " + aps.get(key));
		}
		
		//�����ݴ������ݿ�;
		/*
		for(String ep : ep2id.keySet()){
			//��ep��ep-id �������ݿ�;
			Ep2Id epid = new Ep2Id();
			epid.setEp(ep);
			epid.setId(ep2id.get(ep));
			MyHibernate.sqlSaveOrUpdate(epid);
			
			//����ep-id �� ep �ؼ���;
			int id = ep2id.get(ep);
			Set<String> set = keywords.get(id);
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()) {
				Ep2KeyWord epkeyword = new Ep2KeyWord();
				epkeyword.setEpId(id);
				epkeyword.setKeyWord(iter.next());
				MyHibernate.sqlSaveOrUpdate(epkeyword);
			}
		}
		//����ǰ����Ƭ�ε�aps�������ݿ�;
		List<Integer> eps = new ArrayList<Integer>();
		List<Integer> L = new ArrayList<Integer>();
		for(int epid : aps.keySet()){
			eps.add(epid);
			L.add(aps.get(epid));
		}
		Aps ap = new Aps();
		ap.setEpIds((String)eps.toString().substring(1,eps.toString().length()-1));
		ap.setFreqs((String)L.toString().substring(1,L.toString().length()-1));
		MyHibernate.sqlSaveOrUpdate(ap);
		*/
	}

	private static String code2ep(String code) {
		// System.out.println(code);
		// �������еĹؼ���ת��Ϊ '#';
		String str = code.replaceAll("[^;.()=\\s*,\\r]", "#");
		// System.out.println(str);
		// ȥ���ظ��� '#';
		String ep = str.replaceAll("[#]+", "#");
		// System.out.println(ep);
		return ep;
	}

	private static Set<String> getKeyWords(String code) {
		Set<String> list = new HashSet<String>();
		String[] tokens = code.split("\\.|=|\\(|\\)|\\_|,|;|\\<|\\>|\\+|\\-|\\*|\\/|\\ ");
		for (int j = 0; j < tokens.length; j++) {
			int index = 0;
			String temp = tokens[j];
			String token = temp.replaceAll(" ", "");
			//ȥ�����ַ���;
			if(token.equals("")){
				continue;
			}else{
				//ȥ��������;
				Pattern pattern = Pattern.compile("[0-9]{1,}"); 
				if(pattern.matcher((CharSequence)token).matches()){
					continue;
				}
			}
			
			System.out.println(token+"!!!!!!!!!!!!!!!!!!!!!");
			for (int i = 0; i < token.length(); i++) {
				char ch = token.charAt(i);
				if (ch == '_') {
					list.add(token.substring(index, i));
					index = i;
				} else if (ch <= 'Z' && ch >= 'A') {
					if (i > 0) {
						char ch1 = token.charAt(i - 1);
						if (ch1 <= 'z' && ch1 >= 'a') {
							list.add(token.substring(index, i));
							index = i;
						}
					}
				}
			}
			if(index == 0){
				list.add(token);
			}else if(index < token.length()){
				list.add(token.substring(index,token.length()));
			}
		}
		return list;
	}

	private static List<String> getContent(File file) {
		BufferedReader bufread = null;
		String temp = null;
		List<String> cleanList = new ArrayList<String>();
		try {
			bufread = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while ((temp = bufread.readLine()) != null) {
				/*
				 * // ȥ�������Ʊ���ͻس�; String temp1 = temp.replaceAll("\t|\n|\r",
				 * ""); // ȥ�����ж���Ŀո�; String temp2 = temp1.replaceAll("[ ]+",
				 * " ").replaceAll("; ", ";").replaceAll(" = ", "="); // ȥ��������;
				 * String temp3 = temp2.replaceAll("\\{|\\}", "");
				 * if(!temp3.equals(" "
				 * )&&!temp3.equals("\t")&&!temp3.equals('\n'))
				 * cleanList.add(temp3);
				 */
				// ȥ��������;
				String temp1 = temp.replaceAll("\\{|\\}", " ");
				// ȥ�������Ʊ���ͻس�;
				String temp2 = temp1.replaceAll("\t|\n|\r", " ");
				// ȥ�����ж���Ŀո�;
				String temp3 = temp2.replaceAll("[ ]+", " ").replaceAll("; ", ";").replaceAll(" = ", "=");
				String temp4 = temp3.replaceAll("[\t]+", "");

				if (!temp4.equals(" ") && !temp4.equals("\t") && !temp4.equals('\n'))
					cleanList.add(temp4);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bufread.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ȥ������������;
		cleanList.remove(0);
		// ȥ����ʼ�ͽ�β�Ŀո�;
		for (int i = 0; i < cleanList.size(); i++) {
			if (cleanList.get(i).charAt(0) == ' ') {
				cleanList.set(i, cleanList.get(i).substring(1, cleanList.get(i).length()));
			}

			if (cleanList.get(i).charAt(cleanList.get(i).length() - 1) == ' ') {
				cleanList.set(i, cleanList.get(i).substring(0, cleanList.get(i).length() - 1));
			}
		}
		// ȥ����forѭ���еķֺ�֮�������ֺ�;
		for (int i = 0; i < cleanList.size(); i++) {
			if (!cleanList.get(i).startsWith("for")) {
				cleanList.set(i, cleanList.get(i).replaceAll(";", ""));
			}
		}

		return cleanList;
	}
}
