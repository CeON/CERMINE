package pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YInstitution;
import pl.edu.icm.yadda.bwmeta.model.YPerson;
import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader;
import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader120;
import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader2;
import pl.edu.icm.yadda.common.YaddaException;
import pl.edu.icm.yadda.tools.relations.SesameFeeder;
import pl.edu.icm.yadda.tools.relations.Statements;

public class ExtendedRelationshipStatementBuilderExportTest {
	
	static final String NAMESPACES = ""
		+"aff"  +  "\n"  +  RelConstants.NS_AFFILIATION +  "\n"
		+"con"  +  "\n"  +  RelConstants.NS_CONTRIBUTOR +  "\n"
		+"doc"  +  "\n"  +  RelConstants.NS_DOCUMENT    +  "\n"
		+"ins"  +  "\n"  +  RelConstants.NS_INSTITUTION +  "\n"
		+"per"  +  "\n"  +  RelConstants.NS_PERSON      +  "\n"
		+"ref"  +  "\n"  +  RelConstants.NS_REFERENCE   +  "\n"
//		+"yad"  +  "\n"  +  RelConstants.NS_YADDA       +  "\n"
		+"";
	protected static final String ITEMS_RESOURCE = "pl/edu/icm/yadda/analysis/relations/auxil/statementbuilder/feeder-test-items.xml";
	protected BwmetaReader bwmetaReader = new BwmetaReader120();
	protected ExtendedRelationshipStatementsBuilder builder = new ExtendedRelationshipStatementsBuilder();
	protected String PATH = "/home/pdendek/.aduna/openrdf-sesame/repositories/baztech-sesame/";
	protected MemoryStore store;
	protected SailRepository rep;
	
	@Test
	public void exportTest() throws RepositoryException, YaddaException, UnsupportedEncodingException{
		RepositoryConnection con = initRepo();
//		AddNamespaces.getAddNamespaces().performAction(con, new ByteArrayInputStream(NAMESPACES.getBytes("UTF-8")));
//		ExportRepo.getExportRepo().performAction(con, "/tmp/dump.n3");
	}
	
	
    public RepositoryConnection initRepo() throws RepositoryException, YaddaException{
         
    	store=new MemoryStore();
        rep=new SailRepository(store);
        rep.initialize();
         
        SesameFeeder feeder=new SesameFeeder();
        feeder.setRepository(rep) ;
        ExtendedRelationshipStatementsBuilder statementsBuilder = new ExtendedRelationshipStatementsBuilder();
        InputStream st = this.getClass().getClassLoader().getResourceAsStream(ITEMS_RESOURCE);
        BwmetaReader reader=new BwmetaReader2();
        Object o=reader.read(st, null);
        if (o instanceof ArrayList) {
            for (Object tmp:(ArrayList) o) {
                List<Statements> statements = Collections.emptyList();
                if (tmp instanceof YElement) {
                   statements = statementsBuilder.buildStatements((YElement) tmp);
                } else if (tmp instanceof YPerson) {
                   statements = statementsBuilder.buildStatements((YPerson) tmp);
                } else if (tmp instanceof YInstitution) {
                   statements = statementsBuilder.buildStatements((YInstitution) tmp);
                }
                if (statements != null && !statements.isEmpty()) {
                   feeder.store(statements);
                } 
            }
        }
        return rep.getConnection();
    }
}
