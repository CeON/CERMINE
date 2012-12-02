package pl.edu.icm.cermine.tools.transformers;

import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Interface for converters between models.
 *
 * @author Dominika Tkaczyk
 * @param <S> type of input model
 * @param <T> type of output model
 */
public interface ModelToModelConverter<S, T> {
    
    /**
     * Converts source model into the target model.
     * 
     * @param source the source object
     * @param hints additional hints used during the conversion
     * @return the converted object
     * @throws TransformationException 
     */
    T convert(S source, Object... hints) throws TransformationException;
    
}
