package PA2SubmittedFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
/*
 * @author Yingbei Tong, Tianxiang Gao
 * class: MinHash.
 */
public class MinHash {
	private Map<String,List<Integer>> matrix;//N*k matrix, minHash matrix
	private int numPermutation;
	private String folder;//folder name
	private List<String> allDocs;// an array of Strings of all docs' names
	private Map<String,Integer> terms;// array of all the terms/words
	private List<List<Integer>> MNmatrix;//the M*N matrix
	private Map<String, List<String>> docwords;//a HashMap contains each file's words/terms
	private List<Integer> aAndb;//array of prime numbers > terms.size()
	private int P;
	/*
	 * constructor
	 * @param folder
	 * @param numPermutation
	 */
	public MinHash(String folder , int numPermutation) {
		//initial variables
		this.folder = folder;
		docwords = new HashMap<>();
		MNmatrix = new ArrayList<>();
		terms = new HashMap<String,Integer>();
		this.numPermutation = numPermutation;
		allDocs = new ArrayList<String>();
		matrix = new HashMap<>();
		
		//try to read all the files in the folder
		try{
			File file = new File(folder);
			File[] array = file.listFiles();
			int count = 1;
			for(int i = 0 ; i < array.length; i++) {
				//read each file
				if(array[i].isFile()&&array[i].getName().contains("txt")) {
					//System.out.println(array[i].getName());
					allDocs.add(array[i].getName());//and put them in the allDoc list
				
					Scanner in = new Scanner(array[i]);
					List<String> words = new ArrayList<String>();
					//then read each word in the file
					while(in.hasNext()) {
						
						String s = in.next();
						//need to pre-process the word then add it.
						// remove all the words with length < 3 or word "the"
						//remove symbols: ? , : ; "
						s = s.toLowerCase();
						
						String str = "";
						for(int k = 0 ; k < s.length(); k++) {
							char x = s.charAt(k);
							if(x !='?' && x!=',' && x != ':' && x!=';' && x!='\"'&& x!='.'){
							//if((x >='a' && x <='z') || (x>='0' && x<='9')){	
								str = str + x;
							}
						}					
						if(str.equals("the")|| str.length()<3){continue;}
						//
						//System.out.println(str);
						if(!terms.containsKey(str)) {
							terms.put(str, count++);
						}
						if(!words.contains(str)) {
							words.add(str);
						}
					}
					docwords.put(array[i].getName(), words);
					in.close();//close scanner
				}
			}
			this.P = nextPrime(terms.size());
			aAndb = permutationAandB(this.numPermutation);
			//build the M * N matrix
			//buildMNmatrix();
			//the create minHash N * k matrix
			for(int i = 0 ; i < allDocs.size(); i++) {
				//for each document compute its minHash sig list, and add the result to the matrix
				matrix.put(allDocs.get(i),minHashSig(allDocs.get(i)));
			}
			
		}catch(Exception e){
			System.err.println(e);
		}		
	}
/////////////////////////////end of constructor////////////////////////////////////
	/*
	 * @param file1
	 * @param file2
	 * this method first gets the corresponding words list of file1 and file2,
	 * then compute their intersection( common words),
	 * then compute their union by file1.size+file2.size-intersection
	 * @return (double)intersection/union
	 */
	public double exactJaccard(String file1 , String file2) {
		List<String> l1 = docwords.get(file1);
		List<String> l2 = docwords.get(file2);
		int intersection = 0;
		//next, look at each word in file1, if file2 also contains this word, intersection++
		for(int i = 0; i < l1.size(); i++) {
			if(l2.contains(l1.get(i))) {
				intersection++;
			}
		}
		return (double)(intersection) / (l1.size()+l2.size()-intersection);
	}
	public boolean isPrime(int x){
		for(int i = 2; i <= (int)Math.sqrt(x) ; i++){
			if(x % i ==0){return false;}
		}
		return true;
	}
	/* 
	 * @param n
	 * a method search for the next prime number > n
	 */
	public int nextPrime(int n) {
		int x = n+1;
		while(!isPrime(x)) {
			x++;
		}
		return x;
	}
	/*
	 * @param n
	 * this method is what i use to generate a list of a and b used in ax+b%p
	 * @return a list of numbers (a and b)
	 */
	public List<Integer> permutationAandB(int n) {
		List<Integer> list = new ArrayList<Integer>();
		Random x = new Random();
		for(int i = 0 ; i < n*2; i++) {
			list.add(x.nextInt(this.terms.size()));
		}
		return list;
	}
	/*
	 * @param fileName
	 * this method use a String fileName as input, 
	 * for each permutation, we use (ax+b)%p as the mapping function
	 * here a and b are two random primes greater than p, and p = terms.size(), denote terms.size() as M
	 * so that we make sure that this mapping is a random permutation from {0 to M-1} to {0 to M-1}
	 * for each permutation, we save the min value as minHash signature
	 * @return a list of integer represents minHash signature
	 */
	public List<Integer> minHashSig(String fileName) {
		List<Integer> list = new ArrayList<Integer>();
		List<String> words = docwords.get(fileName);// get the list of words in the file "fileName"
		int size = terms.size();
		for(int i = 0 ; i < this.numPermutation; i++) {
			//for each permutaion compute a min value
			int min = Integer.MAX_VALUE;
			for(int j = 0 ; j < words.size(); j++) {
				//use function pvalue = ax+b%p,
				int pvalue = ((terms.get(words.get(j)))*this.aAndb.get(i*2)+this.aAndb.get(i*2+1))%this.P;//get permutation mapping value
				min = Math.min(min, pvalue);
			}
			list.add(min);
		}
		return list;
	}
	/*
	 * @param file1
	 * @param file2
	 * this method basically just compare minHash sig of file 1 and file2,
	 * for each signature at the same index of file1 and file2, if they are the same, then counter++
	 * @return l/k (counter/number of signature) 
	 */
	public double approximateJaccard (String file1 , String file2) {
		int apjac = 0;// use this variable to count.
		//compare minHash of file1 and file2 and return apJac
		List<Integer> minHash1 = matrix.get(file1);
		List<Integer> minHash2 = matrix.get(file2);
		for(int i = 0 ; i < this.numPermutation; i++) {
			if(minHash1.get(i).equals(minHash2.get(i))) {
				apjac++;
			}
		}
		return (double)(apjac)/ this.numPermutation;
	}
	/*
	 * @return a list of strings of all docs' names
	 */
	public List<String> allDocs() {
		return this.allDocs;
	}
	/*
	 * @return minHashMatrix, this matrix is computed at line 89 
	 */
	public Map<String,List<Integer>> minHashMatrix() {
		return this.matrix;
	}
	/*
	 * @return the size of terms.
	 */
	public int numTerms () {
		return terms.size();
	}
	/*
	 * @return number of permutations
	 */
	public int numPermutations () {
		return this.numPermutation;
	}
	/*
	 * this method is used to build the M*N matrix, although we havn't use it.
	 */
	public void buildMNmatrix(){
		for(int i = 0 ; i < allDocs.size(); i++) {
			List<Integer> binary = new ArrayList<Integer>();
			String f = allDocs.get(i);
			for(int j = 0; j < terms.size(); j++) {
				if(docwords.get(f).contains(terms.get(j))) {
					binary.add(1);
				}
				else binary.add(0);
			}
			MNmatrix.add(binary);
		}
	}
	
}
