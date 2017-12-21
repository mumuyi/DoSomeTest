package cn.nuaa.ai.timi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SaveToFile {

	/**
	 * �����ļ�
	 * 
	 * @param fileName
	 *            �ļ�����
	 * @param filecontent
	 *            �ļ�����
	 * @return �Ƿ񴴽��ɹ����ɹ��򷵻�true
	 */
	public boolean createFile(String filePath, String filecontent) {
		Boolean bool = false;
		File file = new File(filePath);
		try {
			// ����ļ������ڣ��򴴽��µ��ļ�
			if (!file.exists()) {
				file.createNewFile();
				bool = true;
				System.out.println("success create file,the file is " + filePath);
				// �����ļ��ɹ���д�����ݵ��ļ���
				writeFileContent(filePath, filecontent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bool;
	}

	/**
	 * ���ļ���д������
	 * 
	 * @param filepath
	 *            �ļ�·��������
	 * @param newstr
	 *            д�������
	 * @return
	 * @throws IOException
	 */
	public boolean writeFileContent(String filepath, String newstr) throws IOException {
		Boolean bool = false;
		String filein = newstr + "\r\n";// ��д����У�����
		String temp = "";

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filepath);// �ļ�·��(�����ļ�����)
			// ���ļ�����������
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buffer = new StringBuffer();

			// �ļ�ԭ������
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				// ������֮��ķָ��� �൱�ڡ�\n��
				buffer = buffer.append(System.getProperty("line.separator"));
			}
			buffer.append(filein);

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buffer.toString().toCharArray());
			pw.flush();
			bool = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			// ��Ҫ���ǹر�
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return bool;
	}
}