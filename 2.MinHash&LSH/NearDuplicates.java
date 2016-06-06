package PA2SubmittedFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
 * @author Yingbei Tong, Tianxiang Gao
 * 
 */
public class NearDuplicates {
	public static void main(String[] args) {
		String folder = "/Users/frankgao/Box Sync/Box Sync/MasterProgram/2016-Spring/Com S 535X/programmingAssignments/2/space";//folder
		int numPermutation = 800;//could be 400, 600, 800
		int bands = 8;//the number of bands
		double s = 0.9;//similarity threshold
		String docName;
		MinHash minhash = new MinHash(folder,numPermutation);
		Map<String, List<Integer>> mh = minhash.minHashMatrix();// save the minhash matrix
		
		int N = minhash.allDocs().size();//N of minHash Matrix
		int k = minhash.numPermutations();//k of minHash matrix
		String[] docNames = new String[N];//save the docNames
		for(int i = 0 ; i < N;i++) {
			docNames[i] = minhash.allDocs().get(i);
		}
		int[][] minHashMatrix = new int[N][k];
		for(int i = 0 ; i < N; i++) {
			for(int j = 0 ; j < k; j++) {
				minHashMatrix[i][j] = mh.get(docNames[i]).get(j);
			}
		}
		LSH lsh = new LSH(minHashMatrix,docNames,bands);
		// for each txt file(not copy) , call nearDuplicatesOf and get files similar to it.
		for(int i = 0 ; i < 200;i++) {
			String str = docNames[i];
			if(str.endsWith("txt")) {
				List<String> list = lsh.nearDuplicatesOf(str);
				List<String> falsePositive = new ArrayList<String>();
				System.out.println("files similar to :"+str);
				for(int j = 0 ; j < list.size(); j++) {
					System.out.print(list.get(j)+ ",");
					if(minhash.exactJaccard(str, list.get(j))<s) {
						falsePositive.add(list.get(j));
					}
				}
				System.out.println();
				System.out.println("number of false Positives: "+ falsePositive.size());
				
			}
		}
	}
}
