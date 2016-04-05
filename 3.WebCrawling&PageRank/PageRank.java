package PA3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * 2016
 * Apr 2, 2016
 * PageRank.java
 * @author: Tianxiang Gao
 *
 */
public class PageRank {
	private ArrayList<Page> pages;
	private ArrayList<Page> pagesByInNum;
	private ArrayList<Page> pagesByOutNum;
	private int pageNum;
	private HashMap<String, Page> pageFind;
	private int edgeNum;
	private double threshold;

	/**
	 * @param fileName
	 * @param threshold
	 * @description: the constructor will construct the page graph based on the
	 *               file of fileName.
	 * @input: fileName and threshold initialize the threshold for future
	 *         method's usage
	 *
	 */
	PageRank(String fileName, double threshold) {
		this.threshold = threshold;
		this.pageFind = new HashMap<String, Page>();
		this.edgeNum = 0;
		try {
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			this.pageNum = Integer.parseInt(reader.readLine());
			this.pages = new ArrayList<Page>(this.pageNum);
			String line = reader.readLine();
			while (line != null) {
				String[] fromAndTo = line.split(" ");
				String from = fromAndTo[0];
				String to = fromAndTo[1];
				Page fromPage = getOrBuildPage(from);
				Page toPage = getOrBuildPage(to);
				if (!fromPage.outPage.contains(toPage)) {
					fromPage.outPage.add(toPage);
				}
				if (!toPage.incomePage.contains(fromPage)) {
					toPage.incomePage.add(fromPage);
				}
				this.edgeNum++;
				line = reader.readLine();
			}
			reader.close();
			;
		} catch (FileNotFoundException e) {
			System.out.println("Error during construct graph");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param beta
	 * @description: it input the beta value and rank the page by page rank
	 *               algorithm, which used this beta value. it also rank the
	 *               page by score, inDegree and outDegree
	 *
	 */
	public void rankPage(double beta) {
		System.out.println("Page Number: " + this.pageNum + " pages.size() = " + this.pages.size());
		System.out.println("Edge Number = " + this.edgeNum);
		for (Page cur : this.pages) {
			cur.score = 1.0 / this.pageNum;
		}
		int counter = 1;
		while (true) {
			double value = oneMoreStep(beta);
			System.out.println("value : " + value);
			if (value <= this.threshold) {
				break;
			}
			counter++;
		}
		System.out.println("Total rounds: " + counter);
		System.out.println("Finished!!!!!!");
		this.pagesByInNum = new ArrayList<Page>(this.pages);
		this.pagesByOutNum = new ArrayList<Page>(this.pages);
		Collections.sort(this.pages, new SortByScore());
		Collections.sort(this.pagesByInNum, new SortByInNum());
		Collections.sort(this.pagesByOutNum, new SortByOutNum());
		// double total = 0;
		// for(Page page: this.pages){
		// total += page.score;
		// }
		// System.out.println("total : " + total);
	}

	/**
	 * @param beta
	 * @return the NORM value after this time's walking on the graph
	 * @description: ths graph will walking the graph and reassign the score for
	 *               each page by the algorithm of Page Rank
	 *
	 */
	private double oneMoreStep(double beta) {
		HashMap<Page, Double> next = new HashMap<Page, Double>();
		for (Page cur : this.pages) {
			next.put(cur, (1.0 - beta) / this.pageNum);
		}
		double value = 0;
		for (Page cur : this.pages) {
			if (cur.outPage.size() != 0) {
				for (Page temp : cur.outPage) {
					double newScore = next.get(temp) + beta * (cur.score / cur.outPage.size());
					next.put(temp, newScore);
				}
			} else {
				for (Page temp : this.pages) {
					double newScore = next.get(temp) + beta * (cur.score / this.pages.size());
					next.put(temp, newScore);
				}
			}
		}
		for (Page node : this.pages) {
			value += Math.abs(next.get(node) - node.score);
			node.score = next.get(node);
		}
		return value;
	}

	/**
	 * @param link
	 * @return the Page object of this page
	 * @description: get the Page object of preconstructed page or build a new
	 *               one if it is first visited
	 *
	 */
	private Page getOrBuildPage(String link) {
		Page page;
		if (this.pageFind.containsKey(link)) {
			page = this.pageFind.get(link);
		} else {
			page = new Page(link);
			this.pageFind.put(link, page);
			this.pages.add(page);
		}
		return page;
	}

	/**
	 * @param page
	 * @return the score of this page
	 * @description: A method named pageRankOf its gets name of vertex of the
	 *               graph as parameter and returns its page rank.
	 *
	 */
	public double pageRankOf(String page) {
		Page cur = this.pageFind.get(page);
		return cur.score;
	}

	/**
	 * @param page
	 * @return the value of this page's out degree
	 * @description: A method named outDegreeOf its gets name of vertex of the
	 *               graph as parameter and returns its out degree.
	 *
	 */
	public int outDegreeOf(String page) {
		Page cur = this.pageFind.get(page);
		return cur.outPage.size();
	}

	/**
	 * @param page
	 * @return the size of this page's in degree
	 * @description: A method named inDegreeOf its gets name of vertex of the
	 *               graph as parameter and returns its in degree.
	 *
	 */
	public int inDegreeOf(String page) {
		Page cur = this.pageFind.get(page);
		return cur.incomePage.size();
	}

	/**
	 * @description: A method named numEdges that returns number of edges of the
	 *               graph.
	 * @output: the number of edges
	 *
	 */
	public int numEdges() {
		return this.edgeNum;
	}

	/**
	 * @param k
	 * @description: A method named topKPageRank that gets an integer k as
	 *               parameter and returns an array (of strings) of pages with
	 *               top k page ranks.
	 * @input: integer k
	 * @output the array of top k page rank
	 *
	 */
	public String[] topKPageRank(int k) {
		String[] rst = new String[k];
		for (int i = 0; i < k; i++) {
			rst[i] = this.pages.get(i).link;
		}
		return rst;
	}

	/**
	 * @param k
	 * @description: A method named topKInDegree that gets an integer k as
	 *               parameter and returns an array (of strings) of pages with
	 *               top k in degree.
	 * @input: integer k
	 * @output: the array of top k in degree pages
	 *
	 */
	public String[] topKInDegree(int k) {
		String[] rst = new String[k];
		for (int i = 0; i < k; i++) {
			rst[i] = this.pagesByInNum.get(i).link;
		}
		return rst;
	}

	/**
	 * @param k
	 * @description: A method named topKOutDegree that gets an integer k as
	 *               parameter and returns an array (of strings) of pages with
	 *               top k out degree.
	 * @input: top number k
	 * @output: the array of top k outdegree pages
	 *
	 */
	public String[] topKOutDegree(int k) {
		String[] rst = new String[k];
		for (int i = 0; i < k; i++) {
			rst[i] = this.pagesByOutNum.get(i).link;
		}
		return rst;
	}

	/**
	 *
	 * 2016 Apr 2, 2016 PageRank.java
	 * 
	 * @author: frankgao
	 * 
	 *          this is the comparator to rank pages by score
	 *
	 */
	private class SortByScore implements Comparator<Page> {
		@Override
		public int compare(Page p1, Page p2) {
			// TODO Auto-generated method stub
			if (p1.score < p2.score) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	/**
	 *
	 * 2016 Apr 2, 2016 PageRank.java
	 * 
	 * @author: frankgao
	 * 
	 *          this is comparator to rank the pages by out degree
	 *
	 */
	private class SortByOutNum implements Comparator<Page> {
		@Override
		public int compare(Page p1, Page p2) {
			// TODO Auto-generated method stub
			if (p1.outPage.size() < p2.outPage.size()) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	/**
	 *
	 * 2016 Apr 2, 2016 PageRank.java
	 * 
	 * @author: frankgao
	 * 
	 *          this is comparator to rank the pages by in degree
	 *
	 */
	private class SortByInNum implements Comparator<Page> {
		@Override
		public int compare(Page p1, Page p2) {
			// TODO Auto-generated method stub
			if (p1.incomePage.size() < p2.incomePage.size()) {
				return 1;
			} else {
				return -1;
			}
		}
	}
	/**
	 *
	 * 2016 Apr 2, 2016 PageRank.java
	 * 
	 * @author: frankgao
	 *
	 *          this is the class of Page, include the link, score, incomePage
	 *          and outPage
	 *
	 */
	private class Page {
		String link;
		double score;
		ArrayList<Page> incomePage;
		ArrayList<Page> outPage;

		Page(String link) {
			this.link = link;
			this.incomePage = new ArrayList<Page>();
			this.outPage = new ArrayList<Page>();
			this.score = 0;
		}
	}
}


