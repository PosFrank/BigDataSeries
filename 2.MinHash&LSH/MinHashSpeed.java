package PA2SubmittedFiles;

import java.util.List;
/*
 * @author Yingbei Tong, Tianxiang Gao
 * use this class to test running time(speed) of exactJac and approximateJac
 */
public class MinHashSpeed {
	public static void main(String []args) {
		String folder = "/Users/frankgao/Box Sync/Box Sync/MasterProgram/2016-Spring/Com S 535X/programmingAssignments/2/space";
		int numPermutation = 800;//could be 400, 600, 800
		MinHash minhash = new MinHash( folder, numPermutation);
		List<String> alldoc = minhash.allDocs();
		//compute time taken of this nested for loop
		long start = System.currentTimeMillis();
		for(int i = 0 ; i < alldoc.size()-1; i++) {
			for(int j = i+1; j < alldoc.size(); j++ ) {
				double exactJac = minhash.exactJaccard(alldoc.get(i), alldoc.get(j));
				//double approximateJac = minhash.approximateJaccard(alldoc.get(i), alldoc.get(j));
			}	
		}
		long end = System.currentTimeMillis();
		long timetaken = end - start;
		System.out.println("time for exactJac: "+ (double)(1.0*timetaken/1000)+ "s");
		start = System.currentTimeMillis();
		for(int i = 0 ; i < alldoc.size()-1; i++) {
			for(int j = i+1; j < alldoc.size(); j++ ) {
				//double exactJac = minhash.exactJaccard(alldoc.get(i), alldoc.get(j));
				double approximateJac = minhash.approximateJaccard(alldoc.get(i), alldoc.get(j));
			}	
		}
		end = System.currentTimeMillis();
		long timetaken2 = end - start;
		System.out.println("time for approxJac: "+ ((double)timetaken2)/1000 + "s");
		
	}
}
