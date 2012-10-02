package pl.edu.icm.coansys.metaextr.bibref.transformers;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import pl.edu.icm.coansys.metaextr.exception.TransformationException;
import pl.edu.icm.coansys.metaextr.bibref.model.BibEntry;
/**
 * Writer of BibEntry model to BibTeX format.
 * @author estocka
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class BibEntryToBibTeXTransformer {

    public String write(List<BibEntry> objects, Object... hints) throws TransformationException {
		StringBuilder sb = new StringBuilder();
                for(BibEntry obj:objects){
                sb.append(obj.toBibTeX());
                sb.append("\n\n");
                }
                return sb.toString();
	}

	public void write(Writer writer, List<BibEntry> objects, Object... hints) throws TransformationException {
		try {
			writer.write(write(objects, hints));
		} catch (IOException e) {
			throw new TransformationException(e);
		}
	}
}
