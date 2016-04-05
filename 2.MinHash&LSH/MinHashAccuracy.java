package PA2;

public class MinHashAccuracy {
	public static void main(String args[]){
		String folderPath = "/Users/frankgao/Documents/COMS535/2/space/";
		int numOfPermutation = 800;
		MinHash test = new MinHash(folderPath, numOfPermutation);
		String[] files = test.allDocs();
		int numExceed = 0;
		double threshold = 0.04;
		for(int i = 0; i < files.length - 1; i++){
			for(int j = i + 1; j < files.length; j++){
				double realJac = test.exactJaccard(files[i], files[j]);
				double approJac = test.approximateJaccard(files[i], files[j]);
				if(Math.abs(realJac - approJac) > threshold){
					numExceed++;
				}
			}
		}
		System.out.println("result is " + numExceed);
	}
}
