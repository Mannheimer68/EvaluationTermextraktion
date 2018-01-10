package termEvaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

/*
 * Class Venu
 * Implementation of the algorithm from Venu et al.
 * Using SSR and HITS-Algorithm to calculate domain terms
 */
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
	private static String nGrams;

	/*
	 * get unique entries of the list
	 */
	public static ArrayList<String> getUniqueList(ArrayList<String> inpList) {
		ArrayList<String> uniqueList = new ArrayList<String>();
		Iterator<String> itr = inpList.iterator();
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
			ArrayList<String> inpSsrIndex, String inpSsr, String inpNoun) {
		int count = 0;
		Iterator<String> itr = inpSsrIndex.iterator();
		while (itr.hasNext()) {
			String check = itr.next().toString();
			if (check.contains(inpSsr) && check.contains(inpNoun)) {
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
	public static ArrayList<String> eraseNoise(ArrayList<String> inpList) {
		Iterator<String> itr = inpList.iterator();
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
		return inpList;
	}

	/*
	 * transpose a given matrix and return the transposed matrix
	 */
	public static int[][] transposeMatrix(int[][] inpMatrix) {
		int[][] matrixT = new int[inpMatrix[0].length][inpMatrix.length];
		for (int i = 0; i < inpMatrix.length; i++) {
			for (int j = 0; j < inpMatrix[0].length; j++) {
				matrixT[j][i] = inpMatrix[i][j];
			}
		}
		return matrixT;
	}

	/*
	 * calculate the term scores using hits algorithm
	 */
	public static void calculateTermScores(ArrayList<String> inpSsrList,
			ArrayList<String> inpNounList) {
		// initialize the size of the matrix A and the transposed matrix AT
		int ssrLength = inpSsrList.size();
		int nounLength = inpNounList.size();
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
						inpSsrList.get(i), inpNounList.get(j));
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
			scoredSSRList.add(new Term(inpSsrList.get(i), hub[i]));
		}

		// combine calculated authority scores and nouns
		for (int i = 0; i < authority.length; i++) {
			scoredNounList.add(new Term(inpNounList.get(i), authority[i]));
		}

	}

	/*
	 * normalize a given vector and return the unit vector
	 */
	public static double[] normalizeVector(double[] inpVector) {
		double denominator = 0;

		for (int i = 0; i < inpVector.length; i++) {
			denominator = denominator + (inpVector[i] * inpVector[i]);
		}
		denominator = Math.sqrt(denominator);

		for (int i = 0; i < inpVector.length; i++) {
			inpVector[i] = inpVector[i] / denominator;
		}
		return inpVector;
	}

	/*
	 * multiply matrix with vector and return the calculated vector
	 */
	public static double[] getMatrixVectorMultiplikation(int[][] inpMatrix,
			double[] inpVector) {
		double[] calculatedVector = new double[inpMatrix.length];
		// multiply matrix with vector
		for (int i = 0; i < inpMatrix.length; i++) {
			for (int j = 0; j < inpMatrix[0].length; j++) {
				calculatedVector[i] = calculatedVector[i] + inpMatrix[i][j]
						* inpVector[j];
			}
		}
		return calculatedVector;
	}

	/*
	 * return the lemmatized term
	 */
	static String getLemmatizedTerm(String inpString) {
		String lemmatizedTerm = "";
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(inpString);
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
	public static String addArrayListToString(ArrayList<String> inpStringList) {
		String text = "";
		// loop over all terms and write them into a separate line of the string
		for (String string : inpStringList) {
			text = text + string + System.lineSeparator();
		}
		return text;
	}

	/*
	 * sort the term values by their name
	 */
	static ArrayList<String> sortValuesAlphabetical(ArrayList<String> inpList) {
		Collections.sort(inpList);
		return inpList;
	}
	public static void setAnnotateDocument(String inpText) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// create an empty Annotation just with the given text
		document = new Annotation(inpText);
		// run all Annotators on this text
		pipeline.annotate(document);
	}

	public static void setSSR(String inpText) {
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
							+ inpText
									.toString()
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
					if (ssrUniqueList.contains(inpText.toString()
							.substring(indexBeginString, indexEndString)
							.toLowerCase())
							|| inpText
									.toString()
									.substring(indexBeginString, indexEndString)
									.length()
									- inpText
											.toString()
											.substring(indexBeginString,
													indexEndString)
											.replaceAll(" ", "").length() > Integer.valueOf(nGrams)
							|| inpText
									.toString()
									.substring(indexBeginString, indexEndString)
									.length()
									- inpText
											.toString()
											.substring(indexBeginString,
													indexEndString)
											.replaceAll("\n", "").length() > 0) {
						// do not add items which are already in the list
					} else {
						// add ssr to list and replace line breaks by space
						ssrUniqueList.add(inpText.toString()
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

	/*
	 * start calculation if venu et al input param 1: path of the plain text
	 * which should be analyzed input param 2: print status (0 = do not print, 1
	 * = print)
	 */
	public static void startVenu(String inpTextPath, String inpOutputPath, String inpScored,
			int inpPrintStatus) throws IOException {
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
		// write files to disk
		//create unscored output files
		if (inpScored.equals("0")){
			Functions.writeStringToFile(inpOutputPath + "venu_"+nGrams+"-gram_nouns__0",
					Functions.addTermsToString(scoredNounList));		
			Functions.writeStringToFile(inpOutputPath + "venu_"+nGrams+"-gram_ssr__0",
					Functions.addTermsToString(scoredSSRList));
			Functions.writeStringToFile(inpOutputPath + "venu_"+nGrams+"-gram_combined__0",
					Functions.addTermsToString(scoredTermList));
		}
		//create scored output files
		if (inpScored.equals("1")){
		Functions.writeStringToFile(inpOutputPath + "venu_"+nGrams+"-gram_nouns_scored__0",
				Functions.addTermsWithScoreToString(scoredNounList));				
		Functions.writeStringToFile(inpOutputPath + "venu_"+nGrams+"-gram_ssr_scored__0",
				Functions.addTermsWithScoreToString(scoredSSRList));
		Functions.writeStringToFile(inpOutputPath + "venu_"+nGrams+"-gram_combined_scored__0",
				Functions.addTermsWithScoreToString(scoredTermList));
		}
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
			// print ssr
			System.out.println("Sorted SSR:");
			for (Term term : scoredSSRList) {
				System.out.println(String.format(
						"The SSR [%s] has a score of [%s]", term.term,
						term.score));
			}
		}
	}

	/*
	 * sort the term values by their score
	 */
	static ArrayList<Term> sortValuesByScore(ArrayList<Term> inpTerms) {
		Collections.sort(inpTerms, new Comparator<Term>() {
			@Override
			public int compare(Term t1, Term t2) {
				return Double.compare(t2.score, t1.score);
			}
		});
		return inpTerms;
	}

	public static void startVenuWithUserInput() throws IOException {
		// initialize user input
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		System.out.println("1. Please enter the file path of the PLAIN text:"
				+ System.lineSeparator() + "(Leave empty to use example text)"+ System.lineSeparator());
		// ask for input file
		String filePath = br.readLine();
		// replace back slashes with file separator
		filePath = filePath.replace("\\", File.separator);
		File testFile = new File(filePath);
		// set standard text if no path was typed in
		if (filePath.length() == 0) {
			filePath = "text/plain.txt";
			System.out.println("==>Example text " + filePath + " is used"
					+ System.lineSeparator());
		} else {
			// check if entered file path exist and force to enter a correct
			// path
			while (!testFile.exists() || !filePath.contains(".txt")) {
				System.out
						.print("File does not exist. Please enter correct file path of the plain text."
								+ System.lineSeparator());
				filePath = br.readLine();
				filePath = filePath.replace("\\", File.separator);
				testFile = new File(filePath);
			}
			System.out.println("");
		}		
		// ask for the output directory
		System.out.print("2. Please enter an OUTPUT DIRECTORY :"
				+ System.lineSeparator()
				+ "(Leave empty if you want to use the class folder)"+ System.lineSeparator());
		// get input from user
		String outputPath = br.readLine();
		// replace back slashes with file separator
		outputPath = outputPath.replace("\\", File.separator);
		File outputFile = new File(outputPath);
		// check if entered file path exist and force to enter a correct path
		if (outputPath.length() != 0) {
			while (!outputFile.exists()
					|| outputPath.contains(".txt")
					|| !outputPath.substring(outputPath.length() - 1).equals(
							File.separator)) {
				System.out
						.println("Please enter a correct directory. File names are not allowed"+ System.lineSeparator());
				outputPath = br.readLine();
				outputPath = outputPath.replace("\\", File.separator);
				outputFile = new File(outputPath);
			}
			System.out.println("");
		} else {
			System.out.println("==>Standard path is used"
					+ System.lineSeparator());
		}
		// ask for term length (n-gram)
				System.out
						.print("3. Enter the type of N-Grams you want to extract:"
								+ System.lineSeparator()
								+ "( 2 = Unigrams & Bigramms | 3 = Unigrams,Bigramms & Trigrams | 4 = Up to 4-grams)"
								+ System.lineSeparator());
				nGrams = br.readLine();
				// check if user input is either "1" or "2" or "3"
				while (!nGrams.equals("2") && !nGrams.equals("3")
						&& !nGrams.equals("4")) {
					System.out.print("Please enter only 2, 3 or 4"
							+ System.lineSeparator());
					nGrams = br.readLine();
				}
		
		
		// ask for scored or unscored output files				
				System.out
						.print("4. Do you want to save scored or unscored terms in the output files"
								+ System.lineSeparator()
								+ "(0 = unscored | 1 = scored)"+ System.lineSeparator());
				String inputScored = br.readLine();
				// check if user input is either "1" or "0"
				while (!inputScored.equals("0") && !inputScored.equals("1")) {
					System.out
							.println("Please enter '0' (zero) for 'unscored' and '1'(one) for scored:"+ System.lineSeparator());
					inputScored = br.readLine();
				}
				
		// ask for console print
		System.out
				.print("5. Do you want to print the output(0 = No | 1 = Yes) ?"+ System.lineSeparator());
		String inputPrint = br.readLine();
		// check if user input is either "1" or "0"
		while (!inputPrint.equals("0") && !inputPrint.equals("1")) {
			System.out
					.println("Please enter '0' (zero) for 'NO' and '1'(one) for yes:"+ System.lineSeparator());
			inputPrint = br.readLine();
		}
		System.out.println("");
		final long timeStart = System.currentTimeMillis(); //calculate the duration of the programm
		if (inputPrint.equals("0")) {
			startVenu(filePath, outputPath,inputScored, 0);
		}
		if (inputPrint.equals("1")) {
			startVenu(filePath, outputPath,inputScored, 1);
		}
		final long timeEnd = System.currentTimeMillis();
		System.out.println("Duration of run:" + (timeEnd - timeStart)); // print the duration of the programm	
	}

	/*
	 * main
	 */
	public static void main(String[] args) throws IOException {
		startVenuWithUserInput();
	}
}