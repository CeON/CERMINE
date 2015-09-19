/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.structure;

import java.util.Iterator;
import java.util.PriorityQueue;
import org.apache.commons.collections.comparators.ReverseComparator;
import pl.edu.icm.cermine.structure.DocstrumSegmenter.Component;
import pl.edu.icm.cermine.structure.DocstrumSegmenter.Neighbor;
import pl.edu.icm.cermine.structure.DocstrumSegmenter.NeighborReverseDistanceComparator;

/**
 *
 * @author kura
 */
public class PriorityQueueList {
    PriorityQueue<Neighbor> queue;
    int maxSize;
    @SuppressWarnings("unchecked")
    public PriorityQueueList(int maxSize) {
        queue=new PriorityQueue<Neighbor>(maxSize,
            NeighborReverseDistanceComparator.getInstance());
        this.maxSize=maxSize;
    }   
    
    int size() {
       return queue.size();
    }
    
    void add(Neighbor n) {
        queue.offer(n);
        if (queue.size()>maxSize) {
            queue.poll();
        }
    }
    
    void clear(){
        queue.clear();
    }
    
    Neighbor[] getArray(){
        return queue.toArray(new Neighbor[queue.size()]);
    }
    
    double getMaxDist(){
        assert queue.size()>0;
        double maxDist=0;
        Iterator<Neighbor> it=queue.iterator();
        while (it.hasNext()) {
            maxDist=Math.max(maxDist, it.next().getDistance());
        }
        return maxDist;
    }
    
}
