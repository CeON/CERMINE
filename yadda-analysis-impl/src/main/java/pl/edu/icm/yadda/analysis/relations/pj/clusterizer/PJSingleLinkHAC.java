package pl.edu.icm.yadda.analysis.relations.pj.clusterizer;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.Clusterizer;
import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJElement;

/**
 * Complexity: O(N^2)
 * Method to replace similarity: only maximum 
 * 
 * Similarity replacement in {@link PJSimpleHAC} 
 * 
 * @author pdendek
 *
 */
public class PJSingleLinkHAC implements Clusterizer{
	
	private static final Logger log = LoggerFactory.getLogger(PJSingleLinkHAC.class);
	
	public static void main(String[] args){
		double[][] in = {{},{15},{46,3},{2,-18,-20},{-100,-100,3,-200}};
		int[] out = new PJSingleLinkHAC().clusterize(in);
		StringBuilder sb = new StringBuilder(""); 
		for(int i : out) sb.append(i+"\t");
		sb.append("\n");
		log.info(sb.toString());
	}
	
	/**
	 * method giving hierarchical agglomerative clustering among contributors
	 * @param d distances between contributors (level of similarity)
	 * @return  
	 */
	@Override
	public int[] clusterize(double sim[][]){
		int[] I = new int[sim.length];
		PJElement[] nearBestMatch = new PJElement[sim.length];
//		List<SimpleEntry<Double, SimpleEntry<Integer, Integer>>> A = new LinkedList<SimpleEntry<Double, SimpleEntry<Integer, Integer>>>(); 
		
		PJElement[][] C = new PJElement[sim.length][];
		for(int n=0;n<sim.length;n++){
			C[n] = new PJElement[n];
			for(int i=0;i<n;i++){
				C[n][i] = new PJElement(sim[n][i],i);
			}
			I[n]=n;
			nearBestMatch[n]=argMaxElement(C[n]);
		}
		
		int i1=-1,i2=-1;
		for(int n=1;n<sim.length;n++){
			i1=argMaxSequenceIndexExcludeSame(nearBestMatch,I);
			if(i1==-1) continue;
			i2=I[nearBestMatch[i1].index];
			if(i1==i2) continue;
			double simil = (i1 > i2) ? C[i1][i2].sim : C[i2][i1].sim; 
			if(simil<0)
				return I;
			
			
//			A.add(new SimpleEntry<Double, SimpleEntry<Integer,Integer>>(simil,new SimpleEntry<Integer, Integer>(i1,i2)));
			
			for(int i =0; i<I.length;i++){
				if(I[i]==i && i!=i1 && i!=i2)
					if(i1>i && i2>i)
						C[i1][i].sim=SIM(C[i1][i].sim, C[i2][i].sim);
					else if(i1>i && i2<i)
						C[i1][i].sim=SIM(C[i1][i].sim, C[i][i2].sim);
					else if(i1<i && i2>i)
						C[i][i1].sim=SIM(C[i][i1].sim, C[i2][i].sim);
					else //if(i1<i && i2<i)
						C[i][i1].sim=SIM(C[i][i1].sim, C[i][i2].sim);
				if(I[i]==i2)
					I[i]=i1;
			}
			nearBestMatch[i1] = argMaxElementWithConstraints(C[n], I, n);
		}
		return I;
	}

	private int argMaxSequenceIndexExcludeSame(PJElement[] nearBestMatch, int[] I) {
		double maxval=Double.NEGATIVE_INFINITY;
		int maxvalindex=-1;
		
		for(int i=0;i<nearBestMatch.length;i++){
			if(I[i]!=i)continue;
			if(nearBestMatch[i]==null)continue;
			if(i==I[nearBestMatch[i].index])continue;
			if(maxval<nearBestMatch[i].sim){
				maxval=nearBestMatch[i].sim;
				maxvalindex=i;
			}
		}
		return maxvalindex;
	}

	private int argMaxSequenceIndex(PJElement[] nearBestMatch, int[] I) {
		double maxval=Double.NEGATIVE_INFINITY;
		int maxvalindex=-1;
		
		for(int i=0;i<nearBestMatch.length;i++){
			if(I[i]!=i)continue;
			if(nearBestMatch[i]==null)continue;
			if(maxval<nearBestMatch[i].sim){
				maxval=nearBestMatch[i].sim;
				maxvalindex=i;
			}
		}
		return maxvalindex;
	}

	private PJElement argMaxElementWithConstraints(PJElement[] Cn,
			int[] I, int forbidden) {
		double maxval = -1;
		PJElement retEl = null;
		for(int i=0;i<Cn.length;i++){
			if(i==forbidden)continue;
			if(I[i]!=i)continue;
			if(Cn[i].sim>maxval){
				maxval=Cn[i].sim;
				retEl=Cn[i];
			}
		}
		return retEl;
	}
	
	private PJElement argMaxElement(PJElement[] Cn) {
		if(Cn.length==0){
			return null;
		}
		
		PJElement[] Cn_copy = new PJElement[Cn.length];
		
		System.arraycopy(Cn, 0, Cn_copy, 0, Cn.length);
		Arrays.sort(Cn_copy);
		PJElement el = Cn_copy[Cn_copy.length-1];
		Cn_copy=null;
		return el;
	}
	
	@SuppressWarnings("unused")
	private double argMax(double[] sim_n) {
		double[] sim_n_copy = new double[sim_n.length];
		System.arraycopy(sim_n, 0, sim_n_copy, 0, sim_n.length);
		Arrays.sort(sim_n_copy);
		double d = sim_n_copy[sim_n_copy.length-1];
		sim_n_copy=null;
		return d;
	}
	
	private double SIM(double a, double b) {
		return Math.max(a, b);
	}
	
	public Object clone(){
		return new PJSingleLinkHAC();
	}
}