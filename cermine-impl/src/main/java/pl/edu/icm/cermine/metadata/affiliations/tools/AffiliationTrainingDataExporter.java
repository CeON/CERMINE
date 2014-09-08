package pl.edu.icm.cermine.metadata.affiliations.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jdom.JDOMException;
import org.xml.sax.InputSource;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.tools.GrmmUtils;

/**
 * Class for converting affiliation features to GRMM file format.
 * It reads affiliations from an XML or text file, extracts their features and produces
 * a valid input for ACRF model trainer. If the input file is an XML, it uses the tags as labels.
 * 
 * 
 * The ACRF model is prepared by some Python scripts. We use this class to check whether
 * the Java implementation exports raw .txt files or tagged XML to the GRMM format in the
 * exactly same way as the Python code.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationTrainingDataExporter {

	private static final AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	private static final AffiliationFeatureExtractor featureExtractor = new AffiliationFeatureExtractor();

	private static final String DEFAULT_INPUT = "/home/bartek/Projects/affiliations/javatests/affs-real-like.xml";
	private static final String DEFAULT_OUTPUT = "/home/bartek/Projects/affiliations/javatests/features-actual-xml.txt";
	private static final int DEFAULT_NEIGHBOR_THRESHOLD = 1;
	private static final String DEFAULT_INPUT_TYPE = "xml";
	
	private static void writeAffiliation(DocumentAffiliation affiliation, PrintWriter writer,
			int neighborThreshold) {
		featureExtractor.calculateFeatures(affiliation);
		writer.write(GrmmUtils.toGrmmInput(affiliation.getTokens(),
				neighborThreshold));
		writer.write("\n");
	}
	
	private static void addMockAffiliation(PrintWriter writer) {
		writer.write("TEXT ----\n\n");
	}

	public static void main(String[] args) throws AnalysisException,
			ParseException, JDOMException {
		Options options = new Options();
		options.addOption("input", true, "input file (raw strings)");
		options.addOption("output", true, "output file (GRMM format)");
		options.addOption("neighbor", true, "neighbor influence threshold");
		options.addOption("input_type", true, "xml or txt");

		CommandLineParser clParser = new GnuParser();
		CommandLine line = clParser.parse(options, args);
		String inputFileName = line.getOptionValue("input");
		String outputFileName = line.getOptionValue("output");
		String neighborThresholdString = line.getOptionValue("neighbor");
		String inputType = line.getOptionValue("input_type");
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

		if (inputType == null) {
			inputType = DEFAULT_INPUT_TYPE;
		}

		File file = new File(inputFileName);
		BufferedReader reader = null;
		PrintWriter writer = null;
		NLMAffiliationExtractor nlmExtractor = new NLMAffiliationExtractor();

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			writer = new PrintWriter(outputFileName, "UTF-8");

			if (inputType.equals("txt")) {
				while ((text = reader.readLine()) != null) {
					DocumentAffiliation affiliation = new DocumentAffiliation(
							"", text);
					affiliation.setTokens(tokenizer.tokenize(affiliation
							.getRawText()));
					writeAffiliation(affiliation, writer, neighborThreshold);
				}
			} else if (inputType.equals("xml")) {
				FileInputStream is = new FileInputStream(file);
				InputSource source = new InputSource(is);
				List<DocumentAffiliation> affiliations = nlmExtractor.extractStrings(source);
				for (DocumentAffiliation affiliation : affiliations) {
					writeAffiliation(affiliation, writer, neighborThreshold);
				}
			} else {
				throw new ParseException("Unknown input type: " + inputType);
			}
			addMockAffiliation(writer);
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
