package pl.edu.icm.cermine.service;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * Abstraction of the service used to extract metadata from submitted files.
 * Service is responsible for extraction and proper scheduling and resource 
 * allocation.
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */

public interface CermineExtractorService {
    /**
     * Method to extract metadata from the given pdf file. File is represented 
     * as a InputStream, which will be passed to the extractor itself.
     * 
     * @param ii
     * @return result of the extraction, including basic request stats.
     * @throws AnalysisException 
     */
    ExtractionResult extractNLM(InputStream ii)throws AnalysisException;
    
    long initExtractionTask(byte[] pdf, String fileName);
    
}
