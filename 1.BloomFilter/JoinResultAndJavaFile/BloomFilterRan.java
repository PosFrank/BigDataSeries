package PA1;

import java.util.BitSet;

/**
 * @author Tianxiang Gao
 *
 */

public class BloomFilterRan {

	private int setSize;
	private int filterSize;
	private int numberOfHash;
	private int bitsPerElement;
	private int dataSize;
	private BitSet hashTable;
	private int[] a;
	private int[] b;
	
	/**
	 * @param args
	 */
	
	public static void main(String args[]){
		BloomFilterRan bloom = new BloomFilterRan(3, 8);
		bloom.add("bdsjiojo");
		bloom.add("jretvsrg");
		bloom.add("nmesghij");
		System.out.println(bloom.appears("Bdsjiojo"));
		System.out.println(bloom.appears("rbeghutr"));
		System.out.println(bloom.appears("JRETvsrg"));
		System.out.println("filterSize = " + bloom.filterSize());
		System.out.println("dataSize = " + bloom.dataSize());
		System.out.println("numberOfHash = " + bloom.numHashes());
	}
	
	
	/**
	 * 
	 * Creates a Bloom flter that can store a set S of cardinality setSize. 
	 * The size of the Flter should approximately be setSize * bitsPerElement.
	 * The number of hash functions should be the optimal choice which is ln(2 * filterSize / setSize).
	 * 
	 * @param setSize
	 * @param bitsPerElement
	 */
	public BloomFilterRan(int setSize, int bitsPerElement){
		this.dataSize = 0;
		this.setSize = setSize;
		this.bitsPerElement = bitsPerElement;
		this.filterSize = getFilterSize(this.setSize * this.bitsPerElement);
		this.numberOfHash = (int) (Math.log(2) * ((double)this.filterSize / (double)this.setSize));
		this.newHashFunction(this.numberOfHash);
		this.hashTable = new BitSet(filterSize);
	}
	/**
	 * 
	 * @param n
	 * @return the first prime number after n
	 * 
	 * this function will return the first prime number after n
	 * 
	 * */
	private static int getFilterSize(int n){
		n++;
		while(!isPrime(n)){
			n++;
		}
		return n;
	}
	
	/**
	 * 
	 * @param n
	 * @return true if n is prime, false if n is not prime number
	 * 
	 * check if a number is prime number
	 * 
	 * */
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
	/**
	 * 
	 * @param numberOfHash
	 * 
	 * initialize two hash parameters array a[] and b[]
	 * 
	 * for each i from i = 1 to i = numberOfHashFunctions
	 * 		we generate a random value for a[i] and b[i]
	 * 
	 * the random value is range from 0 to the filter size
	 * 
	 * */
	private void newHashFunction(int numberOfHash){
		this.a = new int[this.numberOfHash];
		this.b = new int[this.numberOfHash];
		for(int i = 0; i < numberOfHash; i++){
			this.a[i] = (int) (this.filterSize * Math.random());
			this.b[i] = (int) (this.filterSize * Math.random());
		}
	}
	
	/**
	 * 
	 * generate string's hash value of one specific hash function
	 * 
	 * indexOfHashes means the number of hash function
	 * 
	 * */
	private int getHashValue(String s, int indexOfHashes){
		int stringCode = s.hashCode();
		long value = this.a[indexOfHashes] * stringCode + this.b[indexOfHashes];
		value %= this.filterSize;
		value += this.filterSize;
		value %= this.filterSize;
		return (int)value;
	}
	
	/**
	 * 
	 * Adds the string s to the flter. 
	 * Type of this method is void. 
	 * This method should be case-insensitive. 
	 * For example, it should not distinguish between \Galaxy" and \galaxy".
	 * 
	 * @param s
	 */
	public void add(String s){
		s = s.toLowerCase();
		this.dataSize++;
		int hashValue;
		for(int i = 0; i < this.numberOfHash; i++){
			hashValue = getHashValue(s, i);
			this.hashTable.set(hashValue, true);
		}
	}
	
	/**
	 * @param s
	 * @return is this string contains in the hashtable
	 */
	public boolean appears(String s){
		s = s.toLowerCase();
		boolean rst = true;
		int hashValue;
		for(int i = 0; i < this.numberOfHash && rst == true; i++){
			hashValue = getHashValue(s, i);
			rst = rst && this.hashTable.get(hashValue);
		}
		return rst;
	}
	
	/**
	 * @return the filter size .
	 */
	public int filterSize(){
		return this.filterSize;
	}
	
	/**
	 * @return the number of datas in this filter
	 */
	public int dataSize(){
		return this.dataSize;
	}
	
	/**
	 * @return the number of hash tables in this filter
	 */
	public int numHashes(){
		return this.numberOfHash;
	}
	
}
