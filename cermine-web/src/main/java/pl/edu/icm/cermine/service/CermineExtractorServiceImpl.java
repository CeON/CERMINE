/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

import java.io.InputStream;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.icm.cermine.PdfNLMContentExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
@Component
public class CermineExtractorServiceImpl implements CermineExtractorService {

    Logger log = LoggerFactory.getLogger(CermineExtractorServiceImpl.class);

    @Override
    public String extractNLM(InputStream is) throws AnalysisException {
        log.debug("Invoked extract on some input stream...");
        PdfNLMContentExtractor extractor = new PdfNLMContentExtractor();
        log.debug("Before extract...");
        Element result = extractor.extractContent(is);
        log.debug("Extract OK.");
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        String res = outputter.outputString(result);
        log.debug("Returning xml:\n {}", res);
        return res;
    }
}
