package cn.nuaa.ai;
/**
 * �������Ƭ�����������⡣
 * */
import java.io.File;
import java.io.IOException;

import liuyang.nlp.lda.com.FileUtil;
import liuyang.nlp.lda.main.Documents;
import liuyang.nlp.lda.main.LdaGibbsSampling;
import liuyang.nlp.lda.main.LdaGibbsSampling.modelparameters;
import liuyang.nlp.lda.main.LdaModel;


public class LDACalculate {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//LDA ԭ�ļ�·��;
		String originalDocsPath = "F:\\Java\\DoSomeTest\\LDAData\\LdaSPlitWords";
		//LDA �������·��;
		String resultPath = "F:\\Java\\DoSomeTest\\LDAData\\LdaResults";
		//LDA �����ļ�·��;
		String parameterFile= "F:\\Java\\DoSomeTest\\LDAData\\LdaParameter\\LdaParameters.txt";
		
		//��ȡLDA ����;
		modelparameters ldaparameters = new modelparameters();
		LdaGibbsSampling.getParametersFromFile(ldaparameters, parameterFile);
		
		//��ȡLDA ԭ�ļ�;
		Documents docSet = new Documents();
		docSet.readDocs(originalDocsPath);
		System.out.println("wordMap size " + docSet.termToIndexMap.size());
		
		//����LDA ����ļ�;
		FileUtil.mkdir(new File(resultPath));
		
		//ʵ����LDA model;
		LdaModel model = new LdaModel(ldaparameters);
		//��ʼ��;
		System.out.println("1 Initialize the model ...");
		model.initializeModel(docSet);
		//ѧϰ;
		System.out.println("2 Learning and Saving the model ...");
		model.inferenceModel(docSet);
		//���;
		System.out.println("3 Output the final model ...");
		model.saveIteratedModel(ldaparameters.iteration, docSet);
		System.out.println("Done!");
	}
}
