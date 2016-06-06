package PA4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 *
 * 2016
 * May 6, 2016
 * QueryProcessor.java
 * @author: frankgao
 *
 */
public class QueryProcessor {

	private String folderPath;
	private WordIndex w;
	private BiWordIndex bw;

	/**
	 * @param args
	 * @description: this process will guide to use this processor, repeatly query the files
	 *
	 */
	public static void main(String args[]) {
		String path = "/Users/frankgao/Box Sync/Box Sync/MasterProgram/2016-Spring/Com S 535X/programmingAssignments/4/pa4";
		System.out.println("Start to read the files and initialize WordIndex and BiWordIndex");
		QueryProcessor q = new QueryProcessor(path);
		System.out.println("WordIndex and BiWordIndex built finished. \nStart query:");
		Scanner input = new Scanner(System.in);
		while (true) {
			System.out.println("please type the query: ");
			String query = input.nextLine();
			System.out.println("please type how many top files: ");
			int k = input.nextInt();
			input.nextLine();
			q.query(query, k);
		}
	}

	/**
	 * 
	 * @param folderPath
	 *            is the folder's path which contains all the files
	 * 
	 *            the constructor will initialize WordIndex and BiWordIndex
	 *            object after process the files.
	 * 
	 */
	QueryProcessor(String folderPath) {
		this.folderPath = folderPath;
		this.w = new WordIndex(this.folderPath);
		w.buildIndex();
		this.bw = new BiWordIndex(this.folderPath);
		bw.buildIndex();
	}

	/**
	 * 
	 * @param query
	 *            is the String of query
	 * @param k
	 *            is the number of how many top rank files
	 * 
	 *            this will process this query by print the result of top k rank
	 *            files and their cosine similarity
	 * 
	 */
	private void query(String query, int k) {
		// lower case the query and delete STOP words and so on.
		query = correctQuery(query);

		// use priorityQueue to find the top 2K number of files of highest
		// cosine similarities
		PriorityQueue<DocScore> q2k = new PriorityQueue<DocScore>(2 * k, new DocCompare_cosSim());
		for (int i = 0; i < w.allDocs.size(); i++) {
			String docName = w.allDocs.get(i);
			double cosSim = cosSim(docName, query);
			if (cosSim != 0) {
				System.out.println("doc: " + docName + "\t has cosSim of " + cosSim);
			}
			q2k.add(new DocScore(docName, cosSim));
		}

		// store the top 2K files object into an ArrayList
		ArrayList<DocScore> doc2K = new ArrayList<DocScore>();
		for (int i = 0; i < k * 2; i++) {
			doc2K.add(q2k.poll());
		}

		// print out put the top 2k files

		System.out.println("\n\n the top " + 2 * k + " documents are : \n");
		for (DocScore doc : doc2K) {
			System.out.println(doc.docName + "\t\t" + doc.cosSim);
		}
		// start to find the top k files by frequency and cosine similarity
		// get the biword from the query
		ArrayList<String> biWordsQuery = getBiWordsFromQuery(query);
		// get how many time each biword of query appears in the query
		HashMap<String, Integer> freqOfDoc = computeSd(doc2K, biWordsQuery);
		// find the top k files with highest cosine similarity and frequency
		PriorityQueue<DocScore> q1k = new PriorityQueue<DocScore>(k, new DocCompare_Freq_cosSim());
		for (DocScore doc : doc2K) {
			doc.freq = freqOfDoc.get(doc.docName);
			q1k.add(doc);
		}
		// print out the top k files in 2k files by cosine similarity and weight
		System.out.println("\n the top " + k + " documents are : \n");
		for (int i = 0; i < k; i++) {
			DocScore cur = q1k.poll();
			System.out.println("doc Name: " + cur.docName + "\n cos Sim = " + cur.cosSim + "\n sd = " + cur.freq);
		}
	}

	/**
	 * @param doc2K
	 *            is the top 2k files with highest similarities
	 * @param biWordsQuery
	 *            is the bi-words form query s(d) is the number of bi-word in
	 *            query that appear in document
	 * @return HashMap String -> document Integer-> how many biwords form query
	 *         appears in this document
	 */

