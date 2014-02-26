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
import java.util.SortedSet;

import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.exception.InstanceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Ingest service interface.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public interface IngestService {
	/**
	 * Save or update an Ingest.
	 * 
	 * @param ingest
	 *            the {@link Ingest} to save.
	 * @return the saved ingest instance.
	 */
	Ingest save(Ingest ingest);

	/**
	 * Save an {@link Ingest} into the database. Associate it with the
	 * {@link CustomRepository} having the passed id and {@link InstanceType}.
	 * 
	 * @param ingest
	 *            the <code>Ingest</code> to save.
	 * @param customRepositoryId
	 *            the custom repository identifier to be associated with.
	 * @param customRepoInstanceType
	 *            repository type.
	 * @return the saved Ingest.
	 * @throws InstanceNotFoundException
	 *             if a {@link CustomRepository} with
	 *             <code>customRepositoryId</code> as identifier and type
	 *             <code>customRepositoryId</code> is not found.
	 */
	Ingest save(Ingest ingest, Long customRepositoryId,
			InstanceType customRepoInstanceType)
			throws InstanceNotFoundException;

	/**
	 * Remove the {@link Ingest} with identified id from the database.
	 * 
	 * @param id
	 *            ingest identifier.
	 */
	void delete(Long id);


	/**
	 * Get a {@link Page} of ingests.
	 * 
	 * @param pageable
	 *            parameters used to implement the pagination (sort, page,
	 *            number of elements, ...)
	 * @return a {@link Page} of {@link Ingest}s.
	 */
	Page<Ingest> findAll(Pageable pageable);

	/**
	 * Returns the {@link Ingest} with the passed id.
	 * 
	 * @param id
	 *            ingest identifier.
	 * @return the {@link Ingest} with the passed id.
	 */
	Ingest findById(Long id);

	/**
	 * Unschedule all ingests that use repository parameter.
	 * 
	 * @param repoId
	 *            the repository ID.
	 * @return how many ingests has been unscheduled.
	 */
	int unscheduleByRepository(Long repoId);

	/**
	 * Given a repository identifier returns the count of Ingest that use this
	 * repo and are currently scheduled (its attribute scheduled is
	 * <code>true</code>).
	 * 
	 * @param repoId
	 *            repository identifier.
	 * @return the count of scheduled ingests for the repository.
	 */
	Long countScheduledIngestsByRepo(Long repoId);

	/**
	 * Save the ingest and schedule a trigger for executing it.
	 * 
	 * @param ingest
	 *            the ingest.
	 * @param customRepositoryId
	 *            custom repository identifier.
	 * @param typeOfInstance
	 *            type of instance (SOLR, CSW, ...).
	 * @return the saved ingest.
	 */
	Ingest saveAndSchedule(Ingest ingest, Long customRepositoryId,
			InstanceType typeOfInstance);

	/**
	 * Save the ingest and schedule a trigger for executing it.
	 * 
	 * @param ingest
	 *            the ingest
	 * @return the saved ingest.
	 */
	Ingest saveAndSchedule(Ingest ingest);

	/**
	 * Return the date of the ingest next run.
	 * 
	 * @param ingest
	 *            the ingest.
	 * @return the next run date or null if ingest will not be run more.
	 */
	Date getNextRun(Ingest ingest);

	/**
	 * Return the set of ingest currently being executed.
	 * 
	 * @return the set of the ingest identifiers currently being executed. This
	 *         set is sorted in natural order.
	 */
	SortedSet<Long> getCurrentlyExecutingJobs();

	/**
	 * Unschedule the ingest execution.
	 * 
	 * @param id
	 *            identifier.
	 * @return <code>true</code> if the ingest has been successfully
	 *         unscheduled.
	 */
	boolean unscheduleIngest(Long id);
}
