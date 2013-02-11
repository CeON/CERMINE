/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

/**
 *
 * @author axnow
 */
public class ServiceException extends Exception {

    /**
     * Creates a new instance of
     * <code>ServiceException</code> without detail message.
     */
    public ServiceException() {
    }

    /**
     * Constructs an instance of
     * <code>ServiceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ServiceException(String msg) {
        super(msg);
    }
}
