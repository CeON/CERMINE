/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

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
 * @author Krzysztof Rusek
 */
public final class TrueVizUtils {

    private static final String TRUEVIZ_DTD = "pl/edu/icm/cermine/structure/imports/Trueviz.dtd";

    /**
     * Returns new document builder for creating/parsing TrueViz/Marg documents.
     * @return DocumentBuilder
     * @throws ParserConfigurationException ParserConfigurationException
     */
    public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        return newDocumentBuilder(false);
    }

    /**
     * Returns new document builder for creating/parsing TrueViz/Marg documents.
     * @param validating true if the builder produced will validate documents
     * as they are parsed; false otherwise
     * @return DocumentBuilder
     * @throws ParserConfigurationException ParserConfigurationException
     */
    public static DocumentBuilder newDocumentBuilder(boolean validating) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new EntityResolver() {

            @Override
            public InputSource resolveEntity(String publicID, String systemID) throws SAXException {
                if (systemID != null && systemID.endsWith("/Trueviz.dtd")) {
                    return new InputSource(TrueVizUtils.class.getClassLoader().getResourceAsStream(TRUEVIZ_DTD));
                }
                // If no match, returning null makes process continue normally
                return null;
            }
        });
        return builder;
    }

    private TrueVizUtils() {}
    
}
