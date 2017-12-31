package termEvaluation;

public class Sentence {	
	String word;
	String pos;	
	String sentence;
//	int sentenceNumber;
	
	Sentence( String inpSentence, String inpWord, String inpPos){
		word = inpWord;
		pos = inpPos;
		sentence = inpSentence;
//		sentenceNumber = inpSentenceNumber;
	}
	
	 void word(){
		 System.out.println(word);
	 }
	 String returnSentence(){
		 return sentence;
	 }
	  String returnWord(){
		 return word;
	 }
	 String returnPos(){
		 return pos;
	 }
		

}
