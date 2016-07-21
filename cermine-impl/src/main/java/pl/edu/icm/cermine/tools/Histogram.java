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
package pl.edu.icm.cermine.tools;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Histogram for double values in range [minValue, maxValue].
 *
 * @author Krzysztof Rusek
 */
public class Histogram implements Iterable<Histogram.Bin> {

    private static final double EPSILON = 1.0e-6;

    private final double min;
    private final double delta;
    private final double resolution;

    private double[] frequencies;

    /**
     * Constructs a new histogram for values in range [minValue, maxValue] with
     * given resolution.
     *
     * @param minValue - minimum allowed value
     * @param maxValue - maximum allowed value
     * @param resolution - histogram's resolution
     */
    public Histogram(double minValue, double maxValue, double resolution) {
        this.min = minValue - EPSILON;
        this.delta = maxValue - minValue + 2 * EPSILON;
        int size = Math.max(1, (int) Math.round((maxValue - minValue) / resolution));
        this.resolution = this.delta / size;
        this.frequencies = new double[size];
    }

    /**
     * Smooths the histogram using a rectangular smoothing window.
     *
     * @param windowLength - smoothing window length
     */
    public void smooth(double windowLength) {
        int size = (int) Math.round(windowLength / resolution) / 2;
        double sum = 0.0;
        for (int i = 0; i <= size; i++) {
            sum += frequencies[i];
        }
        double[] newFrequencies = new double[frequencies.length];
        for (int i = 0; i < size; i++) {
            newFrequencies[i] = sum / (2 * size + 1);
            sum += frequencies[i + size + 1];
        }
        for (int i = size; i < frequencies.length - size - 1; i++) {
            newFrequencies[i] = sum / (2 * size + 1);
            sum += frequencies[i + size + 1];
            sum -= frequencies[i - size];
        }
        for (int i = frequencies.length - size - 1; i < frequencies.length; i++) {
            newFrequencies[i] = sum / (2 * size + 1);
            sum -= frequencies[i - size];
        }
        frequencies = newFrequencies;
    }

    /**
     * Circularly smooths the histogram using a rectangular smoothing window.
     *
     * @param windowLength - smoothing window length
     */
    public void circularSmooth(double windowLength) {
        int size = (int) Math.round(windowLength / resolution) / 2;
        double sum = frequencies[0];
        for (int i = 1; i <= size; i++) {
            sum += frequencies[i] + frequencies[frequencies.length - i];
        }

        double[] newFrequencies = new double[frequencies.length];
        for (int i = 0; i < frequencies.length; i++) {
            newFrequencies[i] = sum / (2 * size + 1);
            sum += frequencies[i + size + 1 < frequencies.length ? i + size + 1 : i + size + 1 - frequencies.length];
            sum -= frequencies[i - size < 0 ? frequencies.length + i - size : i - size];
        }
        frequencies = newFrequencies;
    }

    public void kernelSmooth(double[] kernel) {
        double[] newFrequencies = new double[frequencies.length];
        int shift = (kernel.length - 1) / 2;
        for (int i = 0; i < kernel.length; i++) {
            int jStart = Math.max(0, i - shift);
            int jEnd = Math.min(frequencies.length, frequencies.length + i - shift);
            for (int j = jStart; j < jEnd; j++) {
                newFrequencies[j - i + shift] += kernel[i] * frequencies[j];
            }
        }
        frequencies = newFrequencies;
    }

    public void circularKernelSmooth(double[] kernel) {
        double[] newFrequencies = new double[frequencies.length];
        int shift = (kernel.length - 1) / 2;
        for (int i = 0; i < frequencies.length; i++) {
            for (int d = 0; d < kernel.length; d++) {
                int j = i + d - shift;
                if (j < 0) {
                    j += frequencies.length;
                } else if (j >= frequencies.length) {
                    j -= frequencies.length;
                }
                newFrequencies[i] += kernel[d] * frequencies[j];
            }
        }
        frequencies = newFrequencies;
    }

    public double[] createGaussianKernel(double length, double stdDeviation) {
        int r = (int) Math.round(length / resolution) / 2;
        stdDeviation /= resolution;

        int size = 2 * r + 1;
        double[] kernel = new double[size];
        double sum = 0;
        double b = 2 * stdDeviation * stdDeviation;
        double a = 1 / Math.sqrt(Math.PI * b);
        for (int i = 0; i < size; i++) {
            kernel[i] = a * Math.exp(-(i - r) * (i - r) / b);
            sum += kernel[i];
        }
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }

    public void circularGaussianSmooth(double windowLength, double stdDeviation) {
        circularKernelSmooth(createGaussianKernel(windowLength, stdDeviation));
    }

    public void gaussianSmooth(double windowLength, double stdDeviation) {
        kernelSmooth(createGaussianKernel(windowLength, stdDeviation));
    }

    /**
     * Adds single occurrence of given value to the histogram.
     *
     * @param value inserted values
     */
    public void add(double value) {
        frequencies[(int) ((value - min) / resolution)] += 1.0;
    }

    /**
     * Returns histogram's number of bins.
     *
     * @return number of bins
     */
    public int getSize() {
        return frequencies.length;
    }

    /**
     * Returns the height of the bin at the specified position.
     *
     * @param index bin index
     * @return bin height
     */
    public double getFrequency(int index) {
        return frequencies[index];
    }

    /**
     * Finds the histogram's peak value.
     *
     * @return peak value
     */
    public double getPeakValue() {
        int peakIndex = 0;
        for (int i = 1; i < frequencies.length; i++) {
            if (frequencies[i] > frequencies[peakIndex]) {
                peakIndex = i;
            }
        }
        int peakEndIndex = peakIndex + 1;
        final double EPS = 0.0001;
        while (peakEndIndex < frequencies.length && Math.abs(frequencies[peakEndIndex] - frequencies[peakIndex]) < EPS) {
            peakEndIndex++;
        }
        return ((double) peakIndex + peakEndIndex) / 2 * resolution + min;
    }

    public static Histogram fromValues(Collection<Double> samples, double resolution) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double sample : samples) {
            min = Math.min(min, sample);
            max = Math.max(max, sample);
        }
        Histogram histogram = new Histogram(min, max, resolution);
        for (double sample : samples) {
            histogram.add(sample);
        }
        return histogram;
    }

    @Override
    public Iterator<Bin> iterator() {
        return new Iterator() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < frequencies.length;
            }

            @Override
            public Object next() {
                if (index >= frequencies.length) {
                    throw new NoSuchElementException();
                }
                return new Bin(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        };
    }

    public final class Bin {

        private final int index;

        private Bin(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public double getFrequency() {
            return frequencies[index];
        }

        public double getValue() {
            return (index + 0.5) * resolution + min;
        }
    }
}
