/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.edu.icm.yadda.analysis.relations.general2sesame.bwmeta2bigdatasesame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

import pl.edu.icm.yadda.analysis.relations.DisambiguationInterpreter;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;
import pl.edu.icm.yadda.analysis.relations.SesamePersonDirectory;
import pl.edu.icm.yadda.analysis.relations.WeighedDisambiguator;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataDisambiguator;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature1Email;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature2EmailPrefix;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature3CoContribution;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature4CoClassif;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature5CoKeywordPhrase;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature5CoKeywordWords;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature6CoReference;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature7CoISBN;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature7CoISSN;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature9FullInitials;
import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.general2sesame.auxil.BigDataSymetricDisambiguationInterpreter;
import pl.edu.icm.yadda.analysis.relations.general2sesame.auxil.FeatureOccurenceCounter_OnSameSurnames;
//import pl.edu.icm.yadda.analysis.relations.general2sesame.auxil.FeatureOccurenceCounter_OnSamePersons;
import pl.edu.icm.yadda.analysis.relations.general2sesame.auxil.NotNegativeDisambiguatorInterpreter;
import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
import pl.edu.icm.yadda.analysis.relations.manipulations.operations.RenamePrediacate;
import pl.edu.icm.yadda.analysis.relations.pj.clusterizer.PJSingleLinkHAC_Customized;

import com.bigdata.btree.IndexMetadata;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

/**
 * Based on @author kura 's TransferYaddaPackToSesame
 * @author pdendek
 */
public class _2CalculatePersonalitiesOnBigDataSesame {

	private static final Logger log = LoggerFactory.getLogger(_2CalculatePersonalitiesOnBigDataSesame.class);
	
    protected static final String O_STOREDIR = "store-parent";
    protected static final String O_MODE = "mode-of-store";
	protected static final String O_OUTPUTDIR = "output-path";
	protected static final String O_INTERPRETER = "interpret";
	protected static final String O_LOGN3 = "log2n3";
	protected static final String O_ONLY_COUNTER = "only-counter";
	protected static final String O_ONLY_GIVEN_SHARD_NUMBER = "only-given-numbers";
	protected static final String O_MAX_CAPACITY = "max-capacity";
	protected static final String O_MIN_CAPACITY = "min-capacity";
	protected static final String O_ONLY_GIVEN_SIZE = "only-given-size";
	protected static final String O_ONE_SIZE_ONCE = "one-size-once";
	
	
    protected static Options defineOptions() {
		Options options = new Options();
		
		Option oMode = new Option("m", O_MODE, true, "a mode of storage, either SESAME or BDATA");
		oMode.setRequired(true);
		options.addOption(oMode);
		
		Option oStoreDir = new Option("s", O_STOREDIR, true, "store path parent, without a slash in the end of the given path, e.g. \"/tmp/data\"");
		oStoreDir.setRequired(true);
		options.addOption(oStoreDir);
		
		Option oOutputDir = new Option("d", O_OUTPUTDIR, true, "output path parent for CSV files, without a slash in the end of the given path, e.g. \"/tmp/data\"");
		oOutputDir.setRequired(true);
		options.addOption(oOutputDir);
		
		Option oInterpreter = new Option("i", O_INTERPRETER, true, "choose DUMMY for only positive features or SYMETRY for [-1,1] ");
		oInterpreter.setRequired(true);
		options.addOption(oInterpreter);

		Option oLog2N3 = new Option("l", O_LOGN3, true, "YES or NO");
		oLog2N3.setRequired(true);
		options.addOption(oLog2N3);
		
		Option oOnlyCounter = new Option("c", O_ONLY_COUNTER, true, "if this feature is set to any value, only list of contributions with occurence numer will be given");
		oOnlyCounter.setRequired(false);
		options.addOption(oOnlyCounter);
		
		Option oMaxCapacity = new Option("mxc", O_MAX_CAPACITY, true, "if this feature is set only shard with given order numbers will be proceeded. shards are ordered by name.");
		oMaxCapacity.setRequired(false);
		options.addOption(oMaxCapacity);
		
		Option oMinCapacity = new Option("mnc", O_MIN_CAPACITY, true, "if this feature is set only shard with given order numbers will be proceeded. shards are ordered by name.");
		oMinCapacity.setRequired(false);
		options.addOption(oMinCapacity);
		
		Option oOnlyGivenShardNumber = new Option("sn", O_ONLY_GIVEN_SHARD_NUMBER, true, "if this feature is set only shard with given order numbers will be proceeded. shards are ordered by name.");
		oOnlyGivenShardNumber.setRequired(false);
		options.addOption(oOnlyGivenShardNumber);
		
		Option oOnlyGivenSize = new Option("ss", O_ONLY_GIVEN_SIZE, true, "if this feature is set only shard with given order numbers will be proceeded. shards are ordered by name.");
		oOnlyGivenSize.setRequired(false);
		options.addOption(oOnlyGivenSize);
		
		Option oOneSizeOnce = new Option("onesize", O_ONE_SIZE_ONCE, true, "if this feature is set only shard with given order numbers will be proceeded. shards are ordered by name.");
		oOneSizeOnce.setRequired(false);
		options.addOption(oOneSizeOnce);
		
		return options;
	}

