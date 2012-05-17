package pl.edu.icm.yadda.analysis.relations;

/**
 * A dummy clusterizer performing no clusterization.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class DummyClusterizer implements Clusterizer {

    @Override
    public int[] clusterize(double[][] similarities) {
        int size = similarities.length;
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = i;
        }
        return result;
    }

}
