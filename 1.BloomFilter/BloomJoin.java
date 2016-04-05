package PA1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Tianxiang Gao
 *
 */
public class BloomJoin {
	private static BufferedWriter bufferedWriter;
	private static HashMap<String, String> map;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		int r1Size = 2000000;
		int r1Bits = 15;
		BloomFilterRan r1 = new BloomFilterRan(r1Size, r1Bits);
		map = new HashMap<String, String>();
		System.out.println("Start to read r1 into Bloom Filter");
		String relation1path = "/Users/frankgao/Documents/COMS535/Relation1.txt";
		String relation2path = "/Users/frankgao/Documents/COMS535/Relation2.txt";
		String relation3path = "/Users/frankgao/Documents/COMS535/Relation3.txt";
		String finalJoinPath = "/Users/frankgao/Documents/COMS535/FinalJoin.txt";
		addR1IntoBloomFilter(r1, relation1path);
		checkR2AndCreateR3(r1, relation2path, relation3path);
		addR3IntoHashMap(map, relation3path);
		joingR1AndR3(map, relation1path, finalJoinPath);
	}
	/**
	 * 
	 * @param r1
	 * @param relation1
	 * r1 is the bloom filter for relation 1. We will add the data into r1 filter in this function.
	 * 
	 * */
	private static void addR1IntoBloomFilter(BloomFilterRan r1, String relation1path){
		int prev = 0;
		int hasRead = 0;
        // Open the first relation file
        String line = null;
        try {
            FileReader relation1Reader = new FileReader(relation1path);
            BufferedReader bufferedReader1 = new BufferedReader(relation1Reader);
            line = bufferedReader1.readLine();
            while(line != null) {
            	//here we extract only the first column's attribute of this line.
            	line = line.substring(0, 15);
            	//then add it into filter
            	r1.add(line);
            	hasRead++;
            	line = bufferedReader1.readLine();
            	//print some signal text to see it's progress
            	if(hasRead - prev > 10000){
            		System.out.println("has read " + hasRead + " lines of relation 1");
            		prev = hasRead;
            	}
            	
            }
            bufferedReader1.close();         
        } catch (FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + relation1path + "'");                
        } catch(IOException ex) {
            System.out.println(
                "Error reading file '" + relation1path + "'");
        }
	}
	/**
	 * 
	 * @param r1 
	 * @param relation2path
	 * @param relation3path
	 * 
	 * this r1 will be the same r1 we used in the function "addR1IntoBloomFilter".
	 *  
	 * 1. After added all relation 1's attributes into r1, 
	 * 	  we pass this filter to this function
	 * 
	 * 2. we check the attribute intersection of relation 1 and relation 2.
	 * 
	 * 3. we write the lines in relation 2 which have attribute intersection into relation 3.
	 * 
	 * */
	private static void checkR2AndCreateR3(BloomFilterRan r1, String relation2path, String relation3path){
        try {
            FileReader relation2Reader = new FileReader(relation2path);
            BufferedReader bufferedReader2 = new BufferedReader(relation2Reader);
            
            FileWriter writer = new FileWriter(relation3path);
            bufferedWriter = new BufferedWriter(writer);

            int prev = 0;
    		int hasRead = 0;
    		int prevWrite = 0;
    		int hasWrite = 0;
            String line = bufferedReader2.readLine();
            while(line != null) {
            	//we extract the first column's string to test if it is in the filter of relation 1.
            	String a1 = line.substring(0, 15);
            	boolean isInR1 = r1.appears(a1);
            	//if it is in the filter of relation 1, we need to write the WHOLE line into relation 3.
            	if(isInR1){
            		hasWrite++;
            		bufferedWriter.write(line);
            		bufferedWriter.newLine();
            		//print signal text to see it's progress
            			if(hasWrite - prevWrite > 10000){
            				System.out.println("HAS WRITE " + hasWrite + " lines of relation 3");
                    		prevWrite = hasWrite;
                		}
            		}
            	line = bufferedReader2.readLine();
            	hasRead++;
            	//print signal text to see it's progress
            	if(hasRead - prev > 10000){
            		System.out.println("has read " + hasRead + " lines of relation 2");
            		prev = hasRead;
            	}
            }
            bufferedWriter.close();
            bufferedReader2.close();         
        } catch (FileNotFoundException ex) {
        	ex.printStackTrace();              
        } catch(IOException ex) {
        	ex.printStackTrace();
        }
	}
	/**
	 * 
	 * after create relation 3's file. We need to pass this file to relation 1 to do the join.
	 * 
	 * But directly do the join by brute force will cost O(m * n)
	 * 
	 * so I add the attributes of relation 3 as key, the second column as value into a HashMap
	 * 
	 * Then we do the join in the next function "joingR1AndR3"
	 * 
	 * @param map
	 * @param relation3path
	 * */
	private static void addR3IntoHashMap(HashMap<String, String> map, String relation3path){
		try {
			int prev = 0;
			int hasPut = 0;
			String r3Path = relation3path;
			FileReader r3Reader = new FileReader(r3Path);
			BufferedReader bufferedReader3 = new BufferedReader(r3Reader);
			String line = bufferedReader3.readLine();
			//after reading a new line. we extract the attribute and value out.
			String attribute = line.substring(0, 15);
			String value = line.substring(18);
			//now we iteratively add r3's attribute and value into map
			while(line != null){
				attribute = line.substring(0, 15);
				value = line.substring(18);
				map.put(attribute, value);
				hasPut++;
				if(hasPut - prev > 5000){
					System.out.println("has put r3 into HashMap " + hasPut + " lines");
					prev = hasPut;
				}
				line = bufferedReader3.readLine();
			}
			//print the finish signal
			System.out.println("FINISHED R3 HASHMAP STORE");
			bufferedReader3.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		
	}
	/**
	 * 
	 * @param map
	 * @param relation1path
	 * @param finalJoinFilePath
	 * 
	 * in this function, we performed the join of relation 1 and relation 3.
	 * 
	 * relation 3 is much smaller than relation 2 and relation 1
	 * 
	 * so we store the key-value relation in the previous function
	 * 
	 * Then now in this function, we check each attribute in relation1 to see if it is in relation 3
	 * 
	 * if it is in, we need to add both relation 1 and relation 3' value and attrbute into final join file
	 * 
	 * 
	 * */
	private static void joingR1AndR3(HashMap<String, String> map, String relation1path,  String finalJoinFilePath){
		try {
			int prev = 0;
			int hasJoin = 0;
			//initilize file reader and writer
			String r1Path = relation1path;
			FileReader r1Reader = new FileReader(r1Path);
			BufferedReader bufferedReader1 = new BufferedReader(r1Reader);
			
			FileWriter writerFinal = new FileWriter(finalJoinFilePath);
            bufferedWriter = new BufferedWriter(writerFinal);
			
            //extract out attribute and value from relation 1
			String line = bufferedReader1.readLine();
			String attribute = line.substring(0, 15);
			String value = line.substring(18);
			//iteratively check each relation 1's attribute's existence in relation 3
			while(line != null){
				attribute = line.substring(0, 15);
				value = line.substring(18);
				//if relation 1's attribute shows up in relation 3, 
				//we need to join them by writing them into a new file
        		if(map.containsKey(attribute)){
        			bufferedWriter.write(value);
            		bufferedWriter.write("   ");
            		bufferedWriter.write(attribute);
            		bufferedWriter.write("   ");
            		bufferedWriter.write(map.get(attribute));
            		bufferedWriter.newLine();
    				hasJoin++;
    				if(hasJoin - prev > 3000){
    					System.out.println("has joined " + hasJoin + " lines");
    					prev = hasJoin;
    				}
        		}
				line = bufferedReader1.readLine();
			}
			System.out.println("FINISHED Final Join");
			bufferedReader1.close();
			bufferedWriter.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
}
