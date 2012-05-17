package pl.edu.icm.yadda.analysis.relations.pj.clues;

import java.util.LinkedList;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;

/**
 * Disambiguator granting contributors who use only same languages (excluding
 * English). Result (similarity factor) is in {-1,0,1}
 * 
 * Contributor A and B contribute write in langsA and langsB. Let's subtract
 * English from both of sets. Then, if either set langsA or langsB is empty
 * result is 0. If both A and B contain same languages result is 1. If A and B
 * contain different languages result is -1.
 * 
 * @return similarity factor in {-1,0,1}
 * @update @return similarity factor in {-1,0,0.01}
 * 
 * @author pdendek
 * 
 */
public class PJStrictLanguageClue extends PJDisambiguator {

    private static final Logger log = LoggerFactory
            .getLogger(PJStrictLanguageClue.class);

    @Override
    public String id() {
        return "language-clue-strict";
    }

    protected double checkIfSimilar(RepositoryConnection conn, String contribA,
            String contribB) throws RepositoryException,
            MalformedQueryException, QueryEvaluationException {

        LinkedList<String> langA = new LinkedList<String>();
        LinkedList<String> langB = new LinkedList<String>();

        String zeroQueryString = "" + "Select distinct lan  \n" + "from \n"
                + "{doc} <" + RelConstants.RL_HAS_CONTRIBUTOR + "> {<"
                + contribA + ">}, \n" + "{doc} <" + RelConstants.RL_LANGUAGE
                + "> {lan} \n" + "";

        String firstQueryString = "" + "Select distinct lan  \n" + "from \n"
                + "{doc} <" + RelConstants.RL_HAS_CONTRIBUTOR + "> {<"
                + contribB + ">}, \n" + "{doc} <" + RelConstants.RL_LANGUAGE
                + "> {lan} \n" + "";

        TupleQuery query = null;
        TupleQueryResult res = null;

        query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
        res = query.evaluate();
        while (res.hasNext()) {
            langA.add(res.next().getValue("lan").toString());
        }
        res.close();
        query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
        res = query.evaluate();
        while (res.hasNext()) {
            langB.add(res.next().getValue("lan").toString());
        }
        res.close();

        if (langA.size() == 0 || langB.size() == 0)
            return 0;

        @SuppressWarnings("unchecked")
        LinkedList<String> langSum = (LinkedList<String>) langA.clone();
        /*
         * instead of appending all: summation.addAll(langB); add if they are
         * not in the list
         */
        for (String i : langB) {
            if (!langSum.contains(i))
                langSum.add(new String(i));
        }

        System.out.println("langSum " + Integer.toString(langSum.size())
                + " langA " + Integer.toString(langA.size()) + " langB "
                + Integer.toString(langB.size()));

        if (langSum.size() - langA.size() > 1
                || langSum.size() - langB.size() > 1)
            return -1;

        if (langSum.size() == langA.size())
            return 1;
        
        return 0.01;

    }
}
