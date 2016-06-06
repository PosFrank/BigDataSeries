package PA4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * 2016 May 6, 2016 WordIndex.java
 * 
 * @author: frankgao
 *
 */
public class WordIndex {

	private String folderPath;
	protected ArrayList<String> terms;
	protected HashMap<String, PostingList> dict;
	protected ArrayList<String> allDocs;

	public static void main(String args[]) {
		WordIndex t = new WordIndex(
				"/Users/frankgao/Box Sync/Box Sync/MasterProgram/2016-Spring/Com S 535X/programmingAssignments/4/pa4");
		t.buildIndex();
		System.out.println(t.weight("each", "baseball10.txt"));
		System.out.println(t.weight("each", "baseball1.txt"));
		System.out.println(t.weight("each", "hockey120.txt"));
		System.out.println(t.terms.size());
	}

	/**
	 * @param folderPath
	 * @description: initialize terms, dict, allDocs
	 * @input: the folder path of the files' folder
	 *
	 */
	WordIndex(String folderPath) {
		this.folderPath = folderPath;
		this.terms = new ArrayList<String>();
		this.dict = new HashMap<String, PostingList>();
		this.allDocs = new ArrayList<String>();
	}

	/**
	 * 
	 * @description: this method will build the index
	 *
	 */
	public void buildIndex() {
		File dir = new File(this.folderPath);
		File[] documents = dir.listFiles();
		try {
			for (int i = 0; i < documents.length; i++) {
				if (!documents[i].isFile())
					continue;
				// traverse all the documents
				Scanner s = new Scanner(documents[i]);
				// use delimiter to delete the STOP words
				s.useDelimiter(" |\\.|,|\'|\t|:|;|\n");
				String docName = documents[i].getName();
				this.allDocs.add(docName);
				while (s.hasNext()) {
					String word = s.next();
					// change term to lower case
					word = word.toLowerCase();
					// skip the term with lenth smaller than 3 and equals "the"
					if (word.length() < 3 || word.equals("the"))
						continue;
					// add this term into dict, we will pass the term as word
					// and the document name
					updateDict(word, documents[i].getName());
				}
				s.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("error during file reading");
		}
	}

	/**
	 * @param term
	 *            is the term we need to use to update the dict
	 * @param docName
	 *            is the docName we need to update inthe dict
	 * @description: this method will add this docname into the term posting
	 *               list, if don't have the posting list, we will initailze
	 *               one. If have one we will add the document name into the
	 *               docs list and make the hash value of this doceument's name
	 *               add 1
	 *
	 */
	private void updateDict(String term, String docName) {
		if (this.dict.containsKey(term)) {
			PostingList list = this.dict.get(term);
			if (list.freq.containsKey(docName)) {
				list.freq.put(docName, list.freq.get(docName) + 1);
			} else {
				list.docs.add(docName);
				list.freq.put(docName, 1);
			}
		} else {
			this.terms.add(term);
			PostingList list = new PostingList(term);
			list.docs.add(docName);
			list.freq.put(docName, 1);
			this.dict.put(term, list);
		}
	}

	/**
	 * @param term is the term we are going to add
	 * @return the ArrayList that contains tuple of docs
	 * @description: each tuple have document and frequency of term in document d
	 *
	 */
	protected ArrayList<Tuple> postingsList(String term) {
		ArrayList<Tuple> rst = new ArrayList<Tuple>();
		PostingList list = this.dict.get(term);
		ArrayList<String> docs = list.docs;
		//for each docs, we will find how many times this term in this document
		for (String doc : docs) {
			int freq = list.freq.get(doc);
			rst.add(new Tuple(doc, freq));
		}
		return rst;
	}

	/**
	 * 
	 * @param t just print out the documents have this term
	 *
	 */
	protected void printPostingsList(String t) {
		ArrayList<Tuple> tuples = postingsList(t);
		System.out.println("For term: " + t);
		System.out.println("Document Name" + "\t\t" + "Frequency");
		for (Tuple tuple : tuples) {
			System.out.println(tuple.name + "\t\t" + tuple.freq);
		}
	}

	/**
	 * @param term 
	 * @param doc
	 * @return the weight of this document by this term
	 * @description: it will calculate the weight of this document by this term
	 *
	 */
	protected double weight(String term, String doc) {
		PostingList list = this.dict.get(term);
		if (!list.freq.containsKey(doc))
			return 0.0;
		double tftd = list.freq.get(doc);
		double N = this.allDocs.size();
		double dft = list.docs.size();
		double wtd = (Math.log(1 + tftd) / Math.log(2)) * Math.log10(N / dft);
		return wtd;
	}
}