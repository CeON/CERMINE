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
 * Class that throws an exception when given amount of time has already passed
 * when its {@link #check()} method is called.
 *
 * @author Mateusz Kobos
 */
public class Timeout {

    private final long deadlineMillis;

    /**
     * Create a new instance with the deadline set corresponding to given
     * timeout value. If the timeout is set to 0, the first call of
     * {@link #check()} method will result in throwing the
     * {@link TimeoutException}.
     *
     * @param timeoutMillis timeout in milliseconds
     */
    public Timeout(long timeoutMillis) {
        Preconditions.checkArgument(timeoutMillis >= 0);
        long startTime = getCurrentTime();
        this.deadlineMillis = startTime + timeoutMillis;
    }

    /**
     * Create a new instance and with the deadline set in an unattainable
     * future.
     */
    public Timeout() {
        this.deadlineMillis = Long.MAX_VALUE;
    }

    /**
     * Throw exception if it already is the deadline time or past it.
     *
     * @throws TimeoutException TimeoutException
     */
    public void check() throws TimeoutException {
        long currTimeMillis = getCurrentTime();
        if (currTimeMillis >= deadlineMillis) {
            throw new TimeoutException(currTimeMillis, deadlineMillis);
        }
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * Return the timeout corresponding to the more immediate deadline.
     * @param t0 timeout
     * @param t1 timeout
     * @return earlier timeout
     */
    public static Timeout min(Timeout t0, Timeout t1) {
        if (t0.deadlineMillis < t1.deadlineMillis) {
            return t0;
        } else {
            return t1;
        }
    }
}
