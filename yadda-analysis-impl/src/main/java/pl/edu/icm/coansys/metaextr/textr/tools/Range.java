package pl.edu.icm.coansys.metaextr.textr.tools;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.coansys.metaextr.textr.model.BxChunk;

/**
 *
 * @author estocka
 */
public class Range implements Comparable {

    private double rangeStart;
    private double rangeEnd;
    private List<BxChunk> chunksList;

    public Range() {
        this.chunksList = new ArrayList<BxChunk>();
        this.rangeStart = 0.0;
        this.rangeEnd = 0.0;

    }

    public List<BxChunk> getChunksList() {
        return chunksList;
    }

    public void setChunksList(List<BxChunk> chunksList) {
        this.chunksList = chunksList;
    }

    public double getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(double rangeYEnd) {
        this.rangeEnd = rangeYEnd;
    }

    public double getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(double rangeYStart) {
        this.rangeStart = rangeYStart;
    }

    public void addChunk(BxChunk chunk) {
        this.chunksList.add(chunk);
    }
    //checks range and if one of ends is in range and the other not,
    //the range is exeeded

    public boolean inRange(double start, double end) {
        boolean inRangeStart = false;
        boolean inRangeEnd = false;
        boolean inRange = false;
        if (start >= getRangeStart() && start <= getRangeEnd()) {
            inRangeStart = true;
        }
        if (end >= getRangeStart() && end <= getRangeEnd()) {
            inRangeEnd = true;
        }
        if (inRangeStart || inRangeEnd) {
            inRange = true;

        }
        if (inRange && !inRangeStart) {
            this.setRangeStart(start);
        }
        if (inRange && !inRangeEnd) {
            this.setRangeEnd(end);
        }
        return inRange;
    }

    @Override
    public int compareTo(Object o) {
        int result = 0;
        Range sr = (Range) o;
        if (this.getRangeStart() > sr.getRangeStart()) {
            result = 1;
        } else if (this.getRangeStart() < sr.getRangeStart()) {
            result = -1;
        }
        return result;
    }
}
