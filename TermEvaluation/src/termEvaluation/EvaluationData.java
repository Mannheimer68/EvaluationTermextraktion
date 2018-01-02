package termEvaluation;

/*
 * Class Evaluation
 * Object to store multiple evaluation data
 */
public class EvaluationData {
	public int bestTerms;
	public double precision;
	public double recall;
	public double f1;

	/*
	 * constructor of EvaluationData
	 */
	public EvaluationData(int inpBestTerms, double inpPrecision,
			double inpRecall, double inpF1) {
		setBestTerms(inpBestTerms);
		setPrecision(inpPrecision);
		setRecall(inpRecall);
		setF1(inpF1);
	}

	/*
	 * set class variable bestTerms
	 */
	public void setBestTerms(int inpBestTerms) {
		this.bestTerms = inpBestTerms;
	}

	/*
	 * set class variable precision
	 */
	public void setPrecision(double inpPrecision) {
		this.precision = inpPrecision;
	}

	/*
	 * set class variable recall
	 */
	public void setRecall(double inpRecall) {
		this.recall = inpRecall;
	}

	/*
	 * set class variable f1
	 */
	public void setF1(double inpF1) {
		this.f1 = inpF1;
	}

	/*
	 * get class variable bestTerms
	 */

	public int getBestTerms() {
		return bestTerms;
	}

	/*
	 * get class variable precision
	 */
	public double getPrecision() {
		return precision;
	}

	/*
	 * get class variable recall
	 */
	public double getRecall() {
		return recall;
	}

	/*
	 * get class variable f1
	 */
	public double getF1() {
		return f1;
	}

}
