package PA4;

import java.util.Comparator;

/**
 *
 * 2016
 * May 6, 2016
 * DocCompare_Freq_cosSim.java
 * @author: frankgao
 *
 */
class DocCompare_Freq_cosSim implements Comparator<DocScore> {
	@Override
	public int compare(DocScore o1, DocScore o2) {
		// TODO Auto-generated method stub
		if(o1.freq < o2.freq){
			return 1;
		}else if(o1.freq > o2.freq){
			return -1;
		}else{
			if(o1.cosSim < o2.cosSim){
				return 1;
			} else if (o1.cosSim > o2.cosSim){
				return -1;
			}
		}
		return 0;
	}
}