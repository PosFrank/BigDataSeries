package PA4;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * this is the class of posting list, each one contains 
 * one term, 
 * all docs contain this term
 * how many times this term appears in each docs
 * 
 */
public class PostingList {
	String term;
	ArrayList<String> docs;
	HashMap<String, Integer> freq;

	PostingList(String term) {
		this.term = term;
		this.docs = new ArrayList<String>();
		this.freq = new HashMap<String, Integer>();
	}
}
