package pl.edu.icm.yadda.analysis.bibref.parsing.nodes;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krusek
 */
public class CitationsToHMMTrainingElementsConverterNodeTest {

    private HMMTrainingElement<CitationTokenLabel>[] process(Citation[] input) throws Exception {
        ProcessContext context = new ProcessContext("1");
        CitationsToFVHMMTrainingElementsConverterNode node = new CitationsToFVHMMTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(new SimpleFeatureVectorBuilder());
        return node.process(input, context);
    }

    private HMMTrainingElement<CitationTokenLabel>[] process(Citation input) throws Exception {
        return process(new Citation[]{input});
    }

    @Test
    public void testProcess() throws Exception {
        
        Citation input = new Citation();
        input.appendText("text1 text2 surname3 surname4 issue5 content6 content7 text8 articleTitle9 text10 text11");
        input.addToken(new CitationToken("text1", 0, 5, CitationTokenLabel.TEXT));
        input.addToken(new CitationToken("text2", 6, 11, CitationTokenLabel.TEXT));
        input.addToken(new CitationToken("surname3", 12, 20, CitationTokenLabel.SURNAME));
        input.addToken(new CitationToken("surname4", 21, 29, CitationTokenLabel.SURNAME));
        input.addToken(new CitationToken("issue5", 30, 36, CitationTokenLabel.ISSUE));
        input.addToken(new CitationToken("content6", 37, 45, CitationTokenLabel.CONTENT));
        input.addToken(new CitationToken("content7", 46, 54, CitationTokenLabel.CONTENT));
        input.addToken(new CitationToken("text8", 55, 60, CitationTokenLabel.TEXT));
        input.addToken(new CitationToken("articleTitle9", 61, 74, CitationTokenLabel.ARTICLE_TITLE));
        input.addToken(new CitationToken("text10", 75, 81, CitationTokenLabel.TEXT));
        input.addToken(new CitationToken("text11", 82, 88, CitationTokenLabel.TEXT));

        HMMTrainingElement<CitationTokenLabel>[] output = process(input);
        
        assertEquals(11, output.length);

        HMMTrainingElement<CitationTokenLabel> prevElement = null;
        for (HMMTrainingElement<CitationTokenLabel> element : output) {
            assertEquals(prevElement == null, element.isFirst());
            if (prevElement != null) {
                assertEquals(element.getLabel(), prevElement.getNextLabel());
            }
            prevElement = element;
        }
        assertNull(prevElement.getNextLabel());
        
        assertEquals(CitationTokenLabel.TEXT_BEFORE_SURNAME, output[0].getLabel());
        assertEquals(CitationTokenLabel.TEXT_BEFORE_SURNAME, output[1].getLabel());
        assertEquals(CitationTokenLabel.SURNAME_FIRST, output[2].getLabel());
        assertEquals(CitationTokenLabel.SURNAME, output[3].getLabel());
        assertEquals(CitationTokenLabel.ISSUE, output[4].getLabel());
        assertEquals(CitationTokenLabel.CONTENT_FIRST, output[5].getLabel());
        assertEquals(CitationTokenLabel.CONTENT, output[6].getLabel());
        assertEquals(CitationTokenLabel.TEXT_BEFORE_ARTICLE_TITLE, output[7].getLabel());
        assertEquals(CitationTokenLabel.ARTICLE_TITLE_FIRST, output[8].getLabel());
        assertEquals(CitationTokenLabel.TEXT, output[9].getLabel());
        assertEquals(CitationTokenLabel.TEXT, output[10].getLabel());
    }

    @Test
    public void testProcessManyCitations() throws Exception {
        Citation[] input = new Citation[4];
        input[0] = new Citation();
        input[0].appendText("a");
        input[0].addToken(new CitationToken("a", 0, 1, CitationTokenLabel.ARTICLE_TITLE));
        input[1] = new Citation();
        input[1].appendText("a b");
        input[1].addToken(new CitationToken("a", 0, 1, CitationTokenLabel.ARTICLE_TITLE));
        input[1].addToken(new CitationToken("b", 2, 3, CitationTokenLabel.ARTICLE_TITLE));
        input[2] = new Citation();
        input[3] = new Citation();
        input[3].appendText("a b c");
        input[3].addToken(new CitationToken("a", 0, 1, CitationTokenLabel.ARTICLE_TITLE));
        input[3].addToken(new CitationToken("b", 2, 3, CitationTokenLabel.ARTICLE_TITLE));
        input[3].addToken(new CitationToken("c", 4, 5, CitationTokenLabel.ARTICLE_TITLE));

        HMMTrainingElement<CitationTokenLabel>[] output = process(input);

        assertTrue(output[0].isFirst());
        assertTrue(output[1].isFirst());
        assertFalse(output[2].isFirst());
        assertTrue(output[3].isFirst());
        assertFalse(output[4].isFirst());
        assertFalse(output[5].isFirst());
    }
}
