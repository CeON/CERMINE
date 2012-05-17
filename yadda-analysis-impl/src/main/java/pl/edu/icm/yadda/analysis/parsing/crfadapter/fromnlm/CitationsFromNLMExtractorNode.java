package pl.edu.icm.yadda.analysis.parsing.crfadapter.fromnlm;

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
 * Citation from NLM files extractor node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CitationsFromNLMExtractorNode implements IProcessingNode<File[], Citation[]> {

    @Override
    public Citation[] process(File[] input, ProcessContext ctx) throws Exception {
        List<Citation> citations = new ArrayList<Citation>();
        for (File file : input) {
            citations.addAll(NlmCitationExtractor.extractCitations(new InputSource(new FileInputStream(file))));
        }
        return citations.toArray(new Citation[] {});
    }

}
