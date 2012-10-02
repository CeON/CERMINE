package pl.edu.icm.coansys.metaextr.structure;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxBounds;
import pl.edu.icm.coansys.metaextr.structure.model.BxChunk;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.tools.BxBoundsBuilder;


/**
 * Extracts text chunks from PDFs along with their position on the page, width and height.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class ITextCharacterExtractor implements CharacterExtractor {

    Logger log = LoggerFactory.getLogger(ITextCharacterExtractor.class);

    protected static final HashMap<String, PdfName> AltStandardFontsMap = new HashMap<String, PdfName>();

    static {
        AltStandardFontsMap.put("CourierNew",               PdfName.COURIER);
        AltStandardFontsMap.put("CourierNew,Bold",          PdfName.COURIER_BOLD);
        AltStandardFontsMap.put("CourierNew,BoldItalic",    PdfName.COURIER_BOLDOBLIQUE);
        AltStandardFontsMap.put("CourierNew,Italic",        PdfName.COURIER_OBLIQUE);
        AltStandardFontsMap.put("Arial",                    PdfName.HELVETICA);
        AltStandardFontsMap.put("Arial,Bold",               PdfName.HELVETICA_BOLD);
        AltStandardFontsMap.put("Arial,BoldItalic",         PdfName.HELVETICA_BOLDOBLIQUE);
        AltStandardFontsMap.put("Arial,Italic",             PdfName.HELVETICA_OBLIQUE);
        AltStandardFontsMap.put("TimesNewRoman",            PdfName.TIMES_ROMAN);
        AltStandardFontsMap.put("TimesNewRoman,Bold",       PdfName.TIMES_BOLD);
        AltStandardFontsMap.put("TimesNewRoman,BoldItalic", PdfName.TIMES_BOLDITALIC);
        AltStandardFontsMap.put("TimesNewRoman,Italic",     PdfName.TIMES_ITALIC);
    }

    /**
     * Extracts text chunks from PDF using iText and stores them in BxDocument object.
     * Depending on parsed PDF, extracted text chunks may or may not be individual glyphs,
     * they correspond to single string operands of PDF's text-showing operators
     * (Tj, TJ, ' and ").
     * @param stream PDF's stream
     * @return BxDocument containing pages with extracted chunks stored as BxChunk lists
     * @throws AnalysisException
     */
    @Override
    public BxDocument extractCharacters(InputStream stream) throws AnalysisException {
        try {
            BxDocumentCreator documentCreator = new BxDocumentCreator();

            PdfReader reader = new PdfReader(stream);
            PdfContentStreamProcessor processor = new PdfContentStreamProcessor(documentCreator);

            for (int pageNumber = 1; pageNumber <= reader.getNumberOfPages(); pageNumber++) {
                documentCreator.processNewBxPage(reader.getPageSize(pageNumber));

                PdfDictionary resources = reader.getPageN(pageNumber).getAsDict(PdfName.RESOURCES);
                processAlternativeFontNames(resources);

                processor.reset();
                processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNumber), resources);
            }

            return documentCreator.document;
        } catch (IOException ex) {
            throw new AnalysisException("Cannot extract glyphs from PDF file", ex);
        }
    }

    /**
     * Processes PDF's fonts dictionary. During the process alternative names
     * of Standard 14 Fonts are changed to the standard ones, provided that
     * the font definition doesn't include Widths array.
     *
     * Font dictionary in PDF file often includes an array of individual glyphs' widths.
     * Widths array is always required except for the Standard 14 Fonts, which widths
     * are kept by iText itself. Unfortunately, if the font uses alternative name instead of
     * standard one (see PDF Reference 1.7, table H.3), iText doesn't recognize the font as
     * one of the Standard 14 Fonts, and is unable to determine glyphs widths. In such cases
     * this method will change alternative names to standard ones before PDF's parsing process
     */
    private void processAlternativeFontNames(PdfDictionary resources) {
        PdfDictionary fontsDictionary = resources.getAsDict(PdfName.FONT);

        if (fontsDictionary == null) {
            return;
        }
        for (PdfName pdfFontName : fontsDictionary.getKeys()) {
            PRIndirectReference indRef = (PRIndirectReference) fontsDictionary.get(pdfFontName);
            PdfDictionary fontDictionary = (PdfDictionary) PdfReader.getPdfObjectRelease(indRef);

            PdfName baseFont = fontDictionary.getAsName(PdfName.BASEFONT);
            if (baseFont != null) {
                String fontName = PdfName.decodeName(baseFont.toString());
                if (fontDictionary.getAsArray(PdfName.WIDTHS) == null && AltStandardFontsMap.containsKey(fontName)) {
                    fontDictionary.put(PdfName.BASEFONT, AltStandardFontsMap.get(fontName));
                }
            }
        }
    }

    /**
     * Listener class receives information of text chunks and their render info
     * from PDF content processor. Listener uses this to construct a BxDocument object
     * containing lists of BxChunk elements.
     */
    class BxDocumentCreator implements RenderListener {

        private BxDocument document = new BxDocument();
        private BxPage actPage;

        private BxBoundsBuilder boundsBuilder = new BxBoundsBuilder();

        private Rectangle pageRectangle;

        private void processNewBxPage(Rectangle pageRectangle) {
            if (actPage != null) {
                actPage.setBounds(boundsBuilder.getBounds());
                boundsBuilder.clear();
            }
            actPage = new BxPage();
            document.addPage(actPage);

            this.pageRectangle = pageRectangle;
        }

        @Override
        public void beginTextBlock() {
        }

        @Override
        public void renderText(TextRenderInfo tri) {
            for (TextRenderInfo charTri : tri.getCharacterRenderInfos()) {
                String text = charTri.getText();
                char ch = charTri.getText().charAt(0);
                if (ch <= 32 || text.matches("^[\uD800-\uD8FF]$")
                        || text.matches("^[\uDC00-\uDFFF]$")
                        || text.matches("^[\uFFF0-\uFFFF]$")) {
                    continue;
                }
                
                float charLeft = charTri.getDescentLine().getStartPoint().get(Vector.I1) - pageRectangle.getLeft();
                float charBottom = charTri.getDescentLine().getStartPoint().get(Vector.I2) - pageRectangle.getBottom();
                
                float charHeight = charTri.getAscentLine().getStartPoint().get(Vector.I2) 
                        - charTri.getDescentLine().getStartPoint().get(Vector.I2);
                float charWidth = charTri.getDescentLine().getLength();
                
                if (Float.isNaN(charHeight) || Float.isInfinite(charHeight)) {
                    charHeight = 0;
                }
                
                if (Float.isNaN(charWidth) || Float.isInfinite(charWidth)) {
                    charWidth = 0;
                } 
                
                BxBounds bounds = new BxBounds(charLeft, pageRectangle.getHeight() - charBottom - charHeight,
                                               charWidth, charHeight);
                actPage.addChunk(new BxChunk(bounds, text));
                boundsBuilder.expand(bounds);
            }
        }

        @Override
        public void endTextBlock() {
        }

        @Override
        public void renderImage(ImageRenderInfo iri) {
        }

    }
}
