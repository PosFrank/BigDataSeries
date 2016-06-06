package PA4;

/**
 *
 * 2016
 * May 6, 2016
 * DocScore.java
 * @author: frankgao
 * 
 * this class will store the ranked documents information
 *
 */
public class DocScore {
	String docName;
	double cosSim;
	int freq;
	DocScore(String doc, double score){
		this.docName = doc;
		this.cosSim = score;
	}
	DocScore(String doc, double score, int freq){
		this.docName = doc;
		this.cosSim = score;
		this.freq = freq;
	}
}
