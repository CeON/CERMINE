package pl.edu.icm.yadda.analysis.relations.pj.clues.tests;

import java.util.ArrayList;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.clues.PJSameJournalClue;


/**
 * 
 * @author mlukasik
 * 
 */
public class PJSameJournalClueTest extends WeightAssignatorTester{
    
    protected static void add_sample_triples(Repository rep, ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<Literal> journals) throws OpenRDFException {

        ValueFactory f = rep.getValueFactory();

        // create some resources and literals to make statements out of
        URI has_contributor_rel = f.createURI(RelConstants.RL_HAS_CONTRIBUTOR);
        URI journal_rel = f.createURI(RelConstants.RL_JOURNAL);
        RepositoryConnection con = rep.getConnection();
        try {
            for (int i = 0; i < NUM_OF_TRIPLES; i++) {
                con.add(docs.get(i), has_contributor_rel, contributors.get(i));
                con.add(docs.get(i), journal_rel, journals.get(i));
            }
        } finally {
            con.close();
        }
    }

    private static void create_sample_triples(Repository rep, ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<Literal> journals) {
        ValueFactory f = rep.getValueFactory();
        for (int i = 1; i <= NUM_OF_TRIPLES; i++) {
            contributors.add(f.createURI(RelConstants.NS_PERSON + Integer.toString(i)));
            docs.add(f.createURI(RelConstants.NS_DOCUMENT + Integer.toString(i)));
            if (i % 2 == 1)
                journals.add(f.createLiteral("journal1"));
            else
                journals.add(f.createLiteral("journal2"));
        }
    }
    
    //@Test
    public void PJSameJournalClueJTest() throws OpenRDFException {
        Repository rep = get_new_repo();
        rep.initialize();

        ArrayList<URI> contributors = new ArrayList<URI>();
        ArrayList<URI> docs = new ArrayList<URI>();
        ArrayList<Literal> journals = new ArrayList<Literal>();
        
        create_sample_triples(rep, contributors, docs, journals);
        add_sample_triples(rep, contributors, docs, journals);

        PJSameJournalClue clue1 = new PJSameJournalClue();
        
        clue1.setRepository(rep);
        double res1 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(1).toString());
        //Assert.assertEquals(res1, 0.0); 
        
        double res2 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(2).toString());
        //Assert.assertEquals(res2, 1.0); 
    }
}
