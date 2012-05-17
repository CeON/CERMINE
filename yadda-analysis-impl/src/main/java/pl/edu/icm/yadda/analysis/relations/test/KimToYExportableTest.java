package pl.edu.icm.yadda.analysis.relations.test;

import java.io.File;
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

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;
import pl.edu.icm.yadda.analysis.relations.SesameRepositoryManipulator;
import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.trace.KuraForenameSurnameTrace;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.imports.kimtestset.KimToYExportable;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.tools.relations.RelationshipStatementsBuilder;
import pl.edu.icm.yadda.tools.relations.SesameFeeder;
import pl.edu.icm.yadda.tools.relations.Statements;

public class KimToYExportableTest {
	
//	Logger log = LoggerFactory.getLogger(KimToYExportableTest.class);
//	static{
//		BasicConfigurator.configure();	
//	}
	protected static final String ITEMS_RESOURCE = "pl/edu/icm/yadda/imports/kimtestset/KISTI-AD-E-01-TestSet-1000.txt";
	protected static final String REPO_PATH = ("/tmp/repo"+
			//new Date()).replaceAll(" ", "_").replaceAll(":", "_")+
			"");
	
		
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws /*Yadda*/Exception, RepositoryException, IOException, RDFHandlerException, RDFParseException, AnalysisException, MalformedQueryException, QueryEvaluationException {
		
//		BasicConfigurator.configure();
		
		
		SesameRepositoryManipulator srm = null;
		NativeStore store2=null;
		SailRepository rep2=null;
		
		File repo_file = new File(REPO_PATH);
		if(!repo_file.exists()){
			
			repo_file.mkdir();
		
			IMetadataReader<YExportable> reader = new KimToYExportable();
			RelationshipStatementsBuilder builder = new RelationshipStatementsBuilder();
			
			InputStream stream = new KimToYExportableTest().getClass().getClassLoader().getResourceAsStream(ITEMS_RESOURCE);
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
			
			System.out.println(new Date()+"\t020 Zamieniam nazwy w repo");
	        srm = new SesameRepositoryManipulator();
	        srm.setRepository(rep);
	        srm.changeRelationName(RelConstants.RL_IS_PERSON, RelConstants.RL_IS_PERSON_DB);
	        srm.deleteRelation(RelConstants.RL_IS_PERSON);
			
			System.out.print(new Date()+"\t");
			System.out.println("021 Dodaję trace'y");
	        
	        KuraForenameSurnameTrace.burnAnalyze(rep.getConnection());
	        
	        File f21 = new File("/tmp/Test+"+
	        		//new Date()).replaceAll(" ", "_").replaceAll(":", "_")+
	        		"po_podaniu_trace");
			if(f21.exists())f21.delete();
			f21.createNewFile();
			FileOutputStream fos21 = new FileOutputStream(f21);
			RDFWriter qw21 = Rio.createWriter(RDFFormat.N3, fos21);
			rep.getConnection().export(qw21);	
			rep.shutDown();
			rep.initialize();
			fos21.flush();
			
			System.out.print(new Date()+"\t");
			System.out.println("030 Zrzucam inMemoryStrore na dysk");
			store2=new NativeStore(repo_file);
			rep2=new SailRepository(store2);
			rep2.initialize();
			rep2.getConnection().add(f21, "", RDFFormat.N3);
			rep.shutDown();
			rep2.shutDown();
			
			String[] arg = {repo_file.getAbsolutePath()}; 
			System.out.print(new Date()+"\t");
			System.out.println("040 Wykrywam persony");
			PersonDirectoryCreator pdc = new PersonDirectoryCreator();
	        pdc.main(arg);
	        
	        srm.setRepository(rep);
		}else{
			store2=new NativeStore(repo_file);
			rep2=new SailRepository(store2);
	        rep2.initialize();
	        
	        srm = new SesameRepositoryManipulator();
	        srm.setRepository(rep2);
		}
		
        System.out.print(new Date()+"\t");
        System.out.println("050 Zapisuję repo z personami");
        File f1 = new File("/tmp/Test+"+
        		//new Date()).replaceAll(" ", "_").replaceAll(":", "_")+
        		"na_koniec");
		if(f1.exists())f1.delete();
		f1.createNewFile();
		FileOutputStream fos1 = new FileOutputStream(f1);
		RDFWriter qw1 = Rio.createWriter(RDFFormat.N3, fos1);

		rep2.getConnection().export(qw1);	
		
		
		srm.setTrueRelationName(RelConstants.RL_IS_PERSON_DB);
		srm.setTestedRelationName(RelConstants.RL_IS_PERSON);
		srm.printAccuracyStats();
		
		System.out.println("Koniec :-)");
	}
}
