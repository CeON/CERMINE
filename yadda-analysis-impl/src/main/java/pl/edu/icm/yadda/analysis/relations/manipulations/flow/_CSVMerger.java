package pl.edu.icm.yadda.analysis.relations.manipulations.flow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import pl.edu.icm.yadda.analysis.relations.auxil.trash._1MassiveFileIteratorBuilder;

public class _CSVMerger {
	public static void main(String[] args) throws Exception{
		_1MassiveFileIteratorBuilder o1 = new _1MassiveFileIteratorBuilder(); 
    	HashMap<String, String> hm = new HashMap<String, String>();
    	String prefix = "/home/pdendek/destiny_boolean_easy2/";
    	BufferedReader[] brs = new BufferedReader[8];
    	BufferedWriter bw = new BufferedWriter(new FileWriter(prefix+"out.csv")); 
    	String[] strs = new String[8];
    	
    	for(int i=1;i<8;i++){
    		brs[i] = new BufferedReader(new FileReader(new File(prefix+i+".csv")));
    	}
    	brs[0] = new BufferedReader(new FileReader(new File(prefix+0+".same")));
    	
    	int i=0;
    	
    	readLine(brs, strs);
    	
    	while(readLine(brs, strs)){
    		i++;
    		if(i%10000==0){
				bw.flush();
				System.out.println(i+" "+bw);
			}
    		bw.write(i+" ");
    		for(int iii=1; iii<brs.length;iii++){
    			bw.write(strs[iii]+" ");
    		}
    		bw.write(strs[0]+"\n");
    	}
    	bw.flush();
    	bw.close();
    	
    	for(int qqq=0;qqq<8;qqq++){
    		brs[qqq].close();
    	}
    }
	
	public static boolean readLine(BufferedReader[] brs, String[] strs) throws IOException{
		try{
			for(int i = 0; i<brs.length;i++){
				strs[i] = brs[i].readLine().split(" ")[1];
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}
