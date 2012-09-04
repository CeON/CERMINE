package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Classifying zones as: METADATA, BODY, REFERENCES, OTHER. 
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public class SVMInitialZoneClassifier implements ZoneClassifier {

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}