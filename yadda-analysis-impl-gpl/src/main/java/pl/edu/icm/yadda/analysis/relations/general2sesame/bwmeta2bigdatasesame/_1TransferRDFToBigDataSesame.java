/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.edu.icm.yadda.analysis.relations.general2sesame.bwmeta2bigdatasesame;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.btree.IndexMetadata;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

/**
 * Based on @author kura 's TransferYaddaPackToSesame
 * @author pdendek
 */
public class _1TransferRDFToBigDataSesame {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(_1TransferRDFToBigDataSesame.class);
	
    protected static final String O_STOREDIR = "store-parent";
    protected static final String O_FILENAME = "file-name";
    protected static final String O_MODE = "mode-of-store";
    protected static final String O_STEP = "step";

    protected static Options defineOptions() {
		Options options = new Options();

		Option oStep = new Option("p", O_STEP, true, "size of files after which log will be done");
		oStep.setRequired(true);
		options.addOption(oStep);
		
		Option oMode = new Option("m", O_MODE, true, "a mode of storage, either SESAME or BDATA");
		oMode.setRequired(true);
		options.addOption(oMode);
		
		Option oStoreDir = new Option("s", O_STOREDIR, true, "store path parent, without a slash in the end of the given path, e.g. \"/tmp/data\"");
		oStoreDir.setRequired(true);
		options.addOption(oStoreDir);

		Option oFileName= new Option("f", O_FILENAME , true, "Name of pack file");
        oFileName.setRequired(true);
        options.addOption(oFileName);
 
		return options;
	}

    protected static void usage(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out);
		helpFormatter.printUsage(writer, 80, _1TransferRDFToBigDataSesame.class.getSimpleName(), options);
		helpFormatter.printOptions(writer, 80, options, 1, 2);
		writer.flush();
	}

  public static void main(String[]args) throws NumberFormatException, Exception {
      Options options = defineOptions();
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine commandLine = parser.parse(options, args);

			SailRepository sailRep = null;
			String mode = commandLine.getOptionValue(O_MODE);
			if(mode.equals("SESAME")){
				sailRep=new SailRepository(new NativeStore(new File(commandLine.getOptionValue(O_STOREDIR)+System.nanoTime())));
			}else if(mode.equals("BDATA")||mode.equals("BIGDATA")){
				sailRep=createRWStoreSail(commandLine);
			}
			sailRep.initialize();
			
			for(String rdfFile : commandLine.getOptionValues(O_FILENAME)){
				RepositoryConnection conn = sailRep.getConnection();
				conn.setAutoCommit(false);
				conn.add(new File(rdfFile), null, RDFFormat.N3, (Resource)null);
				conn.commit();
				conn.close();
			}
		} catch (ParseException e) {
			usage(options);
		}
  }

	private static SailRepository createRWStoreSail(CommandLine commandLine) {
		Properties properties = new Properties();
	    properties.setProperty(BigdataSail.Options.BUFFER_MODE, "DiskRW");
	    properties.setProperty(BigdataSail.Options.BUFFER_CAPACITY, "100000");
//	    properties.setProperty(BigdataSail.Options.INITIAL_EXTENT, "209715200");
	    // this option can be faster and make better use of disk if you have
	    // enough ram and are doing large writes.
	    properties.setProperty(IndexMetadata.Options.WRITE_RETENTION_QUEUE_CAPACITY,"8000");
	    properties.setProperty(IndexMetadata.Options.BTREE_BRANCHING_FACTOR,"128");
	    properties.setProperty(BigdataSail.Options.ISOLATABLE_INDICES, "false");
	    // triples only.
	    properties.setProperty(BigdataSail.Options.QUADS,"false");
	    // no statement identifiers
	    properties.setProperty(BigdataSail.Options.STATEMENT_IDENTIFIERS,"false");
	    // no free text search
	    properties.setProperty(BigdataSail.Options.TEXT_INDEX, "false");
	    properties.setProperty(BigdataSail.Options.BLOOM_FILTER, "false");
	    properties.setProperty(BigdataSail.Options.AXIOMS_CLASS, "com.bigdata.rdf.axioms.NoAxioms");
	    // when loading a large data file, it's sometimes better to do
	    // database-at-once closure rather than incremental closure.  this
	    // is how you do it.
	    properties.setProperty(BigdataSail.Options.TRUTH_MAINTENANCE, "false");
	    // we won't be doing any retraction, so no justifications either
	    properties.setProperty(BigdataSail.Options.JUSTIFY, "false");
	    if (properties.getProperty(com.bigdata.journal.Options.FILE) == null) {
	        File journal = new File(commandLine.getOptionValue(O_STOREDIR)+System.nanoTime());
	        System.out.println("journalFile="+journal.getAbsolutePath());
	        properties.setProperty(BigdataSail.Options.FILE, journal
	                .getAbsolutePath());
	    }
	    // instantiate a sail
	    BigdataSail sail = new BigdataSail(properties);
	    SailRepository repo = new BigdataSailRepository(sail);
		return repo;
	}


}
