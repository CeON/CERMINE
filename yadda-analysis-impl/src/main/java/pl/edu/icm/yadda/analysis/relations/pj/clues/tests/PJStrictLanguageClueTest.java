package pl.edu.icm.yadda.analysis.relations.pj.clues.tests;

import java.util.ArrayList;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

import pl.edu.icm.yadda.analysis.relations.pj.clues.PJStrictLanguageClue;

/**
 * 
 * @author mlukasik
 * 
 */
public class PJStrictLanguageClueTest extends PJNotStrinctLanguageClueTest{
    
    //@Test
    public void PJNotStrictLanguageClueJTest() throws OpenRDFException {
        Repository rep = get_new_repo();
        rep.initialize();

        ArrayList<URI> contributors = new ArrayList<URI>();
        ArrayList<URI> docs = new ArrayList<URI>();
        ArrayList<URI> langs = new ArrayList<URI>();
        
        System.out.println("TEST NEW");
        
        create_sample_triples(rep, contributors, docs, langs);
        add_sample_triples(rep, contributors, docs, langs);

        PJStrictLanguageClue clue1 = new PJStrictLanguageClue();
        
        clue1.setRepository(rep);
        double res1 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(1).toString());
        /* only one language per author, so only 0.01 */
        //Assert.assertEquals(res1, 0.01); 
        //System.out.println("res1: "+Double.toString(res1));
        
        double res2 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(2).toString());
        //Assert.assertEquals(res2, 1.0); 
        //System.out.println("res2: "+Double.toString(res2));
    }
}
