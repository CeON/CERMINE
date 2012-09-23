package pl.edu.icm.yadda.analysis.classification.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.HMMZoneClassificationDemo;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

public class DirExtractor implements DocumentsExtractor
{
	protected File directory;

	public DirExtractor(String path)
	{
		this.directory = new File(path);
	}
	
	public DirExtractor(File directory)
	{
		this.directory = directory;
	}
	
	public List<BxDocument> getDocuments() throws TransformationException, FileNotFoundException
	{
		String dirPath = directory.getPath();
		TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
		List<BxDocument> documents = new ArrayList<BxDocument>();
		
		if(!dirPath.endsWith(File.separator)) {
			dirPath += File.separator;
		}
        for (String filename : directory.list()) {
            if (!new File(dirPath + filename).isFile()) {
                continue;
            }
    		if (filename.endsWith("xml")) {
    			InputStream is = new FileInputStream(dirPath + filename);
    			List<BxPage> pages = tvReader.read(new InputStreamReader(is));
    			BxDocument newDoc = new BxDocument();
				for(BxPage page: pages)
					page.setContext(newDoc);
    			
    			newDoc.setFilename(filename);
    			newDoc.setPages(pages);
    			
    			documents.add(newDoc);
    		}
    	}
		return documents;
	}
}
