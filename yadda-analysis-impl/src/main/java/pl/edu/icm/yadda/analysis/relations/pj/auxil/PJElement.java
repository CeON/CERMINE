package pl.edu.icm.yadda.analysis.relations.pj.auxil;

public class PJElement implements Comparable<Object>{
	public double sim;
	public int index;
	
	public PJElement(double sim, int index){
		this.sim=sim;
		this.index=index;
	}
	
	@Override
	public int compareTo(Object o2) {
		if(o2==null) return 1;
		if(!(o2 instanceof PJElement)) throw new ClassCastException("" +
				"Comparison between "+this.getClass()+" and "+o2.getClass()+" is illegal!");
		double count = this.sim-((PJElement)o2).sim; 
		if(count>0) return 1;
		else if(count==0) return 0;
		else return -1;
	}
}