package pl.edu.icm.yadda.analysis.relations.pj.clues.tests;

import java.io.File;
import java.util.Date;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Super class for testing classes of clues.
 * 
 * @author mlukasik
 * 
 */
public class WeightAssignatorTester {
    protected static final Logger log = LoggerFactory.getLogger(WeightAssignatorTester.class);
    protected static int NUM_OF_TRIPLES = 3;
    
    protected static void view_contents_of_repo(Repository rep) throws OpenRDFException {
        System.out.println("Connecting a repository...");
        RepositoryConnection con = rep.getConnection();
        System.out.println("After Connecting a repository...");
        try {
            System.out.println("Querying");
            String queryString = "SELECT x, y, p FROM {x} p {y}";
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();

            try {
                System.out.println("Reading result\n");
                System.out.println(result.toString());

                while (result.hasNext()) {
                    System.out.println("Next value of the repo:\n");
                    BindingSet bindingSet = result.next();
                    Value valueOfX = bindingSet.getValue("x");
                    Value valueOfY = bindingSet.getValue("y");
                    Value valueOfP = bindingSet.getValue("p");

                    System.out.println(" - " + valueOfX.stringValue() + " in rel: " + valueOfP.stringValue() + " with "
                            + valueOfY.stringValue() + " - ");
                }
            } finally {
                result.close();
            }
        } finally {
            con.close();
        }
    }

    protected static Repository get_new_repo() {
        return new SailRepository(
                new NativeStore(new File("/tmp/test-repo-from-test-" + (new Date()).toString() + "/")));
    }
}
