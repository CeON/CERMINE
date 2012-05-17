package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature1Email;

import com.bigdata.btree.IndexMetadata;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

public class CheckClue {
	public static void main(String[] args) throws NumberFormatException,
			Exception {
		System.setProperty("org.openrdf.repository.debug", "true");
		SailRepository sailRep = null;
		// sailRep=createRWStoreSail();
		// sailRep.initialize();

		// _3ExportBigDataSesame2N3.exportRepoToN3(sailRep,
		// "/home/pdendek/WMH/00 badania/00 import do jnl/enh."+new
		// Date()+".n3");

		proceedWithRepo(sailRep);

		// sailRep.shutDown();
	}

	private static void proceedWithRepo(SailRepository sailRep)
			throws RepositoryException, QueryEvaluationException,
			MalformedQueryException {
		BigdataFeature1Email bdd;
		bdd = new BigdataFeature1Email();
		bdd.setRepository(sailRep);

		try {

			// RepositoryConnection con = sailRep.getConnection();
			// TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL,
			// "" +
			// "Select distinct o,c1,c2,s  \n" +
			// "from \n" +
			// "{o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c1}, \n" +
			// "{o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c2}, \n" +
			// "{c1} <"+RelConstants.RL_CONTACT_EMAIL+"> {email}, \n" +
			// "{c2} <"+RelConstants.RL_CONTACT_EMAIL+"> {email}, \n" +
			// "{c1} <"+RelConstants.RL_SURNAME+"> {s}, \n" +
			// "{c2} <"+RelConstants.RL_SURNAME+"> {s} \n" +
			// "	WHERE email!=\"\" \n" +
			// ""
			// );
			// TupleQueryResult res = query.evaluate();
			//
			// int num=0;
			// while(res.hasNext()){
			// num++;
			// BindingSet bs = res.next();
			// // String c1 = bs.getValue("c1").stringValue();
			// // String c2 = bs.getValue("c2").stringValue();
			// // String s = bs.getValue("s").stringValue();
			// // String o = bs.getValue("o").stringValue();
			// // System.out.println("====================");
			// // System.out.println("c1: "+c1);
			// // System.out.println("c2: "+c2);
			// // System.out.println("s: "+s);
			// // System.out.println("o: "+o);
			// }
			// System.out.println("================== num ================");
			//
			Repository therepository = null;
			therepository = new SailRepository(new MemoryStore());
			therepository.initialize();
			RepositoryConnection memcon = therepository.getConnection();

			for (int qqq = 1; qqq < 3; qqq++) {
				memcon.add(
						memcon.getValueFactory().createURI("http://a" + qqq),
						memcon.getValueFactory().createURI("http://pred1"),
						memcon.getValueFactory().createURI("http://b" + qqq),
						(Resource) null);

				memcon.add(
						memcon.getValueFactory().createURI("http://b" + qqq),
						memcon.getValueFactory().createURI("http://pred2"),
						memcon.getValueFactory().createURI("http://c" + qqq),
						(Resource) null);
			}

			GraphQuery gQuery = memcon.prepareGraphQuery(QueryLanguage.SERQL,
					"" + "construct *  \n" + "from \n" + "{s1} pred {o1}, \n"
							+ "{o1} pred2 {o2} \n" + "");
			GraphQueryResult gRes = gQuery.evaluate();

			Repository therepository2 = null;
			therepository2 = new SailRepository(new MemoryStore());
			therepository2.initialize();
			RepositoryConnection memcon2 = therepository2.getConnection();

			memcon2.add(gRes, (Resource) null);

			GraphQuery g2Query = memcon2.prepareGraphQuery(QueryLanguage.SERQL,
					"" + "construct *  \n" + "from \n" + "{s1} pred {o1}, \n"
							+ "{o1} pred2 {o2} \n" + "");
			GraphQueryResult g2Res = g2Query.evaluate();

			System.out.println("=========================");
			System.out.println("=========================");
			System.out.println("=========================");
			
			while (g2Res.hasNext()) {
				Statement s = g2Res.next();
				System.out.println(s.getSubject());
				System.out.println(s.getPredicate());
				System.out.println(s.getObject());
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		//
		//
		//
		//
		//
		//
		// GraphQuery gQuery = con.prepareGraphQuery(QueryLanguage.SERQL,
		// "" +
		// "construct *  \n" +
		// "from \n" +
		// "{o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c1}, \n" +
		// "{o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c2}, \n" +
		// "{c1} <"+RelConstants.RL_SURNAME+"> {s}, \n" +
		// "{c2} <"+RelConstants.RL_SURNAME+"> {s} \n" +
		// "	WHERE email!=\"\" \n" +
		// ""
		// );
		// GraphQueryResult gRes = gQuery.evaluate();
		//
		//
		// for(int jjj=10;gRes.hasNext();jjj--){
		// Statement s = gRes.next();
		// System.out.println("SUB:"+s.getSubject()+"---{"+s.getPredicate()+"}--->"+s.getObject());
		// memcon.add(s,(Resource)null);
		// }
		//
		//
		//
		//
		// GraphQuery query2 = memcon.prepareGraphQuery(QueryLanguage.SERQL,
		// "" +
		// "Construct *  \n" +
		// "from \n" +
		// "{o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c1}, \n" +
		// "{o} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {c2}, \n" +
		// "{c1} <"+RelConstants.RL_SURNAME+"> {s}, \n" +
		// "{c2} <"+RelConstants.RL_SURNAME+"> {s} \n" +
		// "	WHERE email!=\"\" \n" +
		// ""
		// );
		// GraphQueryResult res2 = query2.evaluate();
		// System.out.println("==========================================================");
		// System.out.println("==========================================================");
		// System.out.println("==========================================================");
		// while(res2.hasNext()){
		// Statement s = res2.next();
		// System.out.println("SUB:"+s.getSubject()+"---{"+s.getPredicate()+"}--->"+s.getObject());
		// memcon.add(s,(Resource)null);
		// }
		//
		//
		// //
		//
		// //
		// // System.out.println("There were "+num+" occurences of given clue");
		// res2.close();
		// con.close();
	}

	@SuppressWarnings("unused")
	private static SailRepository createRWStoreSail() throws IOException {
		Properties properties = new Properties();
		properties.setProperty(BigdataSail.Options.BUFFER_MODE, "DiskRW");
		properties.setProperty(BigdataSail.Options.BUFFER_CAPACITY, "10000");
		// properties.setProperty(BigdataSail.Options.INITIAL_EXTENT,
		// "209715200");
		// this option can be faster and make better use of disk if you have
		// enough ram and are doing large writes.
		properties.setProperty(
				IndexMetadata.Options.WRITE_RETENTION_QUEUE_CAPACITY, "8000");
		properties.setProperty(IndexMetadata.Options.BTREE_BRANCHING_FACTOR,
				"128");
		properties.setProperty(BigdataSail.Options.ISOLATABLE_INDICES, "false");
		// triples only.
		properties.setProperty(BigdataSail.Options.QUADS, "false");
		// no statement identifiers
		properties.setProperty(BigdataSail.Options.STATEMENT_IDENTIFIERS,
				"false");
		// no free text search
		properties.setProperty(BigdataSail.Options.TEXT_INDEX, "false");
		properties.setProperty(BigdataSail.Options.BLOOM_FILTER, "false");
		properties.setProperty(BigdataSail.Options.AXIOMS_CLASS,
				"com.bigdata.rdf.axioms.NoAxioms");
		// when loading a large data file, it's sometimes better to do
		// database-at-once closure rather than incremental closure. this
		// is how you do it.
		properties.setProperty(BigdataSail.Options.TRUTH_MAINTENANCE, "false");
		// we won't be doing any retraction, so no justifications either
		properties.setProperty(BigdataSail.Options.JUSTIFY, "false");

		File dstFile = new File(
				"/home/pdendek/WMH/00 badania/00 import do jnl/enh.jnl");
		properties.setProperty(BigdataSail.Options.FILE,
				dstFile.getAbsolutePath());

		// instantiate a sail
		BigdataSail sail = new BigdataSail(properties);
		SailRepository repo = new BigdataSailRepository(sail);
		return repo;
	}

}
