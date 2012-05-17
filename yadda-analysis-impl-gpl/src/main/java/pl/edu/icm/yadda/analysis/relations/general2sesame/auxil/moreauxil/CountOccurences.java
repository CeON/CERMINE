package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil.moreauxil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CountOccurences {
	
	public static void main(String[] args) throws IOException{
		
		File f = new File("/home/pdendek/AND_FLOW_CIEMNIAK/PRZELICZENIE_WYSTAPIEN/rozmiary_liczbowo_co_linie.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		HashMap<Integer,Integer> hm = new HashMap<Integer,Integer>();
		
		String s = null;
		while((s=br.readLine())!=null){
			try{
				Integer size = Integer.parseInt(s);
				Integer occNumber = hm.get(size);
				if(occNumber==null){
					hm.put(size, 1);
				}else{
					occNumber++;
					hm.put(size, occNumber);
				}
			}catch(Exception e){
				break;
			}
		}
		
		br.close();
		br = null;
		f = null;
		
		System.out.println("Group size" + "\t\t" + "Occurence Number");
		for(Map.Entry<Integer, Integer> e : hm.entrySet())
			System.out.println(e.getKey() + "\t\t\t" + e.getValue());
		
		hm.clear();
		hm=null;
	}
}
