package pl.edu.icm.cermine.service;

import java.util.Date;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class ExtractionTask {

    public static enum TaskStatus {

        CREATED("queue", "SUBMITTED"),
        QUEUED("queue", "QUEUED"),
        PROCESSING("processing", "PROCESSING"),
        FINISHED("success", "SUCCESS", true),
        FAILED("failure", "FAILURE", true);
        String css;
        String text;
        boolean finalState;

        
        TaskStatus(String css, String text) {
            this(css, text, false);
        }
        
        TaskStatus(String css, String text, boolean f) {
            this.css = css;
            this.text = text;
            finalState = f;
        }

        public String getCss() {
            return css;
        }

        public String getText() {
            return text;
        }

        public boolean isFinalState() {
            return finalState;
        }
        
        
    }
    private long id;
    byte[] pdf;
    String fileName;
    String md5Sum;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFinished() {
        return status.isFinalState();
    }

    public boolean isSucceeded() {
        return isFinished() && status!=TaskStatus.FAILED;
    }
    
    public boolean isFailed() {
        return status==TaskStatus.FAILED;
    }

    
    
}
