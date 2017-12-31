package termEvaluation;
import java.io.*;
import java.util.*;

public class dataInput {
	private Scanner inputText;
	
	public void openFile(){	
		try{
			inputText = new Scanner (new File ("C:/Users/Mannheimer/Desktop/Bachelor/Daten/testdaten.txt"));
		}
		catch(Exception e){
			System.out.println("File could not be found");
		}
	}
	
	public void readFile(){
		String text = null;
		while(inputText.hasNext()){
//			System.out.println(inputText.next());			
			text += inputText.next();			
		}
		System.out.println(text);
	}
	
	public void closeFile(){
		inputText.close();
	}

	public static void main(String[] args) {
		dataInput r = new dataInput();
		r.openFile();
		r.readFile();
		r.closeFile();
	}

}
