/**
 * IngestJob.java
 *
 * Copyright (C) 2014
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
 * Authors:: Juan Luis Rodríguez (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.api.scheduler;

import java.util.UUID;

import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.component.IngestJobFactory;
import org.opengeoportal.harvester.api.component.MetadataIngester;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.exception.InstanceNotFoundException;
import org.opengeoportal.harvester.api.exception.UnscheduledIngestException;
import org.opengeoportal.harvester.api.service.IngestService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An Ingest Job to be executed.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class IngestJob implements Job {

	/**
	 * Key used to store the ingest identifier in the JobDetailsData map.
	 */
	public static final String INGEST_ID = "ingestId";

	/**
	 * Ingest service.
	 */
	@Autowired
	private IngestService ingestService;
	/**
	 * Factory that can create {@link IngestJob} instances based on an
	 * {@link Ingest}.
	 */
	@Autowired
	private IngestJobFactory ingestJobFactory;
	/**
	 * Ingester that will ingest received metadata into the destination.
	 */
	@Autowired
	private MetadataIngester metadataIngester;

	/** Ingest identifier in the database. */
	private Long ingestId;

	/**
	 * Public constructor.
	 */
	public IngestJob() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			// TODO Autowire bean
			Ingest ingest = findAndValidateIngest();
			BaseIngestJob ingestJob = ingestJobFactory.newIngestJob(ingest);
			UUID jobUuid = UUID.randomUUID();
			ingestJob.init(jobUuid, ingest, metadataIngester);
			ingestService.save(ingest);
			ingestJob.run();
			ingestService.save(ingest);

		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	/**
	 * Search ingest with ingestId in the database and check if has been
	 * scheduled.
	 * 
	 * @return the ingest with ingestId if it has been found and it is
	 *         scheduled; otherwise, an exception is thrown.
	 */
	private Ingest findAndValidateIngest() {
		if (ingestId == null) {
			throw new IllegalStateException(
					"ingestId property has not been set");
		}
		Ingest ingest = ingestService.findById(ingestId);
		if (ingest == null) {
			throw new InstanceNotFoundException(
					"Job cannot find Ingest with ingestId " + ingestId);
		}
		if (ingest.isScheduled() == null || !ingest.isScheduled()) {
			throw new UnscheduledIngestException("Ingest " + ingestId
					+ " is not scheduled (its property isScheduled is "
					+ "not true)");
		}
		return ingest;

	}

	/**
	 * @return the ingestId
	 */
	public Long getIngestId() {
		return ingestId;
	}

	/**
	 * @param ingestId
	 *            the ingestId to set
	 */
	public void setIngestId(Long ingestId) {
		this.ingestId = ingestId;
	}

	/**
	 * @param metadataIngester the metadataIngester to set
	 */
	public void setMetadataIngester(MetadataIngester metadataIngester) {
		this.metadataIngester = metadataIngester;
	}

}
