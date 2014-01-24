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
package org.opengeoportal.harvester.api.dao;

import java.util.List;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IngestRepository extends JpaRepository<Ingest, Long> {

	/**
	 * 
	 * @param name
	 *            ingest name.
	 * @return the ingest with the given name.
	 */
	Ingest findByName(String name);

	/**
	 * Set <code>scheduled</code> property to null for all Ingests related with
	 * repository.
	 * 
	 * @param repositoryId
	 *            repository Id
	 * @return modified repositories count.
	 */
	@Modifying
	@Transactional
	@Query(value = "update Ingest i set i.scheduled=false where  i.repository.id=?1")
	int setScheduledForRepositoryId(Long repositoryId);

	/**
	 * Count the ingests scheduled that use a given repository.
	 * 
	 * @param repoId
	 * @return
	 */
	@Query(value = "select count(i) from Ingest i where i.repository.id=?1 and i.scheduled=true")
	Long countByRepositoryIdAndScheduledTrue(Long repoId);

	/**
	 * 
	 * @param repositoryId
	 *            repository id.
	 * @return return the list of scheduled ingests for a given repository.
	 */
	List<Ingest> findByRepositoryIdAndScheduledTrue(Long repositoryId);
}