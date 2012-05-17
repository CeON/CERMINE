package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.general2sesame.auxil.FeatureOccurenceCounter_OnSameSurnames;

public class BigdataFeature1EmailTest{

	private static final Logger log = LoggerFactory.getLogger(BigdataFeature1EmailTest.class);
	
	@SuppressWarnings("deprecation")
	@Test
	public void correctWorkTest() throws IOException, RepositoryException, QueryEvaluationException, MalformedQueryException, RDFHandlerException{
		
		log.debug("zacząłem pracę w testerze");
		
		System.setProperty("org.openrdf.repository.debug","true");
		
		Repository srcRepo=new SailRepository(new MemoryStore());
		srcRepo.initialize();
		RepositoryConnection conn = srcRepo.getConnection();
		ValueFactory vf = conn.getValueFactory();
		
		
		conn.add(vf.createURI(RelConstants.NS_CONTRIBUTOR+"bwmeta1.element.42cb3f21-41e3-349b-afee-a7d95cc63cb5/c0"),
				vf.createURI(RelConstants.RL_CONTACT_EMAIL),
				vf.createLiteral("pdendek@icm.edu.pl"),
				(Resource)null);
		conn.add(vf.createURI(RelConstants.NS_CONTRIBUTOR+"bwmeta1.element.c7339171-6294-3f01-b8ab-d2ccba1672bc/c0"),
				vf.createURI(RelConstants.RL_CONTACT_EMAIL),
				vf.createLiteral("pdendek@icm.edu.pl"),
				(Resource)null);
		conn.add(vf.createURI(RelConstants.NS_CONTRIBUTOR+"bwmeta1.element.3a378150-1f54-3679-967b-702febd8ffdb/c0"),
				vf.createURI(RelConstants.RL_CONTACT_EMAIL),
				vf.createLiteral("pdendek@icm.edu.pl"),
				(Resource)null);
		
		Repository dstRepo=new SailRepository(new MemoryStore());
		dstRepo.initialize();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("http://contributor.pl/bwmeta1.element.42cb3f21-41e3-349b-afee-a7d95cc63cb5/c0");
		list.add("http://contributor.pl/bwmeta1.element.c7339171-6294-3f01-b8ab-d2ccba1672bc/c0");
		list.add("http://contributor.pl/bwmeta1.element.3a378150-1f54-3679-967b-702febd8ffdb/c0");
		
		BigdataFeature1Email bigd = new BigdataFeature1Email();
		bigd.setRepository(srcRepo);
		
		BigdataFeature1Email saild = new BigdataFeature1Email();
		saild.setRepository(dstRepo);
		
		bigd.copyTo(dstRepo, list);
				
		for(int i = 0; i < list.size();i++){
			for(int j = i+1; j < list.size();j++){
				String c1 = list.get(i);
				String c2 = list.get(j);
				log.debug("Sprawdzanie, czy wyniki z kopii bazy głownej jest zgodna z oryginałem.");
				assertEquals(saild.analyze(c1, c2), bigd.analyze(c1, c2),0);
			}
		}
	}
}
