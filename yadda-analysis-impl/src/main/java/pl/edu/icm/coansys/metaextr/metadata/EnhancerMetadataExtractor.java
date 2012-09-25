package pl.edu.icm.coansys.metaextr.metadata;

import pl.edu.icm.coansys.metaextr.metadata.MetadataExtractor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers.EnhancedField;
import pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers.Enhancer;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;

/**
 * Extracting metadata from labelled zones. 
 *
 * @author krusek
 */
public class EnhancerMetadataExtractor implements MetadataExtractor<Element> {

    private final List<Enhancer> enhancers = new ArrayList<Enhancer>();

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
        //metadata.setAttribute("xsi:noNamespaceSchemaLocation", "http://dtd.nlm.nih.gov/archiving/3.0/xsd/archivearticle3.xsd");
        //metadata.setAttribute("xml:lang", "en");
        
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
