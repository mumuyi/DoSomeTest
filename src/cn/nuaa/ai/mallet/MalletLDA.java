package cn.nuaa.ai.mallet;
/**
 * Mallet 实现LDA 的方法，针对代码的具体效果还没有测试，
 * 并且，从目前的情况来看，似乎需要对数据进行预处理，所有数据放入一个文。
 * */



import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

public class MalletLDA {
	private static int numTopics = 100;
	
	public static void main(String[] args) throws Exception {

		Train();
		
		
		File modelFile = new File("F:\\Java\\DoSomeTest\\MalletData\\LDA\\Model\\model.md");
		ParallelTopicModel model = ParallelTopicModel.read(modelFile);

		File instanceFile = new File("F:\\Java\\DoSomeTest\\MalletData\\LDA\\Model\\instance.md");
		InstanceList instances = InstanceList.load(instanceFile);
		

		show(model,instances);
		
		test(model,instances);
		
	}

	private static void Train() throws Exception {
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(
				new File("F:\\Java\\DoSomeTest\\MalletData\\LDA\\StopWords\\en.txt"), "UTF-8", false, false, false));
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		// Reader fileReader = new InputStreamReader(new FileInputStream(new
		// File(args[0])), "UTF-8");
		Reader fileReader = new InputStreamReader(
				new FileInputStream(new File("F:\\Java\\DoSomeTest\\MalletData\\LDA\\Source\\ap.txt")));
		instances
				.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data,
																															// label,
																															// name
																															// fields
		
		//Instance instance = new Instance(data, target, name, source);
		//instances.add(instance);

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is the parameter for a single dimension of the Dirichlet
		// prior.
		
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and
		// combine
		// statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(50);
		model.estimate();

		File modelFile = new File("F:\\Java\\DoSomeTest\\MalletData\\LDA\\Model\\model.md");
		model.write(modelFile);

		File instanceFile = new File("F:\\Java\\DoSomeTest\\MalletData\\LDA\\Model\\instance.md");
		instances.save(instanceFile);
	}
	
	private static void show(ParallelTopicModel model,InstanceList instances){
		// Show the words and topics in the first instance

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();

		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;

		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)),
					topics.getIndexAtPosition(position));
		}
		
		System.out.println(out);
		

		// Estimate the topic distribution of the first instance,
		// given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < numTopics; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
			System.out.println(out);
		}
		
		System.out.println(""+instances.get(0).getName());
	}
	
	private static void test(ParallelTopicModel model,InstanceList instances){
		// Create a new instance with high probability of topic 0
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		Alphabet dataAlphabet = instances.getDataAlphabet();
		StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
			rank++;
		}

		// Create a new instance named "test instance" with empty target and
		// source fields.
		InstanceList testing = new InstanceList(instances.getPipe());
		testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

		TopicInferencer inferencer = model.getInferencer();
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		System.out.println("0\t" + testProbabilities[0]);
		System.out.println(testProbabilities.length);
	}
}
