package pl.edu.icm.yadda.analysis.relations.pj.clues.tests;

import java.util.ArrayList;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

import pl.edu.icm.yadda.analysis.relations.pj.clues.PJNotStrictLessThen70YearsClue;
/**
 * Runs test for PJNotStrictLessThen70YearsClue AND PJStrictLessThen70YearsClueTest
 * @author mlukasik
 * 
 */
public class PJStrictLessThen70YearsClueTest extends PJNotStrictLessThen70YearsClueTest{
    
    //@Test
    public void PJStrictLessThen70YearsClueJTest() throws OpenRDFException {
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
