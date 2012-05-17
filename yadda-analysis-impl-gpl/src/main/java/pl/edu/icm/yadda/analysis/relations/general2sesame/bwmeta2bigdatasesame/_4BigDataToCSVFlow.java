///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package pl.edu.icm.yadda.analysis.relations.general2sesame.bwmeta2bigdatasesame;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Properties;
//
//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.GnuParser;
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.Option;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.ParseException;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.sail.SailRepository;
//import org.openrdf.sail.nativerdf.NativeStore;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature1Email;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature2EmailPrefix;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature3CoContribution;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature4CoClassif;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature5CoKeywordPhrase;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature6CoReference;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature7CoISSN;
//import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature8Year;
//import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._1ShardContribution2Observations;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._2AreTheSameBooleanInitializer;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._3ObservationsFeatureChecker;
//import pl.edu.icm.yadda.analysis.relations.manipulations.operations._4Observations2CSV;
//import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;
//
//import com.bigdata.btree.IndexMetadata;
//import com.bigdata.rdf.sail.BigdataSail;
//import com.bigdata.rdf.sail.BigdataSailRepository;
//
///**
// * Based on @author kura 's TransferYaddaPackToSesame
// * @author pdendek
// */
//public class _4BigDataToCSVFlow {
//
//	@SuppressWarnings("unused")
//	private static final Logger log = LoggerFactory.getLogger(_4BigDataToCSVFlow.class);
//	
//	static SesameManipulator m_contrib2observ, m_areSameInit, m_observFeatureChecker, m_observ2csv;
//	static Repository repository;
//	static Exception e; 
//	
//    protected static final String O_STOREDIR = "store-parent";
//    protected static final String O_MODE = "mode-of-store";
//	protected static final String O_OUTPUT_FILE = "output-path";
//	protected static final String O_LOGN3 = "log2n3";
//	
//	
//    protected static Options defineOptions() {
//		Options options = new Options();
//		
//		Option oMode = new Option("m", O_MODE, true, "a mode of storage, either SESAME or BDATA");
//		oMode.setRequired(true);
//		options.addOption(oMode);
//		
//		Option oStoreDir = new Option("s", O_STOREDIR, true, "store path parent, without a slash in the end of the given path, e.g. \"/tmp/data\"");
//		oStoreDir.setRequired(true);
//		options.addOption(oStoreDir);
//		
//		Option oOutputDir = new Option("d", O_OUTPUT_FILE, true, "output path parent for CSV files, without a slash in the end of the given path, e.g. \"/tmp/data\"");
//		oOutputDir.setRequired(true);
//		options.addOption(oOutputDir);
//
//		Option oLog2N3 = new Option("l", O_LOGN3, true, "YES or NO");
//		oLog2N3.setRequired(true);
//		options.addOption(oLog2N3);
//		
//		return options;
//	}
//
//    protected static void usage(Options options) {
//		HelpFormatter helpFormatter = new HelpFormatter();
//		PrintWriter writer = new PrintWriter(System.out);
//		helpFormatter.printUsage(writer, 80, _4BigDataToCSVFlow.class.getSimpleName(), options);
//		helpFormatter.printOptions(writer, 80, options, 1, 2);
//		writer.flush();
//	}
//
//    public static void proceedRepo(SailRepository sailRep, CommandLine commandLine) throws Exception{
//    	boolean iflog;
//    	if(commandLine.getOptionValue(O_LOGN3).equals("YES")){
//    		iflog=true;
//    	}else{
//    		iflog = false;
//    	}
//    	
////    	if(iflog){
////    		try{
////    			_3ExportBigDataSesame2N3.exportRepoToN3(sailRep, commandLine.getOptionValue(O_STOREDIR)+".beforAnyChanges_"+System.nanoTime()+".n3");  
////    		}catch (Exception e) {
////    			System.out.println(e.toString());
////    		}
////    	}
//    	
//    	repository=sailRep;
//    	
//    	m_contrib2observ = new SesameManipulator(repository, new _1ShardContribution2Observations());
//    	e = (Exception) m_contrib2observ.execute(null);
//    	if(e!=null) throw e;
//    	
//    	m_areSameInit = new SesameManipulator(repository, new _2AreTheSameBooleanInitializer());
//    	e = (Exception) m_areSameInit.execute(null);
//    	if(e!=null) throw e;
//    	
//    	
//    	HashMap<String,Object> hm = new HashMap<String,Object>();
//    	List<PJDisambiguator> pjdislist = new ArrayList<PJDisambiguator>();
//    	hm.put("disambiguatorList", pjdislist);
//    	
//    	PJDisambiguator d;
//    	
//    	d = new BigdataFeature1Email(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//    	//@FIXME ALL VALL ARE NULL
//      	
//      	d = new BigdataFeature2EmailPrefix(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//    	//@FIXME ALL VALL ARE NULL
//      	
//      	d = new BigdataFeature3CoContribution(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//    	
//      	
//      	d = new BigdataFeature4CoClassif(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//      	
//      	
//      	d = new BigdataFeature5CoKeywordPhrase(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//      	
//      	
//      	d = new BigdataFeature6CoReference(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//    	//@FIXME ALL VALL ARE NULL
//      	
//      	d = new BigdataFeature7CoISSN(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//    	//@FIXME ALL VALL ARE NULL
//      	
//      	d = new BigdataFeature8Year(); 
//      	d.setRepository(sailRep);
//      	pjdislist.add(d);
//      	
////      	d = new BigdataFeature9FullInitials(); 
////      	d.setRepository(sailRep);
////      	pjdislist.add(d);
////      	
////      	d = new BigdataFeature10PrefixInitials(); 
////      	d.setRepository(sailRep);
////      	pjdislist.add(d);
//      	
//	  	m_observFeatureChecker = new SesameManipulator(repository, new _3ObservationsFeatureChecker());
//	  	e = (Exception) m_observFeatureChecker.execute(hm);
//		if(e!=null) throw e;
//	  	
//		m_observ2csv = new SesameManipulator(repository, new _4Observations2CSV());
//		int tmp = pjdislist.size();
//		hm.clear();
//		hm.put("featureNum",tmp); 
//		hm.put("csvFilePath",commandLine.getOptionValue(O_OUTPUT_FILE));
//		m_observ2csv.execute(hm);
//    }
//    
//    public static void main(String[]args) throws NumberFormatException, Exception {
//    	Options options = defineOptions();
//    	CommandLineParser parser = new GnuParser();
//    	try {
//				System.setProperty("org.openrdf.repository.debug","true");
//				CommandLine commandLine = parser.parse(options, args);
//				
//				SailRepository sailRep = null;
//				String mode = commandLine.getOptionValue(O_MODE);
//				if(mode.equals("SESAME")){
//					sailRep=new SailRepository(new NativeStore(new File(commandLine.getOptionValue(O_STOREDIR))));
//				}else if(mode.equals("BDATA")||mode.equals("BIGDATA")){
//					sailRep=createRWStoreSail(commandLine);
//				}
//				sailRep.initialize();
//				proceedRepo(sailRep,commandLine);
//				sailRep.shutDown();
//			} catch (ParseException e) {
//				usage(options);
//			}
//    }
//
//	private static SailRepository createRWStoreSail(CommandLine commandLine) throws IOException {
//		Properties properties = new Properties();
//	    properties.setProperty(BigdataSail.Options.BUFFER_MODE, "DiskRW");
//	    properties.setProperty(BigdataSail.Options.BUFFER_CAPACITY, "10000");
////	    properties.setProperty(BigdataSail.Options.INITIAL_EXTENT, "209715200");
//	    // this option can be faster and make better use of disk if you have
//	    // enough ram and are doing large writes.
//	    properties.setProperty(IndexMetadata.Options.WRITE_RETENTION_QUEUE_CAPACITY,"8000");
//	    properties.setProperty(IndexMetadata.Options.BTREE_BRANCHING_FACTOR,"128");
//	    properties.setProperty(BigdataSail.Options.ISOLATABLE_INDICES, "false");
//	    // triples only.
//	    properties.setProperty(BigdataSail.Options.QUADS,"false");
//	    // no statement identifiers
//	    properties.setProperty(BigdataSail.Options.STATEMENT_IDENTIFIERS,"false");
//	    // no free text search
//	    properties.setProperty(BigdataSail.Options.TEXT_INDEX, "false");
//	    properties.setProperty(BigdataSail.Options.BLOOM_FILTER, "false");
//	    properties.setProperty(BigdataSail.Options.AXIOMS_CLASS, "com.bigdata.rdf.axioms.NoAxioms");
//	    // when loading a large data file, it's sometimes better to do
//	    // database-at-once closure rather than incremental closure.  this
//	    // is how you do it.
//	    properties.setProperty(BigdataSail.Options.TRUTH_MAINTENANCE, "false");
//	    // we won't be doing any retraction, so no justifications either
//	    properties.setProperty(BigdataSail.Options.JUSTIFY, "false");
//	    
//	    if (properties.getProperty(com.bigdata.journal.Options.FILE) == null) {
//	    	File dstFile = new File(commandLine.getOptionValue(O_STOREDIR)+".withPersonalities_"+System.nanoTime()+".jnl");
//			copyFile(new File(commandLine.getOptionValue(O_STOREDIR)),dstFile);
//	        System.out.println("journalFile="+dstFile.getAbsolutePath());
//	        properties.setProperty(BigdataSail.Options.FILE, dstFile
//	                .getAbsolutePath());
//	    }
//	    // instantiate a sail
//	    BigdataSail sail = new BigdataSail(properties);
//	    SailRepository repo = new BigdataSailRepository(sail);
//		return repo;
//	}
//
//	private static void copyFile(File srcFile, File dstFile) throws IOException {
//		//if file, then copy it
//		//Use bytes stream to support all file types
//		InputStream in = new FileInputStream(srcFile);
//	    OutputStream out = new FileOutputStream(dstFile); 
//
//	    byte[] buffer = new byte[1024];
//
//	    int length;
//	    //copy the file content in bytes 
//	    while ((length = in.read(buffer)) > 0){
//	    	   out.write(buffer, 0, length);
//	    }
//	    in.close();
//	    out.close();
//	}
//
//
//}
