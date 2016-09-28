/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.service;

import java.util.Date;

/**
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
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
