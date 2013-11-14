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

import java.util.List;

import javax.annotation.Resource;

import org.opengeoportal.harvester.api.dao.CustomRepositoryRepository;
import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Service
public class CustomRepositoryServiceImpl implements CustomRepositoryService {

    @Resource
    private CustomRepositoryRepository customRepositoryRepository;

    @Override
    @Transactional
    public CustomRepository save(CustomRepository customRepository) {
        return customRepositoryRepository.save(customRepository);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        customRepositoryRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomRepository findByName(String name) {
        return customRepositoryRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomRepository> findAll(Pageable pageable) {
        Page<CustomRepository> page = customRepositoryRepository.findAll(pageable);
        return page;
    }

	/* (non-Javadoc)
	 * @see org.opengeoportal.harvester.api.service.CustomRepositoryService#getAllGroupByType()
	 */
	@Override
	@Transactional
	public ListMultimap <String, CustomRepository> getAllGroupByType() {
		Sort typeSortAsc = new Sort(new Order(CustomRepository.COLUMN_SERVICE_TYPE));
		List<CustomRepository> repositories = customRepositoryRepository.findAll(typeSortAsc);
		ListMultimap<String, CustomRepository> map = ArrayListMultimap.create();
		for(CustomRepository repository : repositories) {
			map.put(repository.getServiceType(), repository);
		}
		return map;
	}
}
