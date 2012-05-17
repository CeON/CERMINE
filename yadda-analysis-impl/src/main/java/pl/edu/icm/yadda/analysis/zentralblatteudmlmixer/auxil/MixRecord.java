package pl.edu.icm.yadda.analysis.zentralblatteudmlmixer.auxil;

import java.util.*;

/**
 * 
 * @author pdendek
 *
 */
public class MixRecord {


    private String zbl = null;
    private String nlm = null;

    public MixRecord(String zbl, String nlm) {
    	if(zbl==null || nlm==null) throw new ClassCastException();
    	this.zbl=zbl;
    	this.nlm=nlm;
    }

    public String get10DigitId(){
    	return new String(zbl);
    }
    
    public String getDotId(){
    	return new String(nlm);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof MixRecord)){
            return false;
        }

        MixRecord r = (MixRecord) o;
        if(!r.zbl.equals(this.zbl) || !r.nlm.equals(this.nlm)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "[ "+zbl+", "+nlm+"]";
    }
}
