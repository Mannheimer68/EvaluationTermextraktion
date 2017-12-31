package termEvaluation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import termEvaluation.Term;

public class Evaluation {
	/*
	 * class variables
	 */
	private static double truePositives = 0;
	private static double falsePositives = 0;
	private static double falseNegatives = 0;
	private static double testCounter = 0;
	
	/*
	 * constructor with given terms and gold list
	 */
	
	public Evaluation(ArrayList<Term> termsTerm, ArrayList<String> gold){		
		setEvaluationValues(getTermsTypeArrayString(termsTerm), gold);		
	}
	/*
	 * constructor with terms list and gold text path
	 */
	public Evaluation(ArrayList<Term> termsTerm, String goldPath) throws IOException{
		ArrayList<String> gold = new ArrayList<String>();
		gold = getGold(goldPath);
		setEvaluationValues(getTermsTypeArrayString(termsTerm), gold);		
	}
	/*
	 * constructor with terms path and gold text path
	 */
	public Evaluation(String termPath, String goldPath) throws IOException{
		ArrayList<String> gold = new ArrayList<String>();
		ArrayList<Term> terms = new ArrayList<Term>();
		gold = getGold(goldPath);
		terms = getTerms(termPath);
		setEvaluationValues(getTermsTypeArrayString(terms), gold);		
	}
	
	
	/*
	 * get the array list of the gold standard
	 */	
	public static ArrayList<String> getGold(String goldPath) throws IOException{
		ArrayList<String> goldList = new ArrayList<String>();
		// read txt file for gold standard
		String text = Functions.readFile(goldPath, Charset.defaultCharset());
		
		// split txt file by line breaks
		String[] goldArray = text.split("\n");
		
		// read array in ArrayList
		for (int i = 0; i < goldArray.length; i++){
			goldList.add(goldArray[i].replaceAll("\r", ""));
		}		
		return goldList;	
	}
	
	/*
	 * get the array list of the terms
	 */	
	public static ArrayList<Term> getTerms(String termPath) throws IOException{
		ArrayList<Term> termList = new ArrayList<Term>();
		// read txt file for gold standard
		String text = Functions.readFile(termPath, Charset.defaultCharset());
		
		// split txt file by line breaks
		String[] termArray = text.split("\n");
		
		// read array in ArrayList
		for (int i = 0; i < termArray.length; i++){
			String[] termWithScore = termArray[i].split("_");
			termList.add( new Term(termWithScore[0].replaceAll("\r", ""), Double.parseDouble(termWithScore[1].replaceAll("\r", ""))));
		}		
		return termList;	
	}
	
	/*
	 * parse term list of type ArrayList<Term> into ArrayList<String>
	 */
	public static ArrayList<String> getTermsTypeArrayString( ArrayList<Term> termsTerm){
		ArrayList<String> terms = new ArrayList<String>();
		for (Term t:termsTerm){
			if( t.score >= 0000){// terms.size() < 10000 ){// 
			terms.add(t.term);
			System.out.println(t.term);}
		}
		return terms;
	}
	

	/*
	 * set true positives, false negatives and false positives
	 */
	public static void setEvaluationValues(ArrayList<String> terms, ArrayList<String> gold) {
		// loop over terms to calculate true positives and false positives
		for (String term : terms) {
			if (gold.contains(term)) {
				// true positives + 1 if term is in term list and gold standard
				truePositives = truePositives + 1;
			} else {
				// false positives +1 if term is in term list but not in gold
				// standard
				falsePositives = falsePositives + 1;
			}
		}
		// loop over gold standard to calculate false negatives
		for (String goldTerm : gold) {
			if (terms.contains(goldTerm)) {
				// test counter should be equal to true positives
				testCounter = testCounter + 1;
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
		System.out.println("true positives: " + truePositives);
		System.out.println("false positives: " + falsePositives);
		System.out.println("false negatives: " + falseNegatives);
		System.out.println("testCounter: " + testCounter);
		System.out.println("precision: " + getPrecision());
		System.out.println("recall: " + getRecall());
		System.out.println("f1: " + getF1Score());
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
		double f1 = (2 * precision * recall) / (precision + recall);
		return f1;
	}

	/*
	 * main
	 */
	public static void main(String[] args) throws IOException {
		Evaluation eva = new Evaluation("C:/Users/Mannheimer/workspace/TermEvaluation/balachandran_lemmatized_scored.txt","C:/Users/Mannheimer/Desktop/Bachelor/Daten/gold_test.txt");
		eva.printValues();
	}

}