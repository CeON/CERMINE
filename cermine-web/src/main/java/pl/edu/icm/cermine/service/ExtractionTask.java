/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

import java.util.Date;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class ExtractionTask {
    private long id;
    byte[] pdf;
    String md5Sum;
    
    
    public static enum TaskStatus {
        CREATED,
        QUEUED,
        PROCESSING,
        FINISHED
    }
    private TaskStatus status;
    private Date creationDate;
    private String clientAddress;
    
    private ExtractionResult result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public ExtractionResult getResult() {
        return result;
    }

    public void setResult(ExtractionResult result) {
        this.result = result;
    }
    
    
    
}
