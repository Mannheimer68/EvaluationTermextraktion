package termEvaluation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import termEvaluation.Functions;
import termEvaluation.Term;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Venu {
	/*
	 * class variables
	 */
	private static Annotation document = null;
	private static ArrayList<String> ssrPrimaryIndex = new ArrayList<String>();
	private static ArrayList<String> ssrUniqueList = new ArrayList<String>();
	private static ArrayList<String> nounList = new ArrayList<String>();
	private static ArrayList<Term> scoredSSRList = new ArrayList<Term>();
	private static ArrayList<Term> scoredNounList = new ArrayList<Term>();
	private static ArrayList<Term> scoredTermList = new ArrayList<Term>();
	private static ArrayList<String> noiseTerms = new ArrayList<String>();
	private static int printStatus = 0;

	/*
	 * get unique entries of the list
	 */
	public static ArrayList<String> getUniqueList(ArrayList<String> list) {
		ArrayList<String> uniqueList = new ArrayList<String>();
		Iterator<String> itr = list.iterator();
		while (itr.hasNext()) {
			String check = itr.next().toString();
			if (uniqueList.contains(check)) {
				// do not add duplicates
			} else {
				uniqueList.add(check);
			}
		}
		return uniqueList;
	}

	/*
	 * counts the co-occurence of two given terms in a given ArrayList returns
	 * the number of co-occurences
	 */
	public static Integer countCoOccurencesOfTermsInList(
			ArrayList<String> ssrIndex, String ssr, String noun) {
		int count = 0;
		Iterator<String> itr = ssrIndex.iterator();
		while (itr.hasNext()) {
			String check = itr.next().toString();
			if (check.contains(ssr) && check.contains(noun)) {
				count = count + 1;
			}
		}
		return count;
	}

	/*
	 * removes noise from the list removing terms containing any of the
	 * following characters: = " , % \ / . also removing entries shorter than
	 * two characters the stanford parser sometimes tags the word "the" as a
	 * noun which is not the case
	 */
	public static ArrayList<String> eraseNoise(ArrayList<String> list) {
		Iterator<String> itr = list.iterator();
		while (itr.hasNext()) {
			String check = itr.next().toString();
			if (check.contains("=") || check.contains("%")
					|| check.contains(",") || check.contains("\\")
					|| check.contains("\"") || check.contains("/")
					|| check.contains(".") || check.length() < 2
					|| noiseTerms.contains(check)) {
				itr.remove();
			}
		}
		return list;
	}

	/*
	 * transpose a given matrix returns the transposed matrix
	 */
	public static int[][] transposeMatrix(int[][] matrix) {
		int[][] matrixT = new int[matrix[0].length][matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrixT[j][i] = matrix[i][j];
			}
		}
		return matrixT;

	}

	/*
	 * calculate the term scores using hits algorithm
	 */
	public static void calculateTermScores(ArrayList<String> ssrList,
			ArrayList<String> nounList) {
		// initialize the size of the matrix A and the transposed matrix AT
		int ssrLength = ssrList.size();
		int nounLength = nounList.size();
		// initialize matrix A and transposed matrix AT
		int[][] matrixA = new int[ssrLength][nounLength];
		int[][] matrixAT = new int[nounLength][ssrLength];
		// initialize the hub and authority vectors
		double[] hub = new double[ssrLength];
		double[] authority = new double[nounLength];
		double[] hubOld = new double[ssrLength];
		double[] authorityOld = new double[nounLength];

		// set values for the matrix A
		// link strength is the number of co-occurrences of the SSR term and the
		// noun in the SSR primary index
		for (int i = 0; i < ssrLength; i++) {
			for (int j = 0; j < nounLength; j++) {
				matrixA[i][j] = countCoOccurencesOfTermsInList(ssrPrimaryIndex,
						ssrList.get(i), nounList.get(j));
			}
		}

		// calculate transpose matrix A
		matrixAT = transposeMatrix(matrixA);

		// set initial values for the hub vector to 1
		for (int i = 0; i < ssrLength; i++) {
			hub[i] = 1;
		}

		// HITS algorithm
		// stops when the calculated values are very close to the previous
		// calculated values
		while (Math.abs(Arrays.stream(hubOld).sum() - Arrays.stream(hub).sum()) > 0.00001
				|| Math.abs(Arrays.stream(authorityOld).sum()
						- Arrays.stream(authority).sum()) > 0.00001) {
			// set old authority value for comparing
			authorityOld = authority;
			// calculate hub and authority vectors
			authority = getMatrixVectorMultiplikation(matrixAT, hub);
			hub = getMatrixVectorMultiplikation(matrixA, authority);
			// normalize the vectors
			hub = normalizeVector(hub);
			authority = normalizeVector(authority);
			// set old hub value for comparing
			hubOld = hub;
		}

		// combine calculated hub scores and ssr terms
		for (int i = 0; i < hub.length; i++) {
			scoredSSRList.add(new Term(ssrList.get(i), hub[i]));
		}

		// combine calculated authority scores and nouns
		for (int i = 0; i < authority.length; i++) {
			scoredNounList.add(new Term(nounList.get(i), authority[i]));
		}

	}

	/*
	 * normalize a given vector and return the unit vector
	 */
	public static double[] normalizeVector(double[] vector) {
		double denominator = 0;

		for (int i = 0; i < vector.length; i++) {
			denominator = denominator + (vector[i] * vector[i]);
		}
		denominator = Math.sqrt(denominator);

		for (int i = 0; i < vector.length; i++) {
			vector[i] = vector[i] / denominator;
		}
		return vector;
	}

	/*
	 * multiply matrix with vector and return the calculated vector
	 */
	public static double[] getMatrixVectorMultiplikation(int[][] matrix,
			double[] vector) {
		double[] calculatedVector = new double[matrix.length];
		// multiply matrix with vector
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				calculatedVector[i] = calculatedVector[i] + matrix[i][j]
						* vector[j];
			}
		}
		return calculatedVector;
	}

	/*
	 * return the lemmatized term
	 */

	static String getLemmatizedTerm(String s) {
		String lemmatizedTerm = "";
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(s);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				if (lemmatizedTerm == "") {
					lemmatizedTerm = token.get(LemmaAnnotation.class);
				} else {
					lemmatizedTerm = lemmatizedTerm + " "
							+ token.get(LemmaAnnotation.class);
				}
			}
		}
		return lemmatizedTerm;
	}

	/*
	 * set noun list from the parsed text
	 */
	public static void setNouns() {
		List<CoreMap> sentences = document
				.get(CoreAnnotations.SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence
					.get(CoreAnnotations.TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token
						.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				if (pos.toString().contains("NN")) {
					// lemmatize the noun
					String noun = word.toLowerCase().toString(); // getLemmatizedTerm(word.toLowerCase().toString());
					if (nounList.contains(noun)) {
					} else {
						// add nouns to the noun list if they are not already
						// included
						nounList.add(noun);
					}
				}
			}
		}
	}

	/*
	 * add all ArrayList items to a string (each item in a new line)
	 */
	public static String addArrayListToString(ArrayList<String> stringList) {
		String text = "";
		// loop over all terms and write them into a separate line of the string
		for (String string : stringList) {
			text = text + string + System.lineSeparator();
		}
		return text;
	}

	/*
	 * sort the term values by their name
	 */
	static ArrayList<String> sortValuesAlphabetical(ArrayList<String> list) {
		Collections.sort(list);
		return list;
	}

	public static void setAnnotateDocument(String text) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// create an empty Annotation just with the given text
		document = new Annotation(text);
		// run all Annotators on this text
		pipeline.annotate(document);
	}

	public static void setSSR(String text) {
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			String ssrTokens = "";
			Tree tree = sentence.get(TreeAnnotation.class);
			// Get dependency tree
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
			Collection<TypedDependency> td = gs.typedDependenciesCollapsed();
			Object[] list = td.toArray();
			if (printStatus == 1) {
				System.out.println(td.toString());
			}
			TypedDependency typedDependency;
			for (Object object : list) {
				int indexBeginString = 0;
				int indexEndString = 0;
				typedDependency = (TypedDependency) object;

				// calculate the start and end index of the string
				// use lowest start point as start
				if (typedDependency.gov().beginPosition() < typedDependency
						.dep().beginPosition()) {
					indexBeginString = typedDependency.gov().beginPosition();
					indexEndString = typedDependency.dep().endPosition();
				} else {
					indexBeginString = typedDependency.dep().beginPosition();
					indexEndString = typedDependency.gov().endPosition();
				}

				// write all SSR into a string generating a tokenstream
				if (typedDependency.reln().getShortName().contains("root")) {
					// do not add items which are of the type "root"
				} else {
					ssrTokens = ssrTokens
							+ typedDependency.reln().getShortName().toString()
							+ ": "
							+ text.toString()
									.substring(indexBeginString, indexEndString)
									.toLowerCase() + System.lineSeparator();
				}

				// add all relevant SSR to the secondary index
				// secondary index only includes unique SSR
				if (typedDependency.reln().getShortName().contains("nmod") // relations
						|| typedDependency.reln().getShortName()
								.contains("amod") // adjective modifiers
						|| typedDependency.reln().getShortName()
								.contains("compound") // adjective modifiers
						|| typedDependency.reln().getShortName().equals("dobj")) { // svo
					// if SSR is not already included and term length is larger
					// than 3 or term includes line break
					if (ssrUniqueList.contains(text.toString()
							.substring(indexBeginString, indexEndString)
							.toLowerCase())
							|| text.toString()
									.substring(indexBeginString, indexEndString)
									.length()
									- text.toString()
											.substring(indexBeginString,
													indexEndString)
											.replaceAll(" ", "").length() > 2
							|| text.toString()
									.substring(indexBeginString, indexEndString)
									.length()
									- text.toString()
											.substring(indexBeginString,
													indexEndString)
											.replaceAll("\n", "").length() > 0) {
						// do not add items which are already in the list
					} else {
						// add ssr to list and replace line breaks by space
						ssrUniqueList.add(text.toString()
								.substring(indexBeginString, indexEndString)
								.toLowerCase());
					}
				}
			}
			// add generated tokenstream to the SSR primary index
			ssrPrimaryIndex.add(ssrTokens);
		}
	}

	private static void setPrintStatus(int inpPrintStatus) {
		printStatus = inpPrintStatus;
	}

	/*
	 * set the terms which are indicated as noise
	 */
	public static void setNoiseTerms() {
		noiseTerms.add("the");
	}

	public static void startVenu(String inpTextPath, int inpPrintStatus)
			throws IOException {
		// read file and save to text
		String text = Functions.readFile(inpTextPath, Charset.defaultCharset());
		// set print status
		setPrintStatus(inpPrintStatus);
		// annotate text
		setAnnotateDocument(text);
		// set SSR primary index and unique SSR
		setSSR(text);
		// set the terms which are indicated as noise
		setNoiseTerms();
		// sort unique SSR alphabetical
		ssrUniqueList = sortValuesAlphabetical(ssrUniqueList);
		// erase noise from the sorted unique SSR list
		ssrUniqueList = eraseNoise(ssrUniqueList);

		// set the nouns
		setNouns();
		// sort nouns alphabetical
		nounList = sortValuesAlphabetical(nounList);
		// erase noise from the nouns list
		nounList = eraseNoise(nounList);

		// calculate the term scores
		// creating a matrix and using hits algorithm
		calculateTermScores(ssrUniqueList, nounList);
		
		scoredTermList.addAll(scoredNounList);
		scoredTermList.addAll(scoredSSRList);
		// sort lists by score
		scoredNounList = sortValuesByScore(scoredNounList);
		scoredSSRList = sortValuesByScore(scoredSSRList);
		scoredTermList = sortValuesByScore(scoredTermList);

		printResults();	
		
		Functions.writeStringToFile( "venu_nouns_scored", Functions.addTermsWithScoreToString(scoredNounList));
		Functions.writeStringToFile( "venu_combined_scored", Functions.addTermsWithScoreToString(scoredTermList));
		Functions.writeStringToFile( "venu_ssr_scored", Functions.addTermsWithScoreToString(scoredSSRList));
		 
		//Evaluation eva = new Evaluation(scoredNounList,"C:/Users/Mannheimer/Desktop/Bachelor/Daten/plain.txt");
		//eva.printValues();
		

	}
	/*
	 * print results
	 */
	private static void printResults() {
		if (printStatus == 1) {
			// print nouns
			System.out.println("Sorted Nouns:");
			for (Term term : scoredNounList) {
				System.out.println(String.format(
						"The noun [%s] has a score of [%s]", term.term,
						term.score));
			}

//			// print SSR
//			System.out.println("Sorted SSR:");
//			for (Term term : scoredSSRList) {
//				System.out.println(String.format(
//						"The SSR [%s] has a score of [%s]", term.term,
//						term.score));
//			}
		}
	}

	/*
	 * sort the term values by their score
	 */
	static ArrayList<Term> sortValuesByScore(ArrayList<Term> terms) {
		Collections.sort(terms, new Comparator<Term>() {
			@Override
			public int compare(Term t1, Term t2) {
				return Double.compare(t2.score, t1.score);
			}
		});
		return terms;
	}

	public static void main(String[] args) throws IOException {
		startVenu("C:/Users/Mannheimer/Desktop/Bachelor/Daten/plain.txt", 0);
	}

}