package PA3;

/**
 * @author TianxiangGao
 *
 */

public class WikiTennisCrawler {
	public static void main(String args[]) {
		String seedUrl = "/wiki/Tennis";
		String[] topics = { "tennis", "grand slam" };
		int max = 1000;
		String fileName = "WikiTennisGraph.txt";
		WikiCrawler wc = new WikiCrawler(seedUrl, topics, max, fileName);
		wc.craw();
	}
}
