package termEvaluation;

public class Term {
	public String term;
	public String lemmatizedTerm;
	public double frequencyText;
	public double frequencyContrastDomain1;
	public double frequencyContrastDomain2;
	public double score;

	public Term(String inpTerm, String inpLemmatizedTerm) {
		this.term = inpTerm;
		this.lemmatizedTerm = inpLemmatizedTerm;
	}
	public Term(String inpTerm, double inpScore) {
		this.term = inpTerm;
		this.score = inpScore;
	}
	public Term(String inpTerm) {
		this.term = inpTerm;		
	}

	void term() {
		System.out.println(term);
	}

	public String returnTerm() {
		return term;
	}

	public String returnLemmatizedTerm() {
		return lemmatizedTerm;
	}

	public Double returnScore() {
		return score;
	}

	public double returnFrequencyText() {
		return frequencyText;
	}

	public double returnFrequencyContrastDomain1() {
		return frequencyContrastDomain1;
	}

	public double returnFrequencyContrastDomain2() {
		return frequencyContrastDomain2;
	}

}
