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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkClient;
import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.dao.CustomRepositoryRepository;
import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.exception.BadDatabaseContentsException;
import org.opengeoportal.harvester.api.exception.GeonetworkException;
import org.opengeoportal.harvester.api.exception.OgpSolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

@Service
public class CustomRepositoryServiceImpl implements CustomRepositoryService {
	private Logger logger = LoggerFactory
			.getLogger(CustomRepositoryServiceImpl.class);

	@Resource
	private CustomRepositoryRepository customRepositoryRepository;
	@Resource
	private IngestService ingestService;

	@Override
	@Transactional
	public CustomRepository save(CustomRepository customRepository) {
		return customRepositoryRepository.save(customRepository);
	}

	@Override
	@Transactional(readOnly = false)
	public void logicalDelete(Long id) {
		CustomRepository cRepository = customRepositoryRepository.findOne(id);
		if (cRepository != null) {
			cRepository.setDeleted(true);
			cRepository = customRepositoryRepository.save(cRepository);
			ingestService.unscheduleByRepository(cRepository.getId());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CustomRepository findByName(String name) {
		return customRepositoryRepository.findByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CustomRepository> findAll(Pageable pageable) {
		Page<CustomRepository> page = customRepositoryRepository
				.findByDeletedFalse(pageable);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.CustomRepositoryService#
	 * getAllGroupByType()
	 */
	@Override
	@Transactional(readOnly = true)
	public ListMultimap<InstanceType, CustomRepository> getAllGroupByType() {
		Sort typeSortAsc = new Sort(new Order(
				CustomRepository.COLUMN_SERVICE_TYPE), new Order(
				CustomRepository.COLUMN_NAME));
		List<CustomRepository> repositories = customRepositoryRepository
				.findByDeletedFalse(typeSortAsc);

		ListMultimap<InstanceType, CustomRepository> map = ArrayListMultimap
				.create();
		for (CustomRepository repository : repositories) {
			map.put(repository.getServiceType(), repository);
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.CustomRepositoryService#
	 * getRemoteRepositories
	 * (org.opengeoportal.harvester.api.domain.InstanceType, java.net.URL)
	 */
	@Override
	public List<SimpleEntry<String, String>> getRemoteRepositories(
			InstanceType repoType, URL url) {

		List<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();

		if (repoType == InstanceType.GEONETWORK) {
			result = retrieveGeoNetworkSources(url);
		} else if (repoType == InstanceType.SOLR) {
			result = retrieveSolrInstitutions(url);
		}

		return result;
	}

	/**
	 * @param url
	 * @return
	 */
	private List<SimpleEntry<String, String>> retrieveSolrInstitutions(URL url) {
		List<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
		SolrClient solrClient = new SolrJClient(url.toString());
		try {
			List<String> institutionsList = solrClient.getInstitutions();
			for (String institution : institutionsList) {
				result.add(new SimpleEntry<String, String>(institution,
						institution));
			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Can not retrieve institutions from " + url, e);
			}
			throw new OgpSolrException("Can not retrieve institutions from "
					+ url, e);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.CustomRepositoryService#
	 * getRemoteRepositoriesByRepoId(java.lang.Long)
	 */
	@Override
	public List<SimpleEntry<String, String>> getRemoteRepositoriesByRepoId(
			Long repoId) {
		CustomRepository repository = customRepositoryRepository
				.findOne(repoId);
		List<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
		if (repository != null) {
			String url = repository.getUrl();
			InstanceType serviceType = repository.getServiceType();
			try {
				result = getRemoteRepositories(serviceType, new URL(url));
			} catch (MalformedURLException e) {
				throw new BadDatabaseContentsException(
						"Cannot parse the url stored in database for repository "
								+ repoId + " (" + url + ")", e);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.CustomRepositoryService#findById
	 * (java.lang.Long)
	 */
	@Override
	public CustomRepository findById(Long id) {
		return customRepositoryRepository.findOne(id);
	}

	/**
	 * Retrieve Geonetwork remote sources
	 * 
	 * @param url
	 *            service URL, for example http://www.example.com/geonetwork.
	 * @return
	 */
	private List<SimpleEntry<String, String>> retrieveGeoNetworkSources(URL url) {
		List<SimpleEntry<String, String>> sources = new ArrayList<SimpleEntry<String, String>>();

		try {
			GeoNetworkClient gnClient = new GeoNetworkClient(url);

			sources = gnClient.getSources();
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"Cannot retrieve remote Geonetwork sources for server "
								+ url, e);
			}
			throw new GeonetworkException(
					"Cannot retrieve remote Geonetwork sources for server "
							+ url, e);
		}

		return sources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.CustomRepositoryService#
	 * checkExistActiveRepositoryNameAndType(java.lang.String,
	 * org.opengeoportal.harvester.api.domain.InstanceType)
	 */
	@Override
	public boolean checkExistActiveRepositoryNameAndType(String name,
			InstanceType type) {
		boolean result = false;
		List<CustomRepository> repositories = customRepositoryRepository
				.findByNameAndServiceTypeAndDeleted(name, type, false);
		if (repositories.size() != 0) {
			result = true;
		}
		return result;
	}
}
