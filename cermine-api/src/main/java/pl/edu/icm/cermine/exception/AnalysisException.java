package pl.edu.icm.cermine.exception;

import java.io.Serializable;

/**
 * Thrown when an unrecoverable problem occurs during analysis.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class AnalysisException extends Exception implements Serializable {
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
