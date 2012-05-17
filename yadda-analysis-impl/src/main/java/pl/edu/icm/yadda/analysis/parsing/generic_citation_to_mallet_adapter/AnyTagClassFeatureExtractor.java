package pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.ObjectOperator;

public class AnyTagClassFeatureExtractor {
	
	public static List<List<Feature>> execute(Object[] tokens, ObjectOperator textgetter){
		return calculateFeatureListOfTags(tokens, textgetter);
	}
	
	private static List<List<Feature>> calculateFeatureListOfTags(
			Object[] tokens, ObjectOperator textgetter) {
		
		List<List<Feature>> tok_feature_list = new LinkedList<List<Feature>>(); 
		
		Object token = null;
		List<Feature> feature_list = null;
		
		for(int i = 0; i < tokens.length; i++){
			token = tokens[i];
			String tok_text = textgetter.execute(token);
			feature_list = calculateFeatures(tok_text);
			tok_feature_list.add(feature_list);
			feature_list.add(Feature.TAG_KONCOWY);
		}
		return tok_feature_list;
	}

	private static List<Feature> calculateFeatures(String text){
		ArrayList<Feature> features = new ArrayList<Feature>();
		addChainTokenFeatures(text, features);
		addOneSignFeatures(text, features);
		return features;
	}

	public static void addChainTokenFeatures(String text,
			ArrayList<Feature> features) {
        //check whether the chain is fully numerical or not
        if(isNumerical(text)){
        	doOperationsOnNumerical(text,features); 
        	return;
        }
        //check whether the chain is fully non-numerical or not
        if(isLetterChain(text)){
        	doOperaionsOnLetters(text,features);
        }
        return;
    }

    private static void doOperaionsOnLetters(String text,
			ArrayList<Feature> features) {
    	features.add(Feature.LANCUCH_LITER);
    	if(text.substring(0,1).toLowerCase().equals(text.substring(0,1))){
            features.add(Feature.ZACZYNA_SIE_Z_MALEJ_LITERY);
            if(text.toLowerCase().equals(text))features.add(Feature.ZAWIERA_MALE_ZNAKI);
        }
        else if(text.substring(0,1).toUpperCase().equals(text.substring(0,1))){
            features.add(Feature.ZACZYNA_SIE_Z_DUZEJ_LITERY);
            if(text.toUpperCase().equals(text))features.add(Feature.ZAWIERA_DUZE_ZNAKI);
        }
	}

	private static void doOperationsOnNumerical(String text,
			ArrayList<Feature> features) {
    	//mark that tag is numerical
    	features.add(Feature.LANCUCH_CYFR);
    	//check if chain is a year
    	if(isYear(text)){
        	features.add(Feature.ZAWIERA_ROK);
        	return;
        }
	}

	private static boolean isLetterChain(String text) {
    	final String NIE_LANCUCH_LITER = "([^a-zA-Z])";
    	Matcher matcherNieLiter = Pattern.compile(NIE_LANCUCH_LITER).matcher(text);
        return !matcherNieLiter.find();
	}

	private static boolean isNumerical(String text) {
		final String NIE_LANCUCH_CYFR = "([^0-9])";
		Matcher matcherNieCyfr = Pattern.compile(NIE_LANCUCH_CYFR).matcher(text);
		return !matcherNieCyfr.find();
	}

	private static boolean isYear(String text) {
		int licz = Integer.parseInt(text);
        if(licz<2099 && licz>1800) return true;
        return false;
	}

	@SuppressWarnings("unused")
	private static void addLengthFeature(String text,
			ArrayList<Feature> features) {
        if(text.length()==1)features.add(Feature.MA_DLUGOSC_JEDEN);
        else if(text.length()==2)features.add(Feature.MA_DLUGOSC_DWA);
    }

    private static void addOneSignFeatures(String text,
			ArrayList<Feature> features) {
        if(text.equals("&"))features.add(Feature.JEST_AMPERSANDEM);
        else if(text.equals("'"))features.add(Feature.JEST_APOSTROFEM);
        else if(text.equals("*"))features.add(Feature.JEST_ASTERIKSEM);
        else if(text.equals("\\"))features.add(Feature.JEST_BACK_SLASHEM);
        else if(text.equals("^"))features.add(Feature.JEST_CARET);
        else if(text.equals(":"))features.add(Feature.JEST_DWUKROPKIEM);
        else if(text.equals("*"))features.add(Feature.JEST_GWIAZDKA);
        else if(text.equals("."))features.add(Feature.JEST_KROPKA);
        else if(text.equals("@"))features.add(Feature.JEST_MALPA);
        else if(text.equals("-"))features.add(Feature.JEST_MYSLNIKIEM);
        else if(text.equals(">"))features.add(Feature.JEST_NAWIASEM_KATOWYM_KONC);
        else if(text.equals("<"))features.add(Feature.JEST_NAWIASEM_KATOWYM_POCZ);
        else if(text.equals("}"))features.add(Feature.JEST_NAWIASEM_KLAMROWYM_KONC);
        else if(text.equals("{"))features.add(Feature.JEST_NAWIASEM_KLAMROWYM_POCZ);
        else if(text.equals("]"))features.add(Feature.JEST_NAWIASEM_KWADRATOWYM_KONC);
        else if(text.equals("["))features.add(Feature.JEST_NAWIASEM_KWADRATOWYM_POCZ);
        else if(text.equals(")"))features.add(Feature.JEST_NAWIASEM_OKRAGLYM_KONC);
        else if(text.equals("("))features.add(Feature.JEST_NAWIASEM_OKRAGLYM_POCZ);
        else if(text.equals("%"))features.add(Feature.JEST_PROCENTEM);
        else if(text.equals(","))features.add(Feature.JEST_PRZECINKIEM);
        else if(text.equals("?"))features.add(Feature.JEST_PYTAJNIKIEM);
        else if(text.equals("/"))features.add(Feature.JEST_SLASHEM);
        else if(text.equals(";"))features.add(Feature.JEST_SREDNIKIEM);
        else if(text.equals("~"))features.add(Feature.JEST_TYLDA);
        else if(text.equals("!"))features.add(Feature.JEST_WYKRZYKNIKIEM);
        else if(text.equals("#"))features.add(Feature.JEST_HASHEM);
        else if(text.equals("$"))features.add(Feature.JEST_DOLAREM);
        else if(text.equals("="))features.add(Feature.JEST_ROWNA_SIE);
        else if(text.equals("\""))features.add(Feature.JEST_CUDZYSLOWEM);
        else if(text.equals("|"))features.add(Feature.JEST_KRESKA);
    }
}