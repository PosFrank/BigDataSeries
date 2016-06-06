package PA4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * 2016
 * May 6, 2016
 * BiWordIndex.java
 * @author: frankgao
 *
 */
public class BiWordIndex {
	
	private String folderPath;
	protected ArrayList<String> terms;
	protected HashMap<String, PostingList> dict;
	protected ArrayList<String> allDocs;
	
	public static void main(String args[]){
		BiWordIndex b = new BiWordIndex("/Users/frankgao/Box Sync/Box Sync/MasterProgram/2016-Spring/Com S 535X/programmingAssignments/4/pa4");
		b.buildIndex();
		System.out.println(b.terms.size());
		b.printPostingsList("for each");
	}
	/**
	 * @param folder path of the files folder
	 * construct the biwordindex
	 * 
	 * */
	BiWordIndex(String folderPath){
		this.folderPath = folderPath;
		this.terms = new ArrayList<String>();
		this.dict = new HashMap<String, PostingList>();
		this.allDocs = new ArrayList<String>();
	}
	/**
	 *
	 * this method will build the index for biword in the documents
	 *
	 */
	public void buildIndex() {
		File dir = new File(this.folderPath);
		File[] documents = dir.listFiles();
		try {
			//triverse all the documents
			for (int i = 0; i < documents.length; i++) {
				if (!documents[i].isFile()) continue;
				//use scanner to find every words in the documents
				Scanner s = new Scanner(documents[i]);
				//use delimiter to seperate different words
				s.useDelimiter(" |\\.|,|\'|\t|:|;|\n");
				String docName = documents[i].getName();
				this.allDocs.add(docName);
				String pre = "";
				while (s.hasNext()) {
					//process each word
					String word = s.next();
					word = word.toLowerCase();
					if(word.length() < 3 || word.equals("the")) continue;
					if(pre.length() == 0){
						pre = word;
						continue;
					}
					//combile the words to formulate the biword
					String biword = pre + " " + word;
					pre = word;
					//System.out.println(biword);
					updateDict(biword, documents[i].getName());
				}
				s.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("error during file reading");
		}
	}
	/**
	 * 
	 * this method will update dict by new words and document's name
	 * @param term is the new word we need to add
	 * @param docName is the docName we need to add into dic
	 * */
	private void updateDict(String term, String docName){
		if(this.dict.containsKey(term)){
			PostingList list = this.dict.get(term);
			if(list.freq.containsKey(docName)){
				list.freq.put(docName, list.freq.get(docName) + 1);
			}else{
				list.docs.add(docName);
				list.freq.put(docName, 1);
			}
		}else{
			this.terms.add(term);
			PostingList list = new PostingList(term);
			list.docs.add(docName);
			list.freq.put(docName, 1);
			this.dict.put(term, list);
		}
	}
	
	/**
	 * this method will return the list of posting that contain this biword
	 * @param biword
	 * @return the posting list
	 * 
	 * */
	private ArrayList<String> postingsList(String biword) {
		PostingList list = this.dict.get(biword);
		return list.docs;
	}
	/**
	 * this method will print the list of documents have this biword
	 * @param biword
	 * 
	 * */
	private void printPostingsList(String biword) {
		ArrayList<String> docs = postingsList(biword);
		System.out.println("The documents' name which contains biword \"" + biword + "\" are shown as follows:");
		for(String doc: docs){
			System.out.println(doc);
		}
	}
}
