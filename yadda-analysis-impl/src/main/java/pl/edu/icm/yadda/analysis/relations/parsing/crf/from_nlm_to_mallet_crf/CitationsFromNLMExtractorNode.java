package pl.edu.icm.yadda.analysis.relations.parsing.crf.from_nlm_to_mallet_crf;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Original:
 * Citation from NLM files extractor node.
 *
 * Changes:
 * Additional methods to transform output of CitationsFromNLMExtractorNode to crfadapter input  
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @author Piotr Dendek (p.dendek@icm.edu.pl)
 */
public class CitationsFromNLMExtractorNode implements IProcessingNode<File[], Citation[]> {

	private static String localization = "/home/pdendek/dane_icm/parsowanie_cytowan_crfem/metadata-extraction/refs-parsing/refs-parsing-train";
	
	/**
	 * @author dtkaczyk
	 */
    @Override
	public Citation[] process(File[] input, ProcessContext ctx) throws Exception {
        List<Citation> citations = new ArrayList<Citation>();
        for (File file : input) {
            citations.addAll(NlmCitationExtractor.extractCitations(new InputSource(new FileInputStream(file))));
        }
        return (Citation[]) citations.toArray();
    }

    public Citation[] exec(File[] input) throws Exception {
    	Citation[] cit = process(input,null);
		return cit;
    }
    
    public static void main(String args[]){
    	new File(localization);
    }
}
