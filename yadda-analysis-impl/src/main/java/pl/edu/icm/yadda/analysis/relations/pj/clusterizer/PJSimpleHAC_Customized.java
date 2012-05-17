package pl.edu.icm.yadda.analysis.relations.pj.clusterizer;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.Clusterizer;

/**
 * Gives wrong results!!!
 * 
 * Complexity: O(N^3)
 * Method to replace similarity: minimum if lower then zero, maximum otherwise
 * 
 * Class providing O(N^3) clusterizing (naive) method.
 * The method behaves like described in {@link PJSingleLinkHAC_Customized},
 * which means it choose the lowest similarity if it is below zero,
 * if not it takes the highest similarity
 * 
 * @author pdendek
 *
 */
public class PJSimpleHAC_Customized implements Clusterizer{
	
	private static final Logger log = LoggerFactory.getLogger(PJSimpleHAC_Customized.class);
	
	public static void main(String[] args){
		double[][] in = {{},{15},{46,3},{2,-18,-20},{-100,-100,3,-200}};
		int[] out = new PJSimpleHAC_Customized().clusterize(in);
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
	public int[] clusterize(double[][] sim) {
		int I[] = new int[sim.length];
		Arrays.fill(I, 1);
		int[] retArr = new int[sim.length];
		
		for(int i=0;i<sim.length;i++) retArr[i]=i;
		
		for(int k=0;k<sim.length;k++){
			SimpleEntry<Double, SimpleEntry<Integer, Integer>> se = agrMax(sim, I);
			
			if(se.getValue().getValue()!=-1)
				retArr[se.getValue().getValue()]=se.getValue().getKey();
			
			for(int j=0;j<se.getValue().getKey();j++){
				if(j==se.getValue().getKey()||j==se.getValue().getValue())continue;
				sim[se.getValue().getKey()][j]=SIM(se.getValue().getKey(), se.getValue().getValue(), j, sim);
			}
			if(se.getValue().getValue()!=-1)
				I[se.getValue().getValue()]=0;
		}
		return I;
	}

	private double SIM(int k1, int k2, int i, double[][] sim) {
		if(i>k1 && i>k2) 
			return SIM(sim[i][k1], sim[i][k2]);
		else if(i>k1 && i<k2) 
			return SIM(sim[i][k1], sim[k2][i]);
		else if(i<k1 && i>k2) 
			return SIM(sim[k1][i], sim[i][k2]);
		else //if(i<k1 && i<k2) 
			return SIM(sim[k1][i], sim[k2][i]);
	}

	private double SIM(double a, double b) {
		return minMax(a,b);
	}
	
	public double minMax(double a, double b){
		if(a<0||b<0)
			return Math.min(a, b);
		else return Math.max(a, b);
	}
	
	
	private SimpleEntry<Double, SimpleEntry<Integer, Integer>> agrMax(double[][] sim, int[] I) {
		double maxval=Double.MIN_VALUE; 
		int k1=-1, k2=-1;
		for(int i=0;i<sim.length;i++)
			for(int j=0;j<i;j++){
				if(I[i]== 0 || I[j]==0) continue;
				if(maxval<sim[i][j]){
					maxval= sim[i][j];
					k1=i;
					k2=j;
				}
			}
		return new SimpleEntry<Double, SimpleEntry<Integer, Integer>>(maxval,new SimpleEntry<Integer,Integer>(k1,k2));
	}
	
	public Object clone(){
		return new PJSimpleHAC_Customized();
	}
}
