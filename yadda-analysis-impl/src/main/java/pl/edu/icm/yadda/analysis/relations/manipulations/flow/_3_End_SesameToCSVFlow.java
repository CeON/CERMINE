//package pl.edu.icm.yadda.analysis.relations.manipulations.flow;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Date;
//import java.util.HashMap;
//
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._4Observations2CSV;
//
//public class _3_End_SesameToCSVFlow {
//
//	static SesameManipulator 
////	m0a, m1a, m1b
////	, 
//	m2, m3
//	;
//	static String filePath2 = "/tmp/repo_v5_480632486069";
//	static Repository repository; 
//	
//	public static void setUp() throws Exception {		
//		File dstFolder = new File("/tmp/repo_v5_480632486069_"+System.nanoTime());
//		dstFolder.mkdirs();
//		copyFolder(new File(filePath2),dstFolder);
//	
//		NativeStore store=new NativeStore(dstFolder);		
//		SailRepository rep=new SailRepository(store);
//		rep.initialize();
//		repository=rep;
////		m0a = new SesameManipulator(repository, new MultipleEmailRemover());
////		m1a = new SesameManipulator(repository, new _1ShardContribution2Observations());
////		m1b = new SesameManipulator(repository, new _2AreTheSameBooleanInitializer());
////		m2 = new SesameManipulator(repository, new _3ObservationsFeatureChecker());
//		m3 = new SesameManipulator(repository, new _4Observations2CSV());
//	}
//
//	public static void main(String[] args) throws Exception {
//		
//		System.out.println("start: "+new Date());
//		
//		setUp();
//		
//		try {
//			Exception e;
////			System.out.println("1: "+new Date());
////			e = (Exception) m0a.execute(null);
////			if(e!=null) throw e;
////			System.out.println("2: "+new Date());
////			e = (Exception) m1a.execute(null);
////			if(e!=null) throw e;
////			System.out.println("3: "+new Date());
////			e = (Exception) m1b.execute(null);
////			if(e!=null) throw e;
////			
////			System.out.println("4: "+new Date());
////			
//			HashMap<String,Object> hm = new HashMap<String,Object>();
////			List<PJDisambiguator> pjdislist = new ArrayList<PJDisambiguator>();
////			hm.put("disambiguatorList", pjdislist);
////			
////			PJDisambiguator f0 = new Feature1Email();
////			f0.setRepository(repository);
////			pjdislist.add(f0);
////			
////			PJDisambiguator f1 = new Feature2EmailPrefix();
////			f1.setRepository(repository);
////			pjdislist.add(f1);
////			
////			PJDisambiguator f2 = new Feature3CoContribution();
////			f2.setRepository(repository);
////			pjdislist.add(f2);
////			
////			PJDisambiguator f3 = new Feature4CoClassif();
////			f3.setRepository(repository);
////			pjdislist.add(f3);
////			
////			PJDisambiguator f4 = new Feature5CoKeywordPhrase();
////			f4.setRepository(repository);
////			pjdislist.add(f4);
////			
////			PJDisambiguator f5 = new Feature5CoKeywordWords();
////			f5.setRepository(repository);
////			pjdislist.add(f5);
////			
////			PJDisambiguator f6 = new Feature6CoReference();
////			f6.setRepository(repository);
////			pjdislist.add(f6);
////			
////			e = (Exception) m2.execute(hm);
////			if(e!=null) throw e;
////			
//			System.out.println("5 start: "+new Date());
//			
//			hm.clear();
//			hm.put("featureNum", 2);
//			hm.put("csvFilePath", "/tmp/repo_v4.csv");
//			e = (Exception) m3.execute(hm);
//			if(e!=null) throw e;
//			
//			System.out.println("stop: "+new Date());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	public static void copyFolder(File src, File dest) throws IOException{
//
//		if(src.isDirectory()){
//	
//			//if directory not exists, create it
//			if(!dest.exists()){
//			   dest.mkdir();
//			   System.out.println("Directory copied from " 
//	                          + src + "  to " + dest);
//			}
//	
//			//list all the directory contents
//			String files[] = src.list();
//	
//			for (String file : files) {
//			   //construct the src and dest file structure
//			   File srcFile = new File(src, file);
//			   File destFile = new File(dest, file);
//			   //recursive copy
//			   copyFolder(srcFile,destFile);
//			}
//	
//		}else{
//			//if file, then copy it
//			//Use bytes stream to support all file types
//			InputStream in = new FileInputStream(src);
//		        OutputStream out = new FileOutputStream(dest); 
//	
//		        byte[] buffer = new byte[1024];
//	
//		        int length;
//		        //copy the file content in bytes 
//		        while ((length = in.read(buffer)) > 0){
//		    	   out.write(buffer, 0, length);
//		        }
//	
//		        in.close();
//		        out.close();
//		        System.out.println("File copied from " + src + " to " + dest);
//		}
//	}
//}
