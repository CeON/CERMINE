/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.edu.icm.yadda.analysis.relations.general2sesame.bwmeta2bigdatasesame;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Parallel;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.bwmeta2sesame.ParallelOperator_FromBwmetaToSesame;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta.ZipFileIteratorBuilder;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;

import com.bigdata.btree.IndexMetadata;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

/**
 * Based on @author kura 's TransferYaddaPackToSesame
 * @author pdendek
 */
public class _1TransferYaddaPackToBigDataSesame {

	private static final Logger log = LoggerFactory.getLogger(_1TransferYaddaPackToBigDataSesame.class);
	
    protected static final String O_STOREDIR = "store-parent";
    protected static final String O_FILENAME = "file-name";
    protected static final String O_MODE = "mode-of-store";
    protected static final String O_STEP = "step";
	protected static final String O_READER = "reader";

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
        
        Option oReader= new Option("r", O_READER , true, "version of BWMETA reader");
        oReader.setRequired(true);
        options.addOption(oReader);
                
		return options;
	}

    protected static void usage(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out);
		helpFormatter.printUsage(writer, 80, _1TransferYaddaPackToBigDataSesame.class.getSimpleName(), options);
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
			
			IMetadataReader<YExportable> reader;
			String reader_version = commandLine.getOptionValue(O_READER);
			if(reader_version.equals("BWMETA_1")) reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_1_0, BwmetaTransformers.Y);
			else if(reader_version.equals("BWMETA_1_2")) reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_1_2, BwmetaTransformers.Y);
			else if(reader_version.equals("BWMETA_2")) reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_0, BwmetaTransformers.Y);
			else if(reader_version.equals("BWMETA_2_1")) reader = BwmetaTransformers.BTF.getReader(BwmetaTransformers.BWMETA_2_1, BwmetaTransformers.Y);
			else{
				log.error("You have to choose a BWMETA reader out of the following: {BWMETA_1_0, BWMETA_1_2, BWMETA_2_0, BWMETA_2_1}");
				throw new ParseException("You have to choose a BWMETA reader out of the following: {BWMETA_1_0, BWMETA_1_2, BWMETA_2_0, BWMETA_2_1}");
			}
			
			ParallelOperator_FromBwmetaToSesame zeo = new ParallelOperator_FromBwmetaToSesame(reader,sailRep);
			
			ZipFileIteratorBuilder zfib = new ZipFileIteratorBuilder();
			HashMap<String, String> hm = new HashMap<String, String>();
			
	    	int fileNum = commandLine.getOptionValues(O_FILENAME).length;
	    	int currFile = 0;
			for(String zipFile : commandLine.getOptionValues(O_FILENAME)){
				currFile++;
				log.debug("===== Przetwarzam plik {} [plik {} z {}] =====", new Object[]{zipFile, currFile, fileNum});
				hm.clear();
				hm.put(ZipFileIteratorBuilder.AUX_PARAM_SOURCE_FILE, zipFile);
				ISourceIterator<File> it = zfib.build(hm);
		    	Parallel.For(it, zeo,Integer.parseInt(commandLine.getOptionValue(O_STEP)));
		    	log.debug("===== Przetworzylem plik {} [plik {} z {}] =====", new Object[]{zipFile, currFile, fileNum});
			}
			
			
			_3ExportBigDataSesame2N3.exportRepoToN3(sailRep, commandLine.getOptionValue(O_STOREDIR)+System.nanoTime()+".n3");
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
