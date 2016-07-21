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

package pl.edu.icm.cermine.evaluation.transformers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.metadata.transformers.NLMToMetadataConverter;
import pl.edu.icm.cermine.model.Document;
import pl.edu.icm.cermine.tools.transformers.FormatToModelReader;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class NLMToDocumentReader implements FormatToModelReader<Document> {
    
    @Override
    public Document read(String string, Object... hints) throws TransformationException {
        return read(new StringReader(string), hints);
    }

    @Override
    public Document read(Reader reader, Object... hints) throws TransformationException {
        try {
            ModelToModelConverter<Element, DocumentMetadata> metaConverter = new NLMToMetadataConverter();
            ModelToModelConverter<Element, ContentStructure> structConverter = new NLMToContentConverter();
            ModelToModelConverter<Element, BibEntry> refConverter = new ElementToBibEntryConverter();

    
            Element root = getRoot(reader);
            Document document = new Document();
            document.setMetadata(metaConverter.convert(root.getChild("front")));
            document.setContent(structConverter.convert(root.getChild("body")));
            if (root.getChild("back") != null && root.getChild("back").getChild("ref-list") != null) {                
                document.setReferences(refConverter.convertAll(root.getChild("back")
                    .getChild("ref-list").getChildren("ref")));
            } else {
                document.setReferences(new ArrayList<BibEntry>());
            }
            return document;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        } catch (IOException ex) {
            throw new TransformationException(ex);
        }
    }
   
    private Element getRoot(Reader reader) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        org.jdom.Document dom = builder.build(reader);
        return dom.getRootElement();
    }

    @Override
    public List<Document> readAll(String string, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Document> readAll(Reader reader, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
