package pl.edu.icm.yadda.analysis.relations.pj.clues.tests;

import java.util.ArrayList;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.clues.PJNotStrictLessThen70YearsClue;
/**
 * Runs test for PJNotStrictLessThen70YearsClue
 * @author mlukasik
 * 
 */
public class PJNotStrictLessThen70YearsClueTest extends WeightAssignatorTester {
    protected static void add_sample_triples(Repository rep, ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<Literal> years) throws OpenRDFException {

        ValueFactory f = rep.getValueFactory();

        // create some resources and literals to make statements out of
        URI has_contributor_rel = f.createURI(RelConstants.RL_HAS_CONTRIBUTOR);
        URI year_rel = f.createURI(RelConstants.RL_YEAR);
        RepositoryConnection con = rep.getConnection();
        try {
            for (int i = 0; i < NUM_OF_TRIPLES; i++) {
                con.add(docs.get(i), has_contributor_rel, contributors.get(i));
                con.add(docs.get(i), year_rel, years.get(i));
            }
        } finally {
            con.close();
        }
    }

    protected static void create_sample_triples(Repository rep, ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<Literal> langs) {
        ValueFactory f = rep.getValueFactory();
        for (int i = 1; i <= NUM_OF_TRIPLES; i++) {
            contributors.add(f.createURI(RelConstants.NS_PERSON + Integer.toString(i)));
            docs.add(f.createURI(RelConstants.NS_DOCUMENT + Integer.toString(i)));
            if (i % 2 == 1)
                langs.add(f.createLiteral("1863"));
            else
                langs.add(f.createLiteral("2011"));
        }
    }

    //@Test
    public void PJNotStrictLanguageClueTest() throws OpenRDFException {
        Repository rep = get_new_repo();
        rep.initialize();

        ArrayList<URI> contributors = new ArrayList<URI>();
        ArrayList<URI> docs = new ArrayList<URI>();
        ArrayList<Literal> years = new ArrayList<Literal>();

        create_sample_triples(rep, contributors, docs, years);
        add_sample_triples(rep, contributors, docs, years);
        view_contents_of_repo(rep);

        PJNotStrictLessThen70YearsClue clue1 = new PJNotStrictLessThen70YearsClue();

        clue1.setRepository(rep);
        
        double res1 = clue1.analyze(contributors.get(0).toString(), contributors.get(1).toString());
        //Assert.assertEquals(res1, -1.0);

        double res2 = clue1.analyze(contributors.get(0).toString(), contributors.get(2).toString());
        //Assert.assertEquals(res2, 1.0);
    }
}
