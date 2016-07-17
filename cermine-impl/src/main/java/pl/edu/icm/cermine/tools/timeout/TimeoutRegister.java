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

import com.google.common.base.Preconditions;

/**
 * Thread-local singleton for setting and removing timeout for the thread.
 * <br>
 * The standard way of using this class in order to set timeout is to:
 * <ul>
 * <li>Register the timeout using {@link #set(Timeout)} method.</li>
 * <li>Call {@link #get()} to retrieve the {@link Timeout} object. User can
 * subsequently use the {@link Timeout} object to check if the timeout time has
 * passed or not.</li>
 * <li>Remove the timeout from the register {@link #remove()}, i.e., remove the
 * timeout. After removal, if {@link #get()} is called to get the
 * {@link Timeout} object, an object will be returned with no timeout set.</li>
 * </ul>
 *
 * Typical usage:
 * <pre>
 * <code>
 * try {
 *   TimeoutRegister.set(new Timeout(300));
 *   doStuff();
 * } finally {
 *   TimeoutRegister.remove();
 * }
 * </code>
 * </pre>
 *
 * @author Mateusz Kobos
 */
public class TimeoutRegister {

    private static final Timeout NO_TIMEOUT = new Timeout();

    private static final ThreadLocal<Timeout> INSTANCE = new ThreadLocal<Timeout>() {
        @Override
        protected Timeout initialValue() {
            return NO_TIMEOUT;
        }
    };

    public static Timeout get() {
        return INSTANCE.get();
    }

    public static void set(Timeout timeout) {
        Preconditions.checkNotNull(timeout);
        INSTANCE.set(timeout);
    }

    public static void remove() {
        INSTANCE.remove();
    }
}
