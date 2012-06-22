package pl.edu.icm.yadda.analysis.classification.hmm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;

/**
 * Hidden Markov Model service implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMServiceImpl implements HMMService {

    /**
     * Viterbi algorithm implementation. The method calculates the most
     * probable states of a sequence of objects based on a sequence of messages
     * emited by them. The method uses HMM probabilities (initial, transition
     * and emission) extracted previously from a training set.
     *
     * Probabilities information used by the algorithm are:
     * - initial probability (ip) - for every state s ip(s) is the probability 
     * that first object in the sequence is in state s,
     * - transition probability (tp) - for every state pair s1, s2 tp(s1, s2) 
     * is the probability that state s2 follows s1 in object sequence,
     * - emission probability (ep) - for a state s and message m ep(s, m) is 
     * the probability that on object in state s emited message m.
     *
     * @param <S> A type of states (labels).
     * @param <T> A type of emitted messages.
     * @param probabilityInfo HMM's probability information object.
     * @param states A collection of all possible states.
     * @param messages A sequence of messages emited by objects.
     * @return A sequence of the most probable object states, the order is the
     * same as in messages list.
     */
    @Override
    public <S,T> List<S> viterbiMostProbableStates(HMMProbabilityInfo<S,T> probabilityInfo, Collection<S> states,
                                                 List<T> messages) {

        /* A matrix that keeps track of the most probable state paths and their 
         probabilities. viterbiMatrix(i, s) is a matrix node that stores three
         pieces of information:
         * - the probability of the most optimal path of length i that ends
         in state s,
         * - the ending state,
         * - previous matrix node of the most optimal path.
         */
        Map<Integer, Map<S, MatrixNode<S>>> viterbiMatrix = new HashMap<Integer, Map<S, MatrixNode<S>>>();

        /* Calculating viterbiMatrix(1, s) for all states.
         * viterbiMatrix(1, s).probability = ip(s) * ep(s, m[1])
         */
        Map<S, MatrixNode<S>> initialProb = new HashMap<S, MatrixNode<S>>();
        for (S state : states) {
            double iProb = probabilityInfo.getInitialProbability(state);
            double eProb = probabilityInfo.getEmissionProbability(state, messages.get(0));
            MatrixNode<S> probNode = new MatrixNode<S>(iProb * eProb, state, null);
            initialProb.put(state, probNode);
        }
        viterbiMatrix.put(1, initialProb);

        /* Filling viterbiMtrix starting from shorter paths. */
        for (int i = 2; i <= messages.size(); i++) {
            /* Calculating viterbiMatrix(i, s) for all states. */

            Map<S, MatrixNode<S>> probabilities = new HashMap<S, MatrixNode<S>>();
            Map<S, MatrixNode<S>> prevProb = viterbiMatrix.get(i - 1);

            for (S state : states) {
                /* Calculating viterbiMatrix(i, state) */

                double maxProbability = -1;
                MatrixNode<S> bestPrevNode = null;

                /* For a given state s, we need to choose its previous state, 
                 * so that the result path is the most optimal (has the highest
                 * probability).
                 *
                 * viterbiMatrix(i, s).probability = ep(s, m[i]) *
                 *      max{viterbiMatrix(i-1, sPrev).probability * tp(sPrev, s)}
                 */
                for (S prevState : states) {
                    MatrixNode<S> prevNode = prevProb.get(prevState);
                    double trProbability = probabilityInfo.getTransitionProbability(prevState, state);

                    double prob = prevNode.probability * trProbability;
                    if (prob > maxProbability) {
                        maxProbability = prob;
                        bestPrevNode = prevNode;
                    }
                }

                double emissionProbability = probabilityInfo.getEmissionProbability(state, messages.get(i - 1));
                MatrixNode<S> node = new MatrixNode<S>(maxProbability * emissionProbability, state, bestPrevNode);
                probabilities.put(state, node);
            }

            /*
             * It is important to keep positive probability values in the 
             * Viterbi matrix, no matter how small. If the HMM sequence is long
             * enough, probability values can become very small and eventually
             * turn into 0. We try to prevent it by detecting if probabilities
             * are too small and multiplying them by a certain number.
             *
             * TODO: Use a custom class instead of double. (Unfortunately using
             * BigDecimal causes Viterbi algorithm to be very slow.)
             */
            double max = 0;
            double min = Double.POSITIVE_INFINITY;
            for (S state : states) {
                if (probabilities.get(state).probability > max) {
                    max = probabilities.get(state).probability;
                }
                if (probabilities.get(state).probability != 0 && probabilities.get(state).probability < min) {
                    min = probabilities.get(state).probability;
                }
            }
            if (Math.log10(max) < 0 && Math.log10(min) < 0) {
                int exponent = (int) ((Math.log10(max) - Math.log10(min)) / 2);
                for (S state : states) {
                    probabilities.get(state).probability *= Math.pow(10, exponent);
                }
            }

            viterbiMatrix.put(i, probabilities);
        }

        List<S> labels = new ArrayList<S>();

        /* Checking which last state in the sequence has the highest 
         * probability. This will be the last state of the most optimal path.
         */
        MatrixNode<S> node = null;
        for (S st : states) {
            MatrixNode<S> actNode = viterbiMatrix.get(messages.size()).get(st);
            if (node == null || actNode.probability > node.probability) {
                node = actNode;
            }
        }

        /* Getting the most optimal path using additional label and prevNode 
         * fields.
         */
        while (node != null) {
            labels.add(0, node.label);
            node = node.prevNode;
        }

        return labels;
    }

    private static class MatrixNode<S> {

        double probability;
        S label;
        MatrixNode<S> prevNode;

        public MatrixNode(double probability, S label, MatrixNode<S> prevNode) {
            this.probability = probability;
            this.label = label;
            this.prevNode = prevNode;
        }
    }
}
