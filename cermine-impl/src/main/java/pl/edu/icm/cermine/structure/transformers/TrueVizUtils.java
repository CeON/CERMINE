package pl.edu.icm.cermine.structure.transformers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Contains utility methods/formats for manipulating TrueViz/Marg documents.
 *
 * @author krusek
 */
public final class TrueVizUtils {

    private static final String TRUEVIZ_DTD = "pl/edu/icm/cermine/structure/imports/Trueviz.dtd";

    /**
     * Returns new document builder for creating/parsing TrueViz/Marg documents.
     */
    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return newDocumentBuilder(false);
    }

    /**
     * Returns new document builder for creating/parsing TrueViz/Marg documents.
     * @param validating true if the builder produced will validate documents
     * as they are parsed; false otherwise
     */
    public static DocumentBuilder newDocumentBuilder(boolean validating) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new EntityResolver() {

            @Override
            public InputSource resolveEntity(String publicID, String systemID) throws SAXException {
                if (systemID != null && systemID.endsWith("/Trueviz.dtd")) {
                    return new InputSource(MargToTextrImporter.class.getClassLoader().getResourceAsStream(TRUEVIZ_DTD));
                }
                // If no match, returning null makes process continue normally
                return null;
            }
        });
        return builder;
    }

    private TrueVizUtils() {}
    
}
