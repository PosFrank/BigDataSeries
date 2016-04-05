package PA2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TestGetName {
	public static void main(String args[]) {
		String folderPath = "/Users/frankgao/Documents/COMS535/2/space/";
		File dir = new File(folderPath);
		File[] documents = dir.listFiles();
		System.out.println(documents.length - 1);
		HashMap<String, Integer> terms = new HashMap<String, Integer>();
		int c = 0;
		for (int i = 1; i < documents.length; i++) {
			System.out.println(documents[i].getName());
			List<Integer> listOfTermIndex = new ArrayList<Integer>();
			Scanner reader;
			try {
				reader = new Scanner(documents[i]);
				reader.useDelimiter(",|\\.|:|;|'|\\n|\\s+");
				int termNum = 0;
				while (reader.hasNext()) {
					String word = reader.next();
					word = word.toLowerCase();
					if (word.length() < 3 || word.equals("the")) {
						continue;
					}
					System.out.println(word);
					if (terms.containsKey(word)) {
						if(!listOfTermIndex.contains(terms.get(word))){
							listOfTermIndex.add(terms.get(word));
						}
					} else {
						listOfTermIndex.add(termNum);
						terms.put(word, termNum);
						termNum++;
					}
				}
				Collections.sort(listOfTermIndex);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
