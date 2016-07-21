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
package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToFormatWriter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibEntryToBibTeXWriter implements ModelToFormatWriter<BibEntry> {

    @Override
    public String write(BibEntry object, Object... hints) throws TransformationException {
        return object.toBibTeX();
    }

    @Override
    public void write(Writer writer, BibEntry object, Object... hints) throws TransformationException {
        try {
            writer.write(write(object, hints));
        } catch (IOException e) {
            throw new TransformationException(e);
        }
    }

    @Override
    public String writeAll(List<BibEntry> objects, Object... hints) throws TransformationException {
        StringBuilder sb = new StringBuilder();
        for (BibEntry entry : objects) {
            sb.append(entry.toBibTeX());
            sb.append("\n\n");
        }
        return sb.toString().trim();
    }

    @Override
    public void writeAll(Writer writer, List<BibEntry> objects, Object... hints) throws TransformationException {
        try {
            writer.write(writeAll(objects, hints));
        } catch (IOException e) {
            throw new TransformationException(e);
        }
    }
}
