package PA1;
import java.math.BigInteger;
import java.util.BitSet;

/**
 * @author Tianxiang Gao
 *
 */
public class BloomFilterDet {
	private int setSize;
	private int filterSize;
	private int numberOfHash;
	private int bitsPerElement;
	private int dataSize;
	private static final BigInteger FNV64PRIME = new BigInteger("1099511628211", 10);
	private static final BigInteger FNV64INIT = new BigInteger("14695981039346656037", 10);
	private static final BigInteger MOD64 = new BigInteger("2").pow(64);
	private BitSet hashTable;
	
	/**
	 * @param args
	 */
	public static void main(String args[]){
		BloomFilterDet bloom = new BloomFilterDet(5, 8);
		bloom.add("bdsjiojo");
		bloom.add("jretvsrg");
		bloom.add("nmesghij");
		bloom.add("12345678");
		System.out.println(bloom.appears("Bdsjiojo"));
		System.out.println(bloom.appears("rbeghutr"));
		System.out.println(bloom.appears("JRETvsrg"));
		System.out.println("12345678 " + bloom.appears("12345678"));
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
	public BloomFilterDet(int setSize, int bitsPerElement){
		this.setSize = setSize;
		this.bitsPerElement = bitsPerElement;
		this.filterSize = this.setSize * this.bitsPerElement;
		this.numberOfHash = (int) (Math.log(2) * ((double)this.filterSize / (double)this.setSize));
		this.hashTable = new BitSet(filterSize);
		this.dataSize = 0;
	}
	/**
	 * 
	 * @param input
	 * @param modifier
	 * 
	 * it generate the hash Value of this input string and based on the modifier
	 * 
	 * modifier means how many bits the string will rotated
	 * 
	 * */
	private static BigInteger hashValue(String input, int modifier){
		BigInteger h = FNV64INIT;
		String modified = rotation(input, modifier);
		int length = input.length();
		for(int i = 0; i < length; i++){
			int digit = modified.charAt(i) ^ modifier;
			h = h.xor(BigInteger.valueOf(digit));
			h = h.multiply(FNV64PRIME).mod(MOD64);
		}
		return h;
	}
	/**
	 * 
	 * rotation the string's
	 * example: "abcdefg" after rotation shift 3 steps, it is "defgabc"
	 * 
	 * */
	private static String rotation(String input, int shiftStep){
		shiftStep = shiftStep % input.length();
		StringBuilder b = new StringBuilder();
		b.append(input.substring(shiftStep, input.length()));
		b.append(input.substring(0, shiftStep));
		return b.toString();
	}
	/**
	 * 
	 * Adds the string s to the filter. 
	 * Type of this method is void. 
	 * This method should be case-insensitive. 
	 * For example, it should not distinguish between \Galaxy" and \galaxy".
	 * 
	 * @param s
	 */
	public void add(String s){
		s = s.toLowerCase();
		this.dataSize++;
		BigInteger hashValue;
		for(int i = 0; i < this.numberOfHash; i++){
			hashValue = hashValue(s, i);
			int index = hashValue.mod(BigInteger.valueOf(this.filterSize)).intValue();
			this.hashTable.set(index, true);
		}
	}
	/**
	 * @param s
	 * @return is the filter contains this string or not
	 */
	public boolean appears(String s){
		s = s.toLowerCase();
		BigInteger hashValue;
		boolean rst = true;
		for(int i = 0; i < this.numberOfHash; i++){
			hashValue = hashValue(s, i);
			int index = hashValue.mod(BigInteger.valueOf(this.filterSize)).intValue();
			rst = rst && this.hashTable.get(index);
		}
		return rst;
	}

	/**
	 * @return filter's size
	 */
	public int filterSize(){
		return this.filterSize;
	}
	/**
	 * @return data size
	 */
	public int dataSize(){
		return this.dataSize;
	}

	/**
	 * @return number of hash functions
	 */
	public int numHashes(){
		return this.numberOfHash;
	}
}