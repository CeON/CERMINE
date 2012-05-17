package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2bwmeta;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.lang.NotImplementedException;

import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;

/**
 * Slight modification of @author dtkaczyk AllFilesFromFolderIteratorBuilder
 *  @author pdendek
 */
public class ZipFileIteratorBuilder implements ISourceIteratorBuilder<File> {

    public static final String AUX_PARAM_SOURCE_FILE = "source_file";

	public static String getAuxParamSourceFile() {
		return AUX_PARAM_SOURCE_FILE;
	}
    
	public ISourceIterator<File> build(Map<String,String> ctx) throws Exception {
        String filePath = null;
        if (ctx.get(AUX_PARAM_SOURCE_FILE) != null) {
            filePath = (String)ctx.get(AUX_PARAM_SOURCE_FILE);
        }

        File sourceFile = new File(filePath);
        if (sourceFile.isDirectory()) {
            throw new InvalidParameterException(sourceFile.getAbsolutePath() + " is not a directory!");
        }
        return new ZipFileIterator(new File(filePath));
    }
    
	@Override
    public ISourceIterator<File> build(ProcessContext ctx) throws Exception {
    	String filePath = null;
        if (ctx.containsAuxParam(AUX_PARAM_SOURCE_FILE)) {
        	filePath = (String)ctx.getAuxParam(AUX_PARAM_SOURCE_FILE);
        }

        File sourceFile = new File(filePath);
        if (!sourceFile.isDirectory()) {
            throw new InvalidParameterException(sourceFile.getAbsolutePath() + " is not a directory!");
        }
        return new ZipFileIterator(new File(filePath));
    }

    
    static class ZipFileIterator implements ISourceIterator<File> {

		private Enumeration<? extends ZipEntry> zipFileElements;
		private ZipFile zipFile;
    	
		public ZipFileIterator(File file) throws ZipException, IOException {
			zipFile = new ZipFile(file);
			zipFileElements =  zipFile.entries();
		}

		@Override
		public boolean hasNext() {
			return zipFileElements.hasMoreElements();
		}

		@Override
		public File next() {
			if(hasNext()){
				ZipEntry entry = zipFileElements.nextElement();
				File f = null;
				try {
					if(entry.getName().indexOf("/")!=-1){
						String entryString = entry.getName();
						File parent = new File("/tmp/"+ entryString.substring(0, entry.getName().lastIndexOf("/")));
						parent.deleteOnExit();
						parent.mkdirs();
					}
					f = new File("/tmp/"+entry.getName());
					f.deleteOnExit();
					copyInputStream(zipFile.getInputStream(entry),
					           new BufferedOutputStream(new FileOutputStream(f)));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return f;
			}
			return null;
		}

		protected static final void copyInputStream(InputStream in, OutputStream out)
		  throws IOException
		  {
		    byte[] buffer = new byte[1024];
		    int len;

		    while((len = in.read(buffer)) >= 0)
		      out.write(buffer, 0, len);

		    in.close();
		    out.close();
		  }
		
		@Override
		public void remove() {
			throw new NotImplementedException();
		}

		@Override
		public int getEstimatedSize() throws UnsupportedOperationException {
			return zipFile.size();
		}

		@Override
		public void clean() {
			//FIXME be silent like a ninja
		}
    }

    @Override
    public IIdExtractor<File> getIdExtractor() {
        return new IIdExtractor<File>() {

            @Override
            public String getId(File element) {
                return "files";
            }
        };
    }
}