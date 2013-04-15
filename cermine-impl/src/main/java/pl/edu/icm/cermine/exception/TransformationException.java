package pl.edu.icm.cermine.exception;

/**
 * Thrown when an unrecoverable problem occurs during transformations.
 * 
 * @author Dominika Tkaczyk
 *
 */
public class TransformationException extends Exception {
        private static final long serialVersionUID = -4871017514024156616L;

        public TransformationException() {
                super();
        }

        public TransformationException(String message, Throwable cause) {
                super(message, cause);
        }

        public TransformationException(String message) {
                super(message);
        }

        public TransformationException(Throwable cause) {
                super(cause);
        }
}
