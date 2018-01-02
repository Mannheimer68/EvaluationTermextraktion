package termEvaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import termEvaluation.Term;
import termEvaluation.EvaluationData;



/*
 * Class Evaluation Calculation of precision, recall and f1
 */

public class Evaluation {
	/*
	 * class variables
	 */
	private static double truePositives = 0;
	private static double falsePositives = 0;
	private static double falseNegatives = 0;
	private static int bestTerms = 0;
	public static String evaluationFileName;

	/*
	 * constructor with given terms and gold list useful for starting the
	 * evaluation for a given algorithm
	 */
	public Evaluation(ArrayList<Term> inpTermsTerm, ArrayList<String> inpGold,
			int inpBestTerms) {
		bestTerms = inpBestTerms;
		setEvaluationValues(getTermsTypeArrayString(inpTermsTerm), inpGold);
	}

	/*
	 * constructor with terms list and gold text path useful for starting the
	 * evaluation for a given algorithm
	 */
	public Evaluation(ArrayList<Term> inpTermsTerm, String inpGoldPath,
			int inpBestTerms) throws IOException {
		ArrayList<String> gold = new ArrayList<String>();
		gold = getGold(inpGoldPath);
		bestTerms = inpBestTerms;
		setEvaluationValues(getTermsTypeArrayString(inpTermsTerm), gold);
	}

	/*
	 * constructor with terms path and gold text path useful for starting the
	 * evaluation for a given algorithm
	 */
	public Evaluation(String inpTermPath, String inpGoldPath, int inpBestTerms)
			throws IOException {
		ArrayList<String> gold = new ArrayList<String>();
		ArrayList<Term> terms = new ArrayList<Term>();
		gold = getGold(inpGoldPath);
		terms = getTerms(inpTermPath);
		bestTerms = inpBestTerms;
		setEvaluationValues(getTermsTypeArrayString(terms), gold);
	}

	/*
	 * get the array list of the gold standard
	 */
	public static ArrayList<String> getGold(String inpGoldPath)
			throws IOException {
		ArrayList<String> goldList = new ArrayList<String>();
		// read txt file for gold standard
		String text = Functions.readFile(inpGoldPath, Charset.defaultCharset());

		// split txt file by line breaks
		String[] goldArray = text.split("\n");

		// read array in ArrayList
		for (int i = 0; i < goldArray.length; i++) {
			goldList.add(goldArray[i].replaceAll("\r", ""));
		}
		return goldList;
	}

	/*
	 * get the array list of the terms reads the given file and splits the text
	 * into terms and score
	 */
	public static ArrayList<Term> getTerms(String inpTermPath)
			throws IOException {
		ArrayList<Term> termList = new ArrayList<Term>();
		// read txt file for gold standard
		String text = Functions.readFile(inpTermPath, Charset.defaultCharset());

		// split txt file by line breaks
		String[] termArray = text.split("\n");

		// read array in ArrayList
		for (int i = 0; i < termArray.length; i++) {
			String[] termWithScore = termArray[i].split("_");
			termList.add(new Term(termWithScore[0].replaceAll("\r", ""), Double
					.parseDouble(termWithScore[1].replaceAll("\r", ""))));
		}
		return termList;
	}

	/*
	 * parse term list of type ArrayList<Term> into ArrayList<String> only use
	 * top n entries top n is given by the class variable "bestTerms" which has
	 * to be set before
	 */
	public static ArrayList<String> getTermsTypeArrayString(
			ArrayList<Term> inpTermsTerm) {
		ArrayList<String> terms = new ArrayList<String>();
		// set max value if no restriction is passed
		if (bestTerms == 0) {
			bestTerms = 999999999;
		}
		for (Term t : inpTermsTerm) {
			if (terms.size() < bestTerms) {//
				terms.add(t.term);
			}
		}
		return terms;
	}

	/*
	 * set file name given by the filePath e.g. file path
	 * "C:/User/Desktop/testfile.txt leads to the name "testfile"
	 */
	public static void setEvaluationFileName(String inpFilePath) {
		String[] path = inpFilePath.split("/");
		String[] fileName = path[path.length - 1].split("\\.");
		evaluationFileName = fileName[0];
	}

	/*
	 * set true positives, false negatives and false positives
	 */
	public static void setEvaluationValues(ArrayList<String> inpTerms,
			ArrayList<String> inpGold) {
		// loop over terms to calculate true positives and false positives
		for (String term : inpTerms) {
			if (inpGold.contains(term)) {
				// true positives + 1 if term is in term list and gold standard
				truePositives = truePositives + 1;
			} else {

				falsePositives = falsePositives + 1;
			}
		}
		// loop over gold standard to calculate false negatives
		for (String goldTerm : inpGold) {
			if (inpTerms.contains(goldTerm)) {
			} else {
				// false negatives + 1 if term is in gold standard but not in
				// term list
				falseNegatives = falseNegatives + 1;
			}
		}
	}

