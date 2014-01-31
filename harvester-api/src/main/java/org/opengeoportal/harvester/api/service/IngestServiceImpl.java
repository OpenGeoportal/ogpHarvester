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
package org.opengeoportal.harvester.api.service;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.annotation.Resource;

import org.opengeoportal.harvester.api.dao.IngestRepository;
import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.exception.InstanceNotFoundException;
import org.opengeoportal.harvester.api.scheduler.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ingest service implementation.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
@Service
public class IngestServiceImpl implements IngestService {
	/** The logger. */
	private final Logger logger = LoggerFactory
			.getLogger(IngestServiceImpl.class);

	/** The ingest repository. */
	@Resource
	private IngestRepository ingestRepository;
	/** The custom repository service. */
	@Resource
	private CustomRepositoryService customRepositoryService;
	/** The scheduler. */
	@Resource
	private Scheduler scheduler;

	@Override
	@Transactional
	public Ingest save(Ingest ingest) {
		return ingestRepository.save(ingest);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		ingestRepository.delete(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Ingest findByName(String name) {
		return ingestRepository.findByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Ingest> findAll(Pageable pageable) {
		Page<Ingest> page = ingestRepository.findAll(pageable);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestService#save(org.opengeoportal
	 * .harvester.api.domain.Ingest, java.lang.Long,
	 * org.opengeoportal.harvester.api.domain.InstanceType)
	 */
	@Override
	@Transactional(readOnly = false)
	public Ingest save(Ingest ingest, Long customRepositoryId,
			InstanceType customRepoInstanceType)
			throws InstanceNotFoundException {
		CustomRepository cRepository = customRepositoryService
				.findById(customRepositoryId);
		if (cRepository == null
				|| cRepository.getServiceType() != customRepoInstanceType) {
			throw new InstanceNotFoundException(
					"There is not any CustomRepository with id = "
							+ customRepositoryId + " and serviceType = "
							+ customRepoInstanceType.name());
		}

		ingest.setRepository(cRepository);
		Ingest result = ingestRepository.save(ingest);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestService#findById(java.lang
	 * .Long)
	 */
	@Override
	public Ingest findById(Long id) {
		return ingestRepository.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestService#unscheduleByRepository
	 * (Long repositoryId)
	 */
	@Override
	@Transactional
	public int unscheduleByRepository(Long repositoryId) {
		List<Ingest> scheduledIngests = ingestRepository
				.findByRepositoryIdAndScheduledTrue(repositoryId);
		for (Ingest ingest : scheduledIngests) {
			try {
				scheduler.unschedule(ingest);
			} catch (SchedulerException e) {
				if (logger.isWarnEnabled()) {
					logger.warn(
							"Cannot unschedule ingest with id "
									+ ingest.getId(), e);
				}
			}
		}

		return ingestRepository.setScheduledForRepositoryId(repositoryId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.IngestService#
	 * countScheduledIngestsByRepo(java.lang.Long)
	 */
	@Override
	@Transactional
	public Long countScheduledIngestsByRepo(Long repoId) {
		return ingestRepository.countByRepositoryIdAndScheduledTrue(repoId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestService#saveAndSchedule
	 * (org.opengeoportal.harvester.api.domain.Ingest, java.lang.Long,
	 * org.opengeoportal.harvester.api.domain.InstanceType)
	 */
	@Override
	@Transactional
	public Ingest saveAndSchedule(Ingest ingest, Long customRepositoryId,
			InstanceType typeOfInstance) {
		ingest.setScheduled(true);
		Ingest savedIngest = save(ingest, customRepositoryId, typeOfInstance);
		scheduler.scheduleIngest(savedIngest);
		return savedIngest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestService#saveAndSchedule
	 * (org.opengeoportal.harvester.api.domain.Ingest)
	 */
	@Override
	@Transactional
	public Ingest saveAndSchedule(Ingest ingest) {
		ingest.setScheduled(true);
		Ingest savedIngest = save(ingest);
		scheduler.scheduleIngest(savedIngest);
		return savedIngest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestService#getNextRun(org.
	 * opengeoportal.harvester.api.domain.Ingest)
	 */
	@Override
	@Transactional
	public Date getNextRun(Ingest ingest) {
		Date nextRun = scheduler.getNextRun(ingest);
		return nextRun;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.IngestService#
	 * getCurrentlyExecutingJobs()
	 */
	@Override
	@Transactional
	public SortedSet<Long> getCurrentlyExecutingJobs() {
		return scheduler.getCurrentlyExecutingJobs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestService#unscheduleIngest
	 * (java.lang.Long)
	 */
	@Override
	@Transactional
	public boolean unscheduleIngest(Long id) {
		boolean unscheduled = false;
		Ingest ingest = findById(id);
		if (ingest != null) {
			try {
				scheduler.unschedule(ingest);
				ingest.setScheduled(false);
				ingest = save(ingest);
				unscheduled = true;
			} catch (SchedulerException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Cannot unschedule ingest id = "
							+ ingest.getId());
				}
			}
		} else {
			unscheduled = true;
		}
		return unscheduled;

	}

}
