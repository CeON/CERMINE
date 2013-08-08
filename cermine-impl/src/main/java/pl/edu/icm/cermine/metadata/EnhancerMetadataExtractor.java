package pl.edu.icm.cermine.metadata;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.extraction.enhancers.*;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Extracting metadata from labelled zones. 
 *
 * @author krusek
 */
public class EnhancerMetadataExtractor implements MetadataExtractor<Element> {

    private final List<Enhancer> enhancers = Arrays.<Enhancer>asList(
                new HindawiCornerInfoEnhancer(),
                new TitleMergedWithTypeEnhancer(),
                new TitleEnhancer(),
                new AuthorEnhancer(),
                new EmailEnhancer(),
                new DoiEnhancer(),
                new IssnEnhancer(),
                new UrnEnhancer(),
                new DescriptionEnhancer(),
                new KeywordsEnhancer(),
                new ArticleIdEnhancer(),
                new VolumeEnhancer(),
                new IssueEnhancer(),
                new JournalVolumeIssueWithAuthorEnhancer(),
                new JournalVolumeIssueEnhancer(),
                new JournalVolumePagesEnhancer(),
                new JournalEnhancer(),
                new EditorEnhancer(),
                new AffiliationGeometricEnhancer(),
                new ReceivedDateEnhancer(),
                new AcceptedDateEnhancer(),
                new PublishedDateEnhancer(),
                new RevisedDateEnhancer(),
                new RevisedFormDateEnhancer(),
                new PagesEnhancer(),
                new PagesPartialEnhancer(),
                new CiteAsEnhancer(),
                new YearEnhancer()
                );

    public List<Enhancer> getEnhancers() {
        return enhancers;
    }

    public EnhancerMetadataExtractor addEnhancer(Enhancer enhancer) {
        enhancers.add(enhancer);
        return this;
    }

    public EnhancerMetadataExtractor setEnhancers(List<Enhancer> value) {
        enhancers.clear();
        enhancers.addAll(value);
        return this;
    }

    @Override
    public Element extractMetadata(BxDocument document) throws AnalysisException {
        Set<EnhancedField> enhancedFields = EnumSet.noneOf(EnhancedField.class);
        
        Element metadata = new Element("article");
       
        Element front = new Element("front");
        metadata.addContent(front);
        
        Element journalMeta = new Element("journal-meta");
        front.addContent(journalMeta);
        
        Element articleMeta = new Element("article-meta");
        front.addContent(articleMeta);
        
        Element back = new Element("back");
        metadata.addContent(back);
        
        Element refList = new Element("ref-list");
        back.addContent(refList);
        
        for (Enhancer enhancer : enhancers) {
            enhancer.enhanceMetadata(document, metadata, enhancedFields);
        }
        return metadata;
    }

}
