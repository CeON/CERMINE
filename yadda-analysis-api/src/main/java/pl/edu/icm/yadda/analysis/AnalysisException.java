package pl.edu.icm.yadda.analysis;

/**
 * Thrown when an unrecoverable problem occurs during analysis.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class AnalysisException extends Exception {
	private static final long serialVersionUID = 4601197315845837554L;

	public AnalysisException() {
		super();
	}

	public AnalysisException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnalysisException(String message) {
		super(message);
	}

	public AnalysisException(Throwable cause) {
		super(cause);
	}
}
