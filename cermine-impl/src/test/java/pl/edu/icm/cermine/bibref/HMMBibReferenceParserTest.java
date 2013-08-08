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

package pl.edu.icm.cermine.bibref;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.junit.Before;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.features.*;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.HMMService;
import pl.edu.icm.cermine.tools.classification.hmm.HMMServiceImpl;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMBibReferenceParserTest extends AbstractBibReferenceParserTest {

    protected static final String hmmProbabilitiesFile = "/pl/edu/icm/cermine/bibref/hmmCitationProbabilities.xml";

    private HMMService hmmService = new HMMServiceImpl();

    private HMMBibReferenceParser bibReferenceParser;
    
    private double minPercentage = 0.9;

    @Before
    public void setUp() throws IOException {
        InputStream is = this.getClass().getResourceAsStream(hmmProbabilitiesFile);
        XStream xstream = new XStream();
        HMMProbabilityInfo<CitationTokenLabel> hmmProbabilities;
        try {
            hmmProbabilities = (HMMProbabilityInfo<CitationTokenLabel>) xstream.fromXML(is);
        } finally {
            is.close();
        }

        FeatureVectorBuilder<CitationToken, Citation> vectorBuilder =
        		new FeatureVectorBuilder<CitationToken, Citation>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<CitationToken, Citation>>asList(
                new DigitRelativeCountFeature(),
                new IsAllDigitsFeature(),
                new IsAllLettersFeature(),
                new IsAllLettersOrDigitsFeature(),
                new IsAllLowercaseFeature(),
                new IsAllRomanDigitsFeature(),
                new IsAllUppercaseFeature(),
                new IsAndFeature(),
                new IsCityFeature(),
                new IsClosingParenthesisFeature(),
                new IsClosingSquareBracketFeature(),
                new IsCommaFeature(),
                new IsCommonPublisherWordFeature(),
                new IsCommonSeriesWordFeature(),
                new IsCommonSourceWordFeature(),
                new IsDashBetweenWordsFeature(),
                new IsDashFeature(),
                new IsDigitFeature(),
                new IsDotFeature(),
                new IsLaquoFeature(),
                new IsLowercaseLetterFeature(),
                new IsOpeningParenthesisFeature(),
                new IsOpeningSquareBracketFeature(),
                new IsQuoteFeature(),
                new IsRaquoFeature(),
                new IsSingleQuoteBetweenWordsFeature(),
                new IsSlashFeature(),
                new IsUppercaseLetterFeature(),
                new IsUppercaseWordFeature(),
                new IsWordAndFeature(),
                new IsWordDeFeature(),
                new IsWordHttpFeature(),
                new IsWordJrFeature(),
                new IsWordLeFeature(),
                new IsNumberTextFeature(),
                new IsPagesTextFeature(),
                new IsWordTheFeature(),
                new IsWordTheoryFeature(),
                new IsCommonSurnamePartFeature(),
                new IsVolumeTextFeature(),
                new IsYearFeature(),
                new LengthFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseRelativeCountFeature(),
                new StartsWithUppercaseFeature(),
                new StartsWithWordMcFeature(),
                new UppercaseRelativeCountFeature()));

        bibReferenceParser = new HMMBibReferenceParser(hmmService, hmmProbabilities, vectorBuilder);
    }

    @Override
    protected BibReferenceParser<BibEntry> getParser() {
        return bibReferenceParser;
    }

    @Override
    protected double getMinPercentage() {
        return minPercentage;
    }
}
