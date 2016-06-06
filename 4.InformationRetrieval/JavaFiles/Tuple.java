package PA4;

/**
 *
 * 2016 May 6, 2016 Tuple.java
 * 
 * @author: frankgao
 *
 *          this class is the tuple used in WordIndex, it will be used for a
 *          specific term to check a list of documents. Each class represent the
 *          information of the term name is the document's name freq is the
 *          number of times that term appears in the document
 *
 */
public class Tuple {
	String name;
	int freq;

	Tuple(String name, int frequency) {
		this.name = name;
		this.freq = frequency;
	}
}
