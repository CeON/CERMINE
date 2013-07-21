package pl.edu.icm.cermine.service;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class ExtractionResult {

    Logger log = LoggerFactory.getLogger(ExtractionResult.class);
    String requestMD5;
    Date submit, processingStart, processingEnd;
    String nlm;
    String dublinCore;
    ArticleMeta meta;
    String html;
    boolean succeeded;
    Throwable error;

    public ExtractionResult() {
    }

    public String getRequestMD5() {
        return requestMD5;
    }

    public void setRequestMD5(String requestMD5) {
        this.requestMD5 = requestMD5;
    }

    public Date getSubmit() {
        return submit;
    }

    public void setSubmit(Date submit) {
        this.submit = submit;
    }

    public Date getProcessingStart() {
        return processingStart;
    }

    public void setProcessingStart(Date processingStart) {
        this.processingStart = processingStart;
    }

    public Date getProcessingEnd() {
        return processingEnd;
    }

    public void setProcessingEnd(Date processingEnd) {
        this.processingEnd = processingEnd;
    }

    public String getNlm() {
        return nlm;
    }

    public void setNlm(String nlm) {
        this.nlm = nlm;
    }

    public String getDublinCore() {
        return dublinCore;
    }

    public void setDublinCore(String dublinCore) {
        this.dublinCore = dublinCore;
    }

    public ArticleMeta getMeta() {
        return meta;
    }

    public void setMeta(ArticleMeta meta) {
        this.meta = meta;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public double getQueueTimeSec() {
        return (processingStart.getTime() - submit.getTime()) / 1000.;
    }

    public double getProcessingTimeSec() {
        return (processingEnd.getTime() - processingStart.getTime()) / 1000.;
    }

    public String getErrorMessage() {
        String res;
        if (error != null) {
            res = error.getMessage();
            if(res==null || res.isEmpty()) {
                res = "Exception is: "+error.getClass().toString();
            }
        } else {
            res = "Unknown error";
            log.warn("Unexpected question for error message while no exception. Wazzup?");
        }
        return res;
    }
}
