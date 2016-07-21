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
package pl.edu.icm.cermine.exception;

/**
 * Thrown when an unrecoverable problem occurs during analysis.
 *
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 */
public class AnalysisException extends Exception {

    private static final long serialVersionUID = 4601197315845837554L;

    public AnalysisException() {
        super();
    }

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalysisException(String message) {
        super(message);
    }

    public AnalysisException(Throwable cause) {
        super(cause);
    }
}
