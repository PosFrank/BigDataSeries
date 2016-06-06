package PA4;

import java.util.Comparator;
/**
 * 
 * this class is the comparator to rank the files by their cosine similarity only.
 * 
 * */
/**
 *
 * 2016
 * May 6, 2016
 * DocCompare_cosSim.java
 * @author: frankgao
 *
 */
class DocCompare_cosSim implements Comparator<DocScore>{

	@Override
	public int compare(DocScore o1, DocScore o2) {
		// TODO Auto-generated method stub
		if(o1.cosSim < o2.cosSim){
			return 1;
		}else if(o1.cosSim > o2.cosSim){
			return -1;
		}
		return 0;
	}
	
}