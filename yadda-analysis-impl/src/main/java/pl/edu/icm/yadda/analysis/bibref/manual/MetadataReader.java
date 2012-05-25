package pl.edu.icm.yadda.analysis.bibref.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import pl.edu.icm.yadda.bwmeta.RelationsToElements;
import pl.edu.icm.yadda.bwmeta.model.YAncestor;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YCurrent;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YId;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YStructure;
import pl.edu.icm.yadda.imports.transformers.NlmToYTransformer;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.tools.bibref.model.DocSimpleMetadata;
import pl.edu.icm.yadda.tools.bibref.model.SimpleMetadata;

/**
 * Reads document metadata from an NLM file.
 * 
 * Mostly copied from eu.eudml.enhancement.bibref.MetadataBibReferenceMatcher.
 * 
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 *
 */
public class MetadataReader {
    private String scheme;
    
    public MetadataReader(String scheme) {
        this.scheme = scheme;
    }
    
    /**
     * Extracts all important metadata of the document and its references
     * from its content.
     *
     * @param nlm the document in NLM format
     * @return an object storing all needed metadata
     * @throws TransformationException
     * @throws FileNotFoundException 
     */
    public DocSimpleMetadata readFromNLMFile(String nlm) throws TransformationException {
        NlmToYTransformer transformer = new NlmToYTransformer();
        return yExportablesToMetadata(transformer.read(nlm));    
    }
    
    
    /**
     * Extracts all important metadata of the document and its references
     * from its content.
     *
     * @param nlm the file containing a document in NLM format
     * @return an object storing all needed metadata
     * @throws TransformationException
     * @throws FileNotFoundException 
     */
    public DocSimpleMetadata readFromNLMFile(File nlm) throws TransformationException, FileNotFoundException {
        NlmToYTransformer transformer = new NlmToYTransformer();
        return yExportablesToMetadata(transformer.read(new FileReader(nlm)));
    }
    
    /**
     * Returns DocSimpleMetadata based on passed list of YExportables.
     * 
     * @param yExportables a list of YExportables
     * @return an object storing all needed metadata
     */
    private DocSimpleMetadata yExportablesToMetadata(List<YExportable> yExportables) {
        DocSimpleMetadata docMetadata = new DocSimpleMetadata();
        for (YExportable yExportable : yExportables) {
            if (yExportable instanceof YElement) {
                YElement yElement = (YElement) yExportable;
                // Here's the change (different schema name)
                docMetadata.setDocId(yElement.getId(scheme));
                docMetadata.setPosition(0);
                YCurrent current = yElement.getStructure(YConstants.EXT_HIERARCHY_JOURNAL).getCurrent();
                if (current.getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_ARTICLE)) {
                    fillDocumentMetadata(yElement, docMetadata);
                    List<YElement> citationElements = RelationsToElements.convert(yElement);
                    int position = 1;
                    for (YElement citationElement : citationElements) {
                        SimpleMetadata refMetadata = new SimpleMetadata();
                        fillDocumentMetadata(citationElement, refMetadata);
                        refMetadata.setDocId(yElement.getId(YConstants.EXT_SCHEMA_EUDML));
                        refMetadata.setPosition(position);
                        docMetadata.addReference(refMetadata);
                        position++;
                    }
                    return docMetadata;
                }
            }
        }
        return null;
    }
    /**
     * Fills the SimpleMetadata object with important metadata
     * from YElement object.
     *
     * @param yElement
     * @param docMetadata
     */
    private void fillDocumentMetadata(YElement yElement, SimpleMetadata docMetadata) {
        List<YContributor> yContributors = yElement.getContributors();
        for (YContributor yContributor : yContributors) {
            YName surname = yContributor.getOneName(YConstants.NM_SURNAME);
            YName givennames = yContributor.getOneName(YConstants.NM_FORENAMES);

            String surnameText = surname == null ? null : surname.getText();
            String givennamesText = givennames == null ? null : givennames.getText();

            if (surnameText != null || givennamesText != null) {
                docMetadata.addAuthor(surnameText, givennamesText);
            }
        }

        for (YId yId : yElement.getIds()) {
            docMetadata.addId(yId.getScheme() + "###" + yId.getValue());
        }

        YName title = yElement.getOneName(YConstants.NM_CANONICAL);
        if (title != null) {
            docMetadata.setTitle(title.getText());
        }

        YStructure yStructure = yElement.getStructure(YConstants.EXT_HIERARCHY_JOURNAL);
        if (yStructure != null) {
            YAncestor journalAncestor = yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL);
            if (journalAncestor != null) {
                YName journalName = journalAncestor.getOneName(YConstants.NM_CANONICAL);
                docMetadata.setJournal(journalName == null ? null : journalName.getText());
            }
            YAncestor volumeAncestor = yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME);
            if (volumeAncestor != null) {
                YName volumeName = volumeAncestor.getOneName(YConstants.NM_CANONICAL);
                docMetadata.setVolume(volumeName == null ? null : volumeName.getText());
            }
            YAncestor issueAncestor = yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE);
            if (issueAncestor != null) {
                YName issueName = issueAncestor.getOneName(YConstants.NM_CANONICAL);
                docMetadata.setIssue(issueName == null || issueName.getText().equals("[unknown]")
                        ? null : issueName.getText());
            }
        }
        if (yElement.getDate(YConstants.DT_PUBLISHED) != null) {
            docMetadata.setYear(Integer.toString(yElement.getDate(YConstants.DT_PUBLISHED).getYear()));
        }
    }
}
