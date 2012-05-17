package pl.edu.icm.yadda.analysis.relations.pj.clues.tests;

import java.util.ArrayList;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.clues.PJNotStrictLanguageClue;

/**
 * 
 * @author mlukasik
 * 
 */
public class PJNotStrinctLanguageClueTest extends WeightAssignatorTester{

    protected static void add_sample_triples(Repository rep,
            ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<URI> langs) throws OpenRDFException {
        
        ValueFactory f = rep.getValueFactory();

        // create some resources and literals to make statements out of
        URI has_contributor_rel = f.createURI(RelConstants.RL_HAS_CONTRIBUTOR);
        URI lang_rel = f.createURI(RelConstants.RL_LANGUAGE);
        RepositoryConnection con = rep.getConnection();
        try {
            for (int i = 0; i < NUM_OF_TRIPLES; i++) {
                con.add(docs.get(i), has_contributor_rel, contributors.get(i));
                con.add(docs.get(i), lang_rel, langs.get(i));
            }
        } finally {
            con.close();
        }
    }
    
    protected static void create_sample_triples(Repository rep, ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<URI> langs) {
        ValueFactory f = rep.getValueFactory();
        for (int i = 1; i <= NUM_OF_TRIPLES; i++) {
            contributors.add(f.createURI(RelConstants.NS_PERSON
                    + Integer.toString(i)));
            docs.add(f.createURI(RelConstants.NS_DOCUMENT
                    + Integer.toString(i)));
            if(i%2==1)
                langs.add(f.createURI(RelConstants.RL_LANGUAGE + "russian"));
            else
                langs.add(f.createURI(RelConstants.RL_LANGUAGE + "french"));
        }
    }
    
    //@Test
    public void PJNotStrictLanguageClueJTest() throws OpenRDFException {
        Repository rep = get_new_repo();
        rep.initialize();

        ArrayList<URI> contributors = new ArrayList<URI>();
        ArrayList<URI> docs = new ArrayList<URI>();
        ArrayList<URI> langs = new ArrayList<URI>();
        
        create_sample_triples(rep, contributors, docs, langs);
        add_sample_triples(rep, contributors, docs, langs);

        PJNotStrictLanguageClue clue1 = new PJNotStrictLanguageClue();
        
        clue1.setRepository(rep);
        double res1 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(1).toString());
        //Assert.assertEquals(res1, -1.0); 

        double res2 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(2).toString());
        //Assert.assertEquals(res2, 1.0); 
    }
}
