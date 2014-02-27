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

import java.util.Date;
import java.util.Calendar;
import java.util.UUID;

import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.component.IngestJobFactory;
import org.opengeoportal.harvester.api.component.MetadataIngester;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.exception.InstanceNotFoundException;
import org.opengeoportal.harvester.api.exception.UnscheduledIngestException;
import org.opengeoportal.harvester.api.service.IngestJobStatusService;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportService;
import org.opengeoportal.harvester.api.service.IngestService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * An Ingest Job to be executed.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
@Component
public class IngestJob implements InterruptableJob, IngestJobFactorySetter {

	/**
	 * Key used to store the ingest identifier in the JobDetailsData map.
	 */
	public static final String INGEST_ID = "ingestId";

	/**
	 * Ingest service.
	 */
	@Autowired
	private IngestService ingestService;

	/** Job status service. */
	@Autowired
	private IngestJobStatusService jobStatusService;

	/** Ingest Report Service. */
	@Autowired
	private IngestReportService reportService;

	/**
	 * The error service.
	 */
	@Autowired
	private IngestReportErrorService errorService;

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
	private String ingestId;
    /** Actual job to be executed. */
    private BaseIngestJob job;

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
	@Transactional
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
        Date startTimestamp = Calendar.getInstance().getTime();
        Ingest ingest = null;
		try {
            ingest = findAndValidateIngest();
            job = ingestJobFactory.newIngestJob(ingest);
			job.setJobStatusService(jobStatusService);
			job.setReportService(reportService);
			job.setErrorService(errorService);
			UUID jobUuid = UUID.randomUUID();
			job.init(jobUuid, ingest, metadataIngester);
			ingestService.save(ingest);
			job.run();
			ingestService.save(ingest);

		} catch (Exception e) {
			throw new JobExecutionException(e);
		} finally {
			if (ingest != null) {
				ingest = ingestService.findById(ingest.getId());
				ingest.setLastRun(startTimestamp);
				ingestService.save(ingest);
			}
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
		Ingest ingestFound = ingestService.findById(Long.valueOf(ingestId));
		if (ingestFound == null) {
			throw new InstanceNotFoundException(
					"Job cannot find Ingest with ingestId " + ingestId);
		}
		if (ingestFound.isScheduled() == null || !ingestFound.isScheduled()) {
			throw new UnscheduledIngestException("Ingest " + ingestId
					+ " is not scheduled (its property isScheduled is "
					+ "not true)");
		}
		return ingestFound;

	}

	/**
	 * @return the ingestId
	 */
	public String getIngestId() {
		return ingestId;
	}

	/**
	 * @param ingestId
	 *            the ingestId to set
	 */
	public void setIngestId(String ingestId) {
		this.ingestId = ingestId;
	}

	/**
	 * @param metadataIngester
	 *            the metadataIngester to set
	 */
	public void setMetadataIngester(MetadataIngester metadataIngester) {
		this.metadataIngester = metadataIngester;
	}

	/**
	 * @return the ingestService
	 */
	public IngestService getIngestService() {
		return ingestService;
	}

	/**
	 * @param ingestService
	 *            the ingestService to set
	 */
	public void setIngestService(IngestService ingestService) {
		this.ingestService = ingestService;
	}

	/**
	 * @param ingestJobFactory the ingestJobFactory to set
	 */
	public void setIngestJobFactory(IngestJobFactory ingestJobFactory) {
		this.ingestJobFactory = ingestJobFactory;
	}

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        if (job != null) {
            job.interrupt();
        }
    }
}
