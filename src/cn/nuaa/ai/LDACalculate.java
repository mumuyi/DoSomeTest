package cn.nuaa.ai;
/**
 * 计算代码片段所属的主题。
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
		//LDA 原文件路径;
		String originalDocsPath = "F:\\Java\\DoSomeTest\\LDAData\\LdaSPlitWords";
		//LDA 结果保存路径;
		String resultPath = "F:\\Java\\DoSomeTest\\LDAData\\LdaResults";
		//LDA 参数文件路径;
		String parameterFile= "F:\\Java\\DoSomeTest\\LDAData\\LdaParameter\\LdaParameters.txt";
		
		//获取LDA 参数;
		modelparameters ldaparameters = new modelparameters();
		LdaGibbsSampling.getParametersFromFile(ldaparameters, parameterFile);
		
		//获取LDA 原文件;
		Documents docSet = new Documents();
		docSet.readDocs(originalDocsPath);
		System.out.println("wordMap size " + docSet.termToIndexMap.size());
		
		//建立LDA 结果文件;
		FileUtil.mkdir(new File(resultPath));
		
		//实例化LDA model;
		LdaModel model = new LdaModel(ldaparameters);
		//初始化;
		System.out.println("1 Initialize the model ...");
		model.initializeModel(docSet);
		//学习;
		System.out.println("2 Learning and Saving the model ...");
		model.inferenceModel(docSet);
		//输出;
		System.out.println("3 Output the final model ...");
		model.saveIteratedModel(ldaparameters.iteration, docSet);
		System.out.println("Done!");
	}
}
