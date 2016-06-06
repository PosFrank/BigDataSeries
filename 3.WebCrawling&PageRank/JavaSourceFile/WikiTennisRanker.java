package PA3;

import java.util.HashSet;

/**
 *
 * 2016
 * Apr 2, 2016
 * WikiTennisRanker.java
 * @author: TianxiangGao
 *
 */
public class WikiTennisRanker {
	/**
	 * @param args
	 * @description: input three files and output each files' top K ranks.
	 *
	 */
	public static void main(String args[]) {
		String f2 = "/PA3/WikiTennisGraph.txt";

		double threshold = 0.005;
		double beta = 0.85;

		topK(15, threshold, beta, f2);
	}

	/**
	 * @param k
	 * @param threshold
	 * @param beta
	 * @param fileName
	 * @description: print the top k number of page by score, indegree and
	 *               outdegree. And calculate the Jaccard Simularity of each two
	 *               pair of them
	 *
	 */
	private static void topK(int k, double threshold, double beta, String fileName) {
		PageRank rank = new PageRank(fileName, threshold);
		rank.rankPage(beta);
		String[] r = rank.topKPageRank(k);
		String[] in = rank.topKInDegree(k);
		String[] out = rank.topKOutDegree(k);
		System.out.println("---------------\n top k rank \n---------------");
		for (String link : r) {
			System.out.println(link);
		}
		System.out.println("---------------\n top k indegree \n---------------");
		for (String link : in) {
			System.out.println(link);
		}
		System.out.println("---------------\n top k outdegree \n---------------");
		for (String link : out) {
			System.out.println(link);
		}
		System.out.println("Jac between r and in:" + jac(r, in));
		System.out.println("Jac between r and out:" + jac(r, out));
		System.out.println("Jac between in and out:" + jac(in, out));
	}

	/**
	 * @param d1
	 * @param d2
	 * @return the Jaccard Simularity of d1 and d2
	 * @description: calculate the Jac(d1, d2)
	 *
	 */
	private static double jac(String[] d1, String[] d2) {
		HashSet<String> s1 = new HashSet<String>();
		for (String s : d1) {
			s1.add(s);
		}
		int inter = 0;
		int union = s1.size();
		for (String s : d2) {
			if (s1.contains(s)) {
				inter++;
			} else {
				union++;
			}
		}
		return (double) inter / (double) union;
	}
}