    protected static void usage(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		PrintWriter writer = new PrintWriter(System.out);
		helpFormatter.printUsage(writer, 80, _2CalculatePersonalitiesOnBigDataSesame.class.getSimpleName(), options);
		helpFormatter.printOptions(writer, 80, options, 1, 2);
		writer.flush();
	}

    public static void proceedRepo(SailRepository sailRep, CommandLine commandLine) throws Exception{
    	boolean iflog = getLoggerOption(commandLine);
    	logBefore(sailRep, commandLine, iflog);
		renameOldPersonPredicates(sailRep);
		logWithourPersonalities(sailRep, commandLine, iflog);
		
		PersonDirectoryCreator pdc = preparePersonDirecotryCreator(sailRep,commandLine);
		DisambiguationInterpreter di = getInterpreter(sailRep, commandLine);
    	String onlyCounter = commandLine.getOptionValue(O_ONLY_COUNTER);
    	String maxCapacity = commandLine.getOptionValue(O_MAX_CAPACITY);
    	String minCapacity = commandLine.getOptionValue(O_MIN_CAPACITY);
    	String once = commandLine.getOptionValue(O_ONE_SIZE_ONCE);
    	Boolean oneSizeOnce = getOnlyOneSize(once);
    	HashSet<Integer> hs = getOnlyGivenShardNumber(commandLine);
    	HashSet<Integer> hs2 = getOnlyGivenSizeOption(commandLine);
		
    	LoggerFactory.getLogger(_2CalculatePersonalitiesOnBigDataSesame.class).info("Starting transfer");
        pdc.createPersonDirectory(new Object[]{commandLine.getOptionValue(O_OUTPUTDIR), di, onlyCounter,maxCapacity,hs,minCapacity,hs2,oneSizeOnce});
        LoggerFactory.getLogger(_2CalculatePersonalitiesOnBigDataSesame.class).info("End transfer");	 
  	  	logWithPersonalities(sailRep, commandLine, iflog);
    }

	private static void logWithPersonalities(SailRepository sailRep,
			CommandLine commandLine, boolean iflog) {
		if(iflog){
  	  		try{
  	  			_3ExportBigDataSesame2N3.exportRepoToN3(sailRep, commandLine.getOptionValue(O_STOREDIR)+"."+System.nanoTime()+".withPersonalities"+".n3");
  	  		}catch(Exception e){
  	  			System.out.println(e.toString());
  	  		}
  	  	}
	}

	private static boolean getLoggerOption(CommandLine commandLine) {
		boolean iflog;
    	if(commandLine.getOptionValue(O_LOGN3).equals("YES")){
    		iflog=true;
    	}else{
    		iflog = false;
    	}
		return iflog;
	}