	/*
	 * print values
	 */
	public void printValues() {
		System.out.println("====================");
		System.out.println("best terms:      " + bestTerms);
		System.out.println("true positives:  " + truePositives);
		System.out.println("false positives: " + falsePositives);
		System.out.println("false negatives: " + falseNegatives);
		System.out.println("precision:       " + getPrecision());
		System.out.println("recall:          " + getRecall());
		System.out.println("f1:              " + getF1Score());
		System.out.println("====================");
	}

	/*
	 * calculate precision
	 */
	public static double getPrecision() {
		double precision = truePositives / (truePositives + falsePositives);
		return precision;
	}

	/*
	 * calculate recall
	 */
	public static double getRecall() {
		double recall = truePositives / (truePositives + falseNegatives);
		return recall;
	}

	/*
	 * calculate f1 score
	 */
	public static double getF1Score() {
		double precision = getPrecision();
		double recall = getRecall();
		double f1 = 0;
		if (precision == -recall || (recall == 0 && precision == 0)) {
			f1 = 0;
		} else {
			f1 = (2 * precision * recall) / (precision + recall);
		}
		return f1;
	}

	/*
	 * sort the evaluation data by precision
	 */
	static ArrayList<EvaluationData> sortEvaDataByPrecision(
			ArrayList<EvaluationData> inpEva) {
		Collections.sort(inpEva, new Comparator<EvaluationData>() {
			@Override
			public int compare(EvaluationData t1, EvaluationData t2) {
				return Double.compare(t2.precision, t1.precision);
			}
		});
		return inpEva;
	}

	/*
	 * sort the evaluation data by recall
	 */
	static ArrayList<EvaluationData> sortEvaDataByRecall(
			ArrayList<EvaluationData> inpEva) {
		Collections.sort(inpEva, new Comparator<EvaluationData>() {
			@Override
			public int compare(EvaluationData t1, EvaluationData t2) {
				return Double.compare(t2.recall, t1.recall);
			}
		});
		return inpEva;
	}

	/*
	 * sort the evaluation data by f1
	 */
	static ArrayList<EvaluationData> sortEvaDataByF1(
			ArrayList<EvaluationData> inpEva) {
		Collections.sort(inpEva, new Comparator<EvaluationData>() {
			@Override
			public int compare(EvaluationData t1, EvaluationData t2) {
				return Double.compare(t2.f1, t1.f1);
			}
		});
		return inpEva;
	}

	/*
	 * Returns a string of max scores 
	 * Form: 
	 * 		The algorithm found 123 terms.
	 * 		The gold standard is 125 terms large. 	 
	 * 		The highest precision of 0.10494623655913979 is reached by using Top 300 terms. 
	 * 		The highest recall of 0.44757094757094756 is reached by using Top 990 terms. 
	 * 		The highest f1 score of 0.15550124292369075 is reached by using Top 990 terms.
	 * 		========================================
	 */
	public static String getMaxStatisticAsString(
			ArrayList<EvaluationData> inpEvaData, int inpGoldSize) {
		String max = "";
		// copy the ArrayList from input to protect it from overwriting
		ArrayList<EvaluationData> evaData = new ArrayList<EvaluationData>();
		evaData.addAll(inpEvaData);
		// sort ArrayList by precision, recall and f1 and add them to the return
		// string
		ArrayList<EvaluationData> maxPrecision = sortEvaDataByPrecision(evaData);
		max = "The algorithm found "+ maxPrecision.size()+ " terms."
				+ System.lineSeparator();
		max = max + "The gold standard is " + inpGoldSize + " terms large"
				+ System.lineSeparator();
		max = max + "The highest precision of " + maxPrecision.get(0).precision
				+ " is reached by using Top " + maxPrecision.get(0).bestTerms
				+ " terms." + System.lineSeparator();
		ArrayList<EvaluationData> maxRecall = sortEvaDataByRecall(evaData);
		max = max + "The highest recall of " + maxRecall.get(0).recall
				+ " is reached by using Top " + maxRecall.get(0).bestTerms
				+ " terms." + System.lineSeparator();
		ArrayList<EvaluationData> maxF1 = sortEvaDataByF1(evaData);
		max = max + "The highest f1 score of " + maxF1.get(0).f1
				+ " is reached by using Top " + maxF1.get(0).bestTerms
				+ " terms." + System.lineSeparator();
		max = max + "========================================"
				+ System.lineSeparator();
		return max;
	}

