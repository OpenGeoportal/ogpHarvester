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

import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomRepositoryRepository
        extends JpaRepository<CustomRepository, Long> {

    /**
     * @param pageable
     * @return
     */
    Page<CustomRepository> findByDeletedFalse(Pageable pageable);

    List<CustomRepository> findByDeletedFalse(Sort sort);

    CustomRepository findByName(String name);

    /**
     * Find all not deleted repository with the name and service type passed.
     * 
     * @param name
     *            the name.
     * @param serviceType
     *            the service type {@link InstanceType}.
     * @param deleted
     * @return the list of all {@link CustomRepository} with deleted=false, name
     *         and serviceType.
     */
    List<CustomRepository> findByNameAndServiceTypeAndDeleted(String name,
            InstanceType serviceType, boolean deleted);
}