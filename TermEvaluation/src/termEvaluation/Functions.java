package termEvaluation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class Functions {
	
	public static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	/*
	 * add all given terms to a string
	 */
	static String addTermsToString(ArrayList<Term> termList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : termList) {
			terms = terms + term.term + System.lineSeparator();
		}
		return terms;
	}
	/*
	 * add all given lemmatiezed terms to a string
	 */
	static String addLemmatizedTermsToString(ArrayList<Term> termList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : termList) {
			terms = terms + term.lemmatizedTerm + System.lineSeparator();
		}
		return terms;
	}
	
	
	/*
	 * add all given terms and scores to a string
	 */
	static String addTermsWithScoreToString(ArrayList<Term> termList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : termList) {
			terms = terms + term.term +"_"+term.score+ System.lineSeparator();
		}
		return terms;
	}
	
	/*
	 * add all given lemmatized terms and scores to a string
	 */
	static String addLemmatizedTermsWithScoreToString(ArrayList<Term> termList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : termList) {
			terms = terms + term.lemmatizedTerm +"_"+term.score+ System.lineSeparator();
		}
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
	}
	

}
