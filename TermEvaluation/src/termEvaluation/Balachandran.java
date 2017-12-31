package termEvaluation;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.io.FileUtils;
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
	static Integer getCountOccurences(String term, String text) {
		return StringUtils.countMatches(text.toLowerCase(), term.toLowerCase());
	}

	/*
	 * Returns a relevant term according to the rules of Balachandran et al.
	 * Input: Single Sentence as an ArrayList of typeSentence
	 */
	static ArrayList<String> getRelevantTermsByRules(
			ArrayList<Sentence> singleSentence, ArrayList<String> terms) {
		int termListLength = 0;
		// loop over the terms of the sentence and apply rules
		for (int i = 2; i < singleSentence.size(); i++) {
			// rule1: 3-gram rule
			// save length of term list before applying the rule
			termListLength = terms.size();
			// apply rule
			//terms = rule1(singleSentence, terms, i);
			// if the rule adds a term to the list, increase the index
			if (terms.size() > termListLength) {
				if (i + 3 < singleSentence.size()) {
					i = i + 3;
				}
			}
			// rule2: 3-gram rule
			// save length of term list before applying the rule
			termListLength = terms.size();
			// apply rule
			//terms = rule2(singleSentence, terms, i);
			// if the rule adds a term to the list, increase the index
			if (terms.size() > termListLength) {
				if (i + 3 < singleSentence.size()) {
					i = i + 3;
				}
			}
			// rule3: 2-gram rule
			// save length of term list before applying the rule
			termListLength = terms.size();
			// apply rule
			terms = rule3(singleSentence, terms, i);
			// if the rule adds a term to the list, increase the index
			if (terms.size() > termListLength) {
				if (i + 3 < singleSentence.size()) {
					i = i + 3;
				}
			}

			// rule4: 2-gram rule
			// save length of term list before applying the rule
			termListLength = terms.size();
			// apply rule
			terms = rule4(singleSentence, terms, i);
			// if the rule adds a term to the list, increase the index
			if (terms.size() > termListLength) {
				if (i + 3 < singleSentence.size()) {
					i = i + 3;
				}
			}

			// rul5: unigram rule
			// apply rule (automatic increase of index by for-loop)

			terms = rule5(singleSentence, terms, i);
		}

		// delete terms which contains noise
		terms = deleteNoise(terms);
		return terms;
	}

	/*
	 * Rule 5 of Balachandran et al. Add words to the term list if P2 = NN, NNS,
	 * NNP or NNPS
	 */
	static ArrayList<String> rule5(ArrayList<Sentence> s, ArrayList<String> t,
			int i) {
		// check if terms fit the rule
		if (s.get(i - 2).pos.equals("NN") || s.get(i - 2).pos.equals("NNS")
				|| s.get(i - 2).pos.equals("NNP")
				|| s.get(i - 2).pos.equals("NNPS")) {

			if (t.contains(s.get(i - 2).word.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add term to term list if it fit the rules
				t.add(s.get(i - 2).word.toLowerCase());
			}
		}

		return t;
	}

	/*
	 * Rule 4 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P1 = JJ, JJR or JJS
	 */
	static ArrayList<String> rule4(ArrayList<Sentence> s, ArrayList<String> t,
			int i) {
		String nTerm;
		// check if terms fit the rule

		if ((s.get(i).pos.equals("NN") || s.get(i).pos.equals("NNS")
				|| s.get(i).pos.equals("NNP") || s.get(i).pos.equals("NNPS"))
				&& (s.get(i - 1).pos.equals("JJ")
						|| s.get(i - 1).pos.equals("JJR") || s.get(i - 1).pos
						.equals("JJS"))) {
			nTerm = s.get(i - 1).word + " " + s.get(i).word;
			if (t.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				t.add(nTerm.toLowerCase());
				}
		}

		return t;
	}

	/*
	 * Rule 3 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P1 = NN, NNS or NNPS
	 */
	static ArrayList<String> rule3(ArrayList<Sentence> s, ArrayList<String> t,
			int i) {
		String nTerm;
		// check if terms fit the rule

		if ((s.get(i).pos.equals("NN") || s.get(i).pos.equals("NNS")
				|| s.get(i).pos.equals("NNP") || s.get(i).pos.equals("NNPS"))
				&& (s.get(i - 1).pos.equals("NN")
						|| s.get(i - 1).pos.equals("NNS")
						|| s.get(i - 1).pos.equals("NNP") || s.get(i - 1).pos
						.equals("NNPS"))) {
			nTerm = s.get(i - 1).word + " " + s.get(i).word;
			if (t.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				t.add(nTerm.toLowerCase());				
			}
		}

		return t;
	}

	/*
	 * Rule 2 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P2 = NN, NNS or NNPS and P1 = any tag
	 */
	static ArrayList<String> rule2(ArrayList<Sentence> s, ArrayList<String> t,
			int i) {
		String nTerm;
		// check if terms fit the rule
		if ((s.get(i).pos.equals("NN") || s.get(i).pos.equals("NNS")
				|| s.get(i).pos.equals("NNP") || s.get(i).pos.equals("NNPS"))
				&& (s.get(i - 2).pos.equals("NN")
						|| s.get(i - 2).pos.equals("NNS")
						|| s.get(i - 2).pos.equals("NNP") || s.get(i - 2).pos
						.equals("NNPS"))) {
			nTerm = s.get(i - 2).word + " " + s.get(i - 1).word + " "
					+ s.get(i).word;
			if (t.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				t.add(nTerm.toLowerCase());				
			}
		}
		return t;
	}

	/*
	 * Rule 1 of Balachandran et al. Add words to the term list if P0 = NN, NNS,
	 * NNP or NNPS and P1 = NN, NNS or NNPS and P2 = JJ, JJR or JJS
	 */
	static ArrayList<String> rule1(ArrayList<Sentence> s, ArrayList<String> t,
			int i) {
		String nTerm;
		// check if terms fit the rule
		if ((s.get(i).pos.equals("NN") || s.get(i).pos.equals("NNS")
				|| s.get(i).pos.equals("NNP") || s.get(i).pos.equals("NNPS"))
				&& (s.get(i - 1).pos.equals("NN")
						|| s.get(i - 1).pos.equals("NNS")
						|| s.get(i - 1).pos.equals("NNP") || s.get(i - 1).pos
						.equals("NNPS"))
				&& (s.get(i - 2).pos.equals("JJ")
						|| s.get(i - 2).pos.equals("JJR") || s.get(i - 2).pos
						.equals("JJS"))) {
			nTerm = s.get(i - 2).word + " " + s.get(i - 1).word + " "
					+ s.get(i).word;
			if (t.contains(nTerm.toLowerCase())) {
				// do not add term if it already exists in the term list
			} else {
				// add combination of terms to the term list if it fit the
				// rules
				t.add(nTerm.toLowerCase());				
			}
		}

		return t;
	}

	static void printTermsOfBalachandran(String path) throws IOException {
		String textCorpus = Functions.readFile(path, Charset.defaultCharset());
		ArrayList<String> terms = getRelevantTerms(textCorpus);

	}

	static ArrayList<Term> getLemmatizedTermList(ArrayList<String> termList) {
		ArrayList<Term> lemmatizedTerms = new ArrayList<Term>();
		for (int i = 0; i < termList.size(); i++) {
			String term = termList.get(i).toString();
			String lemmatizedTerm = getLemmatizedTerm(termList.get(i)
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

	static ArrayList<Term> addOccurences(ArrayList<Term> lemmatizedTerm,
			String text, int frequencyType) {
		StringTokenizer countAllWords = new StringTokenizer(text);
		double counterAllWords = countAllWords.countTokens();

		// loop over all lemmatized terms
		for (int i = 0; i < lemmatizedTerm.size(); i++) {
			int counter = 0;
			// count the occurrence of the original term inside the text if it
			// is a multi term
			if (lemmatizedTerm.get(i).term.replace(" ", "").length() != lemmatizedTerm
					.get(i).term.length()) {
				/*
				 * TEST
				 */

				counter = getCountOccurences(
						lemmatizedTerm.get(i).lemmatizedTerm, text);
			}

			// split multi terms into single terms
			String[] termParts = lemmatizedTerm.get(i).lemmatizedTerm
					.split(" ");
			// count each occurrence of the single terms inside the text and
			// increase the counter
			for (String termPart : termParts) {
				// count only if the term is countable
				counter = counter + getCountOccurences(termPart, text);
			}
			// if the lemmatized unigram was not found search for the original
			// word
			if (counter == 0) {
				counter = getCountOccurences(lemmatizedTerm.get(i).term, text);
			}
			// add the counted values to the lemmatized term structure and the
			// according frequency type
			switch (frequencyType) {
			case 0:
				lemmatizedTerm.get(i).frequencyText = counter / counterAllWords;
				break;
			case 1:
				lemmatizedTerm.get(i).frequencyContrastDomain1 = (counter + 1)
						/ counterAllWords;
				break;
			case 2:
				lemmatizedTerm.get(i).frequencyContrastDomain2 = (counter + 1)
						/ counterAllWords;
				break;

			}

			// System.out.println(String.format(
			// "The term [%s] ([%s]) occurres [%s] in the text",
			// lemmatizedTerm.get(i).term,
			// lemmatizedTerm.get(i).lemmatizedTerm, counter));
		}
		return lemmatizedTerm;
	}

	static ArrayList<String> getRelevantTerms(String text) {

		ArrayList<String> terms = new ArrayList<String>();
		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(text);
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
	static ArrayList<Term> addScore(ArrayList<Term> lemmatizedTerm) {
		for (int i = 0; i < lemmatizedTerm.size(); i++) {
			if (lemmatizedTerm.get(i).frequencyContrastDomain1 == 0
					&& lemmatizedTerm.get(i).frequencyContrastDomain2 == 0) {
				lemmatizedTerm.get(i).score = lemmatizedTerm.get(i).frequencyText;
			} else {
				// select the highest frequency of the contrast domains
				if (lemmatizedTerm.get(i).frequencyContrastDomain1 > lemmatizedTerm
						.get(i).frequencyContrastDomain2) {
					// score = frequency in text / frequency in contrast domain
					lemmatizedTerm.get(i).score = lemmatizedTerm.get(i).frequencyText
							/ (double) lemmatizedTerm.get(i).frequencyContrastDomain1;
				} else {
					// score = frequency in text / frequency in contrast domain
					lemmatizedTerm.get(i).score = lemmatizedTerm.get(i).frequencyText
							/ (double) lemmatizedTerm.get(i).frequencyContrastDomain2;
				}
			}
		}
		return lemmatizedTerm;
	}

	/*
	 * Starts the algorithm of Balachandran et al.
	 */
	static void startBalachandran(String textPath, String compareText1Path,
			String compareText2Path) throws IOException {
		// read the transfered file path and write into string
		String text = Functions.readFile(textPath, Charset.defaultCharset());
		String compareText1 = Functions.readFile(compareText1Path,
				Charset.defaultCharset());
		String compareText2 = Functions.readFile(compareText2Path,
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

		Functions.writeStringToFile("balachandran_lemmatized",
				Functions.addLemmatizedTermsToString(lemmatizedTerms));
		Functions.writeStringToFile("balachandran_lemmatized_scored",
				Functions.addLemmatizedTermsWithScoreToString(lemmatizedTerms));
		System.out.println("Sorted:");
		for (Term term : lemmatizedTerms) {
			System.out
					.println(String
							.format("The term [%s] lemma [%s] has a score of [%s] with FREQ [%s] CP1 [%s] CP2 [%s]",
									term.term, term.lemmatizedTerm, term.score,
									term.frequencyText,
									term.frequencyContrastDomain1,
									term.frequencyContrastDomain2));
		}
	}

	/*
	 * Remove Noise from the list of terms
	 */
	private static ArrayList<String> deleteNoise(ArrayList<String> terms) {
		for (int i = 0; i < terms.size(); i++) {
			if (terms.get(i).contains("%") || terms.get(i).contains("*")
					|| terms.get(i).contains("<") || terms.get(i).contains(">")
					|| terms.get(i).contains("=")) {
				terms.remove(i);
			}
		}

		// return the term list without noise
		return terms;
	}

	/*
	 * add all terms to a string
	 */
	static String addTermsToString(ArrayList<Term> termList) {
		String terms = "";
		// loop over all terms and write them into a sperate line of the string
		for (Term term : termList) {
			terms = terms + term.term + System.lineSeparator();
		}
		return terms;

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

	/*
	 * save string to a text file
	 */
	@SuppressWarnings("deprecation")
	static void writeStringToFile(String name, String text) throws IOException {
		name = name + ".txt";
		FileUtils.writeStringToFile(new File(name), text);
		System.out.println(String.format("The file [%s] was generated.", name));
	}

	public static void main(String[] args) throws IOException {
		startBalachandran(
				"C:/Users/Mannheimer/Desktop/Bachelor/Daten/plain.txt",
				"C:/Users/Mannheimer/Desktop/Bachelor/Daten/genia.txt",
				"C:/Users/Mannheimer/Desktop/Bachelor/Daten/reuters.txt");
	}

}
