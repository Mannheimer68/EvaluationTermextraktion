package termEvaluation;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;

public class Balachandran {
	/*
	 * Counts the occurrences of a given term inside a given text Input: term
	 * and text
	 */
	static Integer getCountOccurences(String inpTerm, String inpText) {
		return StringUtils.countMatches(inpText.toLowerCase(), inpTerm.toLowerCase());
	}

	/*
	 * Returns a relevant term according to the rules of Balachandran et al.
	 * Input: Single Sentence as an ArrayList of typeSentence
	 */
	static ArrayList<String> getRelevantTermsByRules(
			ArrayList<Sentence> inpSingleSentence, ArrayList<String> inpTerms) {
		int termListLength = 0;
		// loop over the terms of the sentence and apply rules
		for (int i = 2; i < inpSingleSentence.size(); i++) {
			// rule1: 3-gram rule
			// save length of term list before applying the rule
			termListLength = inpTerms.size();
			// apply rule
			// terms = rule1(singleSentence, terms, i);
			// if the rule adds a term to the list, increase the index
			if (inpTerms.size() > termListLength) {
				if (i + 3 < inpSingleSentence.size()) {
					i = i + 3;
				}
			}
			// rule2: 3-gram rule
			// save length of term list before applying the rule
			termListLength = inpTerms.size();
			// apply rule
			// terms = rule2(singleSentence, terms, i);
			// if the rule adds a term to the list, increase the index
			if (inpTerms.size() > termListLength) {
				if (i + 3 < inpSingleSentence.size()) {
					i = i + 3;
				}
			}
			// rule3: 2-gram rule
			// save length of term list before applying the rule
			termListLength = inpTerms.size();
			// apply rule
			inpTerms = rule3(inpSingleSentence, inpTerms, i);
			// if the rule adds a term to the list, increase the index
			if (inpTerms.size() > termListLength) {
				if (i + 3 < inpSingleSentence.size()) {
					i = i + 3;
				}
			}

			// rule4: 2-gram rule
			// save length of term list before applying the rule
			termListLength = inpTerms.size();
			// apply rule
			inpTerms = rule4(inpSingleSentence,inpTerms, i);
			// if the rule adds a term to the list, increase the index
			if (inpTerms.size() > termListLength) {
				if (i + 3 < inpSingleSentence.size()) {
					i = i + 3;
				}
			}

			// rul5: unigram rule
			// apply rule (automatic increase of index by for-loop)

			inpTerms = rule5(inpSingleSentence, inpTerms, i);
		}

		// delete terms which contains noise
		inpTerms = deleteNoise(inpTerms);
		return inpTerms;
	}

