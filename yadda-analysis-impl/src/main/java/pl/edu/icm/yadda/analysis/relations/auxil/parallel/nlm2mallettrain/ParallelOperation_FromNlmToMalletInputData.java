package pl.edu.icm.yadda.analysis.relations.auxil.parallel.nlm2mallettrain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.bibref.parsing.tools.NlmCitationExtractor;
import pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.citationtoken.CitationTokenToMalletTrainingFile;
import pl.edu.icm.yadda.analysis.relations.auxil.parallel.Operation;
import pl.edu.icm.yadda.analysis.relations.auxil.trash.YToCatObjProcessingNode;
import pl.edu.icm.yadda.imports.transformers.NlmToYTransformer;

public class ParallelOperation_FromNlmToMalletInputData implements Operation<File> {

    static YToCatObjProcessingNode o3 = new YToCatObjProcessingNode();
    static NlmToYTransformer nlmToYransformer = new NlmToYTransformer();

    public ParallelOperation_FromNlmToMalletInputData(){
    }
    
	@Override
	public void perform(File f) {
		System.out.println("\r"+f.getAbsolutePath());
		try{
			List<Citation> citations = new ArrayList<Citation>();
			citations.addAll(NlmCitationExtractor.extractCitations(new InputSource(new FileInputStream(f))));
			
			StringBuffer out = new StringBuffer();
			for(Citation citation : citations){
				List<CitationToken> tmp = citation.getTokens(); 
				CitationToken[] ct = (CitationToken[]) tmp.toArray(new CitationToken[tmp.size()]);
				CitationTokenToMalletTrainingFile.execute(out,ct , 5);
			}
			
			File destFile = new File(f.getAbsolutePath()+".cit.txt");
			if(destFile.exists()) destFile.delete();
			writeToFile(out,destFile);
			
        }catch(Exception e){
        	System.out.println("In file: "+f.getAbsolutePath());
        	System.out.println("Following exception occured:");
        	e.printStackTrace();
        }
	}
	
	private void writeToFile(StringBuffer out, File destFile) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(destFile));
		bw.write(out.toString());
		bw.flush();
		bw.close();
	}

	@Override
	public Operation<File> replicate() {
		return this;
	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutDown() {
		// TODO Auto-generated method stub
		
	}	
}
