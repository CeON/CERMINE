package pl.edu.icm.yadda.analysis.bibref;

import pl.edu.icm.yadda.metadata.transformers.MetadataFormat;
import pl.edu.icm.yadda.metadata.transformers.MetadataModel;

/**
 * Identifiers of analysis-related models and formats.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class BibRefTransformers {

	/** BibTeX format. */
	public static final MetadataFormat BibTeX = new MetadataFormat("BibTeX", null);

	/** Metadata model based on the BibTeX format. */
	public static final MetadataModel<BibEntry> BibEntry = new MetadataModel<BibEntry>("BibEntry", BibEntry.class);
}
