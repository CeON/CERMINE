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
//import java.util.List;
//
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//import org.openrdf.repository.RepositoryException;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.rio.RDFFormat;
//import org.openrdf.rio.RDFHandlerException;
//import org.openrdf.rio.RDFWriter;
//import org.openrdf.rio.Rio;
//import org.openrdf.sail.nativerdf.NativeStore;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._3ObservationsFeatureChecker;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._4aPArtOfObservations2CSV;
//import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature1Email;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature2EmailPrefix;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature3CoContribution;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature4CoClassif;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature5CoKeywordWords;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature6CoReference;
//import pl.edu.icm.yadda.analysis.relations.pj.clues_occurence.Feature8Year;
//
//public class _InitilizedSesameTalkToHand {
//
//	static int which = 0;
//	static SesameManipulator m2, m3;
//	static String filePath2 = "/home/pdendek/repo_with_initialized_observations/";
//	static Repository repository; 
//	
//	public static void main(String[] args) throws Exception {
//		
//		System.out.println("start: "+new Date());
//		
//		try {
//			re_initRepository();
//			
//			TupleQuery query = null;
//			TupleQueryResult res = null;
//			
//			RepositoryConnection conn = repository.getConnection();  
//			
//			String queryString =
//				" select distinct email, c1,c2 " +
//				" from " +
//				" {o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c1}, "+
//				" {o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c2}, "+
//				" {c1} yad:has-contact-email {email}, " +
//				" {c1} yad:has-surname {surname}, " +
//				" {c2} yad:has-contact-email {email}, " +
//				" {c2} yad:has-surname {surname} " +
//				" where " +
//				" c1 != c2 " +
//				" and email != \"\" " +
//				" USING NAMESPACE " +
//				" yad=<http://yadda.icm.edu.pl/yadda#> " +
//				"";
//			
//			query = conn.prepareTupleQuery(QueryLanguage.SERQL, queryString);
//		    res = query.evaluate();
//			
//		    int iii=0;
//		    
//			while(res.hasNext()){
//				res.next();
//				iii++;
//			}	
//			System.out.println("stop: "+new Date());			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	private static List<PJDisambiguator> initializeList() {
//		List<PJDisambiguator> pjdislist = new ArrayList<PJDisambiguator>();
//
//		PJDisambiguator f = new Feature1Email();
//		f.setRepository(repository);
//		pjdislist.add(f);
//
//		f = new Feature2EmailPrefix();
//		f.setRepository(repository);
//		pjdislist.add(f);
//		
//		f = new Feature3CoContribution();
//		f.setRepository(repository);
//		pjdislist.add(f);;
//		
//		f = new Feature4CoClassif();
//		f.setRepository(repository);
//		pjdislist.add(f);
//		
////		f = new Feature5CoKeywordPhrase();
////		f.setRepository(repository);
////		pjdislist.add(f);
//		
//		f = new Feature5CoKeywordWords();
//		f.setRepository(repository);
//		pjdislist.add(f);
//		
//		f = new Feature6CoReference();
//		f.setRepository(repository);
//		pjdislist.add(f);
//		
//		f = new Feature8Year();
//		f.setRepository(repository);
//		pjdislist.add(f);
//		
//		return pjdislist;
//	}
//
//
//	public static void copyFolder(File src, File dest) throws IOException{
//		if(src.isDirectory()){
//			if(!dest.exists()){
//			   dest.mkdir();
//			   System.out.println("Directory copied from " 
//	                          + src + "  to " + dest);
//			}
//			String files[] = src.list();
//			for (String file : files) {
//			   File srcFile = new File(src, file);
//			   File destFile = new File(dest, file);
//			   copyFolder(srcFile,destFile);
//			}
//		}else{
//			InputStream in = new FileInputStream(src);
//		        OutputStream out = new FileOutputStream(dest); 
//		        byte[] buffer = new byte[1024];
//		        int length; 
//		        while ((length = in.read(buffer)) > 0){
//		    	   out.write(buffer, 0, length);
//		        }
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
//	
//	public static void re_initRepository() throws Exception {
//		which++;
//		
////		if(which==1){
////			System.out.println("usuwam niepotrzebne dane");
////			NativeStore store=new NativeStore(new File(filePath2));	
////			SailRepository rep=new SailRepository(store);
////			rep.initialize();
////			repository=rep;
////			rep.getConnection().remove(null, rep.getValueFactory().createURI(RelConstants.RL_HAS_OBSERVATION_FEATURE+0), (Value)null, (Resource)null);
////			System.out.println("usunalem");
////			rep.getConnection().commit();
////			rep.shutDown();
////			System.out.println("koncze pre-processing");
////		}
//		
//		File dstFolder = new File("/tmp/repo_destiny_part_"+which+"_"+System.nanoTime());
//		dstFolder.mkdirs();
//		copyFolder(new File(filePath2),dstFolder);
//		NativeStore store=new NativeStore(dstFolder);	
//		SailRepository rep=new SailRepository(store);
//		rep.initialize();
//		repository=rep;
//		rep.getConnection().setAutoCommit(false);
//		m2 = new SesameManipulator(repository, new _3ObservationsFeatureChecker());
//		m3 = new SesameManipulator(repository, new _4aPArtOfObservations2CSV());
//	}
//}
