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
package pl.edu.icm.cermine.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Pawel Szostek
 */
public class FileExtractor {

    private final InputStream inputStream;

    public FileExtractor(InputStream is) {
        this.inputStream = is;
    }

    public BxDocument getDocument() throws TransformationException {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(inputStream, "UTF-8");
            TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
            return new BxDocument().setPages(reader.read(isr));
        } catch (UnsupportedEncodingException ex) {
            throw new TransformationException("Unsupported encoding!", ex);
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
