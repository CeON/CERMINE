package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToFormatWriter;

/**
 * @author Dominika Tkaczyk
 */
public class BibEntryToBibTeXWriter implements ModelToFormatWriter<BibEntry> {

    @Override
    public String write(BibEntry object, Object... hints) throws TransformationException {
        return object.toBibTeX();        
    }

    @Override
    public void write(Writer writer, BibEntry object, Object... hints) throws TransformationException {
        try {
			writer.write(write(object, hints));
		} catch (IOException e) {
			throw new TransformationException(e);
		}
    }

    @Override
    public String writeAll(List<BibEntry> objects, Object... hints) throws TransformationException {
        StringBuilder sb = new StringBuilder();
        for (BibEntry entry: objects) {
            sb.append(entry.toBibTeX());
            sb.append("\n\n");
        }
        return sb.toString().trim();
    }

    @Override
    public void writeAll(Writer writer, List<BibEntry> objects, Object... hints) throws TransformationException {
        try {
			writer.write(writeAll(objects, hints));
		} catch (IOException e) {
			throw new TransformationException(e);
		}
    }
}
