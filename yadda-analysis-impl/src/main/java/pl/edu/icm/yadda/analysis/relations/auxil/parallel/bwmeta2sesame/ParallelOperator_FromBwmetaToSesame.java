package pl.edu.icm.yadda.analysis.relations.auxil.parallel.bwmeta2sesame;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Operation;
import pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder.ExtendedRelationshipStatementsBuilder;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.tools.relations.SesameFeeder;
import pl.edu.icm.yadda.tools.relations.Statements;


/**
 * 
 * @author pdendek
 *
 */
public class ParallelOperator_FromBwmetaToSesame implements Operation<File>{
	
	final protected SesameFeeder feeder = new SesameFeeder();
	final protected ExtendedRelationshipStatementsBuilder statementBuilder = new ExtendedRelationshipStatementsBuilder();
	final protected IMetadataReader<YExportable> reader;
	final protected Repository repository; 
	
	protected int stms = 0;
	protected int num = 1;
	protected long start = System.nanoTime();
	
	public ParallelOperator_FromBwmetaToSesame(IMetadataReader<YExportable> reader, Repository repository){
		this.reader=reader;
		this.repository=repository;
		feeder.setRepository(repository);
    }
	
	@Override
	public void perform(File f) {
		try{
			if(repository==null) return;
			LinkedList<YExportable> lye = new LinkedList<YExportable>();
			for(YElement e : toYElements(f)) lye.add(e);
			List<Statements> l = statementBuilder.buildStatements(lye);
			int count = feeder.store(l);
			stms += count;
			if(num%1000==0){
				System.out.println("Raport number:\t"+num);
				System.out.println("Stms this time:\t"+count);
				System.out.println("Overal Stms number:\t"+stms);
				System.out.println("Avg [per write]: "+(stms/num));
				System.out.println("Avg [per sec]: "+(stms*Math.pow(10,9)/((System.nanoTime()-start))));
				System.out.println("====================================");
			}
			num++;
        }catch(Exception e){
    		try{
    			System.out.println("Following exception occurred in file: "+f.getAbsolutePath());
    		}catch(Exception NullPointerException){
    			System.out.println("Following exception occurred in file: >>Null<<");
    		}
    		e.printStackTrace();
    	}
	}

	private List<YElement> toYElements(File f) throws Exception {
		FileReader fr = new FileReader(f);
		List<YExportable> lye = reader.read(fr, (Object[])null);
		fr.close();
		fr=null;
		List<YElement> yel = new LinkedList<YElement>();
		for(YExportable ye : lye)
			if(ye instanceof YElement) yel.add((YElement)ye);
		return yel;
	}

	@Override
	public Operation<File> replicate() {
		ParallelOperator_FromBwmetaToSesame zeo = new ParallelOperator_FromBwmetaToSesame(this.reader, repository);
    	return zeo;
	}

	@Override
	public void setUp() throws RepositoryException {
        repository.initialize();
	}

	@Override
	public void shutDown() throws RepositoryException {
		repository.shutDown();
	}
}
