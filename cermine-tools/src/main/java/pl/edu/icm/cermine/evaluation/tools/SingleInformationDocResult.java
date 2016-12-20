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

package pl.edu.icm.cermine.evaluation.tools;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <T> result type
 */
public interface SingleInformationDocResult<T> {

    EvalInformationType getType();
    
    boolean hasExpected();

    boolean hasExtracted();

    T getExpected();
    
    T getExtracted();
    
    void setExpected(T expected);
    
    void setExtracted(T extracted);
    
    Double getPrecision();

    Double getRecall();

    Double getF1();

    void prettyPrint();
    
    void printCSV();
    
}
