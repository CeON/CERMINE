package pl.edu.icm.yadda.analysis.bibref;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import pl.edu.icm.yadda.metadata.transformers.IMetadataWriter;
import pl.edu.icm.yadda.metadata.transformers.MetadataFormat;
import pl.edu.icm.yadda.metadata.transformers.MetadataModel;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.metadata.transformers.AbstractMetadataWriter;
/**
 * Writer of BibEntry model to BibTeX format.
 * @author estocka
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class BibEntryToBibTeXTransformer extends AbstractMetadataWriter implements IMetadataWriter<BibEntry> {

	@Override
	public MetadataModel<BibEntry> getSourceModel() {
		return BibRefTransformers.BibEntry;
	}

	@Override
	public MetadataFormat getTargetFormat() {
		return BibRefTransformers.BibTeX;
	}

	@Override
	public String write(List<BibEntry> objects, Object... hints) throws TransformationException {
		StringBuilder sb = new StringBuilder();
                for(BibEntry obj:objects){
                sb.append(obj.toBibTeX());
                sb.append("\n\n");
                }
                return sb.toString();
	}

	@Override
	public void write(Writer writer, List<BibEntry> objects, Object... hints) throws TransformationException {
		try {
			writer.write(write(objects, hints));
		} catch (IOException e) {
			throw new TransformationException(e);
		}
	}
}
