package PA3;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

/**
 *
 * 2016
 * Apr 2, 2016
 * WikiCrawler.java
 * @author: TianxiangGao
 *
 */
public class WikiCrawler {

	private static final String BASE_URL = "https://en.wikipedia.org";
	private String seedUrl;
	private String[] topics;
	private int max;
	private String fileName;
	private BufferedWriter writer;
	private HashSet<String> disAllow;
	private Scanner scan;
	private HashSet<String> edges;
	private HashSet<String> isValidSites;
	private ArrayList<String> sites;
	private HashSet<String> isNotValid;

	/**
	 * @param seedUrl
	 * @param topics
	 * @param max
	 * @param fileName
	 * @description: construct the crawler
	 *
	 */
	public WikiCrawler(String seedUrl, String[] topics, int max, String fileName) {
		this.seedUrl = seedUrl;
		this.topics = topics;
		for (int i = 0; i < this.topics.length; i++) {
			topics[i] = topics[i].toLowerCase();
			// System.out.println(topics[i]);
		}
		this.max = max;
		this.fileName = fileName;
		this.edges = new HashSet<String>();
		this.isValidSites = new HashSet<String>();
		this.isNotValid = new HashSet<String>();
		this.sites = new ArrayList<String>(this.max);
	}

	/**
	 * 
	 * @description: first get all disallowed sites, then find all the valid
	 *               sites, finally construct the graph
	 *
	 */
	public void craw() {
		System.out.println("First get disallowed sites list.");
		getDisAllow();
		System.out.println("Find all " + this.max + " sites");
		findAllSites();
		System.out.println("Draw the graph");
		writeGraph("");
	}

