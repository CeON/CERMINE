package pl.edu.icm.cermine.structure.tools;

import java.util.Comparator;
import pl.edu.icm.cermine.structure.model.BxBounds;


/**
 *
 * @author estocka
 */
public class BoundsComparator implements Comparator {

    @Override
    public int compare(Object bounds1, Object bounds2) {
        int comparisonResult;
        double x1 = ((BxBounds)bounds1).getX();
        double x2 = ((BxBounds)bounds2).getX();
        comparisonResult = Double.compare(x1, x2);
        if(comparisonResult==0){
        double y1 = ((BxBounds)bounds1).getY();
        double y2 = ((BxBounds)bounds2).getY();
        comparisonResult = Double.compare(y1, y2);
        }

        return comparisonResult;
    }
}