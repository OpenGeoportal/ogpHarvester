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

import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.exception.InstanceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IngestService {
	public Ingest save(Ingest ingest);

	/**
	 * Save an {@link Ingest} into the database. Associate it with the
	 * {@link CustomRepository} having the passed id and {@link InstanceType}.
	 * 
	 * @param ingest
	 *            the <code>Ingest</code> to save.
	 * @param customRepositoryId
	 *            the custom repository identifier to be associated with.
	 * @return the saved Ingest.
	 * @throws InstanceNotFoundException
	 *             if a {@link CustomRepository} with
	 *             <code>customRepositoryId</code> as identifier and type
	 *             <code>customRepositoryId</code> is not found.
	 */
	public Ingest save(Ingest ingest, Long customRepositoryId,
			InstanceType customRepoInstanceType)
			throws InstanceNotFoundException;

	public void delete(Long id);

	public Ingest findByName(String name);

	public Page<Ingest> findAll(Pageable pageable);

	/**
	 * Returns the {@link Ingest} with the passed id.
	 * 
	 * @param id
	 *            ingest identifier.
	 * @return the {@link Ingest} with the passed id.
	 */
	public Ingest findById(Long id);
}
