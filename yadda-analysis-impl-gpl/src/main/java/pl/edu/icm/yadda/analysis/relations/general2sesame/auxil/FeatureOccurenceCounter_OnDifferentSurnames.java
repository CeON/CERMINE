package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataDisambiguator;
import pl.edu.icm.yadda.analysis.relations.Clusterizer;
import pl.edu.icm.yadda.analysis.relations.Disambiguator;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;
import pl.edu.icm.yadda.analysis.relations.WeighedDisambiguator;

/**
 * Creates a person directory using a number of so-called {@link Disambiguator}
 * s.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public class FeatureOccurenceCounter_OnDifferentSurnames extends PersonDirectoryCreator{
		
	private static final Logger log = LoggerFactory.getLogger(FeatureOccurenceCounter_OnDifferentSurnames.class);
    FileWriter fw;

    /**
     * Creates a person directory. Processes contributions group-by-group.
     * Grouping is provided by the person directory backend. For each group,
     * calls the configured {@link Disambiguator}s in order to assess similarity
     * of contributions. Next, calls the configured {@link Clusterizer} to
     * identify persons. Finally, persons are stored in the directory via the
     * configured {@link PersonDirectoryBackend}.
     * @throws Exception 
     */
    public void createOccurenceCountsCSVFile() throws Exception {
    	createPersonDirectory((Object[])null);
    }
    
    /**
     * Creates a person directory. Processes contributions group-by-group.
     * Grouping is provided by the person directory backend. For each group,
     * calls the configured {@link Disambiguator}s in order to assess similarity
     * of contributions. Next, calls the configured {@link Clusterizer} to
     * identify persons. Finally, persons are stored in the directory via the
     * configured {@link PersonDirectoryBackend}.
     * @throws Exception 
     */
    public void createPersonDirectory(Object[] objects) throws Exception {
    	
    	long traceTime = System.nanoTime();
    	traceTime = System.nanoTime()-traceTime;

    	String time = DateFormat.getDateTimeInstance()
		.format(new Date()).replaceAll(" ", "_")
		.replaceAll(":", "-"); 
        File f = new File("/home/pdendek/dane_icm/2012-04-20-CEDRAM_N3_NEWPREDICATES/",time+".diff.csv");
        fw = new FileWriter(f);    	
    	
    	Repository repo = (Repository) this.getBackend().getRepository();
    	RepositoryConnection conn = repo.getConnection();
    	
    	int occurences = 0;
    	Random rnd = new Random();
    	ArrayList<String> list = new ArrayList<String>();
    	for(int all = 0;all<68675;all+=occurences){
    		log.debug("Przetworzono {} wystapien",all); 
    		
    		TupleQueryResult tqr = conn.prepareTupleQuery(QueryLanguage.SPARQL, 
        			"select distinct ?s1 ?s2 \n" +
        			"{ \n" +
        			"?c1 <http://has-surname.pl> ?s1 . \n" +
        			"?c2 <http://has-surname.pl> ?s2 . \n" +
        			"FILTER(?s1<?s2) \n" +
        			"} \n" +
        			"OFFSET " +rnd.nextInt(600)*100+ " \n" +
        			"LIMIT 100 \n\n" +
        			"").evaluate();
        	
        	while(tqr.hasNext()){
        		BindingSet bs = tqr.next();
        		String s1 = bs.getValue("s1").stringValue();
        		String s2 = bs.getValue("s2").stringValue();
        		
        		tqr = conn.prepareTupleQuery(QueryLanguage.SPARQL, 
            			"select distinct ?c1 ?c2 \n" +
            			"{ \n" +
            			"?c1 <http://has-surname.pl> \""+s1+"\" . \n" +
            			"?c2 <http://has-surname.pl> \""+s2+"\" \n" +
            			"} \n" +

            			"LIMIT 100 \n" +
            			"").evaluate();
        		occurences=0;
            	while(tqr.hasNext()){
            		occurences++;
            		bs = tqr.next();
            		String c1 = bs.getValue("c1").stringValue();
            		String c2 = bs.getValue("c2").stringValue();
            		
            		StringBuilder sb = new StringBuilder();
        	        for (WeighedDisambiguator wd : regularWeighedDisambiguators) {
        	            Disambiguator disambiguator = wd.getDisambiguator();
        	            sb.append(disambiguator.analyze(c1, c2)+"\t");
        	        }
        	        sb.append(1+"\n");
        	        list.add(sb.toString());
            	}
            	persistOccurences(f, list);
            	list.clear();
            	log.debug("Next {} occurences added",occurences);
        	}
    		tqr.close();
    	}
    	
    	fw.close();
    }

	private void persistOccurences(File f, ArrayList<String> list)
			throws IOException {
		for(String l : list){fw.write(l);}
		fw.flush();
	}
}
