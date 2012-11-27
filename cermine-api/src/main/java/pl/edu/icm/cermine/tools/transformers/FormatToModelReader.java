package pl.edu.icm.cermine.tools.transformers;

import java.io.Reader;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Interface for readers of model objects.
 * 
 * @author Dominika Tkaczyk
 * @param <T> the type of model
 */
public interface FormatToModelReader<T> {
    
    /**
     * Reads the format into the model object.
     * 
     * @param string input object in a certain format
     * @param hints additional hints used during the conversion
     * @return a model object
     * @throws TransformationException 
     */
    T read(String string, Object... hints) throws TransformationException;
    
    /**
     * Reads the format into the model object.
     * 
     * @param reader input reader
     * @param hints additional hints used during the conversion
     * @return a model object
     * @throws TransformationException 
     */
    T read(Reader reader, Object... hints) throws TransformationException;

}
