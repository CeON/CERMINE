package pl.edu.icm.yadda.analysis.textr;

import java.io.InputStream;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

public interface GlyphExtractor {
	
	public BxDocument extractGlyphs(InputStream stream) throws AnalysisException;
}
