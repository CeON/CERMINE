package pl.edu.icm.cermine.exception;

import java.io.Serializable;

public class TransformationException extends Exception implements Serializable {
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
