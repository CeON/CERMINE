package pl.edu.icm.yadda.analysis.relations.manipulations.YYYYYYYYYYYY.stattest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;

public abstract class SesameBasicOperations  {

	SesameManipulator m0a, m1a, m1b;
	String srcPath = "/home/pdendek/repo_with_initialized_observations";
	String dstPath = "/tmp/repo_v4_"+System.nanoTime();
	public Repository repository; 
	SesameBasicOperations t;
	
	public void setUp() throws Exception {		
		File dstFolder = new File(dstPath);
		dstFolder.mkdirs();
		copyFolder(new File(this.srcPath),dstFolder);
		if(repository==null){
			NativeStore store=new NativeStore(dstFolder);		
			SailRepository rep=new SailRepository(store);
			rep.initialize();
			repository=rep;
		}
	}

	public void execute() throws Exception {
		t = this;
		System.out.println("start: "+new Date()); 
		
		t.setUp();
		
		try {
			t.test();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public HashMap<String,Set<String>> getShards() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		return getShards(1);
	}
	
	public HashMap<String,Set<String>> getShards(int size) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		
		RepositoryConnection con = repository.getConnection();
		
		HashMap<String,Set<String>> hm = new HashMap<String,Set<String>>(); 
		for(String sur : getShardSurnames()){
			String contribQuery = "" +
			" Select distinct c" +
			" from" +
			" {c} <"+RelConstants.RL_IS_PERSON+"> {p}" +
			" ," +
			" {c} <"+RelConstants.RL_SURNAME+"> {\""+sur+"\"}" +
			" where" +
			" p!=<http://yadda.icm.edu.pl/person#zbl#->" +
			"";
			
			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
			TupleQueryResult res = query.evaluate();
			
			HashSet<String> hs = new HashSet<String>(); 
			
			while(res.hasNext())
				hs.add(res.next().getValue("c").stringValue());
			
			if(hs.size()>size)
				hm.put(sur, hs);
		}
		con.close();
		con = null;
		return hm;
	}
	
	public Set<String> getShardSurnames() throws RepositoryException, MalformedQueryException, QueryEvaluationException{
		
		RepositoryConnection con = repository.getConnection();
		
		String contribQuery = "" +
		" Select distinct s" +
		" from" +
		" {c} <"+RelConstants.RL_IS_PERSON+"> {p}" +
		" ," +
		" {c} <"+RelConstants.RL_SURNAME+"> {s}" +
		" where" +
		" p!=<http://yadda.icm.edu.pl/person#zbl#->" +
		"";
		
		TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
		TupleQueryResult res = query.evaluate();
		
		HashSet<String> hs = new HashSet<String>(); 
		
		while(res.hasNext()){
			hs.add(res.next().getValue("s").stringValue());
		}
		con.close();
		con = null;
		return hs;
		
	}
	
	public abstract void test();
	
	public void copyFolder(File src, File dest) throws IOException{

		if(src.isDirectory()){
	
			//if directory not exists, create it
			if(!dest.exists()){
			   dest.mkdir();
			   System.out.println("Directory copied from " 
	                          + src + "  to " + dest);
			}
	
			//list all the directory contents
			String files[] = src.list();
	
			for (String file : files) {
			   //construct the src and dest file structure
			   File srcFile = new File(src, file);
			   File destFile = new File(dest, file);
			   //recursive copy
			   copyFolder(srcFile,destFile);
			}
	
		}else{
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
		        OutputStream out = new FileOutputStream(dest); 
	
		        byte[] buffer = new byte[1024];
	
		        int length;
		        //copy the file content in bytes 
		        while ((length = in.read(buffer)) > 0){
		    	   out.write(buffer, 0, length);
		        }
	
		        in.close();
		        out.close();
		        System.out.println("File copied from " + src + " to " + dest);
		}
	}
}
