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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Check;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Check(constraints = "(url IS NULL AND repository_id IS NOT NULL) OR (url IS NOT NULL AND repository_id IS NULL)")
public abstract class Ingest extends AbstractPersistable<Long> {
    /** Version UID for serialisation. */
    private static final long serialVersionUID = -3390351777452085398L;

    /** Ingest name. */
    @Column(unique = true, nullable = false)
    private String name;

    /** Ingest begin date. */
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date beginDate;

    /** Frequency of execution. */
    @Column
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    /**
     * Repository URL. If it is <code>null</code> a {@link CustomRepository}
     * must be provided instead.
     */
    @Column
    private String url;

    /** <code>true</code> if there is any future execution scheduled. */
    @Column
    private Boolean scheduled;

    /** Last run timestamp. */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastRun;

    /** Repository where the data will be stored. */
    @Column
    private String nameOgpRepository;

    /**
     * Mandatorye fields that a record must have to be stored into the local
     * instance.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private final Set<String> requiredFields = new HashSet<String>();

    /**
     * Set of the fields that an ingest can have. This can be different for each
     * child class.
     */
    @Transient
    protected Set<String> validRequiredFields = new HashSet<String>();

    /**
     * If {@link Ingest#url} is null a custom repository has to be set. The data
     * will be retrieved from this repository.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "repository_id")
    private CustomRepository repository;

    /**
     * List of previous executions of the ingest.
     */
    @OneToMany
    @JoinColumn(name = "ingest_id")
    private List<IngestJobStatus> ingestJobStatuses = new ArrayList<IngestJobStatus>();

    public void addJobStatus(final IngestJobStatus job) {
        this.ingestJobStatuses.add(job);
    }

    /**
     * Sets the attribute with the given name to the given value.
     * 
     * @param field
     *            must not be {@literal null} or empty.
     */
    public void addRequiredField(final String field) {
        Assert.hasText(field);

        if (this.validRequiredFields.contains(field)) {
            this.requiredFields.add(field);
        }
    }

    /**
     * Return the actual Ingest URL. This can be the URL of the associated
     * {@link CustomRepository} or the one stored in {@link Ingest#url}
     * property.
     * 
     * @return the actual URL.
     */
    public String getActualUrl() {
        String actualUrl = null;

        if (this.getRepository() != null) {
            actualUrl = this.getRepository().getUrl();
        } else {
            actualUrl = this.url;
        }

        return actualUrl;

    }

    /**
     * @return the beginDate
     */
    public Date getBeginDate() {
        return this.beginDate;
    }

    /**
     * @return the frequency
     */
    public Frequency getFrequency() {
        return this.frequency;
    }

    /**
     * @return the ingestJobStatuses
     */
    public List<IngestJobStatus> getIngestJobStatuses() {
        return this.ingestJobStatuses;
    }

    /**
     * @return the lastRun
     */
    public Date getLastRun() {
        return this.lastRun;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the nameOgpRepository
     */
    public String getNameOgpRepository() {
        return this.nameOgpRepository;
    }

    /**
     * @return the repository
     */
    public CustomRepository getRepository() {
        return this.repository;
    }

    /**
     * Returns all the required fields of the {@link Ingest}.
     * 
     * @return
     */
    public Set<String> getRequiredFields() {
        return this.requiredFields;
    }

    /**
     * @return the scheduled
     */
    public Boolean getScheduled() {
        return this.scheduled;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Returns all the valid required fields that can be added to the
     * {@link Ingest}.
     * 
     * @return
     */
    public Set<String> getValidRequiredFields() {
        return Collections.unmodifiableSet(this.validRequiredFields);
    }

    public Boolean isScheduled() {
        return this.scheduled;
    }

    /**
     * @param beginDate
     *            the beginDate to set
     */
    public void setBeginDate(final Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * @param frequency
     *            the frequency to set
     */
    public void setFrequency(final Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * @param ingestJobStatuses
     *            the IngestJobStatus list to set
     */
    public void setIngestJobStatuses(
            final List<IngestJobStatus> ingestJobStatuses) {
        this.ingestJobStatuses = ingestJobStatuses;
    }

    /**
     * @param lastRun
     *            the lastRun to set
     */
    public void setLastRun(final Date lastRun) {
        this.lastRun = lastRun;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param nameOgpRepository
     *            the nameOgpRepository to set
     */
    public void setNameOgpRepository(final String nameOgpRepository) {
        this.nameOgpRepository = nameOgpRepository;
    }

    /**
     * @param repository
     *            the repository to set
     */
    public void setRepository(final CustomRepository repository) {
        this.repository = repository;
    }

    public void setScheduled(final Boolean scheduled) {
        this.scheduled = scheduled;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }
}
