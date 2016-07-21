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
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Pawel Szostek
 */
public class DirExtractor implements DocumentsExtractor {

    protected File directory;

    public DirExtractor(String path) {
        directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Source directory for documents doesn't exist: " + path);
        }
    }

    public DirExtractor(File directory) {
        this.directory = directory;
    }

    @Override
    public List<BxDocument> getDocuments() throws TransformationException {
        String dirPath = directory.getPath();
        TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
        List<BxDocument> documents = new ArrayList<BxDocument>();

        if (!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }
        for (String filename : directory.list()) {
            if (!new File(dirPath + filename).isFile()) {
                continue;
            }
            if (filename.endsWith("xml")) {
                InputStream is = null;
                try {
                    is = new FileInputStream(dirPath + filename);
                    List<BxPage> pages = tvReader.read(new InputStreamReader(is));
                    BxDocument newDoc = new BxDocument();
                    for (BxPage page : pages) {
                        page.setParent(newDoc);
                    }
                    newDoc.setFilename(filename);
                    newDoc.setPages(pages);
                    documents.add(newDoc);
                } catch (IllegalStateException ex) {
                    System.err.println(ex.getMessage());
                    System.err.println(dirPath + filename);
                    throw ex;
                } catch (FileNotFoundException ex) {
                    throw new TransformationException("File not found!", ex);
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
        }
        return documents;
    }
}
