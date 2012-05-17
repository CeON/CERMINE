//package pl.edu.icm.yadda.analysis.relations.manipulations.flow;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.rio.RDFFormat;
//import org.openrdf.rio.RDFHandlerException;
//import org.openrdf.rio.RDFWriter;
//import org.openrdf.rio.Rio;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._1ShardContribution2Observations;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._2AreTheSameBooleanInitializer;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._3ObservationsFeatureChecker;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._4Observations2CSV;
//import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature1Email;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature2EmailPrefix;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature3CoContribution;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature4CoClassif;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature5CoKeywordPhrase;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature6CoReference;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature8Year;
//
//public class _1_Begining_SesameToCSVFlow {
//
//	static int which = 0;
//	static SesameManipulator m0a, m1a, m1b
//	, m2, m3
//	;
//	
////	static String filePath2 = "/tmp/repo_v5+53375084356988_61246669178614";
//	
//	static String filePath2 = "/tmp/repo_f166938580087824/";
////	static String filePath2 = "/tmp/repo_v5_53375084356988";
//	
////	static String filePath2 = "/home/pdendek/sample/repo_v4";
//	
//	
//	static Repository repository; 
//	
//	public static void re_initRepository() throws Exception {		
//		File dstFolder = new File("/tmp/repo_destiny_part_"+which+"_"+System.nanoTime());
//		which++;
//		dstFolder.mkdirs();
//		copyFolder(new File(filePath2),dstFolder);
//	
//		NativeStore store=new NativeStore(dstFolder);	
////		store.setForceSync(true);
//		SailRepository rep=new SailRepository(store);
//		rep.initialize();
//		repository=rep;
//		rep.getConnection().setAutoCommit(false);
////		m0a = new SesameManipulator(repository, new MultipleEmailRemover());
//		m1a = new SesameManipulator(repository, new _1ShardContribution2Observations());
//		m1b = new SesameManipulator(repository, new _2AreTheSameBooleanInitializer());
//		m2 = new SesameManipulator(repository, new _3ObservationsFeatureChecker());
//		m3 = new SesameManipulator(repository, new _4Observations2CSV());
//	}
//
//	public static void main(String[] args) throws Exception {
//		try {
//			System.out.println("start: "+new Date());
//		
//			re_initRepository();
//		
//		
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
////			
////			
////			exportRepoToN3(repository,"/tmp/repo_export.N3");
////			
////			
////			System.out.println("4: "+new Date());
////			
//			HashMap<String,Object> hm = new HashMap<String,Object>();
//			List<PJDisambiguator> pjdislist = new ArrayList<PJDisambiguator>();
//			hm.put("disambiguatorList", pjdislist);
////			
//			
//			System.out.println("f0: "+new Date());
//			PJDisambiguator f = new Feature1Email();
//			f.setRepository(repository);
//			pjdislist.add(f);
////			
//			System.out.println("f1: "+new Date());
//			f = new Feature2EmailPrefix();
//			f.setRepository(repository);
//			pjdislist.add(f);
//			
//			f = new Feature3CoContribution();
//			f.setRepository(repository);
//			pjdislist.add(f);;
//			
//			f = new Feature4CoClassif();
//			f.setRepository(repository);
//			pjdislist.add(f);
//			
//			f = new Feature5CoKeywordPhrase();
//			f.setRepository(repository);
//			pjdislist.add(f);
//			
//			f = new Feature6CoReference();
//			f.setRepository(repository);
//			pjdislist.add(f);
//			
//			f = new Feature8Year();
//			f.setRepository(repository);
//			pjdislist.add(f);
//			
//			e = (Exception) m2.execute(hm);
//			if(e!=null) throw e;
//			
//			exportRepoToN3(repository,"/tmp/repo_export"+System.nanoTime()+".N3");
//			
//			System.out.println("5 start: "+new Date());
//			
//			hm.clear();
//			hm.put("featureNum", 1);
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
//	
//	public static void exportRepoToN3(Repository rep, String filePath) throws RepositoryException, FileNotFoundException, RDFHandlerException{
//		File repoFile = new File(filePath);
//		FileOutputStream fosRepo = new FileOutputStream(repoFile);
//		RDFWriter repoWriter = Rio.createWriter(RDFFormat.N3, fosRepo);
//		rep.getConnection().export(repoWriter);
//	}
//}
