package pl.edu.icm.cermine.evaluation.tools;

import java.io.File;

public class PdfNlmPair {
	public File pdf;
	public File nlm;
	
	public PdfNlmPair(File pdf, File nlm) {
		super();
		this.pdf = pdf;
		this.nlm = nlm;
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
