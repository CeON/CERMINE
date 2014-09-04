package pl.edu.icm.cermine.metadata.affiliations.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationTokenizedString;
import pl.edu.icm.cermine.parsing.tools.GrmmUtils;

// NOTE: For testing purposes only
// The ACRF model is prepared by some Python scripts. We use this class to check whether
// the Java implementation exports raw strings to the GRMM format in the exactly same way.
public class AffiliationTrainingDataExporter {
	
	private static final String DEFAULT_INPUT = "/home/bartek/Projects/affiliations/javatests/affs.txt";
	private static final String DEFAULT_OUTPUT = "/home/bartek/Projects/affiliations/javatests/features-actual.txt";
	private static final int DEFAULT_NEIGHBOR_THRESHOLD = 1;
	
    public static void main(String[] args) throws AnalysisException, ParseException {
        Options options = new Options();
        options.addOption("input", true, "input file (raw strings)");
        options.addOption("output", true, "output file (GRMM format)");
        options.addOption("neighbor", true, "neighbor influence threshold");
        
        CommandLineParser clParser = new GnuParser();
        CommandLine line = clParser.parse(options, args);
        String inputFileName = line.getOptionValue("input");
        String outputFileName = line.getOptionValue("output");
        String neighborThresholdString = line.getOptionValue("neighbor");
        int neighborThreshold = DEFAULT_NEIGHBOR_THRESHOLD;
        
        if (inputFileName == null) {
        	inputFileName = DEFAULT_INPUT;
        }
        
        if (outputFileName == null) {
        	outputFileName = DEFAULT_OUTPUT;
        }
        
        if (neighborThresholdString != null) {
        	neighborThreshold = Integer.parseInt(neighborThresholdString);
        }
        
        File file = new File(inputFileName);
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;
            writer = new PrintWriter(outputFileName, "UTF-8");
            while ((text = reader.readLine()) != null) {
            	AffiliationTokenizedString affiliation = new AffiliationTokenizedString(text);
            	affiliation.calculateFeatures();
            	writer.write(GrmmUtils.toGrmmInput(affiliation.getTokens(), neighborThreshold));
            	writer.write("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                	writer.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
