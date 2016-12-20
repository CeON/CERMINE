/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
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

import com.google.common.collect.Lists;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import com.itextpdf.text.pdf.parser.Vector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxChunk;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Extracts text chunks from PDFs along with their position on the page, width and height.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ITextCharacterExtractor implements CharacterExtractor {
    
    public static final int DEFAULT_FRONT_PAGES_LIMIT = 20;
    
    public static final int DEFAULT_BACK_PAGES_LIMIT = 20;
    
    
    private int frontPagesLimit = DEFAULT_FRONT_PAGES_LIMIT;
    
    private int backPagesLimit = DEFAULT_BACK_PAGES_LIMIT;

    private static final int PAGE_GRID_SIZE = 10;
    
    private static final int CHUNK_DENSITY_LIMIT = 15;
    
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
     * @throws AnalysisException AnalysisException
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
                processAlternativeColorSpace(resources);

                processor.reset();
                processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNumber), resources);
                TimeoutRegister.get().check();
            }

            BxDocument doc = filterComponents(removeDuplicateChunks(documentCreator.document));
            if (doc.getFirstChild() == null) {
                throw new AnalysisException("Document contains no pages");
            }
            return doc;
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
        if (resources == null) {
            return;
        }
        PdfDictionary fontsDictionary = resources.getAsDict(PdfName.FONT);

        if (fontsDictionary == null) {
            return;
        }
        for (PdfName pdfFontName : fontsDictionary.getKeys()) {
            if (!(fontsDictionary.get(pdfFontName) instanceof PRIndirectReference)) {
                return;
            }
            PRIndirectReference indRef = (PRIndirectReference) fontsDictionary.get(pdfFontName);
            if (!(PdfReader.getPdfObjectRelease(indRef) instanceof PdfDictionary)) {
                return;
            }
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

    private void processAlternativeColorSpace(PdfDictionary resources) {
        if (resources == null) {
            return;
        }
        PdfDictionary csDictionary = resources.getAsDict(PdfName.COLORSPACE);
        if (csDictionary == null) {
            return;
        }
        for (PdfName csName : csDictionary.getKeys()) {
            if (csDictionary.getAsArray(csName) != null) {
                csDictionary.put(csName, PdfName.DEVICEGRAY);
            }
        }
    }
                
    private BxDocument removeDuplicateChunks(BxDocument document) {
        for (BxPage page : document) {
            List<BxChunk> chunks = Lists.newArrayList(page.getChunks());
            List<BxChunk> filteredChunks = new ArrayList<BxChunk>();
            Map<Integer, Map<Integer, Set<BxChunk>>> chunkMap = new HashMap<Integer, Map<Integer, Set<BxChunk>>>();
            for (BxChunk chunk : chunks) {
                int x = (int) chunk.getX();
                int y = (int) chunk.getY();
                boolean duplicate = false;
                duplicateSearch:
                for (int i = x-1; i <= x+1; i++) {
                    for (int j = y-1; j <= y+1; j++) {
                        if (chunkMap.get(i) == null || chunkMap.get(i).get(j) == null) {
                            continue;
                        }
                        for (BxChunk ch : chunkMap.get(i).get(j)) {
                            if (chunk.toText().equals(ch.toText()) && chunk.getBounds().isSimilarTo(ch.getBounds(), 1)) {
                                duplicate = true;
                                break duplicateSearch;
                            }
                        }
                    }
                }
                if (!duplicate) {
                    filteredChunks.add(chunk);
                    x = (int) chunk.getX();
                    y = (int) chunk.getY();
                    if (chunkMap.get(x) == null) {
                        chunkMap.put(x, new HashMap<Integer, Set<BxChunk>>());
                    }
                    if (chunkMap.get(x).get(y) == null) {
                        chunkMap.get(x).put(y, new HashSet<BxChunk>());
                    }
                    chunkMap.get(x).get(y).add(chunk);
                }
            }
            page.setChunks(filteredChunks);
        }
        return document;
    }
    
    private BxDocument filterComponents(BxDocument document) {
        for (BxPage page : document) {
            BxBoundsBuilder bounds = new BxBoundsBuilder();
            List<BxChunk> chunks = Lists.newArrayList(page.getChunks());
            for (BxChunk ch : chunks) {
                bounds.expand(ch.getBounds());
            }
                    
            double density = (double)100.0*chunks.size() / (bounds.getBounds().getWidth()*bounds.getBounds().getHeight());
            if (Double.isNaN(density) || density < CHUNK_DENSITY_LIMIT) {
                continue;
            }
            
            Map<String, List<BxChunk>> map = new HashMap<String, List<BxChunk>>();
            for (BxChunk ch : chunks) {
                int x = (int)ch.getX()/PAGE_GRID_SIZE;
                int y = (int)ch.getY()/PAGE_GRID_SIZE;
                String key = Integer.toString(x)+" "+Integer.toString(y);
                if (map.get(key) == null) {
                    map.put(key, new ArrayList<BxChunk>());
                }
                map.get(key).add(ch);
            }

            for (List<BxChunk> list : map.values()) {
                if (list.size() > CHUNK_DENSITY_LIMIT) {
                    for (BxChunk ch : list) {
                        chunks.remove(ch);
                    }
                }
            }
            page.setChunks(chunks);
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
                if (text == null || text.isEmpty()) {
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
         
                char[] textChars = text.toCharArray();
                double chw = bounds.getWidth() / textChars.length;
                for (int i = 0; i < textChars.length; i++) {
                    char ch = textChars[i];
                    if (ch <= ' ' || text.matches("^[\uD800-\uD8FF]$")
                        || text.matches("^[\uDC00-\uDFFF]$")
                        || text.matches("^[\uFFF0-\uFFFF]$")) {
                        continue;
                    }
                    BxBounds chBounds = new BxBounds(bounds.getX() + i * chw,
                            bounds.getY(), chw, bounds.getHeight());
                    BxChunk chunk = new BxChunk(chBounds, String.valueOf(ch));
                    chunk.setFontName(tri.getFont().getFullFontName()[0][3]);
                    actPage.addChunk(chunk);
                    boundsBuilder.expand(bounds);
                }
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
     * @param frontPagesLimit front pages limit
     * @param backPagesLimit back pages limit
     */
    public void setPagesLimits(int frontPagesLimit, int backPagesLimit) {
        this.frontPagesLimit = frontPagesLimit;
        this.backPagesLimit = backPagesLimit;
    }
    
}