	/*
	 * saves the evaluation data 
	 * Form: 
	 * 		=====Top N Terms: 30 
	 * 		Precision: 0.016666666666666666 
	 * 		Recall:    0.003968253968253968 
	 * 		F1-Score:  0.006410256410256409 
	 *	 	=========================
	 */
	public static void saveEvaluationData(ArrayList<EvaluationData> inpEvaData, int inpGoldSize)
			throws IOException {
		// create output string
		String evaString = "";
		// add max precision, max recall and max f1 score information to the
		// output string
		evaString = evaString + getMaxStatisticAsString(inpEvaData, inpGoldSize);
		// loop over all evaluated data and add the calculated scores to the
		// output string
		for (EvaluationData eva : inpEvaData) {
			evaString = evaString + "=====Top N Terms: " + eva.getBestTerms()
					+ System.lineSeparator();
			evaString = evaString + "Precision: " + eva.getPrecision()
					+ System.lineSeparator();
			evaString = evaString + "Recall:    " + eva.getRecall()
					+ System.lineSeparator();
			evaString = evaString + "F1-Score:  " + eva.getF1()
					+ System.lineSeparator();
			evaString = evaString + "========================="
					+ System.lineSeparator();
		}
		// write file from the given output string
		String [] fileName = evaluationFileName.split("__");
		Functions.writeStringToFile(fileName[0]+"_eva", evaString);
	}

	/*
	 * start evaluation manually input required the path of the gold standard
	 * (simple text file with each term in a separate line) also required is the
	 * path of the scored term list document created by Venu.JAVA or
	 * Balachandran.JAVA
	 */
	public static void startCalculation(String inpTermPath, String inpGoldPath)
			throws IOException {
		ArrayList<String> gold = new ArrayList<String>();
		ArrayList<Term> terms = new ArrayList<Term>();
		ArrayList<EvaluationData> evaluationData = new ArrayList<EvaluationData>();
		// get ArrayList from given file path
		gold = getGold(inpGoldPath);
		terms = getTerms(inpTermPath);
		// set output file name for evaluation from given term path
		setEvaluationFileName(inpTermPath);
		bestTerms = 10;
		// calculate scores for top 1,2,3,4 and so on terms
		while (bestTerms < terms.size()) {
			// set precision, recall and f score
			setEvaluationValues(getTermsTypeArrayString(terms), gold);
			// add calculated scores to ArrayList
			evaluationData.add(new EvaluationData(bestTerms, getPrecision(),
					getRecall(), getF1Score()));
			// increase the top n terms by 1
			bestTerms = bestTerms + 1;
		}
		// save evaluation data to file
		saveEvaluationData(evaluationData, gold.size());
	}

	/*
	 * start evaluation ask for input of scored terms list and gold standard
	 */
	public static void startEvaluation() throws IOException {
		// initialize user entry
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		System.out.print("Please enter the file path of the scored terms:");
		// get input from user
		String filePath = br.readLine();
		// replace back slashes with file separator
		filePath = filePath.replace("\\", File.separator);
		File testFile = new File(filePath);
		// check if entered file path exist and force to enter a correct path
		while (!testFile.exists() || !filePath.contains(".txt")) {
			System.out.print("Path not correcet." + System.lineSeparator()
					+ "Please enter correct file path of the scored terms:"
					+ System.lineSeparator());
			filePath = br.readLine();
			filePath = filePath.replace("\\", File.separator);
			testFile = new File(filePath);
		}

		System.out.print("Please enter the file path of the gold standard:");
		// get input from user
		String goldPath = br.readLine();
		// replace back slashes with file separator
		goldPath = goldPath.replace("\\", File.separator);
		testFile = new File(goldPath);
		// check if entered file path exist and force to enter a correct path
		while (!testFile.exists() || !goldPath.contains(".txt")) {
			System.out.print("Path not correcet." + System.lineSeparator()
					+ "Please enter correct  path of the gold standard:"
					+ System.lineSeparator());
			goldPath = br.readLine();
			goldPath = goldPath.replace("\\", File.separator);
			testFile = new File(goldPath);
		}
		// start the calculation of precision, recall and f1 score
		startCalculation(filePath, goldPath);
	}

	/*
	 * main
	 */
	public static void main(String[] args) throws IOException {
		startEvaluation();
		// C:\Users\Mannheimer\git\EvaluationTermextraktion\TermEvaluation\venu_combined_scored__3.txt
		// C:\Users\Mannheimer\Desktop\Bachelor\Daten\gold.txt
		// C:/Users/Mannheimer/git/EvaluationTermextraktion/TermEvaluation/balachandran_lemmatized_scored.txt
		// C:/Users/Mannheimer/Desktop/Bachelor/Daten/gold_test.txt		
	}

}