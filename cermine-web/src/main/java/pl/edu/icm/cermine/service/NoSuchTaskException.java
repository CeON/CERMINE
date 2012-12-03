/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class NoSuchTaskException extends Exception {

    long taskId;

    public NoSuchTaskException(long taskId) {
        super(String.format("Task %d is not registered.", taskId));
        this.taskId = taskId;
    }
}
