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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
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
