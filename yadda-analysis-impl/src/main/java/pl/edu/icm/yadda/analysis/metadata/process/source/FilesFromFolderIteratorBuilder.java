package pl.edu.icm.yadda.analysis.metadata.process.source;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;
import pl.edu.icm.yadda.process.model.EnrichedPayload;

/**
 * Files from folder iterator builder.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class FilesFromFolderIteratorBuilder implements ISourceIteratorBuilder<EnrichedPayload<File>> {

    public static final String AUX_PARAM_SOURCE_DIR = "source_dir";

    public static final String AUX_PARAM_RECURSIVE = "recursive";

    private String sourceDir;

    private String[] extensions;

    @Override
    public ISourceIterator<EnrichedPayload<File>> build(ProcessContext ctx) throws Exception {
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
            recursive = false;
        }

        final int size = FileUtils.listFiles(sourceFile, extensions, recursive).size();
        final Iterator<File> iterator = FileUtils.iterateFiles(sourceFile, extensions, recursive);

        return new ISourceIterator<EnrichedPayload<File>>() {

            @Override
            public int getEstimatedSize() throws UnsupportedOperationException {
                return size;
            }

            @Override
            public void clean() {
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing is not supported!");
            }

            @Override
            public EnrichedPayload<File> next() {
                return new EnrichedPayload(iterator.next(), null, null);
            }
        };
    }

    @Override
    public IIdExtractor<EnrichedPayload<File>> getIdExtractor() {
        return new IIdExtractor<EnrichedPayload<File>>() {

            @Override
            public String getId(EnrichedPayload<File> element) {
                return FilenameUtils.getBaseName(element.getObject().getName());
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
