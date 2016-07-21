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

package pl.edu.icm.cermine.service;

/**
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
 */
public class ServiceException extends Exception {

    /**
     * Creates a new instance of
     * <code>ServiceException</code> without detail message.
     */
    public ServiceException() {
    }

    /**
     * Constructs an instance of
     * <code>ServiceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServiceException(String msg) {
        super(msg);
    }
    
    public ServiceException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
