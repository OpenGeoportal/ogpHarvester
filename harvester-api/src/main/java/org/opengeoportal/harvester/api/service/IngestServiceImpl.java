/*
 * IngestServiceImpl.java
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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.opengeoportal.harvester.api.client.solr.SolrSearchParams;
import org.opengeoportal.harvester.api.dao.IngestRepository;
import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestOGP;
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
 * @author <a href="mailto:jose.garcia@geocat.net">Jose Garcia</a>.
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

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.service.IngestService#
     * countScheduledIngestsByRepo(java.lang.Long)
     */
    @Override
    @Transactional
    public Long countScheduledIngestsByRepo(final Long repoId) {
        return this.ingestRepository
                .countByRepositoryIdAndScheduledTrue(repoId);
    }

    @Override
    @Transactional
    public void delete(final Long id) {
        this.ingestRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ingest> findAll(final Pageable pageable) {
        final Page<Ingest> page = this.ingestRepository.findAll(pageable);
        return page;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.service.IngestService#findById(java.lang
     * .Long)
     */
    @Override
    public Ingest findById(final Long id) {
        return this.ingestRepository.findOne(id);
    }

    @Override
    @Transactional
    public Ingest findByName(final String name) {

        return this.ingestRepository.findByName(name);
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
        return this.scheduler.getCurrentlyExecutingJobs();
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
    public Date getNextRun(final Ingest ingest) {
        final Date nextRun = this.scheduler.getNextRun(ingest);
        return nextRun;
    }

    @Override
    @Transactional
    public boolean interruptIngest(final Long id) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Interrupting ingest with id " + id);
        }
        boolean interrupted = true;
        final Ingest ingest = this.findById(id);
        if (ingest != null) {
            try {
                interrupted = this.scheduler.interrupt(ingest);
            } catch (final SchedulerException se) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error("Cannot interrupt inget id = " + id);
                }
            }
        }
        return interrupted;
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
    public void runNow(final Ingest ingest) {
        this.scheduler.scheduleIngest(ingest);

    }

    @Override
    @Transactional
    public Ingest save(final Ingest ingest) {
        return this.ingestRepository.save(ingest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.service.IngestService#save(
     * org.opengeoportal .harvester.api.domain.Ingest, java.lang.Long,
     * org.opengeoportal.harvester.api.domain.InstanceType)
     */
    @Override
    @Transactional(readOnly = false)
    public Ingest save(final Ingest ingest, final Long customRepositoryId,
            final InstanceType customRepoInstanceType) {
        final CustomRepository cRepository = this.customRepositoryService
                .findById(customRepositoryId);
        if ((cRepository == null)
                || (cRepository.getServiceType() != customRepoInstanceType)) {
            throw new InstanceNotFoundException(
                    "There is not any CustomRepository with id = "
                            + customRepositoryId + " and serviceType = "
                            + customRepoInstanceType.name());
        }

        ingest.setRepository(cRepository);
        Ingest result;
        if (ingest instanceof IngestOGP) {
            result = this.saveAndUpdateServerQuery(ingest);
        } else {
            result = this.ingestRepository.save(ingest);
        }

        return result;
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
    public Ingest saveAndSchedule(final Ingest ingest) {
        ingest.setScheduled(true);
        Ingest savedIngest;
        if (ingest instanceof IngestOGP) {
            savedIngest = this.saveAndUpdateServerQuery(ingest);
        } else {
            savedIngest = this.save(ingest);
        }

        this.scheduler.scheduleIngest(savedIngest);
        return savedIngest;
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
    public Ingest saveAndSchedule(final Ingest ingest,
            final Long customRepositoryId, final InstanceType typeOfInstance) {
        ingest.setScheduled(true);
        final Ingest savedIngest = this.save(ingest, customRepositoryId,
                typeOfInstance);
        this.scheduler.scheduleIngest(savedIngest);
        return savedIngest;
    }

    @Override
    public Ingest saveAndUpdateServerQuery(final Ingest ingest) {
        try {
            if (ingest instanceof IngestOGP) {
                final IngestOGP ingestOGP = (IngestOGP) ingest;
                final SolrSearchParams params = new SolrSearchParams(ingestOGP);
                final SolrQuery query = params.toSolrQuery();
                final String queryString = query.toString();
                if (!StringUtils.equals(queryString,
                        ingestOGP.getServerQuery())) {
                    ingestOGP.setServerQuery(queryString);
                }
            }
        } catch (final Exception e) {
            this.logger.error(
                    "Error generating server query parameters before saving ingest",
                    e);
        }
        return this.ingestRepository.save(ingest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.service.IngestService#
     * unscheduleByRepository (Long repositoryId)
     */
    @Override
    @Transactional
    public int unscheduleByRepository(final Long repositoryId) {
        final List<Ingest> scheduledIngests = this.ingestRepository
                .findByRepositoryIdAndScheduledTrue(repositoryId);
        for (final Ingest ingest : scheduledIngests) {
            try {
                this.scheduler.unschedule(ingest);
            } catch (final SchedulerException e) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Cannot unschedule ingest with id "
                            + ingest.getId(), e);
                }
            }
        }

        return this.ingestRepository.setScheduledForRepositoryId(repositoryId);

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
    public boolean unscheduleIngest(final Long id) {
        boolean unscheduled = false;
        Ingest ingest = this.findById(id);
        if (ingest != null) {
            try {
                unscheduled = this.scheduler.unschedule(ingest);
                ingest.setScheduled(false);
                ingest = this.save(ingest);
                unscheduled = true;
            } catch (final SchedulerException e) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error(
                            "Cannot unschedule ingest id = " + ingest.getId());
                }
            }
        } else {
            unscheduled = true;
        }
        return unscheduled;
    }

}
