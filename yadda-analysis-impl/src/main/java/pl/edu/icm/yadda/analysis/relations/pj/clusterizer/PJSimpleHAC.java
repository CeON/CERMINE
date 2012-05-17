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
 * Method to replace similarity: only maximum
 * 
 * Similarity replacement in {@link PJSingleLinkHAC}
 * 
 * Class providing O(N^3) clusterizing (naive) method.
 * It is simpliest implementation of SingleLinkMethod.
 * Similarity of any object in cluster A to object b
 * is similarity of the most similar object in cluster A to object b. 
 * 
 * There are also implementation 
 * * choosing minimum similarity over maximum only if this similarity is lower then zero: {@link PJSimpleHAC_Customized}
 * * in better time complexity {@link PJSingleLinkHAC}
 * * in better time complexity and choosing minimum similarity over maximum only if this similarity is lower then zero: {@link PJSingleLinkHAC_Customized}
 * 
 * @author pdendek
 *
 */
public class PJSimpleHAC implements Clusterizer{
	
	
	private static final Logger log = LoggerFactory.getLogger(PJSimpleHAC.class);
	
	public static void main(String[] args){
		double[][] in = {{},{15},{46,3},{2,-18,-20},{-100,-100,3,-200}};
		int[] out = new PJSimpleHAC().clusterize(in);
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
				sim[se.getValue().getKey()][j]=  SIM(se.getValue().getKey(), se.getValue().getValue(), j, sim);
			}
			if(se.getValue().getValue()!=-1)
				I[se.getValue().getValue()]=0;
		}
		return retArr;
	}

	private double SIM(int k1, int i, int k2, double[][] sim) {
			if(i>k1 && i>k2) 
				return Math.max(sim[i][k1], sim[i][k2]);
			else if(i>k1 && i<k2) 
				return Math.max(sim[i][k1], sim[k2][i]);
			else if(i<k1 && i>k2) 
				return Math.max(sim[k1][i], sim[i][k2]);
			else //if(i<k1 && i<k2) 
				return Math.max(sim[k1][i], sim[k2][i]);			
	}

	/**
	 * cluster1>cluster2
	 * 
	 * 
	 * @param sim
	 * @param I
	 * @return [best_similarity:double,[cluster1:integer,cluster2:integer]],
	 * cluster1>cluster2 
	 */
	private SimpleEntry<Double, SimpleEntry<Integer, Integer>> agrMax(double[][] sim, int[] I) {
		double maxval=Double.MIN_VALUE; 
		int k1=-1, k2=-1;
		for(int i=0;i<sim.length;i++)
			for(int j=0;j<i;j++){
				if(I[i]==0 || I[j]==0) continue;
				if(maxval<sim[i][j]){
					maxval= sim[i][j];
					k1=i;
					k2=j;
				}
			}
		return new SimpleEntry<Double, SimpleEntry<Integer, Integer>>(maxval,new SimpleEntry<Integer,Integer>(k1,k2));
	}
	
	public Object clone(){
		return new PJSimpleHAC();
	}
}
