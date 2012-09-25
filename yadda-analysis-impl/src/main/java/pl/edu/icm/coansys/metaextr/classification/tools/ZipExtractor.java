package pl.edu.icm.coansys.metaextr.classification.tools;

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
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.transformers.TrueVizToBxDocumentReader;

public class ZipExtractor implements DocumentsExtractor {
	protected ZipFile zipFile;

	public ZipExtractor(String path) throws ZipException, IOException,
			URISyntaxException {
		URL url = path.getClass().getResource(path);
		URI uri = url.toURI();
		File file = new File(uri);
		this.zipFile = new ZipFile(file);
	}

	public ZipExtractor(ZipFile zipFile) {
		this.zipFile = zipFile;
	}

	public List<BxDocument> getDocuments()
			throws ZipException, IOException, URISyntaxException,
			ParserConfigurationException, SAXException, AnalysisException,
			TransformationException {
		List<BxDocument> documents = new ArrayList<BxDocument>();

		TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			if (zipEntry.getName().endsWith("xml")) {
				List<BxPage> pages = tvReader.read(new InputStreamReader(
						zipFile.getInputStream(zipEntry)));
				BxDocument newDoc = new BxDocument();
				for(BxPage page: pages)
					page.setContext(newDoc);
				newDoc.setFilename(zipEntry.getName());
				newDoc.setPages(pages);
				
				documents.add(newDoc);
			}
		}
		return documents;
	}
}
