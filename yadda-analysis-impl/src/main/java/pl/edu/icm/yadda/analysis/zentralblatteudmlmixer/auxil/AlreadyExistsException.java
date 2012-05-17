package pl.edu.icm.yadda.analysis.zentralblatteudmlmixer.auxil;

public class AlreadyExistsException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5722016903429087638L;

	public AlreadyExistsException(){
		super();
	}
	
	public AlreadyExistsException(final String string){
		super(string);
	}
}