	/*
	 * Rule 5 of Balachandran et al. Add words to the term list if P2 = NN, NNS,
	 * NNP or NNPS
	 */
	static ArrayList<String> rule5(ArrayList<Sentence> inpSentence, ArrayList<String> inpTerm,
			int i) {
		// check if terms fit the rule
		if (inpSentence.get(i - 2).pos.equals("NN") || inpSentence.get(i - 2).pos.equals("NNS")
				|| inpSentence.get(i - 2).pos.equals("NNP")
				|| inpSentence.get(i - 2).pos.equals("NNPS")) {

			if (inpTerm.contains(inpSentence.get(i - 2).word.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add term to term list if it fit the rules
				inpTerm.add(inpSentence.get(i - 2).word.toLowerCase());
			}
		}

		return inpTerm;
	}

	/*
	 * Rule 4 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P1 = JJ, JJR or JJS
	 */
	static ArrayList<String> rule4(ArrayList<Sentence> inpSentence, ArrayList<String> inpTerm,
			int i) {
		String nTerm;
		// check if terms fit the rule

		if ((inpSentence.get(i).pos.equals("NN") || inpSentence.get(i).pos.equals("NNS")
				|| inpSentence.get(i).pos.equals("NNP") || inpSentence.get(i).pos.equals("NNPS"))
				&& (inpSentence.get(i - 1).pos.equals("JJ")
						|| inpSentence.get(i - 1).pos.equals("JJR") || inpSentence.get(i - 1).pos
						.equals("JJS"))) {
			nTerm = inpSentence.get(i - 1).word + " " + inpSentence.get(i).word;
			if (inpTerm.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				inpTerm.add(nTerm.toLowerCase());
			}
		}

		return inpTerm;
	}

	/*
	 * Rule 3 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P1 = NN, NNS or NNPS
	 */
	static ArrayList<String> rule3(ArrayList<Sentence> inpSentence, ArrayList<String> inpTerm,
			int i) {
		String nTerm;
		// check if terms fit the rule

		if ((inpSentence.get(i).pos.equals("NN") || inpSentence.get(i).pos.equals("NNS")
				|| inpSentence.get(i).pos.equals("NNP") || inpSentence.get(i).pos.equals("NNPS"))
				&& (inpSentence.get(i - 1).pos.equals("NN")
						|| inpSentence.get(i - 1).pos.equals("NNS")
						|| inpSentence.get(i - 1).pos.equals("NNP") || inpSentence.get(i - 1).pos
						.equals("NNPS"))) {
			nTerm = inpSentence.get(i - 1).word + " " + inpSentence.get(i).word;
			if (inpTerm.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				inpTerm.add(nTerm.toLowerCase());
			}
		}

		return inpTerm;
	}

	/*
	 * Rule 2 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P2 = NN, NNS or NNPS and P1 = any tag
	 */
	static ArrayList<String> rule2(ArrayList<Sentence> inpSentence, ArrayList<String> inpTerm,
			int i) {
		String nTerm;
		// check if terms fit the rule
		if ((inpSentence.get(i).pos.equals("NN") || inpSentence.get(i).pos.equals("NNS")
				|| inpSentence.get(i).pos.equals("NNP") || inpSentence.get(i).pos.equals("NNPS"))
				&& (inpSentence.get(i - 2).pos.equals("NN")
						|| inpSentence.get(i - 2).pos.equals("NNS")
						|| inpSentence.get(i - 2).pos.equals("NNP") || inpSentence.get(i - 2).pos
						.equals("NNPS"))) {
			nTerm = inpSentence.get(i - 2).word + " " + inpSentence.get(i - 1).word + " "
					+ inpSentence.get(i).word;
			if (inpTerm.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				inpTerm.add(nTerm.toLowerCase());
			}
		}
		return inpTerm;
	}

	/*
	 * Rule 1 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P1 = NN, NNS or NNPS and P2 = JJ, JJR or JJS
	 */
	static ArrayList<String> rule1(ArrayList<Sentence> inpSentence, ArrayList<String> inpTerm,
			int i) {
		String nTerm;
		// check if terms fit the rule
		if ((inpSentence.get(i).pos.equals("NN") || inpSentence.get(i).pos.equals("NNS")
				|| inpSentence.get(i).pos.equals("NNP") || inpSentence.get(i).pos.equals("NNPS"))
				&& (inpSentence.get(i - 1).pos.equals("NN")
						|| inpSentence.get(i - 1).pos.equals("NNS")
						|| inpSentence.get(i - 1).pos.equals("NNP") || inpSentence.get(i - 1).pos
						.equals("NNPS"))
				&& (inpSentence.get(i - 2).pos.equals("JJ")
						|| inpSentence.get(i - 2).pos.equals("JJR") || inpSentence.get(i - 2).pos
						.equals("JJS"))) {
			nTerm = inpSentence.get(i - 2).word + " " + inpSentence.get(i - 1).word + " "
					+ inpSentence.get(i).word;
			if (inpTerm.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				inpTerm.add(nTerm.toLowerCase());
			}
		}

		return inpTerm;
	}

	static ArrayList<Term> getLemmatizedTermList(ArrayList<String> inpTermList) {
		ArrayList<Term> lemmatizedTerms = new ArrayList<Term>();
		for (int i = 0; i < inpTermList.size(); i++) {
			String term = inpTermList.get(i).toString();
			String lemmatizedTerm = getLemmatizedTerm(inpTermList.get(i)
					.toString());
			// only add the lemmatized terms once
			int checkExistence = 0;
			for (Term t : lemmatizedTerms) {
				// check if term already exists in list
				if (t.lemmatizedTerm.equals(lemmatizedTerm)) {
					checkExistence = checkExistence + 1;
				}
			}
			// if term does not exists in current list add it to the list
			if (checkExistence == 0) {
				lemmatizedTerms.add(new Term(term, lemmatizedTerm));
			}
		}
		return lemmatizedTerms;
	}

	static String getLemmatizedTerm(String inpText) {
		String lemmatizedTerm = "";
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(inpText);
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

	static ArrayList<Term> addOccurences(ArrayList<Term> inpLemmatizedTerm,
			String inpText, int inpFrequencyType) {
		StringTokenizer countAllWords = new StringTokenizer(inpText);
		double counterAllWords = countAllWords.countTokens();

		// loop over all lemmatized terms
		for (int i = 0; i < inpLemmatizedTerm.size(); i++) {
			int counter = 0;
			// count the occurrence of the original term inside the text if it
			// is a multi term
			if (inpLemmatizedTerm.get(i).term.replace(" ", "").length() != inpLemmatizedTerm
					.get(i).term.length()) {
				/*
				 * TEST
				 */

				counter = getCountOccurences(
						inpLemmatizedTerm.get(i).lemmatizedTerm, inpText);
			}

			// split multi terms into single terms
			String[] termParts = inpLemmatizedTerm.get(i).lemmatizedTerm
					.split(" ");
			// count each occurrence of the single terms inside the text and
			// increase the counter
			for (String termPart : termParts) {
				// count only if the term is countable
				counter = counter + getCountOccurences(termPart, inpText);
			}
			// if the lemmatized unigram was not found search for the original
			// word
			if (counter == 0) {
				counter = getCountOccurences(inpLemmatizedTerm.get(i).term, inpText);
			}
			// add the counted values to the lemmatized term structure and the
			// according frequency type
			switch (inpFrequencyType) {
			case 0:
				inpLemmatizedTerm.get(i).frequencyText = counter / counterAllWords;
				break;
			case 1:
				inpLemmatizedTerm.get(i).frequencyContrastDomain1 = (counter + 1)
						/ counterAllWords;
				break;
			case 2:
				inpLemmatizedTerm.get(i).frequencyContrastDomain2 = (counter + 1)
						/ counterAllWords;
				break;
			}			
		}
		return inpLemmatizedTerm;
	}

	static ArrayList<String> getRelevantTerms(String inpText) {

		ArrayList<String> terms = new ArrayList<String>();
		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(inpText);
		pipeline.annotate(document);

		List<CoreMap> sentences = document
				.get(CoreAnnotations.SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			ArrayList<Sentence> tokenizedSentence = new ArrayList<Sentence>();
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence
					.get(CoreAnnotations.TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token
						.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				// Add the tokenized words and POS-Tags to the ArrayList
				tokenizedSentence.add(new Sentence(sentence.toString(), word
						.toString(), pos.toString().replaceAll("\\s+", "")
						.toUpperCase()));
			}
			// terms.addAll(getRelevantTermsByRules(tokenizedSentence, terms));
			terms = getRelevantTermsByRules(tokenizedSentence, terms);
		}
		return terms;
	}

	/*
	 * Add scores to the lemmatized Terms
	 */
	static ArrayList<Term> addScore(ArrayList<Term> inpLemmatizedTerm) {
		for (int i = 0; i < inpLemmatizedTerm.size(); i++) {
			if (inpLemmatizedTerm.get(i).frequencyContrastDomain1 == 0
					&& inpLemmatizedTerm.get(i).frequencyContrastDomain2 == 0) {
				inpLemmatizedTerm.get(i).score = inpLemmatizedTerm.get(i).frequencyText;
			} else {
				// select the highest frequency of the contrast domains
				if (inpLemmatizedTerm.get(i).frequencyContrastDomain1 > inpLemmatizedTerm
						.get(i).frequencyContrastDomain2) {
					// score = frequency in text / frequency in contrast domain
					inpLemmatizedTerm.get(i).score = inpLemmatizedTerm.get(i).frequencyText
							/ (double) inpLemmatizedTerm.get(i).frequencyContrastDomain1;
				} else {
					// score = frequency in text / frequency in contrast domain
					inpLemmatizedTerm.get(i).score = inpLemmatizedTerm.get(i).frequencyText
							/ (double) inpLemmatizedTerm.get(i).frequencyContrastDomain2;
				}
			}
		}
		return inpLemmatizedTerm;
	}

	/*
	 * Starts the algorithm of Balachandran et al.
	 */
	static void startBalachandran(String inpTextPath, String inpCompareText1Path,
			String inpCompareText2Path, String inpOutputPath, int inpPrintStatus)
			throws IOException {
		// read the transfered file path and write into string
		String text = Functions.readFile(inpTextPath, Charset.defaultCharset());
		String compareText1 = Functions.readFile(inpCompareText1Path,
				Charset.defaultCharset());
		String compareText2 = Functions.readFile(inpCompareText2Path,
				Charset.defaultCharset());

		// extract the relevant terms of the given text
		ArrayList<String> terms = getRelevantTerms(text);
		// lemmatize the terms
		ArrayList<Term> lemmatizedTerms = getLemmatizedTermList(terms);
		// add the occurrences of the terms in the text
		lemmatizedTerms = addOccurences(lemmatizedTerms, text, 0);
		// add the occurrences of the terms in contrast 1 corpora
		lemmatizedTerms = addOccurences(lemmatizedTerms, compareText1, 1);
		// add the occurrences of the terms in contrast 2 corpora
		lemmatizedTerms = addOccurences(lemmatizedTerms, compareText2, 2);
		// add the score based on the occurrences
		lemmatizedTerms = addScore(lemmatizedTerms);
		lemmatizedTerms = sortValuesByScore(lemmatizedTerms);

		Functions.writeStringToFile(inpOutputPath + "balachandran_scored__0",
				Functions.addLemmatizedTermsWithScoreToString(lemmatizedTerms));
		if (inpPrintStatus == 1) {
			System.out.println("Sorted:");
			for (Term term : lemmatizedTerms) {
				System.out
						.println(String
								.format("The term [%s] lemma [%s] has a score of [%s] with FREQ [%s] CP1 [%s] CP2 [%s]",
										term.term, term.lemmatizedTerm,
										term.score, term.frequencyText,
										term.frequencyContrastDomain1,
										term.frequencyContrastDomain2));
			}
		}
	}

	/*
	 * Remove Noise from the list of terms
	 */
	private static ArrayList<String> deleteNoise(ArrayList<String> inpTerms) {
		for (int i = 0; i < inpTerms.size(); i++) {
			if (inpTerms.get(i).contains("%") || inpTerms.get(i).contains("*")
					|| inpTerms.get(i).contains("<") || inpTerms.get(i).contains(">")
					|| inpTerms.get(i).contains("=")) {
				inpTerms.remove(i);
			}
		}

		// return the term list without noise
		return inpTerms;
	}

	/*
	 * add all terms to a string
	 */
	static String addTermsToString(ArrayList<Term> inpTermList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : inpTermList) {
			terms = terms + term.term + System.lineSeparator();
		}
		return terms;

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

	/*
	 * start user input for balachandran
	 */
	public static void startBalachandranWithUserInput() throws IOException {
		// initialize user input
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		//
		// input of the plain text
		//
		System.out.print("1. Please enter the file path of the PLAIN text:"
				+ System.lineSeparator() + "(Leave empty to use example text)");
		// get input from user
		String textPath = br.readLine();
		// replace back slashes with file separator
		textPath = textPath.replace("\\", File.separator);
		File textFile = new File(textPath);
		// set standard text if no path was typed in
		if (textPath.length() == 0) {
			textPath = "text/plain.txt";
			System.out.print("==>Example text " + textPath + " is used"
					+ System.lineSeparator() + System.lineSeparator());
		} else {
			// check if entered file path exist and force to enter a correct
			// path
			while (!textFile.exists() || !textPath.contains(".txt")) {
				System.out
						.print("File does not exist. Please enter correct file path for the plain text:"
								+ System.lineSeparator());
				textPath = br.readLine();
				textPath = textPath.replace("\\", File.separator);
				textFile = new File(textPath);
			}
		}
		//
		// input if the first contrast text
		//
		System.out
				.print("2. Please enter the file path of the FIRST CONTRAST text:"
						+ System.lineSeparator()
						+ "(Leave empty to use GENIA as first contrast text)"
						+ System.lineSeparator());
		// get input from user
		String contrast1Path = br.readLine();
		// replace back slashes with file separator
		contrast1Path = contrast1Path.replace("\\", File.separator);
		File contrast1File = new File(contrast1Path);
		// set standard text if no path was typed in
		if (contrast1Path.length() == 0) {
			contrast1Path = "text/genia.txt";
			System.out.print("==>Standard text " + contrast1Path + " is used"
					+ System.lineSeparator() + System.lineSeparator());
		} else {
			// check if entered file path exist and force to enter a correct
			// path
			while (!contrast1File.exists() || !contrast1Path.contains(".txt")
					|| contrast1Path.equals(textPath)) {
				System.out
						.print("File does not exist or is equal to previous file. Please enter a correct file path for the first contrast text:"
								+ System.lineSeparator());
				contrast1Path = br.readLine();
				contrast1Path = contrast1Path.replace("\\", File.separator);
				contrast1File = new File(contrast1Path);
			}
		}
		//
		// input if the second contrast text
		//
		System.out
				.print("3. Please enter the file path of the SECOND CONTRAST text:"
						+ System.lineSeparator()
						+ "(Leave empty to use REUTERS as second contrast text)"
						+ System.lineSeparator());
		// get input from user
		String contrast2Path = br.readLine();
		// replace back slashes with file separator
		contrast2Path = contrast2Path.replace("\\", File.separator);
		File contrast2File = new File(contrast2Path);
		// set standard text if no path was typed in
		if (contrast2Path.length() == 0) {

			contrast2Path = "text/reuters.txt";
			System.out.print("==>Standard text " + contrast2Path + " is used"
					+ System.lineSeparator() + System.lineSeparator());
		} else {
			// check if entered file path exist and force to enter a correct
			// path
			while (!contrast2File.exists() || !contrast2Path.contains(".txt")
					|| contrast2Path.equals(textPath)
					|| contrast2Path.equals(contrast1Path)) {
				System.out
						.print("File does not exist or is equal to previous file. Please enter a correct file path for the second contrast text:"
								+ System.lineSeparator());
				contrast2Path = br.readLine();
				contrast2Path = contrast2Path.replace("\\", File.separator);
				contrast2File = new File(contrast2Path);
			}
		}
		//
		// input of the output directory file
		//
		System.out.print("4. Please enter an OUTPUT DIRECTORY:"
				+ System.lineSeparator()
				+ "(Leave empty if you want to use the class folder):"
				+ System.lineSeparator());
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
						.print("==>Please enter a correct directory. File names are not allowed"
								+ System.lineSeparator());
				outputPath = br.readLine();
				outputPath = outputPath.replace("\\", File.separator);
				outputFile = new File(outputPath);
			}
		} else {
			System.out.print("Standard path is used" + System.lineSeparator()
					+ System.lineSeparator());
		}

		//
		// get input from user
		//
		System.out
				.print("5. Do you want to PRINT the output(0 = No | 1 = Yes)?"
						+ System.lineSeparator()
						+ "(Data will still be saved to the output directory)"
						+ System.lineSeparator());
		String inputPrint = br.readLine();
		// check if user input is either "1" or "0"
		while (!inputPrint.equals("0") && !inputPrint.equals("1")) {
			System.out
					.print("Please enter '0' (zero) for 'NO' and '1'(one) for yes:"
							+ System.lineSeparator());
			inputPrint = br.readLine();
		}
		if (inputPrint.equals("0")) {
			startBalachandran(textPath, contrast1Path, contrast2Path,
					outputPath, 0);
		}
		if (inputPrint.equals("1")) {
			startBalachandran(textPath, contrast1Path, contrast2Path,
					outputPath, 1);
		}
	}

	/*
	 * main
	 */
	public static void main(String[] args) throws IOException {
		startBalachandranWithUserInput();
	}

}
