package PA2;

public class MinHashSpeed {
	public static void main(String args[]) {
		String folderPath = "/Users/frankgao/Documents/COMS535/2/space/";
		int numOfPermutation = 800;
		MinHash test = new MinHash(folderPath, numOfPermutation);
		int minHash = getTimeByMinHash(test);
		int exact = getTimeByExact(test);
		System.out.println("exact takes : " + exact + " secends");
		System.out.println("minHash takes : " + minHash + " secends");
	}

	private static int getTimeByExact(MinHash test) {
		long startTime = System.currentTimeMillis();
		String[] files = test.allDocs();
		for (int i = 0; i < files.length - 1; i++) {
			for (int j = i + 1; j < files.length; j++) {
				double realJac = test.exactJaccard(files[i], files[j]);
			}
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		return (int) totalTime / 1000;
	}

	private static int getTimeByMinHash(MinHash test) {
		long startTime = System.currentTimeMillis();
		String[] files = test.allDocs();
		for (int i = 0; i < files.length - 1; i++) {
			for (int j = i + 1; j < files.length; j++) {
				double approJac = test.approximateJaccard(files[i], files[j]);
			}
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		return (int) totalTime / 1000;
	}
}
