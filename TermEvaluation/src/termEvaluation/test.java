package termEvaluation;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;

public class test {
	

	static ArrayList<Term> getLemmatizedTermList(ArrayList<String> term) {
		ArrayList<Term> lemmatizedTerms = new ArrayList<Term>();
		for (int i = 0; i < term.size(); i++) {
			lemmatizedTerms.add(new Term(term.get(i).toString(),
					getLemmatizedTerm(term.get(i).toString())));
		}
		return lemmatizedTerms;
	}

	static String getLemmatizedTerm(String s) {
		String lemmatizedTerm = "";
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		Annotation document = new Annotation(s);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			// Iterate over all tokens in a sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Retrieve and add the lemma for each word into the
				// list of lemmas
				if (lemmatizedTerm == "") {
					lemmatizedTerm = token.get(LemmaAnnotation.class);
				} else {
					lemmatizedTerm = lemmatizedTerm + " "
							+ token.get(LemmaAnnotation.class);
				}
			}
		}
		return lemmatizedTerm;
	}

	
	public static void main(String[] args) throws IOException {	
		System.out.println(getLemmatizedTerm("customers"));
	}

}