	private static void renameOldPersonPredicates(SailRepository sailRep) {
	    //change old RL_IS_PERSON (which is product of THIS method) into RL_IS_PERSON_DB
		SesameManipulator sm = new SesameManipulator(sailRep,new RenamePrediacate());
		HashMap<String, Object> operationParam = new HashMap<String, Object>(); 
		operationParam.put("oldRelationName", RelConstants.RL_IS_PERSON);
		operationParam.put("newRelationName", RelConstants.RL_IS_PERSON_DB);
		sm.execute(operationParam);
	}

	private static void logBefore(SailRepository sailRep,
			CommandLine commandLine, boolean iflog) {
		if(iflog){
    		try{
    			_3ExportBigDataSesame2N3.exportRepoToN3(sailRep, commandLine.getOptionValue(O_STOREDIR)+"."+System.nanoTime()+".beforAnyChanges"+".n3");  
    		}catch (Exception e) {
    			System.out.println(e.toString());
    		}
    	}
	}

	private static void logWithourPersonalities(SailRepository sailRep,
			CommandLine commandLine, boolean iflog) {
		if(iflog){		  
			try{
				_3ExportBigDataSesame2N3.exportRepoToN3(sailRep, commandLine.getOptionValue(O_STOREDIR)+"."+System.nanoTime()+".withoutPersonalities"+".n3");
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}

	private static PersonDirectoryCreator preparePersonDirecotryCreator(SailRepository sailRep,
			CommandLine commandLine) throws Exception {
		SesamePersonDirectory backend = new SesamePersonDirectory();
		backend.setRepository(sailRep);
    	
//    	PersonDirectoryCreator pc = new CachedPersonDirectoryCreator();
		PersonDirectoryCreator pc = new FeatureOccurenceCounter_OnSameSurnames();
    	pc.setBackend(backend);
    	pc.setClusterizer(new PJSingleLinkHAC_Customized());
    	pc.setThreshold(0);
    	List<WeighedDisambiguator> weighedDisambiguators = getDisambiguators(sailRep);
    	pc.setWeighedDisambiguators(weighedDisambiguators);
    	return pc;
	}

	private static List<WeighedDisambiguator> getDisambiguators(
			SailRepository sailRep) {
		List<WeighedDisambiguator> weighedDisambiguators = new LinkedList<WeighedDisambiguator>();
    	BigdataDisambiguator d;
    	
    	d = new BigdataFeature1Email(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(1,d));
    	
    	d = new BigdataFeature2EmailPrefix(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(1,d));
    	
		d = new BigdataFeature3CoContribution(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(1.7*Math.pow(10, -5),d));
    	
    	d = new BigdataFeature4CoClassif(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(0.99,d));
    	
    	d = new BigdataFeature5CoKeywordPhrase(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(0.99,d));
    	
    	d = new BigdataFeature5CoKeywordWords(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(3.7*Math.pow(10, -5),d));
    	
    	d = new BigdataFeature6CoReference(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(3.7*Math.pow(10, -5),d));
    	
    	d = new BigdataFeature7CoISSN(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(3.8*Math.pow(10, -6),d));

    	d = new BigdataFeature7CoISBN(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(3.8*Math.pow(10, -6),d));
    	
//    	BigdataFeature8Year d1 = new BigdataFeature8Year(); 
//    	d1.setRepository(sailRep);
//    	weighedDisambiguators.add(new WeighedDisambiguator(3.8*Math.pow(10, -6),d1));

    	d = new BigdataFeature9FullInitials(); 
    	d.setRepository(sailRep);
    	weighedDisambiguators.add(new WeighedDisambiguator(3.8*Math.pow(10, -6),d));
    	
		return weighedDisambiguators;
	}

	private static DisambiguationInterpreter getInterpreter(
			SailRepository sailRep, CommandLine commandLine) {
		DisambiguationInterpreter di = null;
    	if(commandLine.getOptionValue(O_INTERPRETER).equals("DUMMY")){
    		di = null;
    	}else if(commandLine.getOptionValue(O_INTERPRETER).equals("NOTNEGATIVE")){
    		di = new NotNegativeDisambiguatorInterpreter();
    	}else if(commandLine.getOptionValue(O_INTERPRETER).equals("SYMETRY")){
    		di = new BigDataSymetricDisambiguationInterpreter();
    	}
		return di;
	}

	private static Boolean getOnlyOneSize(String once) {
		Boolean oneSizeOnce = false;
    	if(once == null);
    	else{
    		if("yes".equalsIgnoreCase(once)) oneSizeOnce = true;
    		else oneSizeOnce = false;
    	}
		return oneSizeOnce;
	}

	private static HashSet<Integer> getOnlyGivenShardNumber(
			CommandLine commandLine) throws Exception {
		HashSet<Integer> hs;
		String[] onlyGivenSNumbers = commandLine.getOptionValues(O_ONLY_GIVEN_SHARD_NUMBER);
    	if(onlyGivenSNumbers==null)hs=null;
    	else{
    		hs = new HashSet<Integer>();
        	for(String s : onlyGivenSNumbers){
        		try{
        			hs.add(Integer.parseInt(s));
        		}catch(Exception e){
        			log.debug(e.toString());
        			throw e;
        		}
        	}
    	}
		return hs;
	}

	private static HashSet<Integer> getOnlyGivenSizeOption(
			CommandLine commandLine) throws Exception {
		HashSet<Integer> hs2;
		String[] onlyGivenSSize = commandLine.getOptionValues(O_ONLY_GIVEN_SIZE);
    	if(onlyGivenSSize==null)hs2=null;
    	else{
    		hs2 = new HashSet<Integer>();
        	for(String s : onlyGivenSSize){
        		try{
        			hs2.add(Integer.parseInt(s));
        		}catch(Exception e){
        			log.debug(e.toString());
        			throw e;
        		}
        	}
    	}
		return hs2;
	}
    
	public static void main(String[]args) throws NumberFormatException, Exception {
    	Options options = defineOptions();
    	CommandLineParser parser = new GnuParser();
		try {
			System.setProperty("org.openrdf.repository.debug","true");
			CommandLine commandLine = parser.parse(options, args);

			SailRepository sailRep = null;
			String mode = commandLine.getOptionValue(O_MODE);
			if(mode.equals("SESAME")){
				sailRep=new SailRepository(new NativeStore(new File(commandLine.getOptionValue(O_STOREDIR))));
			}else if(mode.equals("BDATA")||mode.equals("BIGDATA")){
				sailRep=createRWStoreSail(commandLine);
			}
			sailRep.initialize();
			proceedRepo(sailRep,commandLine);
			sailRep.shutDown();
		} catch (ParseException e) {
			usage(options);
		}
    }

	private static SailRepository createRWStoreSail(CommandLine commandLine) throws IOException {
		Properties properties = new Properties();
	    properties.setProperty(BigdataSail.Options.BUFFER_MODE, "DiskRW");
	    properties.setProperty(BigdataSail.Options.BUFFER_CAPACITY, "10000");
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
	    	File dstFile = new File(commandLine.getOptionValue(O_STOREDIR)+"."+System.nanoTime()+".withPersonalities"+".jnl");
			copyFile(new File(commandLine.getOptionValue(O_STOREDIR)),dstFile);
	        System.out.println("journalFile="+dstFile.getAbsolutePath());
	        properties.setProperty(BigdataSail.Options.FILE, dstFile
	                .getAbsolutePath());
	    }
	    // instantiate a sail
	    BigdataSail sail = new BigdataSail(properties);
	    SailRepository repo = new BigdataSailRepository(sail);
		return repo;
	}

	private static void copyFile(File srcFile, File dstFile) throws IOException {
		//if file, then copy it
		//Use bytes stream to support all file types
		InputStream in = new FileInputStream(srcFile);
	    OutputStream out = new FileOutputStream(dstFile); 

	    byte[] buffer = new byte[1024];

	    int length;
	    //copy the file content in bytes 
	    while ((length = in.read(buffer)) > 0){
	    	   out.write(buffer, 0, length);
	    }
	    in.close();
	    out.close();
	}


}
