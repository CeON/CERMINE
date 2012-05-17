package pl.edu.icm.yadda.analysis.relations.manipulations.flow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import pl.edu.icm.yadda.analysis.relations.auxil.trash._1MassiveFileIteratorBuilder;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

public class _OccurenceCSVToTriCSV {
	public static void main(String[] args) throws Exception{
		_1MassiveFileIteratorBuilder o1 = new _1MassiveFileIteratorBuilder(); 
    	HashMap<String, String> hm = new HashMap<String, String>();
    	hm.put(o1.AUX_PARAM_SOURCE_DIR, "/home/pdendek/destiny2a/");
    	String[] ext = {"csv"}; 
    	o1.setExtensions(ext);
    	ISourceIterator<File> it = o1.build(hm);
    	
    	while(it.hasNext()){
    		File fin = it.next();
    		String o_path = fin.getAbsolutePath().substring(0, fin.getAbsolutePath().length());
    		o_path += ".boolean.csv";
    		
    		File fout = new File(o_path);
    		
    		fout.delete();
    		
    		BufferedReader br = new BufferedReader(new FileReader(fin));
    		BufferedWriter bw = new BufferedWriter(new FileWriter(fout));
    		
    		bw.write("id f\n");
    		br.readLine();
    		
    		int iii=0;
    		for(String line = br.readLine(); line!=null;line = br.readLine()){
    			iii++;
    			if(iii%10000==0){
    				bw.flush();
    				System.out.println(iii+" "+fout);
    			}
    			bw.write(line.split(" ")[0]+" ");
    			
    			
    			double val = Double.parseDouble(line.split(" ")[1]);
    			int i = (int) val;
    			if(i==-1) i=0;
    			else if(i==0) i=-1;
    			else i=1;
//    			---------XXXXX---------
//    			int i=-3;
//    			if(line.split(" ")[1].trim().equals("TRUE")) i=1;  
//    			else i=-1; 
//    			bw.write(i+"\n");
    		}
    		bw.flush();
    		bw.close();
    		br.close();
    	}
    	
	}
}
