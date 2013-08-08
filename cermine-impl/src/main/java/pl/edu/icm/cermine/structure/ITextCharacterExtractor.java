/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.structure;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxChunk;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;


/**
 * Extracts text chunks from PDFs along with their position on the page, width and height.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class ITextCharacterExtractor implements CharacterExtractor {
    
    public static final int DEFAULT_FRONT_PAGES_LIMIT = 20;
    
    public static final int DEFAULT_BACK_PAGES_LIMIT = 20;
    
    
    private int frontPagesLimit = DEFAULT_FRONT_PAGES_LIMIT;
    
    private int backPagesLimit = DEFAULT_BACK_PAGES_LIMIT;
    
    protected static final Map<String, PdfName> ALT_TO_STANDART_FONTS = new HashMap<String, PdfName>();

    static {
        ALT_TO_STANDART_FONTS.put("CourierNew",               PdfName.COURIER);
        ALT_TO_STANDART_FONTS.put("CourierNew,Bold",          PdfName.COURIER_BOLD);
        ALT_TO_STANDART_FONTS.put("CourierNew,BoldItalic",    PdfName.COURIER_BOLDOBLIQUE);
        ALT_TO_STANDART_FONTS.put("CourierNew,Italic",        PdfName.COURIER_OBLIQUE);
        ALT_TO_STANDART_FONTS.put("Arial",                    PdfName.HELVETICA);
        ALT_TO_STANDART_FONTS.put("Arial,Bold",               PdfName.HELVETICA_BOLD);
        ALT_TO_STANDART_FONTS.put("Arial,BoldItalic",         PdfName.HELVETICA_BOLDOBLIQUE);
        ALT_TO_STANDART_FONTS.put("Arial,Italic",             PdfName.HELVETICA_OBLIQUE);
        ALT_TO_STANDART_FONTS.put("TimesNewRoman",            PdfName.TIMES_ROMAN);
        ALT_TO_STANDART_FONTS.put("TimesNewRoman,Bold",       PdfName.TIMES_BOLD);
        ALT_TO_STANDART_FONTS.put("TimesNewRoman,BoldItalic", PdfName.TIMES_BOLDITALIC);
        ALT_TO_STANDART_FONTS.put("TimesNewRoman,Italic",     PdfName.TIMES_ITALIC);
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
                if (frontPagesLimit > 0 && backPagesLimit > 0 && pageNumber > frontPagesLimit 
                        && pageNumber < reader.getNumberOfPages() - 1 - backPagesLimit) {
                    continue;
                }
                documentCreator.processNewBxPage(reader.getPageSize(pageNumber));

                PdfDictionary resources = reader.getPageN(pageNumber).getAsDict(PdfName.RESOURCES);
                processAlternativeFontNames(resources);

                processor.reset();
                processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNumber), resources);
            }

            return removeDuplicateChunks(documentCreator.document);
        } catch (InvalidPdfException ex) {
            throw new AnalysisException("Invalid PDF file", ex);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot extract characters from PDF file", ex);
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
                if (fontDictionary.getAsArray(PdfName.WIDTHS) == null && ALT_TO_STANDART_FONTS.containsKey(fontName)) {
                    fontDictionary.put(PdfName.BASEFONT, ALT_TO_STANDART_FONTS.get(fontName));
                }
            }
        }
    }

    private BxDocument removeDuplicateChunks(BxDocument document) {
        for (BxPage page : document.getPages()) {
            List<BxChunk> chunks = page.getChunks();
            List<BxChunk> filteredChunks = new ArrayList<BxChunk>();
            for (BxChunk chunk : chunks) {
                boolean duplicate = false;
                for (BxChunk ch : filteredChunks) {
                    if (chunk.getText().equals(ch.getText()) && chunk.getBounds().isSimilarTo(ch.getBounds(), 0.01)) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    filteredChunks.add(chunk);
                }
            }
            page.setChunks(filteredChunks);
        }
        return document;
    }

    /**
     * Listener class receives information of text chunks and their render info
     * from PDF content processor. Listener uses this to construct a BxDocument object
     * containing lists of BxChunk elements.
     */
    static class BxDocumentCreator implements RenderListener {

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
                if (ch <= ' ' || text.matches("^[\uD800-\uD8FF]$")
                        || text.matches("^[\uDC00-\uDFFF]$")
                        || text.matches("^[\uFFF0-\uFFFF]$")) {
                    continue;
                }
                
                float absoluteCharLeft = charTri.getDescentLine().getStartPoint().get(Vector.I1);
                float absoluteCharBottom = charTri.getDescentLine().getStartPoint().get(Vector.I2);
                
                float charLeft = absoluteCharLeft - pageRectangle.getLeft();
                float charBottom = absoluteCharBottom - pageRectangle.getBottom();
                
                float charHeight = charTri.getAscentLine().getStartPoint().get(Vector.I2) 
                        - charTri.getDescentLine().getStartPoint().get(Vector.I2);
                float charWidth = charTri.getDescentLine().getLength();
                
                if (Float.isNaN(charHeight) || Float.isInfinite(charHeight)) {
                    charHeight = 0;
                }
                
                if (Float.isNaN(charWidth) || Float.isInfinite(charWidth)) {
                    charWidth = 0;
                } 
                
                if (absoluteCharLeft < pageRectangle.getLeft() 
                        || absoluteCharLeft + charWidth > pageRectangle.getRight()
                        || absoluteCharBottom < pageRectangle.getBottom() 
                        || absoluteCharBottom + charHeight > pageRectangle.getTop()) {
                    continue;
                }
                
                BxBounds bounds = new BxBounds(charLeft, pageRectangle.getHeight() - charBottom - charHeight,
                                               charWidth, charHeight);
                
                if (Double.isNaN(bounds.getX()) || Double.isInfinite(bounds.getX())
                        || Double.isNaN(bounds.getY()) || Double.isInfinite(bounds.getY())
                        || Double.isNaN(bounds.getHeight()) || Double.isInfinite(bounds.getHeight())
                        || Double.isNaN(bounds.getWidth()) || Double.isInfinite(bounds.getWidth())) {
                    continue;
                }
                
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

    public int getBackPagesLimit() {
        return backPagesLimit;
    }

    public int getFrontPagesLimit() {
        return frontPagesLimit;
    }

    /**
     * Sets the number of front and back pages to be processed and returned.
     * If any of the values is set to 0 or less, the whole document is processed.
     * This may cause long processing time for large documents.
     */
    public void setPagesLimits(int frontPagesLimit, int backPagesLimit) {
        this.frontPagesLimit = frontPagesLimit;
        this.backPagesLimit = backPagesLimit;
    }
    
}
