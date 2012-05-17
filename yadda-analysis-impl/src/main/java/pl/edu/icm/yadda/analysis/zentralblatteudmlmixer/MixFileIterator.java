package pl.edu.icm.yadda.analysis.zentralblatteudmlmixer;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.NotImplementedException;

import pl.edu.icm.yadda.analysis.zentralblatteudmlmixer.auxil.MixRecord;

/**
 * Iterates over stream of MixFile which contains mapping between Zbl and EuDML.
 * 
 * Sample is:
 * 00000932 0767.93061
 * 
 * Where 00000932 is Zbl ID and 0767.93061 is EuDML ID 
 * 
 * @author pdendek
 */
public class MixFileIterator implements Iterator<MixRecord> {
    protected BufferedReader br = null;
    protected String nextLine = null;
    protected MixRecord currLine = null;

    public MixFileIterator(File f) throws IOException {
    	FileReader fr = new FileReader(f);
        br = new BufferedReader(fr);
        try {
			nextLine = br.readLine();
		} catch (IOException e) {
			nextLine = null;
		}
    }
    
    public MixFileIterator(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        try {
			nextLine = br.readLine();
		} catch (IOException e) {
			nextLine = null;
		}
    }

	public MixFileIterator(Reader isr) throws IOException {
        br = new BufferedReader(isr);
        try {
			nextLine = br.readLine();
		} catch (IOException e) {
			nextLine = null;
		}
    }

	@Override
	public boolean hasNext() {
		if(nextLine!=null) return true;
		else return false;
	}

	@Override
	public MixRecord next() {
		MixRecord mr = new MixRecord(nextLine.substring(0, nextLine.indexOf(" ")), nextLine.substring(nextLine.indexOf(" ")+1));
		try {
			nextLine = br.readLine();
		} catch (IOException e) {
			nextLine = null;
		}
		return mr;
	}
	
	@Override
	public void remove() {
		throw new NotImplementedException();	
	}
}
