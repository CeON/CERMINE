package pl.edu.icm.yadda.analysis.textr.transformers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.edu.icm.yadda.metadata.transformers.MetadataFormat;

/**
 * Contains utility methods/formats for manipulating TrueViz/Marg documents.
 *
 * @author krusek
 */
public class TrueVizUtils {

    public static final MetadataFormat MARG_FORMAT = new MetadataFormat("Marg", "1.0");
    public static final MetadataFormat TRUEVIZ_FORMAT = new MetadataFormat("TrueViz", "1.0");

    private static final String TRUEVIZ_DTD = "pl/edu/icm/yadda/analysis/textr/imports/Trueviz.dtd";

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
}
