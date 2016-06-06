package PA1;

import java.util.HashSet;

/**
 * @author Tianxiang Gao
 *
 */

public class FalsePositives {
	private static int detTotalTest = 0;
	private static int detTrueNegative = 0;
	private static int detFalsePositive = 0;
	
	private static int ranTotalTest = 0;
	private static int ranTrueNegative = 0;
	private static int ranFalsePositive = 0;
	
	/**
	 * @param args
	 */
	public static void main(String args[]){
		int testTimes = 1000000;
		int setSize = 100000;
		int bitsPerElement = 8;
		testFalsePositive(setSize, bitsPerElement,	testTimes);
		
	}
	
	private static void testFalsePositive(int setSize, int bitsPerElement,	int testTimes){
		System.out.println("setSize = " + setSize + " bitsPerElement = " + bitsPerElement + " testTimes = " + testTimes);
		System.out.println();
		double falsePositiveRateDet = getFalsePositiveRateDet(setSize, bitsPerElement, testTimes);
		double falsePositiveRateRan = getFalsePositiveRateRan(setSize, bitsPerElement, testTimes);
		double theoryRate = Math.pow(0.618, bitsPerElement);
		System.out.println("Theory False Positive Rate should be " + theoryRate);
		System.out.println();
		System.out.println("False Positive Rate Det = " + falsePositiveRateDet);
		System.out.println("error rate = " + (int)((falsePositiveRateDet - theoryRate) / theoryRate * 100) + "%");
		System.out.println();
		System.out.println("False Positive Rate Ran= " + falsePositiveRateRan);
		System.out.println("error rate = " + (int)((falsePositiveRateRan - theoryRate) / theoryRate * 100) + "%");
		System.out.println();
		System.out.println("Theory False Positive Case Number should be " + (int)(testTimes * theoryRate));
		System.out.println();
		System.out.println("detTotalTest = " + detTotalTest);
		System.out.println("detFalsePositive number is " + detFalsePositive);
		System.out.println("detTrueNegative = " + detTrueNegative);
		System.out.println();
		System.out.println("ranTotalTest = " + ranTotalTest);
		System.out.println("ranFalsePositive number is " + ranFalsePositive);
		System.out.println("ranTrueNegative = " + ranTrueNegative);
		System.out.println();
	}
	
	/**
	 * @param setSize
	 * @param bitsPerElement
	 * @param testTimes   how many times test it needs to perform
	 * @return the false positive rate this deterministic function performs
	 * get the false positive rate of deterministic function
	 * 
	 * */
	
	private static double getFalsePositiveRateDet(int setSize, int bitsPerElement, int testTimes){
		//initialize a new bloom filter using deterministic function
		BloomFilterDet det = new BloomFilterDet(setSize, bitsPerElement);
		//use a HashSet to store the real strings that added into the filter
		HashSet<String> words = new HashSet<String>();
		//first generate x number of strings; x = setSize
		//then add the strings into filter
		for(int k = 0; k < setSize; k++){
			String temp = randomString(bitsPerElement);
			det.add(temp);
			words.add(temp);
		}
		//generate random string to test if it is in the filter
		//if it is not in the filter and the filter returns true. the false positive counter + 1
		//if it is not in the filter and the filter returns false, the true negative counter + 1
		for(int k = 0; k < testTimes; k++){
			String temp = randomString(bitsPerElement);
			boolean isInFilter = det.appears(temp);
			boolean isInSet = words.contains(temp);
			detTotalTest++;
			if(isInFilter && !isInSet){
				detFalsePositive++;
			}else if(!isInFilter && !isInSet){
				detTrueNegative++;
			}
		}
		//finally we calculate the false positive rate by
		//
		//									false positive counter
		//false positive rate = ________________________________________________
		//						 false positive counter + true negative counter
		double falsePositiveRate = (double)detFalsePositive / ((double)detFalsePositive + (double)detTrueNegative);
		return falsePositiveRate;
	}
	/**
	 * 
	 * @param stringLength
	 * @return a random string
	 * input how long the string we hope to get
	 * 
	 * Then use a string builder to build the string
	 * 
	 * */
	private static String randomString(int bitsPerElement){
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < bitsPerElement; i++){
			builder.append(randomChar());
		}
		return builder.toString();
	}
	/**
	 * @return a random char value from 'a' to 'z'
	 * 
	 * */
	private static char randomChar(){
		int random = (int)(Math.random() * 26);
		char rst;
		rst = (char)(65 + random);
		return rst;
	}
	/**
	 * @param setSize
	 * @param bitsPerElement
	 * @param testTime
	 * @return the false positive rate this random function performs
	 * 
	 * get the false positive rate of random function
	 * 
	 * */
	private static double getFalsePositiveRateRan(int setSize, int bitsPerElement, int testTimes){
		//initialize a new bloom filter using random function
		BloomFilterRan ran = new BloomFilterRan(setSize, bitsPerElement);
		//use a HashSet to store the real strings that added into the filter
		HashSet<String> words = new HashSet<String>();
		StringBuilder builder = new StringBuilder();
		//first generate x number of strings; x = setSize
		//then add the strings into filter
		for(int k = 0; k < setSize; k++){
			for(int i = 0; i < bitsPerElement; i++){
				builder.append(randomChar());
			}
			String temp = builder.toString();
			ran.add(temp);
			words.add(temp);
			builder.delete(0, bitsPerElement);
		}
		//generate random string to test if it is in the filter
		//if it is not in the filter and the filter returns true. the false positive counter + 1
		//if it is not in the filter and the filter returns false, the true negative counter + 1
		for(int k = 0; k < testTimes; k++){
			for(int i = 0; i < bitsPerElement; i++){
				builder.append(randomChar());
			}
			String temp = builder.toString();
			boolean isInFilter = ran.appears(temp);
			boolean isInSet = words.contains(temp);
			ranTotalTest++;
			if(isInFilter && !isInSet){
				ranFalsePositive++;
			}else if(!isInFilter && !isInSet){
				ranTrueNegative++;
			}
			builder.delete(0, bitsPerElement);
		}
		//finally we calculate the false positive rate by
		//
		//									false positive counter
		//false positive rate = ________________________________________________
		//						 false positive counter + true negative counter
		double falsePositiveRate = (double)ranFalsePositive / ((double)ranFalsePositive + (double)ranTrueNegative);
		return falsePositiveRate;
	}
}
