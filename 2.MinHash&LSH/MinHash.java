package PA2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MinHash {

	private String folder;
	private int numPermutations;
	private int[][] minHashMatrix;
	private Scanner reader;
	private ArrayList<String> allDocs;
	private List<List<Integer>> docs;

	public MinHash(String folder, int numPermutations) {
		this.folder = folder;
		this.numPermutations = numPermutations;
		this.allDocs = new ArrayList<String>();
		this.minHashMatrix = this.buildMinHashMatrix();
	}

	private int[][] buildMinHashMatrix() {
		File dir = new File(this.folder);
		File[] documents = dir.listFiles();
		HashMap<String, Integer> terms = new HashMap<String, Integer>();
		List<List<Integer>> docs = new ArrayList<List<Integer>>();
		int termNum = 0;
		// first we traverse all the documents, we extract terms and stores
		// in "terms" HashMap, then for each document, we put a list of terms
		// it contains in each List<Integer> of docs list.
		try {
			for (int i = 1; i < documents.length; i++) {
				List<Integer> listOfTermIndex = new ArrayList<Integer>();
				if (documents[i].isFile()) {
					this.allDocs.add(documents[i].getName());
					reader = new Scanner(documents[i]);
					//reader.useDelimiter(",|\\.|:|;|'|\\n|\\s+");
					while (reader.hasNext()) {
						String word = reader.next();
						word = word.toLowerCase();
						if (word.length() < 3 || word.equals("the")) {
							continue;
						}
						word = processWord(word);
						if (terms.containsKey(word)) {
							if(!listOfTermIndex.contains(terms.get(word))){
								listOfTermIndex.add(terms.get(word));
							}
						} else {
							listOfTermIndex.add(termNum);
							terms.put(word, termNum);
							termNum++;
						}
					}
					Collections.sort(listOfTermIndex);
					docs.add(listOfTermIndex);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
		this.docs = docs;
		return buildMatrix(termNum);
	}
	
	private String processWord(String word){
		String output = "";
		for(int i1 = 0; i1 < word.length(); i1++){
			char x = word.charAt(i1);
			if(x !='?' && x!=',' && x != ':' && x!=';' && x!='\"'&& x!='.'){
				//if((x >='a' && x <='z') || (x>='0' && x<='9')){	
				output = output + x;
			}
		}
		return output;
	}

	private int[][] buildMatrix(int termNum) {
		int[][] rst = new int[this.numPermutations][this.docs.size()];
		
		// for each permutation, we compute the minHash of all docs
		for (int k = 0; k < this.numPermutations; k++) {
			int x = getRandomPrime(this.numPermutations);
			int y = getRandomPrime(this.numPermutations);
			int p = getRandomPrime(termNum);
			// now we have the function to computer permutation (x * value + y)
			// % p
			// next we need to compute each document's minHash
			for (int i = 0; i < docs.size(); i++) {
				List<Integer> termsInDocI = docs.get(i);
				int min = Integer.MAX_VALUE;
				// for each docs we traverse all the term number inside that
				// document, then
				// get the min value computed by permutation.
				for (int j = 0; j < termsInDocI.size(); j++) {
					int value = (x * termsInDocI.get(j) + y) % p;
					min = (value < min) ? value : min;
				}
				rst[k][i] = min;
			}
		}
		return rst;
	}
	
	private static int getRandomPrime(int n){
		n++;
		while(!isPrime(n)){
			n++;
		}
		return n;
	}
	
	private static boolean isPrime(int n){
		if(n < 2){
			return false;
		}
		boolean isPrime = true;
		for(int i = 2; i * i <= n; i++){
			if(n % i == 0){
				isPrime = false;
				break;
			}
		}
		return isPrime;
	}
	
	public String[] allDocs() {
		String[] docs = new String[this.allDocs.size()];
		for(int i = 0; i < docs.length; i++){
			docs[i] = this.allDocs.get(i);
		}
		return docs;
	}

	public double exactJaccard(String file1, String file2) {
		int f1 = this.allDocs.indexOf(file1);
		int f2 = this.allDocs.indexOf(file2);
		List<Integer> termInFile1 = this.docs.get(f1);
		List<Integer> termInFile2 = this.docs.get(f2);
		int common = 0;
		for(int i = 0; i < termInFile1.size(); i++){
			int temp = termInFile1.get(i);
			if(termInFile2.contains(temp)){
				common++;
			}
		}
		return (double) common / (double) (termInFile1.size() + termInFile2.size() - common);
	}

	public int[] minHashSig(String fileName) {
		int[] rst = new int[this.numPermutations];
		int fileIndex = this.allDocs.indexOf(fileName);
		for (int i = 0; i < rst.length; i++) {
			rst[i] = this.minHashMatrix[i][fileIndex];
		}
		return rst;
	}

	public double approximateJaccard(String file1, String file2) {
		int common = 0;
		int[] f1 = this.minHashSig(file1);
		int[] f2 = this.minHashSig(file2);
		for (int i = 0; i < f1.length; i++) {
			if (f1[i] == f2[i]) {
				common++;
			}
		}
		return (double) common / (double) this.numPermutations;
	}

	public int[][] minHashMatrix() {
		return this.minHashMatrix;
	}

	public int numTerms() {
		return this.minHashMatrix[0].length;
	}

	public int numPermutations() {
		return this.numPermutations;
	}
}
