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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class IngestReportError extends AbstractPersistable<Long> {
    private static final long serialVersionUID = -760972452971727537L;

    /** Error type. */
    @Column
    @Enumerated(EnumType.STRING)
    private IngestReportErrorType type;

    /** Field name where the error exists. */
    @Column
    private String field;

    /** Error message. */
    @Column
    private String message;

    /** Original metadata. */
    @Column
    @Lob
    private String metadata;

    /** Report where the error was detected. */
    @ManyToOne
    private IngestReport report;

    /**
     * @return the field
     */
    public String getField() {
        return this.field;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the metadata
     */
    public String getMetadata() {
        return this.metadata;
    }

    /**
     * @return the report
     */
    public IngestReport getReport() {
        return this.report;
    }

    /**
     * @return the type
     */
    public IngestReportErrorType getType() {
        return this.type;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(final String field) {
        this.field = field;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @param metadata
     *            the metadata to set
     */
    public void setMetadata(final String metadata) {
        this.metadata = metadata;
    }

    /**
     * @param report
     *            the report to set
     */
    public void setReport(final IngestReport report) {
        this.report = report;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final IngestReportErrorType type) {
        this.type = type;
    }

}
