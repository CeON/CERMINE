package pl.edu.icm.yadda.analysis.relations.pj.auxil;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;

class SimpleEnteryComparator implements Comparator<SimpleEntry<Double, SimpleEntry<Integer, Integer>>>{

	@Override
	public int compare(SimpleEntry<Double, SimpleEntry<Integer, Integer>> o1,
			SimpleEntry<Double, SimpleEntry<Integer, Integer>> o2) {
		if(o1==null&&o2==null) return 0;
		if(o1==null) return 1;
		if(o2==null) return -1;
		
		if(
				!(o1 instanceof SimpleEntry)
				|| !(o1 instanceof SimpleEntry)
		  ) throw new ClassCastException("incorect comparison between "+o1.getClass()+" and "+o2.getClass());
		
		return (int) (o1.getKey()-o2.getKey());
	}
	
}