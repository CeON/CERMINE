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

package pl.edu.icm.cermine.evaluation.systems;

import com.google.common.collect.Lists;
import java.util.*;
import pl.edu.icm.cermine.evaluation.exception.EvaluationException;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.evaluation.transformers.NLMToDocumentReader;
import pl.edu.icm.cermine.evaluation.transformers.ParscitToDocumentReader;
import pl.edu.icm.cermine.model.Document;
import pl.edu.icm.cermine.tools.transformers.FormatToModelReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ParscitNlmEvaluator extends SystemEvaluator {

    private final NLMToDocumentReader origReader = new NLMToDocumentReader();
    private final ParscitToDocumentReader testReader = new ParscitToDocumentReader();
    
    @Override
    protected List<EvalInformationType> getTypes() {
        return Lists.newArrayList(EvalInformationType.TITLE, EvalInformationType.ABSTRACT,
            EvalInformationType.AUTHORS, EvalInformationType.AFFILIATIONS, EvalInformationType.EMAILS,
            EvalInformationType.HEADERS, EvalInformationType.HEADER_LEVELS, EvalInformationType.REFERENCES);
    }

    @Override
    protected FormatToModelReader<Document> getOrigReader() {
        return origReader;
    }

    @Override
    protected FormatToModelReader<Document> getExtrReader() {
        return testReader;
    }

    public static void main(String[] args) throws EvaluationException {
        ParscitNlmEvaluator e = new ParscitNlmEvaluator();
        e.process(args);
    }
    
}
