/*
 * Frequency.java
 *
 * Copyright (C) 2013
 *
 * This file is part of Open Geoportal Harvester
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

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Every execution of a Ingest Job creates a new IngestJob.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
@Entity
public class IngestJobStatus extends AbstractPersistable<Long> {
    /** Unique identifier for serialization. */
    private static final long serialVersionUID = -5208109428117150361L;

    /**
     * Each IngestJobStatus has a {@link IngestReportError} where to store the
     * results of the ingest.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "report_id")
    private IngestReport ingestReport;

    /**
     * The status of the execution.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private IngestJobStatusValue status;

    /**
     * Start time.
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    /**
     * End time.
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    /**
     * Job execution identifier. This can be used to retrieve actual job in the
     * scheduler.
     */
    @Column
    private UUID jobExecutionIdentifier;

    /**
     * The owner ingest of the status.
     */
    @ManyToOne
    @JoinColumn(name = "ingest_id")
    private Ingest ingest;

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return this.endTime;
    }

    /**
     * @return the ingest
     */
    public Ingest getIngest() {
        return this.ingest;
    }

    /**
     * @return the ingestReport
     */
    public IngestReport getIngestReport() {
        return this.ingestReport;
    }

    /**
     * @return the jobExecutionIdentifier
     */
    public UUID getJobExecutionIdentifier() {
        return this.jobExecutionIdentifier;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return this.startTime;
    }

    /**
     * @return the status
     */
    public IngestJobStatusValue getStatus() {
        return this.status;
    }

    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @param ingest
     *            the ingest to set
     */
    public void setIngest(final Ingest ingest) {
        this.ingest = ingest;
    }

    /**
     * @param ingestReport
     *            the ingestReport to set
     */
    public void setIngestReport(final IngestReport ingestReport) {
        this.ingestReport = ingestReport;
    }

    /**
     * @param jobExecutionIdentifier
     *            the jobExecutionIdentifier to set
     */
    public void setJobExecutionIdentifier(final UUID jobExecutionIdentifier) {
        this.jobExecutionIdentifier = jobExecutionIdentifier;
    }

    /**
     * @param startTime
     *            the startTime to set
     */
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(final IngestJobStatusValue status) {
        this.status = status;
    }

}
