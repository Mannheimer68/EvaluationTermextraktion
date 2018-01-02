package termEvaluation;
/*
 * Class Sentence
 * Used as an object for storing terms of a sentence with POS-Tags
 */
public class Sentence {	
	/*
	 * Class variables
	 */
	String word;
	String pos;	
	String sentence;

	/*
	 * constructor
	 */
	Sentence( String inpSentence, String inpWord, String inpPos){
		word = inpWord;
		pos = inpPos;
		sentence = inpSentence;
	}
	 /*
	  * returns the sentence
	  */
	 public String getSentence(){
		 return sentence;
	 }
	 /*
	  * returns the word
	  */
	  public String getWord(){
		 return word;
	 }
	  /*
	   * returns the pos tag
	   */
	 public String getPos(){
		 return pos;
	 }
		

}
