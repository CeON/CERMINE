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

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.XMLTools;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ElementToBibEntryConverter implements ModelToModelConverter<Element, BibEntry> {

    @Override
    public BibEntry convert(Element source, Object... hints) throws TransformationException {
        BibEntry bibEntry = new BibEntry(BibEntry.TYPE_ARTICLE);
        String text = XMLTools.getTextContent(source);
        bibEntry.setText(text);
        
        return bibEntry;
    }

    @Override
    public List<BibEntry> convertAll(List<Element> source, Object... hints) throws TransformationException {
        List<BibEntry> entries = new ArrayList<BibEntry>(source.size());
        for (Element element : source) {
            entries.add(convert(element, hints));
        }
        return entries;
    }
    
}
