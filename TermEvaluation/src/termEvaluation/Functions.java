package termEvaluation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
/*
 * Class Functions
 * Some useful functions which where used by all algorithms
 */
public class Functions {

	public static String readFile(String inpPath, Charset inpEncoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(inpPath));
		return new String(encoded, inpEncoding);
	}

	/*
	 * add all given terms to a string
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
	 * add all given lemmatized terms to a string
	 */
	static String addLemmatizedTermsToString(ArrayList<Term> inpTermList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : inpTermList) {
			terms = terms + term.lemmatizedTerm + System.lineSeparator();
		}
		return terms;
	}

	/*
	 * add all given terms and scores to a string
	 */
	static String addTermsWithScoreToString(ArrayList<Term> inpTermList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : inpTermList) {
			terms = terms + term.term + "_" + term.score
					+ System.lineSeparator();
		}
		return terms;
	}

	/*
	 * add all given lemmatized terms and scores to a string
	 */
	static String addLemmatizedTermsWithScoreToString(ArrayList<Term> inpTermList) {
		String terms = "";
		// loop over all terms and write them into a separate line of the string
		for (Term term : inpTermList) {
			terms = terms + term.lemmatizedTerm + "_" + term.score
					+ System.lineSeparator();
		}
		return terms;
	}

	/*
	 * save string to a text file and add a suffix if it already exists e.g. if
	 * "file.txt" exists the new filename is "file__1.txt" than "file__2.txt"
	 * and so on
	 */
	@SuppressWarnings("deprecation")
	static void writeStringToFile(String inpPath, String inpText) throws IOException {				
		int fileNumber = 0;
		File testFile = new File(inpPath + ".txt");
		// check if filename exist and increase the suffix if it exists
		while (testFile.exists()) {
			// split file name at the defined suffix "__"
			String[] split = inpPath.split("__");
			inpPath = split[0];
			inpPath = inpPath + "__" + fileNumber;
			testFile = new File(inpPath + ".txt");
			fileNumber = fileNumber + 1;

		}
		inpPath = inpPath + ".txt";
		//create file on disk
		FileUtils.writeStringToFile(new File(inpPath), inpText);
		System.out.println(String.format("The file [%s] was generated.", inpPath));
	}

	public static void main(String[] args) throws IOException {
	}

}
