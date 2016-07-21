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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Pawel Szostek
 */
public class ZipExtractor implements DocumentsExtractor {

    protected ZipFile zipFile;

    public ZipExtractor(String path) throws IOException, URISyntaxException {
        URL url = path.getClass().getResource(path);
        URI uri = url.toURI();
        File file = new File(uri);
        this.zipFile = new ZipFile(file);
    }

    public ZipExtractor(File file) throws IOException {
        this.zipFile = new ZipFile(file);
    }

    public ZipExtractor(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public List<BxDocument> getDocuments() throws TransformationException {
        List<BxDocument> documents = new ArrayList<BxDocument>();

        TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (zipEntry.getName().endsWith("xml")) {
                try {
                    List<BxPage> pages = tvReader.read(new InputStreamReader(zipFile.getInputStream(zipEntry)));
                    BxDocument newDoc = new BxDocument();
                    for (BxPage page : pages) {
                        page.setParent(newDoc);
                    }
                    newDoc.setFilename(zipEntry.getName());
                    newDoc.setPages(pages);
                    documents.add(newDoc);
                } catch (IOException ex) {
                    throw new TransformationException("Cannot read file!", ex);
                }
            }
        }
        return documents;
    }
}
