package PA2SubmittedFiles;

import java.util.List;
/*
 * @author Yingbei Tong, Tianxiang Gao
 * this class is used to test the minHash's accuracy.
 */
public class MinHashAccuracy {
	public static void main(String []args) {
		String folder = "/Users/frankgao/Box Sync/Box Sync/MasterProgram/2016-Spring/Com S 535X/programmingAssignments/2/space";//folder name
		int numPermutation = 800;//could be 400, 600, 800
		double error = 0.04;//error parameter, could be 0.04,0.07,0.09
		MinHash minhash = new MinHash( folder, numPermutation);
		List<String> alldoc = minhash.allDocs();
		int count = 0;
		//System.out.println(minhash.numTerms());
		for(int i = 0 ; i < alldoc.size()-1; i++) {
			for(int j = i+1; j < alldoc.size(); j++ ) {
				double exactJac = minhash.exactJaccard(alldoc.get(i), alldoc.get(j));
				double approximateJac = minhash.approximateJaccard(alldoc.get(i), alldoc.get(j));
				//System.out.println(alldoc.get(i)+" and "+alldoc.get(j)+":");
				//System.out.println("exactJac:" + exactJac);
				//System.out.println("appJac: "+ approximateJac);
				double diff = Math.abs(exactJac - approximateJac);
				if(diff > error){count++;}
			}	
		}
		//so count is the number of pairs that differ > error
		System.out.println("the number of pairs that the differ is more that error: " + count);
		
	}
}
