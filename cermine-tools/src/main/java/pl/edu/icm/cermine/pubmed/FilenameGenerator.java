package pl.edu.icm.cermine.pubmed;

import java.io.IOException;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

public class FilenameGenerator extends EvalFunc<String> {

	@Override
	public String exec(Tuple input) throws IOException {
		String journalDir = (String) input.get(0);
		return journalDir+".xml";
	}

}
