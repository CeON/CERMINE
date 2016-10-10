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
package pl.edu.icm.cermine;

import static org.junit.Assert.fail;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.tools.timeout.TimeoutException;

/**
 * @author Mateusz Kobos
 */
public class ContentExtractorTimeoutTest {

    static final private String COMPLEX_PDF_PATH = "/pl/edu/icm/cermine/tools/timeout/complex.pdf";
    static final private String SIMPLE_PDF_PATH = "/pl/edu/icm/cermine/tools/timeout/simple.pdf";
    static final private long ACCEPTABLE_DELAY_MILLIS = 5000;

    @Test
    public void testNoTimeout()
            throws IOException, TimeoutException, AnalysisException {
        InputStream in = this.getClass().getResourceAsStream(SIMPLE_PDF_PATH);
        ContentExtractor extractor = new ContentExtractor();
        extractor.setPDF(in);
        extractor.getBxDocument();
    }

    @Test
    public void testObjectTimeoutRemoval()
            throws IOException, TimeoutException, AnalysisException {
        InputStream in = this.getClass().getResourceAsStream(SIMPLE_PDF_PATH);
        ContentExtractor extractor = new ContentExtractor();
        extractor.setPDF(in);
        extractor.setTimeout(0);
        extractor.removeTimeout();
        extractor.getBxDocument();
    }

    @Test
    public void testObjectTimeoutSetInConstructor()
            throws IOException, TimeoutException, AnalysisException {
        InputStream in = this.getClass().getResourceAsStream(COMPLEX_PDF_PATH);
        long start = System.currentTimeMillis();
        try {
            ContentExtractor extractor = new ContentExtractor(1);
            extractor.setPDF(in);
            extractor.getBxDocument();
        } catch (TimeoutException ex) {
            assumeTimeoutWithinTimeBound(start);
            return;
        } finally {
            in.close();
        }
        fail("The processing should have been interrupted by timeout but wasn't");
    }

    @Test
    public void testObjectTimeout()
            throws IOException, TimeoutException, AnalysisException {
        assumeOperationsEndInTimeout(
                new ContentExtractorFactory() {
            @Override
            public ContentExtractor create(InputStream document)
                    throws AnalysisException, IOException {
                ContentExtractor extractor = new ContentExtractor();
                extractor.setTimeout(1);
                extractor.setPDF(document);
                return extractor;
            }
        }, Collections.singletonList(
                        new ExtractorOperation() {
                    @Override
                    public void run(ContentExtractor extractor)
                            throws TimeoutException, AnalysisException {
                        extractor.getBxDocument();
                    }
                })
        );
    }

    @Test
    public void testMethodTimeout()
            throws IOException, TimeoutException, AnalysisException {
        assumeOperationsEndInTimeout(Collections.singletonList(
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getBxDocument(1);
            }
        }
        ));
    }

    @Test
    public void testAllExtractionOperationsEndInTimeout()
            throws AnalysisException, IOException {
        /**
         * The timeout set here is zero to make sure that the methods end in
         * timeout no matter how short they take to execute.
         */
        List<? extends ExtractorOperation> list = Arrays.asList(
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getBxDocument(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getBxDocumentWithGeneralLabels(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getBxDocumentWithSpecificLabels(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getMetadata(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getMetadataAsNLM(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getReferences(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getReferencesAsNLM(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getRawFullText(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getLabelledFullText(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getBody(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getBodyAsNLM(0);
            }
        },
                new ExtractorOperation() {
            @Override
            public void run(ContentExtractor extractor)
                    throws TimeoutException, AnalysisException {
                extractor.getContentAsNLM(0);
            }
        });
        assumeOperationsEndInTimeout(list);
    }

    @Test
    public void testObjectAndMethodTimeoutCombinedWithObjectTimeoutActive()
            throws IOException, TimeoutException, AnalysisException {
        assumeOperationsEndInTimeout(
                new ContentExtractorFactory() {
            @Override
            public ContentExtractor create(InputStream document)
                    throws AnalysisException, IOException {
                ContentExtractor extractor = new ContentExtractor();
                extractor.setTimeout(1);
                extractor.setPDF(document);
                return extractor;
            }
        }, Collections.singletonList(
                        new ExtractorOperation() {
                    @Override
                    public void run(ContentExtractor extractor)
                            throws TimeoutException, AnalysisException {
                        extractor.getBxDocument(100);
                    }
                })
        );
    }

    @Test
    public void testObjectAndMethodTimeoutCombinedWithMethodTimeoutActive()
            throws IOException, TimeoutException, AnalysisException {
        assumeOperationsEndInTimeout(
                new ContentExtractorFactory() {
            @Override
            public ContentExtractor create(InputStream document)
                    throws AnalysisException, IOException {
                ContentExtractor extractor = new ContentExtractor();
                extractor.setTimeout(100);
                extractor.setPDF(document);
                return extractor;
            }
        }, Collections.singletonList(
                        new ExtractorOperation() {
                    @Override
                    public void run(ContentExtractor extractor)
                            throws TimeoutException, AnalysisException {
                        extractor.getBxDocument(1);
                    }
                })
        );
    }

    private static void assumeOperationsEndInTimeout(
            Collection<? extends ExtractorOperation> operations)
            throws AnalysisException, IOException {
        assumeOperationsEndInTimeout(new ContentExtractorFactory() {
            @Override
            public ContentExtractor create(InputStream document)
                    throws AnalysisException, IOException {
                ContentExtractor extractor = new ContentExtractor();
                extractor.setPDF(document);
                return extractor;
            }
        }, operations);
    }

    private static void assumeOperationsEndInTimeout(
            ContentExtractorFactory factory,
            Collection<? extends ExtractorOperation> operations)
            throws AnalysisException, IOException {
        InputStream in = ContentExtractorTimeoutTest.class.getClass()
                .getResourceAsStream(COMPLEX_PDF_PATH);
        ContentExtractor extractor = factory.create(in);
        for (ExtractorOperation op : operations) {
            long start = System.currentTimeMillis();
            try {
                op.run(extractor);
            } catch (TimeoutException ex) {
                assumeTimeoutWithinTimeBound(start);
                return;
            } finally {
                in.close();
            }
            fail("The processing should have been interrupted by timeout "
                    + "but wasn't");
        }
    }

    private static void assumeTimeoutWithinTimeBound(long startMillis) {
        long endMillis = System.currentTimeMillis();
        long diff = endMillis - startMillis;
        if (diff > ACCEPTABLE_DELAY_MILLIS) {
            fail("The processing interrupted by the timeout took " + diff
                    + " milliseconds while it should have taken no more than "
                    + ACCEPTABLE_DELAY_MILLIS + " milliseconds");
        }
    }

}

interface ExtractorOperation {

    void run(ContentExtractor extractor)
            throws TimeoutException, AnalysisException;
}

interface ContentExtractorFactory {

    ContentExtractor create(InputStream document)
            throws AnalysisException, IOException;
}