	private HashMap<String, Integer> computeSd(ArrayList<DocScore> doc2K, ArrayList<String> biWordsQuery) {
		HashMap<String, Integer> sd = new HashMap<String, Integer>();
		// first set all be 0
		for (DocScore doc : doc2K) {
			sd.put(doc.docName, 0);
		}
		// for each document, if it can be seem in biword dict, then we modify
		// the hashMap by add 1
		for (DocScore doc : doc2K) {
			for (String biWord : biWordsQuery) {
				if (bw.dict.get(biWord).freq.containsKey(doc.docName)) {
					sd.put(doc.docName, sd.get(doc.docName) + 1);
				}
			}
		}
		return sd;
	}

	/**
	 * 
	 * @param query
	 *            is the corrected query
	 * 
	 * @return the ArrayList of all the bi-word combination in the query
	 * 
	 */
	private ArrayList<String> getBiWordsFromQuery(String query) {
		ArrayList<String> bwq = new ArrayList<String>();
		String pre = "";
		Scanner s = new Scanner(query);
		while (s.hasNext()) {
			String cur = s.next();
			if (pre.length() == 0) {
				pre = cur;
				continue;
			}
			bwq.add(pre + " " + cur);
			pre = cur;
		}
		s.close();
		return bwq;
	}

	/**
	 * 
	 * @param doc
	 *            is the document name
	 * @param query
	 *            is the corrected query
	 * @return the cosine similarity of this document and query
	 * 
	 */
	private double cosSim(String doc, String query) {
		// intialize the documents vector and query vector
		double[] vDoc = new double[w.terms.size()];
		double[] vQuery = new double[w.terms.size()];
		// calculate the document vector
		for (int i = 0; i < vDoc.length; i++) {
			vDoc[i] = w.weight(w.terms.get(i), doc);
		}
		// calculate the query vector
		/**
		 * 
		 * queryTerm: String -> Integer String: each term in the query Integer
		 * -> how many times this term appears in the query
		 * 
		 */
		HashMap<String, Integer> queryTerm = new HashMap<String, Integer>();
		Scanner s = new Scanner(query);
		s.useDelimiter(" |\\.|,|\'|\t|:|;|\n");
		while (s.hasNext()) {
			String cur = s.next();
			cur = cur.toLowerCase();
			if (cur.length() < 3 || cur.equals("the"))
				continue;
			if (queryTerm.containsKey(cur)) {
				queryTerm.put(cur, queryTerm.get(cur) + 1);
			} else {
				queryTerm.put(cur, 1);
			}
		}
		s.close();

		/**
		 *
		 * calculate the query weight in each term in the query
		 * 
		 */
		for (int i = 0; i < vQuery.length; i++) {
			vQuery[i] = queryWeight(w.terms.get(i), queryTerm);
		}
		double vq_Dot_Product_vd = 0;
		for (int i = 0; i < vDoc.length; i++) {
			vq_Dot_Product_vd += vDoc[i] * vQuery[i];
		}
		double vd_length = getLength(vDoc);
		double vq_length = getLength(vQuery);
		return vq_Dot_Product_vd / (vd_length * vq_length);
	}

	/**
	 * 
	 * this method gets the length of this vector
	 * 
	 * @param vector
	 *            is the weight vector
	 * @return the length of vector
	 * 
	 */
	private double getLength(double[] vector) {
		double length = 0;
		for (double v : vector) {
			length += v * v;
		}
		return Math.sqrt(length);
	}

	/**
	 * 
	 * @param term
	 *            is the term we want to check
	 * @param queryTerm
	 *            is the how many times this String appears in the query
	 * @return the weight of this query
	 * 
	 */
	private double queryWeight(String term, HashMap<String, Integer> queryTerm) {
		if (!queryTerm.containsKey(term))
			return 0.0;
		double tftq = queryTerm.get(term);
		double wtq = (Math.log(1 + tftq) / Math.log(2));
		return wtq;
	}

	/**
	 * 
	 * @param quey
	 *            is the original query
	 * @return the modified query by delete STOP words and to lower case.
	 * 
	 */
	private String correctQuery(String query) {
		Scanner s = new Scanner(query);
		s.useDelimiter(" |\\.|,|\'|\t|:|;|\n");
		StringBuilder sb = new StringBuilder();
		while (s.hasNext()) {
			String word = s.next();
			word = word.toLowerCase();
			if (word.length() < 3 || word.equals("the"))
				continue;
			sb.append(word + " ");
		}
		s.close();
		return sb.toString();
	}
}
