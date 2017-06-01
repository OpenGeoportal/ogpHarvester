/*
 * IngestFormBean.java
 *
 * Copyright (C) 2013
 *
 * This file is part of Open Geoportal Harvester.
 *
 * This software is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * As a special exception, if you link this library with other files to produce
 * an executable, this library does not by itself cause the resulting executable
 * to be covered by the GNU General Public License. This exception does not
 * however invalidate any other reasons why the executable file might be covered
 * by the GNU General Public License.
 *
 * Authors:: Jose García (mailto:jose.garcia@geocat.net)
 */
package org.opengeoportal.harvester.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class IngestReport extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 2909056496657495298L;

    @Column
    private long restrictedRecords;

    @Column
    private long publicRecords;

    @Column
    private long vectorRecords;

    @Column
    private long rasterRecords;

    @Column
    private long unrequiredFieldWarnings;

    @Column
    private long webServiceWarnings;

    @Column(nullable = false)
    private long failedRecordsCount;

    @OneToOne
    private IngestJobStatus jobStatus;

    @OneToMany
    @JoinColumn(name = "report_id")
    private List<IngestReportError> errors = new ArrayList<IngestReportError>();

    public void addError(final IngestReportError error) {
        this.errors.add(error);
    }

    public List<IngestReportError> getErrors() {
        return this.errors;
    }

    public long getFailedRecordsCount() {
        return this.failedRecordsCount;
    }

    /**
     * @return the jobStatus
     */
    public IngestJobStatus getJobStatus() {
        return this.jobStatus;
    }

    public long getPublicRecords() {
        return this.publicRecords;
    }

    public long getRasterRecords() {
        return this.rasterRecords;
    }

    public long getRestrictedRecords() {
        return this.restrictedRecords;
    }

    public long getUnrequiredFieldWarnings() {
        return this.unrequiredFieldWarnings;
    }

    public long getVectorRecords() {
        return this.vectorRecords;
    }

    public long getWebServiceWarnings() {
        return this.webServiceWarnings;
    }

    public void increaseUnrequiredFieldWarnings() {
        this.unrequiredFieldWarnings++;
    }

    public void setErrors(final List<IngestReportError> errors) {
        this.errors = errors;
    }

    public void setFailedRecordsCount(final long failedRecordsCount) {
        this.failedRecordsCount = failedRecordsCount;
    }

    /**
     * @param jobStatus
     *            the jobStatus to set
     */
    public void setJobStatus(final IngestJobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public void setPublicRecords(final long publicRecords) {
        this.publicRecords = publicRecords;
    }

    public void setRasterRecords(final long rasterRecords) {
        this.rasterRecords = rasterRecords;
    }

    public void setRestrictedRecords(final long restrictedRecords) {
        this.restrictedRecords = restrictedRecords;
    }

    public void setUnrequiredFieldWarnings(final long unrequiredFieldWarnings) {
        this.unrequiredFieldWarnings = unrequiredFieldWarnings;
    }

    public void setVectorRecords(final long vectorRecords) {
        this.vectorRecords = vectorRecords;
    }

    public void setWebServiceWarnings(final long webServiceWarnings) {
        this.webServiceWarnings = webServiceWarnings;
    }

}
