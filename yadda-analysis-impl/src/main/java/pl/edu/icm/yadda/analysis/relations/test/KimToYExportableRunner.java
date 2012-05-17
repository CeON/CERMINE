package pl.edu.icm.yadda.analysis.relations.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.List;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.relations.SesameRepositoryManipulator;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.imports.kimtestset.KimToYExportable;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.tools.relations.RelationshipStatementsBuilder;
import pl.edu.icm.yadda.tools.relations.SesameFeeder;
import pl.edu.icm.yadda.tools.relations.Statements;

public class KimToYExportableRunner {
	
	Logger log = LoggerFactory.getLogger(KimToYExportableTest.class);
	private String repoFilePath = null;
	private String inputFilePath = null;
	
	public String getRepoFilePath() {
		return repoFilePath;
	}

	public void setRepoFilePath(String repoFilePath) {
		this.repoFilePath = repoFilePath;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	
	public static void main(String args[]) throws RepositoryException, RDFHandlerException, RDFParseException, MalformedQueryException, QueryEvaluationException, IOException, AnalysisException, Exception{
		KimToYExportableRunner run = new KimToYExportableRunner();
		run.setInputFilePath("/home/pdendek/dir/yadda-analysis-impl/trunk/src/main/resources/pl/edu/icm/yadda/imports/kimtestset/KISTI-AD-E-01-TestSet-1000.txt");
		run.setRepoFilePath("/tmp/fullrepo13");
		run.proceed();
	}
	
	public void proceed() throws /*Yadda*/Exception, RepositoryException, IOException, RDFHandlerException, RDFParseException, AnalysisException, MalformedQueryException, QueryEvaluationException {
		
		if(repoFilePath==null || inputFilePath == null){
			System.out.println("insufficient data. set both \"repoFilePath\" and \"inputFilePath\" fields");
			return;
		}else{
			System.out.println("repoFilePath: "+repoFilePath);
			System.out.println("inputFilePath: "+inputFilePath);
		}
		
//		BasicConfigurator.configure();
		
		SesameRepositoryManipulator srm = null;
		NativeStore store2=null;
		SailRepository rep2=null;
		
		File repo_file = new File(repoFilePath);
		
		if(repo_file.exists()) repo_file.delete();
		repo_file.mkdir();
		
		IMetadataReader<YExportable> reader = new KimToYExportable();
		RelationshipStatementsBuilder builder = new RelationshipStatementsBuilder();
		
		InputStream stream = new FileInputStream(new File(inputFilePath));
		Reader streamReader = new InputStreamReader(stream); 
		List<YExportable> exportables = (List<YExportable>) reader.read(streamReader);
		List<Statements> statementsList = builder.buildStatements(exportables);
	
		
		MemoryStore store=new MemoryStore();
		SailRepository rep=new SailRepository(store);
		rep.initialize();
		
		System.out.println(new Date()+"\t010 Karmię repo");
        SesameFeeder feeder=new SesameFeeder();
        feeder.setRepository(rep) ;
        feeder.store(statementsList);
		
        File f21 = new File(repoFilePath+".N3");
		if(f21.exists())f21.delete();
		f21.createNewFile();
		FileOutputStream fos21 = new FileOutputStream(f21);
		RDFWriter qw21 = Rio.createWriter(RDFFormat.N3, fos21);
		rep.getConnection().export(qw21);	
		fos21.flush();
		
		System.out.print(new Date()+"\t");
		System.out.println("030 Zrzucam inMemoryStrore na dysk");
		store2=new NativeStore(repo_file);
		rep2=new SailRepository(store2);
		rep2.initialize();
		rep2.getConnection().add(f21, "", RDFFormat.N3);
		rep.shutDown();
        File f1 = new File(("/tmp/sesame_repo+"+
        		new Date()).replaceAll(" ", "_").replaceAll(":", "_"));
		if(f1.exists())f1.delete();
		f1.createNewFile();
		FileOutputStream fos1 = new FileOutputStream(f1);
		RDFWriter qw1 = Rio.createWriter(RDFFormat.N3, fos1);

		rep2.getConnection().export(qw1);	
		rep2.shutDown();
	}
}
