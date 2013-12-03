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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.hibernate.annotations.Check;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Check(constraints = "(url IS NULL AND repository_id IS NOT NULL) OR (url IS NOT NULL AND repository_id IS NULL)")
public abstract class Ingest extends AbstractPersistable<Long> {
	private static final long serialVersionUID = -3390351777452085398L;

	@Column(unique = true, nullable = false)
	private String name;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date beginDate;

	@Column
	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	@Column
	private String url;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRun;
	
	@Column
	private String nameOgpRepository;

	@ElementCollection
	private Set<String> requiredFields = new HashSet<String>();

	@Transient
	protected Set<String> validRequiredFields = new HashSet<String>();

	@ManyToOne
	@JoinColumn(name = "repository_id")
	private CustomRepository repository;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="ingest_id")
    private List<IngestJobStatus> ingestJobStatuses = new ArrayList<IngestJobStatus>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	/**
	 * Sets the attribute with the given name to the given value.
	 * 
	 * @param field
	 *            must not be {@literal null} or empty.
	 */
	public void addRequiredField(String field) {
		Assert.hasText(field);

		if (validRequiredFields.contains(field))
			requiredFields.add(field);
	}

	/**
	 * Returns all the required fields of the {@link Ingest}.
	 * 
	 * @return
	 */
	public Set<String> getRequiredFields() {
		return Collections.unmodifiableSet(requiredFields);
	}

	/**
	 * Returns all the valid required fields that can be added to the
	 * {@link Ingest}.
	 * 
	 * @return
	 */
	public Set<String> getValidRequiredFields() {
		return Collections.unmodifiableSet(validRequiredFields);
	}

	/**
	 * @return the repository
	 */
	public CustomRepository getRepository() {
		return repository;
	}

	/**
	 * @param repository
	 *            the repository to set
	 */
	public void setRepository(CustomRepository repository) {
		this.repository = repository;
	}

	/**
	 * @return the lastRun
	 */
	public Date getLastRun() {
		return lastRun;
	}

	/**
	 * @param lastRun
	 *            the lastRun to set
	 */
	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	/**
	 * @return the nameOgpRepository
	 */
	public String getNameOgpRepository() {
		return nameOgpRepository;
	}

	/**
	 * @param nameOgpRepository the nameOgpRepository to set
	 */
	public void setNameOgpRepository(String nameOgpRepository) {
		this.nameOgpRepository = nameOgpRepository;
	}


    /**
     * @return the ingestJobStatuses
     */
    public List<IngestJobStatus> getIngestJobStatuses() {
        return Collections.unmodifiableList(ingestJobStatuses);
    }

    /**
     * @param ingestJobStatuses the IngestJobStatus list to set
     */
    public void setIngestJobStatuses(List<IngestJobStatus> ingestJobStatuses) {
        this.ingestJobStatuses = ingestJobStatuses;
    }

    public void addJobStatus(IngestJobStatus job) {
        ingestJobStatuses.add(job);
    }
}
