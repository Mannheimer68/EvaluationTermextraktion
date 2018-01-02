package termEvaluation;
/*
 * Class Term
 * Used as an object to store terms with their scores
 */

public class Term {
	public String term;
	public String lemmatizedTerm;
	public double frequencyText;
	public double frequencyContrastDomain1;
	public double frequencyContrastDomain2;
	public double score;
	
	/*
	 * constructor for creating a term giving the input term and the lemmatized term
	 */
	public Term(String inpTerm, String inpLemmatizedTerm) {
		this.term = inpTerm;
		this.lemmatizedTerm = inpLemmatizedTerm;
	}
	/*
	 * constructor for creating a term giving the input term and the score
	 */
	public Term(String inpTerm, double inpScore) {
		this.term = inpTerm;
		this.score = inpScore;
	}
	/*
	 * constructor for creating a term giving only the term
	 */
	public Term(String inpTerm) {
		this.term = inpTerm;		
	}
	/*
	 * returns the term
	 */
	public String getTerm() {
		return term;
	}
	/*
	 * returns the lemmatized term
	 */
	public String getLemmatizedTerm() {
		return lemmatizedTerm;
	}
	/*
	 * returns the score
	 */
	public Double getScore() {
		return score;
	}
	/*
	 * returns the frequency
	 */
	public double getFrequencyText() {
		return frequencyText;
	}
	/*
	 * returns the frequency in contrast domain1
	 */
	public double getFrequencyContrastDomain1() {
		return frequencyContrastDomain1;
	}
	/*
	 * returns the frequency in contrast domain2
	 */
	public double getFrequencyContrastDomain2() {
		return frequencyContrastDomain2;
	}

}
