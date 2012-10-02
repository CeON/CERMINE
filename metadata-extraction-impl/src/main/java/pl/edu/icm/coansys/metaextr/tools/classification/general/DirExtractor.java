package pl.edu.icm.coansys.metaextr.tools.classification.general;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.transformers.TrueVizToBxDocumentReader;

public class DirExtractor implements DocumentsExtractor
{
	protected File directory;

	public DirExtractor(String path)
	{
		this.directory = new File(path);
		if(!directory.exists() || !directory.isDirectory()) {
			throw new RuntimeException("Source directory for documents doesn't exist: " + path);
		}
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
					page.setParent(newDoc);
    			
    			newDoc.setFilename(filename);
    			newDoc.setPages(pages);
    			
    			documents.add(newDoc);
    		}
    	}
		return documents;
	}
}
