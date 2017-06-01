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
    private final Logger logger = LoggerFactory
            .getLogger(CustomRepositoryServiceImpl.class);

    @Resource
    private CustomRepositoryRepository customRepositoryRepository;
    @Resource
    private IngestService ingestService;

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.service.CustomRepositoryService#
     * checkExistActiveRepositoryNameAndType(java.lang.String,
     * org.opengeoportal.harvester.api.domain.InstanceType)
     */
    @Override
    public boolean checkExistActiveRepositoryNameAndType(final String name,
            final InstanceType type) {
        boolean result = false;
        final List<CustomRepository> repositories = this.customRepositoryRepository
                .findByNameAndServiceTypeAndDeleted(name, type, false);
        if (repositories.size() != 0) {
            result = true;
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomRepository> findAll(final Pageable pageable) {
        final Page<CustomRepository> page = this.customRepositoryRepository
                .findByDeletedFalse(pageable);
        return page;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.service.CustomRepositoryService#findById
     * (java.lang.Long)
     */
    @Override
    public CustomRepository findById(final Long id) {
        return this.customRepositoryRepository.findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomRepository findByName(final String name) {
        return this.customRepositoryRepository.findByName(name);
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
        final Sort typeSortAsc = new Sort(
                new Order(CustomRepository.COLUMN_SERVICE_TYPE),
                new Order(CustomRepository.COLUMN_NAME));
        final List<CustomRepository> repositories = this.customRepositoryRepository
                .findByDeletedFalse(typeSortAsc);

        final ListMultimap<InstanceType, CustomRepository> map = ArrayListMultimap
                .create();
        for (final CustomRepository repository : repositories) {
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
            final InstanceType repoType, final URL url) {

        List<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();

        if (repoType == InstanceType.GEONETWORK) {
            result = this.retrieveGeoNetworkSources(url);
        } else if (repoType == InstanceType.SOLR) {
            result = this.retrieveSolrInstitutions(url);
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
            final Long repoId) {
        final CustomRepository repository = this.customRepositoryRepository
                .findOne(repoId);
        List<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
        if (repository != null) {
            final String url = repository.getUrl();
            final InstanceType serviceType = repository.getServiceType();
            try {
                result = this.getRemoteRepositories(serviceType, new URL(url));
            } catch (final MalformedURLException e) {
                throw new BadDatabaseContentsException(
                        "Cannot parse the url stored in database for repository "
                                + repoId + " (" + url + ")",
                        e);
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void logicalDelete(final Long id) {
        CustomRepository cRepository = this.customRepositoryRepository
                .findOne(id);
        if (cRepository != null) {
            cRepository.setDeleted(true);
            cRepository = this.customRepositoryRepository.save(cRepository);
            this.ingestService.unscheduleByRepository(cRepository.getId());
        }
    }

    /**
     * Retrieve Geonetwork remote sources
     * 
     * @param url
     *            service URL, for example http://www.example.com/geonetwork.
     * @return
     */
    private List<SimpleEntry<String, String>> retrieveGeoNetworkSources(
            final URL url) {
        List<SimpleEntry<String, String>> sources = new ArrayList<SimpleEntry<String, String>>();

        try {
            final GeoNetworkClient gnClient = new GeoNetworkClient(url);

            sources = gnClient.getSources();
        } catch (final Exception e) {
            if (this.logger.isDebugEnabled()) {
                this.logger
                        .debug("Cannot retrieve remote Geonetwork sources for server "
                                + url, e);
            }
            throw new GeonetworkException(
                    "Cannot retrieve remote Geonetwork sources for server "
                            + url,
                    e);
        }

        return sources;
    }

    /**
     * @param url
     * @return
     */
    private List<SimpleEntry<String, String>> retrieveSolrInstitutions(
            final URL url) {
        final List<SimpleEntry<String, String>> result = new ArrayList<SimpleEntry<String, String>>();
        final SolrClient solrClient = new SolrJClient(url.toString());
        try {
            final List<String> institutionsList = solrClient.getInstitutions();
            for (final String institution : institutionsList) {
                result.add(new SimpleEntry<String, String>(institution,
                        institution));
            }
        } catch (final Exception e) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Can not retrieve institutions from " + url,
                        e);
            }
            throw new OgpSolrException(
                    "Can not retrieve institutions from " + url, e);
        }
        return result;
    }

    @Override
    @Transactional
    public CustomRepository save(final CustomRepository customRepository) {
        return this.customRepositoryRepository.save(customRepository);
    }
}
