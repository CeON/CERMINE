package pl.edu.icm.yadda.analysis.relations.pj.clues.tests;

import java.util.ArrayList;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.clues.PJTagWordsClue;

/**
 * 
 * "Select distinct text, lan  \n" + "from \n" +
 * "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">}, \n" +
 * "{doc} <"+RelConstants.RL_TAG+"> {tag}, \n" +
 * "{tag} <"+RelConstants.RL_TEXT+"> {text}, \n" +
 * "{tag} <"+RelConstants.RL_LANGUAGE+"> {lan} \n" + "";
 * 
 * @author michal
 * 
 */

public class PJTagWordsClueTest extends WeightAssignatorTester {

    protected static void add_sample_triples(Repository rep,
            ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<URI> tags, ArrayList<Literal> texts,
            ArrayList<URI> langs)
            throws OpenRDFException {

        ValueFactory f = rep.getValueFactory();

        // create some resources and literals to make statements out of
        URI has_contributor_rel = f.createURI(RelConstants.RL_HAS_CONTRIBUTOR);
        URI tag_rel = f.createURI(RelConstants.RL_TAG);
        URI text_rel = f.createURI(RelConstants.RL_TEXT);
        URI lang_rel = f.createURI(RelConstants.RL_LANGUAGE);
        RepositoryConnection con = rep.getConnection();
        try {
            for (int i = 0; i < NUM_OF_TRIPLES; i++) {
                con.add(docs.get(i), has_contributor_rel, contributors.get(i));
                con.add(docs.get(i), tag_rel, tags.get(i));
                con.add(tags.get(i), text_rel, texts.get(i));
                con.add(tags.get(i), lang_rel, langs.get(i));
            }
        } finally {
            con.close();
        }
    }

    private static void create_sample_triples(Repository rep,
            ArrayList<URI> contributors, ArrayList<URI> docs,
            ArrayList<URI> tags, ArrayList<Literal> texts,
            ArrayList<URI> langs) {
        ValueFactory f = rep.getValueFactory();
        for (int i = 0; i < NUM_OF_TRIPLES; i++) {
            contributors.add(f.createURI(RelConstants.NS_PERSON
                    + Integer.toString(i)));
            docs.add(f.createURI(RelConstants.NS_DOCUMENT + Integer.toString(i)));
            tags.add(f.createURI(RelConstants.NS_TAG + Integer.toString(i)));
            if (i % 3 == 0) {
                texts.add(f
                        .createLiteral("programming java artificial intelligence"));
                langs.add(f.createURI(RelConstants.RL_LANGUAGE + "russian"));
            } else if (i % 3 == 1) {
                texts.add(f
                        .createLiteral("time frequency analysis otoacoustics"));
                langs.add(f.createURI(RelConstants.RL_LANGUAGE + "italian"));
            } else {
                texts.add(f
                        .createLiteral("programming python artificial intelligence"));
                langs.add(f.createURI(RelConstants.RL_LANGUAGE + "russian"));
            }
        }
    }

    //@Test
    public void PJSameJournalClueJTest() throws OpenRDFException {
        Repository rep = get_new_repo();
        rep.initialize();

        ArrayList<URI> contributors = new ArrayList<URI>();
        ArrayList<URI> docs = new ArrayList<URI>();
        ArrayList<URI> tags = new ArrayList<URI>();
        ArrayList<Literal> texts = new ArrayList<Literal>();
        ArrayList<URI> langs = new ArrayList<URI>();

        create_sample_triples(rep, contributors, docs, tags, texts, langs);
        add_sample_triples(rep, contributors, docs, tags, texts, langs);
        view_contents_of_repo(rep);

        PJTagWordsClue clue1 = new PJTagWordsClue();

        clue1.setRepository(rep);
        double res1 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(1).toString());
        //Assert.assertEquals(res1, 0.0);
        //System.out.println("res1: "+Double.toString(res1));

        double res2 = clue1.analyze(contributors.get(0).toString(),
                contributors.get(2).toString());
        //Assert.assertEquals(res2, 1.0);
        //System.out.println("res2: "+Double.toString(res2));
    }
}
