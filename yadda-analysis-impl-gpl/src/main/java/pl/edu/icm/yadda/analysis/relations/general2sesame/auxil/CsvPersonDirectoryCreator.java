package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;

public class CsvPersonDirectoryCreator extends PersonDirectoryCreator {
	
	@Override
	protected void persistResults(Map<Integer, List<String>> clusters,
			Object[] ctx) throws Exception {
		
		File parentDir = new File(ctx[0].toString());
        parentDir.mkdirs();
		File topFolder = parentDir;
        
		for(Entry<Integer,List<String>> e : clusters.entrySet()){
			String personId = generatePersonId(e.getValue());
			List<String> lines = writeAllMembers(personId, e.getValue());
			
			personId = personId.substring(personId.indexOf("#")+1); 
			String per = personId.replaceAll("-", "/");
			
			File child;  
			String abs = parentDir.getAbsolutePath();
			
			if(abs.charAt(abs.length()-1)+""=="/"){
				File fileParent = new File(topFolder,per);
				fileParent.mkdirs();
				child = new File(fileParent,"file.csv");
			}else{
				File fileParent = new File(abs+per);
				fileParent.mkdirs();
				child = new File(fileParent,"file.csv");
			}
				
            FileWriter fw = new FileWriter(child);
            
            for(String l : lines){
            	fw.write(l+"\n");
            }
            
            fw.flush();
            fw.close();
            fw = null;
            child = null;
		}
		
	}

	private static List<String> writeAllMembers(String personId, List<String> members) {
		/*
		 * The pattern of a contribution id:
		 * http://yadda.icm.edu.pl/contributor#bwmeta1.element.92e37cf8-fd20-321b-82d2-8d694444377f#c0
		 * Contrib Prefix:
		 * http://yadda.icm.edu.pl/contributor#
		 * Element id: 
		 * bwmeta1.element.92e37cf8-fd20-321b-82d2-8d694444377f
		 * Contribution position:
		 * 0   
		 * 
		 * The pattern of a reference contribution:
		 * http://yadda.icm.edu.pl/contributor#bwmeta1.element.92e37cf8-fd20-321b-82d2-8d694444377f#r1#c2
		 * (...)
		 * Reference position:
		 * 1   
		 * Contribution position:
		 * 0   
		 */
		
		ArrayList<String> ret = new ArrayList<String>();
		
		for(String all : members){
			int hashSignNumber = all.length() - all.replaceAll("#", "").length();
			
			String docId="";
			String refId="";
			String conId="";
			if(hashSignNumber==2){//it is contribution from main doc
				docId = all.substring(all.indexOf("#")+1,all.lastIndexOf("#"));
				refId = "-1";
				conId = all.substring(all.lastIndexOf("#c")+2);
				
			}else if(hashSignNumber==3){//it is contribution from main doc
				
				String fromDocId = all.substring(all.indexOf("#")+1); // bwmeta1.element.92e37cf8-fd20-321b-82d2-8d694444377f#r1#c2>
				String fromRefId = fromDocId.substring(fromDocId.indexOf("#")+1); // r1#c2>
				String fromConId = fromRefId.substring(fromRefId.indexOf("#")+1); // c2>
				
				docId = fromDocId.substring(0,fromDocId.indexOf("#")); // bwmeta1.element.92e37cf8-fd20-321b-82d2-8d694444377f
				refId = fromRefId.substring(1,fromRefId.indexOf("#")); // 1
				conId = fromConId.substring(1); // 2
			}
			ret.add(docId+"\t"+refId+"\t"+conId+"\t"+personId);
		}
		return ret;
	}
	
	public static void main(String args[]){
		for(String s: writeAllMembers(
							"perId",
							Arrays.asList(
									new String[]{
											"http://yadda.icm.edu.pl/contributor#bwmeta1.element.92e37cf8-fd20-321b-82d2-8d694444377f#r1#c2",
											"http://yadda.icm.edu.pl/contributor#bwmeta1.element.92e37cf8-fd20-321b-82d2-8d694444377f#c0"
									}
							)
						)){
			System.out.println(s);
		}
	}
	
}