	/**
	 * @param outputPath
	 * @description: check every links in every page and output the edge if the
	 *               links in that page are also in the list of valid pages
	 *
	 */
	private void writeGraph(String outputPath) {
		String filePath = outputPath + this.fileName;
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(filePath);
			writer = new BufferedWriter(fileWriter);
			writer.flush();
			// first write one line of page number
			writer.write(Integer.toString(this.max));
			writer.newLine();
			// traverse all the valid pages
			for (int i = 0; i < this.max; i++) {
				// get all the links in page i
				List<String> connectWith = getLinksFromURL(sites.get(i));
				// traverse all the links
				for (String nextPage : connectWith) {
					// build the edge string
					String edge = sites.get(i) + " " + nextPage;
					// if this link nextPage is in isValidSites and this edge
					// has not been added, add this edge into edge list
					if (this.isValidSites.contains(nextPage) && !edges.contains(edge)
							&& !sites.get(i).equals(nextPage)) {
						System.out.println("output edge: " + edge);
						writer.write(edge);
						writer.newLine();
						edges.add(edge);
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @description: the method will find all the files
	 *
	 */
	private void findAllSites() {
		int requestTimes = 0;
		Queue<String> quque = new LinkedList<String>();
		// initialize a queue of links
		quque.add(this.seedUrl);
		// add the seedURL into the queue
		while (!quque.isEmpty()) {
			// get the fist link
			String url = quque.poll();
			// if this page is visited continue;
			if (this.isNotValid.contains(url) || this.isValidSites.contains(url)) {
				continue;
			}
			// if it is not visited, check if it contains all the keywords
			if (isContainAllKeywords(url)) {
				// if contains all the keywords, we need to get all the links in
				// this page and add them into queue
				List<String> links = getLinksFromURL(url);
				for (String link : links) {
					quque.add(link);
				}
				// then mark this page as visited by add it into isValidSites
				this.isValidSites.add(url);
				// add this page into list of pages
				this.sites.add(url);
				System.out.println(this.isValidSites.size() + ": " + url);
				if (this.isValidSites.size() > 0 && this.isValidSites.size() % 25 == 0) {
					System.out.println("-------------------\n has find " + this.isValidSites.size()
							+ " number of vertices\n-------------------");
				}
				// if the pages number has reached the number we need, stop the
				// crawling
				if (this.isValidSites.size() == this.max) {
					break;
				}
			} else {
				// mark this page as visited by add this page into isNotValid
				this.isNotValid.add(url);
			}
			requestTimes++;
			if (requestTimes >= 50) {
				sleep(2500);
				requestTimes = 0;
			}
		}
	}

	/**
	 * @param ms
	 * @description: wait ms milliseconds to make sure not overload the websites
	 *
	 */
	private void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.out.println("error when try to sleep");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * @param link
	 * @return true if this page contains all the links otherwise return false
	 * @description: check if the page of this link has all the keywaords
	 *
	 */
	private boolean isContainAllKeywords(String link) {
		HashSet<String> keywordsInThisPage = new HashSet<String>();
		try {
			// https://en.wikipedia.org/w/index.php?title=xxxxxx&action=raw
			// use the text only website to check if this page contains all the
			// keywords
			String category = link.substring("/Wiki/".length());
			URL url = new URL("https://en.wikipedia.org/w/index.php?title=" + category + "&action=raw");
			InputStream is = url.openStream();
			scan = new Scanner(is);
			while (scan.hasNext()) {
				String line = scan.nextLine();
				line = line.toLowerCase();
				for (String keyword : this.topics) {
					if (keywordsInThisPage.contains(keyword)) {
						continue;
					}
					if (line.contains(keyword)) {
						// System.out.println("in " + category + "find one
						// contains " + keyword);
						keywordsInThisPage.add(keyword);
					}
				}
				if (keywordsInThisPage.size() == this.topics.length) {
					break;
				}
			}
			scan.close();
		} catch (IOException e) {
			System.out.println("Error occured during crawlering");
			e.printStackTrace();
		}
		return keywordsInThisPage.size() == this.topics.length;
	}

	/**
	 * @param pageURL
	 * @return the list of links from this URL
	 * @description: get the links after "<p>" and check them then return them
	 *
	 */
	private List<String> getLinksFromURL(String pageURL) {
		List<String> urls = new LinkedList<String>();
		try {
			URL url = new URL(BASE_URL + pageURL);
			InputStream is = url.openStream();
			Scanner scan = new Scanner(is);
			String line = "";
			/*
			 * first find the "<p>" then start to find the links
			 */
			while (scan.hasNext()) {
				line = scan.nextLine();
				if (line.startsWith("<p>")) {
					break;
				}
			}
			// find all the links and check them
			while (true) {
				List<String> linksInTheString = extractLinkFromString(line);
				if (linksInTheString.size() != 0) {
					for (String link : linksInTheString) {
						// it should not in the disallow list and start with
						// /wiki/ and should not contain "#" and ":"
						if (this.disAllow.contains(link) || !link.startsWith("/wiki/") || link.contains("#")
								|| link.contains(":")) {
							continue;
						}
						// System.out.println(link);
						urls.add(link);
					}
				}
				if (!scan.hasNext()) {
					break;
				}
				line = scan.nextLine();
			}
			scan.close();
		} catch (IOException e) {
			System.out.println("Error occured during getting pages");
			e.printStackTrace();
		}
		return urls;
	}

	/**
	 * @param line
	 * @return the list of all link String in this line String
	 * @description: this method will get all the links of this line and return
	 *               a list
	 *
	 */
	private List<String> extractLinkFromString(String line) {
		List<String> linksInThisLine = new LinkedList<String>();
		int startIndex = stringSearchKMP(line, "href=\"", 0);
		while (startIndex != -1) {
			int endIndex = stringSearchKMP(line, "\"", startIndex + 6);
			// System.out.println(startIndex+ " " + endIndex);
			String substring = line.substring(startIndex + 6, endIndex);
			linksInThisLine.add(substring);
			startIndex = stringSearchKMP(line, "href=\"", endIndex);
		}
		return linksInThisLine;
	}

	/**
	 * @param source
	 * @param target
	 * @param startIndex
	 * @return the fist index of the target String in the source String after
	 *         the startIndex
	 * @description: this method will use KMP algorithm to find the next
	 *               position of target String in source String after the
	 *               startIndex
	 *
	 */
	public int stringSearchKMP(String source, String target, int startIndex) {
		if (source == null || target == null || source.length() < target.length() || target.length() == 0) {
			return -1;
		}
		int[] table = preprocessKMP(target);
		int rst = -1;
		int move = 0;
		for (int i = startIndex; i <= source.length() - target.length();) {
			for (int j = 0; j < target.length(); j++) {
				if (source.charAt(i + j) != target.charAt(j)) {
					if (j == 0) {
						move = 1;
						break;
					} else if (j > 0) {
						move = j - table[j - 1];
						break;
					}
				} else if (source.charAt(i + j) == target.charAt(j) && j == target.length() - 1) {
					rst = i;
					return rst;
				}
			}
			i = i + move;
		}
		return rst;
	}

	/**
	 * @param target
	 * @return the preprocessed target int[] array
	 * @description: this is the preprocess step of KMP algotihm
	 *
	 */
	private int[] preprocessKMP(String target) {
		int[] partialMatch = new int[target.length()];
		partialMatch[0] = 0;
		for (int i = 1; i < target.length(); i++) {
			int pre = partialMatch[i - 1];
			if (target.charAt(pre) == target.charAt(i)) {
				partialMatch[i] = pre + 1;
			}
		}
		return partialMatch;
	}

	// By using Regular Expression:
	// private List<String> getLinksFromURL(String pageURL) {
	// List<String> urls = new LinkedList<String>();
	// String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
	// String HTML_A_HREF_TAG_PATTERN =
	// "\\s*(?i)href\\s*=\\s*\"(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))\"";
	// Pattern patternTag, patternLink;
	// Matcher matcherTag, matcherLink;
	// patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
	// patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
	// try {
	// URL url = new URL(BASE_URL + pageURL);
	// InputStream is = url.openStream();
	// Scanner scan = new Scanner(is);
	// String line = "";
	// while (scan.hasNext()) {
	// line = scan.nextLine();
	// if (line.startsWith("<p>")) {
	// break;
	// }
	// }
	// while (true) {
	// matcherTag = patternTag.matcher(line);
	// while (matcherTag.find()) {
	// String href = matcherTag.group(1); // href
	// matcherLink = patternLink.matcher(href);
	// while (matcherLink.find()) {
	// String link = matcherLink.group(1); // link
	// if (this.disAllow.contains(link) || !link.startsWith("/wiki/") ||
	// link.contains("#") || link.contains(":")) {
	// continue;
	// }
	// System.out.println(link);
	// urls.add(link);
	// }
	// }
	// if (!scan.hasNext()) {
	// break;
	// }
	// line = scan.nextLine();
	// }
	// scan.close();
	// } catch (IOException e) {
	// System.out.println("Error occured during getting pages");
	// e.printStackTrace();
	// }
	// return urls;
	// }

	/**
	 * 
	 * @description: get all the disallowed links
	 *
	 */
	private void getDisAllow() {
		this.disAllow = new HashSet<String>();
		int disallowStringLength = "Disallow: ".length();
		try {
			URL url = new URL(BASE_URL + "/robots.txt");
			InputStream is = url.openStream();
			Scanner scan = new Scanner(is);
			while (scan.hasNext()) {
				String line = scan.nextLine();
				if (line.startsWith("Disallow: ")) {
					String link = line.substring(disallowStringLength);
					link.trim();
					this.disAllow.add(link);
				}
			}
			scan.close();
		} catch (IOException e) {
			System.out.println("Failed to get disallowed pages link");
			e.printStackTrace();
		}
	}
}
