package PA3;

/**
 *
 * 2016
 * Apr 2, 2016
 * MyWkiCrawler.java
 * @author: Tianxiang Gao
 *
 */
public class MyWkiCrawler {
	/**
	 * @param args
	 * @description: change the parameters in this method to craw the files.
	 *
	 */
	public static void main(String args[]) {
		String seedUrl = "/wiki/Academy_Awards";
		String[] topics = { "Oscar", "Best Picture" };
		int max = 5;
		String fileName = "/PA3/MyWikiGraph5.txt";
		WikiCrawler wc = new WikiCrawler(seedUrl, topics, max, fileName);
		wc.craw();
	}
}
