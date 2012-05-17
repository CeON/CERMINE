package pl.edu.icm.yadda.analysis.textr.transformers;

import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.metadata.transformers.MetadataModel;

/**
 * Identifiers of analysis-related models and formats.
 * 
 * @author krusek
 */
public class BxDocumentTransformers {

    public static final MetadataModel<BxPage> MODEL = new MetadataModel("BxDocument", BxPage.class);
}
