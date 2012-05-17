package pl.edu.icm.yadda.analysis.relations.auxil.parallel;



/**
 * Code taken from http://stackoverflow.com/questions/4010185/parallel-for-for-java
 * Person who wrote answer: @author mlaw
 * 
 * @author pdendek
 *
 */
public interface Operation<T> {
    public void perform(T pParameter);

	public Operation<T> replicate();
	public void setUp() throws Exception;
	public void shutDown() throws Exception;
}
