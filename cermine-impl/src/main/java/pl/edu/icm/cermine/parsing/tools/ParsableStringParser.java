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
package pl.edu.icm.cermine.parsing.tools;

import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.parsing.model.ParsableString;

/**
 * Generic parser, processes an instance of ParsableString by generating and
 * tagging its tokens.
 *
 * @author Bartosz Tarnawski
 *
 * @param <PS> parsable string type
 */
public interface ParsableStringParser<PS extends ParsableString<?>> {

    /**
     * Sets the token list of the parsable string so that their labels determine
     * the tagging of its text content.
     *
     * @param text the parsable string instance to parse
     * @throws AnalysisException AnalysisException
     */
    void parse(PS text) throws AnalysisException;

    /**
     * @param text string to parse
     * @return XML Element with the tagged text in NLM format
     * @throws TransformationException TransformationException
     * @throws AnalysisException AnalysisException
     */
    Element parse(String text) throws AnalysisException, TransformationException;
}
