package pl.edu.icm.yadda.analysis.classification.hmm.process.source;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;

/**
 * Files from folder iterator builder. The iterator returns the array of all
 * files in one next() method call.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class AllFilesFromFolderIteratorBuilder implements ISourceIteratorBuilder<File[]> {

    public static final String AUX_PARAM_SOURCE_DIR = "source_dir";

    public static final String AUX_PARAM_RECURSIVE = "recursive";

    private String sourceDir;

    private String[] extensions;

    @Override
    public ISourceIterator<File[]> build(ProcessContext ctx) throws Exception {
        String dirPath = sourceDir;
        if (ctx.containsAuxParam(AUX_PARAM_SOURCE_DIR)) {
            dirPath = (String)ctx.getAuxParam(AUX_PARAM_SOURCE_DIR);
        }

        File sourceFile = new File(dirPath);
        if (!sourceFile.isDirectory()) {
            throw new InvalidParameterException(sourceFile.getAbsolutePath() + " is not a directory!");
        }

        boolean recursive = false;
        if (ctx.containsAuxParam(AUX_PARAM_RECURSIVE)) {
            recursive = true;
        }

        final Collection<File> files = FileUtils.listFiles(sourceFile, extensions, recursive);
        
        return new ISourceIterator<File[]>() {

            boolean returned = false;

            @Override
            public int getEstimatedSize() {
                return 1;
            }

            @Override
            public void clean() {
            }

            @Override
            public boolean hasNext() {
                return !returned;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing is not supported!");
            }

            @Override
            public File[] next() {
                returned = true;
                return files.toArray(new File[]{});
            }

        };
    }

    @Override
    public IIdExtractor<File[]> getIdExtractor() {
        return new IIdExtractor<File[]>() {

            @Override
            public String getId(File[] element) {
                return "files";
            }
        };
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setExtensions(String[] extensions) {
        this.extensions = extensions;
    }

}
