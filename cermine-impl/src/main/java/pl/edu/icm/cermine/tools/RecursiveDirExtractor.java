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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Pawel Szostek
 */
public class RecursiveDirExtractor implements DocumentsExtractor {

    protected File directory;

    public RecursiveDirExtractor(String path) {
        directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Source directory for documents doesn't exist: " + path);
        }
    }

    public RecursiveDirExtractor(File directory) {
        this.directory = directory;
    }

    @Override
    public List<BxDocument> getDocuments() throws TransformationException {
        TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
        List<BxDocument> documents = new ArrayList<BxDocument>();

        for (File file : FileUtils.listFiles(directory, new String[]{"xml"}, true)) {
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                List<BxPage> pages = tvReader.read(new InputStreamReader(is));
                BxDocument doc = new BxDocument();
                doc.setFilename(file.getName());
                doc.setPages(pages);
                documents.add(doc);
            } catch (FileNotFoundException ex) {
                throw new TransformationException(ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        throw new TransformationException("Cannot close stream!", ex);
                    }
                }
            }
        }

        return documents;
    }

}
