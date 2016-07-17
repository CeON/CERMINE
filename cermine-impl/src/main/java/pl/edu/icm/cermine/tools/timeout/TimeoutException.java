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
package pl.edu.icm.cermine.tools.timeout;

/**
 * @author Mateusz Kobos
 */
public class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TimeoutException(long currentTimeMillis, long deadlineMillis) {
        super(String.format("Timeout occured: when checked, it was "
                + "%d milliseconds past the deadline time",
                currentTimeMillis - deadlineMillis));
    }

    /**
     * Constructor to be used when you want to re-throw a timeout-related
     * exception.
     *
     * @param ex original exception
     */
    public TimeoutException(Exception ex) {
        super(ex);
    }
}
