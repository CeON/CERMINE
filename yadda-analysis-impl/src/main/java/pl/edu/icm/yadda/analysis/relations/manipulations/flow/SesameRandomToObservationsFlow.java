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
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations.AreTheSameIntegerInitializer;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations.MultipleEmailRemover;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations.RandomFingerprintEmailContribution2Observations;
//
//public class SesameRandomToObservationsFlow {
//
//	static SesameManipulator m0a, m1a, m1b;
//	static String filePath2 = "/home/pdendek/sample/repo_v4";
//	static Repository repository; 
//	static double random = 0.1;
//	
//	public static void setUp() throws Exception {		
//		File dstFolder = new File("/tmp/repo_v4_rnd"+random+"_"+System.nanoTime());
//		dstFolder.mkdirs();
//		copyFolder(new File(filePath2),dstFolder);
//	
//		NativeStore store=new NativeStore(dstFolder);		
//		SailRepository rep=new SailRepository(store);
//		rep.initialize();
//		repository=rep;
//		m0a = new SesameManipulator(repository, new MultipleEmailRemover());
//		m1a = new SesameManipulator(repository, new RandomFingerprintEmailContribution2Observations());
//		m1b = new SesameManipulator(repository, new AreTheSameIntegerInitializer());
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
//			System.out.println("1: "+new Date());
//			e = (Exception) m0a.execute(null);
//			if(e!=null) throw e;
//			System.out.println("2: "+new Date());
//			
//			HashMap hm = new HashMap();
//			hm.put("random",random);
//			e = (Exception) m1a.execute(hm);
//			if(e!=null) throw e;
//			System.out.println("3: "+new Date());
//			e = (Exception) m1b.execute(null);
//			if(e!=null) throw e;
//			
//			System.out.println("stop: "+new Date());
//			
//		} catch (Exception e) {
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
