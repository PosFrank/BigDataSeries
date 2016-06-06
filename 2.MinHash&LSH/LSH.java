package PA2SubmittedFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/*
 * @author Yingbei Tong, Tianxiang Gao
 * 
 */
public class LSH {
	private String[] docNames;//list of names of documents
	private int bands;
	private int[][] minHashMatrix;
	private List<Map<Integer,List<String>>> maplist;// list of hash tables
	private int T; // T is a prime number > N
	/*
	 * constructor
	 * @param minHashMatrix
	 * @param docNames
	 * @param bands
	 * in the constructor, for each file we seperate the k minHash result into b bands,
	 * then for each permutation compute a hashvalue and put them to the corresponding List
	 */
	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		this.bands = bands;
		this.minHashMatrix = minHashMatrix;
		this.docNames = docNames;
		maplist = new ArrayList<>();
		for(int i = 0 ; i < bands; i++) {
			maplist.add(new HashMap<Integer,List<String>>());
		}
		int k = minHashMatrix[0].length;
		getT(docNames.length);// set T as a prime number > N
		for(int i = 0 ; i < docNames.length; i++) {
			//compute |bands| hash values here and then put them in the hash table
			for(int j = 0 ; j < this.bands; j++) {
				 int h = hash(this.minHashMatrix, i, k/bands*j, Math.min(k/bands*(j+1)-1,k-1));
				 if(!maplist.get(j).containsKey(h)) {
					 maplist.get(j).put(h, new ArrayList<String>());
				 }
				 if(!maplist.get(j).get(h).contains(docNames[i])){
					 maplist.get(j).get(h).add(docNames[i]);
				 }
			}
		}
		
	}
 	/*
	 * @param docName
	 * this method read docName as input, then compute the hash value of the b bands of docName's minHash signature
	 * get those files in the same List with docName and return 
	 * @return a list of String contains files considered as docName's nearDuplicates
	 */
	public List<String> nearDuplicatesOf(String docName) {
		List<String> list= new ArrayList<String>();
		int index = 0;
		int k = this.minHashMatrix[0].length;
		//find docName's corresponding index, 
		for(int i = 0 ; i < docNames.length; i++) {
			if(docNames[i].equals(docName)) {
				index = i;
				break;
			}
		}
		/*
		 * given index, compute the hash value in each band, get the corresponding lists
		 * so these Strings in the same lists are files "nearDuplicatesOf" docName,
		 * add them into list and return the list
		 */
		for(int j = 0 ; j < bands; j++) {
			int h = hash(this.minHashMatrix, index, k/bands*j, Math.min(k/bands*(j+1)-1, k-1));
			List<String> temp = maplist.get(j).get(h);
			for(int i = 0 ; i < temp.size(); i++) {
				if(!temp.get(i).equals(docName) && !list.contains(temp.get(i))) {
					list.add(temp.get(i));
				}
			}
		}
		return list;
	}
	/*
	 * @param x
	 * a method that gets integer x as input
	 * @return if x is a prime return true, otherwise return false
	 */
	public boolean isPrime(int x){
		for(int i = 2; i <= (int)Math.sqrt(x) ; i++){
			if(x % i ==0){return false;}
		}
		return true;
	}
	/*
	 * @param initial
	 * set T's value to a prime number > N
	 */
	public void getT(int initial) {
		int x = initial+1;
		while(true) {
			if(isPrime(x)) {
				this.T = x;
				return;
			}
			x++;
		}
	}
	/*
	 * @param matrix
	 * @param row
	 * @param colstart
	 * @param colend
	 * this method is the hash function.
	 * read numbers x from matrix[row][colstart] to matrix[row][colend], use variable h = (h * x + 1)% T 
	 * @return  hash value h 
	 */
	public int hash(int[][] matrix, int row, int colstart, int colend) {
		int h = 1;
		for(int i = 0 ; i < colend-colstart+1; i++) {
			int x = matrix[row][colstart+i];
			h = (h * x + 1)% this.T;
		}
		return h;
	}
}
