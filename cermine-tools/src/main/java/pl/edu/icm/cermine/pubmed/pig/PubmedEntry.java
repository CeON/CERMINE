package pl.edu.icm.cermine.pubmed.pig;

import java.io.File;

public class PubmedEntry {

    private String key;
    private File pdf;
    private File nlm;

    public PubmedEntry() {
    }

    public PubmedEntry(String key, File pdf, File nlm) {
        super();
        this.key = key;
        this.pdf = pdf;
        this.nlm = nlm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public File getPdf() {
        return pdf;
    }

    public void setPdf(File pdf) {
        this.pdf = pdf;
    }

    public File getNlm() {
        return nlm;
    }

    public void setNlm(File nlm) {
        this.nlm = nlm;
    }
}
