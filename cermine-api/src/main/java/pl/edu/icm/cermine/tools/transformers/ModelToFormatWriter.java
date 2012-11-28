package pl.edu.icm.cermine.tools.transformers;

import java.io.Writer;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Interface for writers of model objects.
 * 
 * @author Dominika Tkaczyk
 * @param <T> the type of model
 */
public interface ModelToFormatWriter<T> {
    
    /**
     * Writes a model object to a string.
     * 
     * @param object a model object
     * @param hints additional hints used during the conversion
     * @return written object
     * @throws TransformationException 
     */
    String write(T object, Object... hints) throws TransformationException;
    
    /**
     * Writes a model object using the given writer.
     * 
     * @param writer writer
     * @param object a model object
     * @param hints additional hints used during the conversion
     * @throws TransformationException 
     */
    void write(Writer writer, T object, Object... hints) throws TransformationException;

}
