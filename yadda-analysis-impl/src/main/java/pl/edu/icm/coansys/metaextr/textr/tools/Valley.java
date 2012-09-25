package pl.edu.icm.coansys.metaextr.textr.tools;

/**
 *
 * @author estocka
 */
public class Valley implements Comparable {

    private double valleyStart;
    private double valleyEnd;
    private double length;
    private Direction direction;

    public Valley() {
        this.valleyStart = 0.0;
        this.valleyEnd = 0.0;
        this.length = 0.0;


    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getValleyEnd() {
        return valleyEnd;
    }

    public void setValleyEnd(double valleyEnd) {
        this.valleyEnd = valleyEnd;
        computeLength();
    }

    public double getValleyStart() {
        return valleyStart;
    }

    public void setValleyStart(double valleyStart) {
        this.valleyStart = valleyStart;
        computeLength();
    }

    public double getLength() {
        return length;
    }

    void computeLength() {
        this.length = this.valleyEnd - this.valleyStart;
    }

    @Override
    public int compareTo(Object o) {
        int result = 0;
        Valley v = (Valley) o;
        if (this.getLength() > v.getLength()) {
            result = 1;
        } else if (this.getLength() < v.getLength()) {
            result = -1;
        }
        return result;
    }
}
