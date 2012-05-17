package pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter;

import java.util.List;

public class AnyTagClassToMalletTrainingFile {
	
	public static void execute(StringBuffer out, Object[] tokens, ObjectOperator textgetter, ObjectOperator labelgetter, int max_distance){
		List<List<Feature>> tag_feature_list = AnyTagClassFeatureExtractor.execute(tokens, textgetter); 
		transferToFile(out, tokens, textgetter, labelgetter,max_distance,tag_feature_list);
	}

    private static void transferToFile(StringBuffer out, Object[] tokens,
			ObjectOperator textgetter, ObjectOperator labelgetter, int max_dist,
			List<List<Feature>> tag_feature_list) {
    	//the index of a considered object called X from the citations array
    	int i_outer = 0;
    	//the index of a considered object Y (a neighbor of X) from a citation array
        int i_inner = 0;
        
        for(i_outer=0;i_outer<tokens.length;i_outer++){
        	outerRoutine(tokens, textgetter, labelgetter,tag_feature_list, i_outer, out);
        	
        	i_inner = Math.max(0, i_outer - max_dist); 
        	for(;i_inner<tokens.length 
        		&& i_inner<(i_outer+max_dist);i_inner++){
        		innerRoutine(labelgetter.execute(tokens[i_inner]) , tag_feature_list, out, i_inner, i_outer);
        	}
        }
	}


	private static void innerRoutine(String label,
			List<List<Feature>> tag_feature_list, StringBuffer sb,
			int i_inner, int i_outer) {
		
		if(i_inner==i_outer) return;
		int pos = i_inner-i_outer;
		
		List<Feature> inner_features = tag_feature_list.get(i_inner);
		
		for(Feature feature : inner_features){
		    sb.append(feature+"@"+pos+" ");
		}
		sb.append(label+"@"+pos+" ");
		
		return;
	}


	private static void outerRoutine(Object[] tokens,
			ObjectOperator textgetter, ObjectOperator labelgetter,
			List<List<Feature>> tag_feature_list, int i_outer, StringBuffer sb) {
		String out_label = labelgetter.execute(tokens[i_outer]);
		String out_text = textgetter.execute(tokens[i_outer]);
		List<Feature> out_features = tag_feature_list.get(i_outer);
		sb.append("\n" + out_label + " ---- ");
		sb.append("W="+out_text+" ");
		for(Feature feature : out_features){
		    sb.append(feature+" ");
		}
		return;
	}
}
