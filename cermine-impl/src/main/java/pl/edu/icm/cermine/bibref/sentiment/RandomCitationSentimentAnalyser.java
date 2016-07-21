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

package pl.edu.icm.cermine.bibref.sentiment;

import java.util.List;
import java.util.Random;
import pl.edu.icm.cermine.bibref.sentiment.model.CiTOProperty;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationSentiment;

/**
 * Citation sentiment analyzer assigning random properties.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class RandomCitationSentimentAnalyser implements CitationSentimentAnalyser {
    
    @Override
    public CitationSentiment analyzeSentiment(List<String> contexts) {
        StringBuilder sb = new StringBuilder();
        for (String context : contexts) {
            sb.append(context);
        }
        int seed = sb.toString().hashCode();

        CitationSentiment sentiment = new CitationSentiment();
        sentiment.addProperty(randomProperty(seed, 0.8));
        sentiment.addProperty(randomProperty(2*seed, 0.5));
        sentiment.addProperty(randomProperty(3*seed, 0.2));

        return sentiment;
    }
    
    private CiTOProperty randomProperty(int seed, double probability) {
        List<CiTOProperty> properties = CiTOProperty.getValuesOfParent(CiTOProperty.CITES);
        int size = (int) (properties.size() / probability);
        Random randomGenerator = new Random(seed);
        int index = randomGenerator.nextInt(size);
        return (index < properties.size()) ? properties.get(index) : null;
    }
    
}
