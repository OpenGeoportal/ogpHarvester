/*
 * CustomRepositoryController.java
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
 * Authors:: Juan Luis Rodríguez (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.exception.GeonetworkException;
import org.opengeoportal.harvester.api.exception.InstanceNotFoundException;
import org.opengeoportal.harvester.api.exception.OgpSorlException;
import org.opengeoportal.harvester.api.service.CustomRepositoryService;
import org.opengeoportal.harvester.api.service.IngestService;
import org.opengeoportal.harvester.mvc.bean.CustomRepositoryFormBean;
import org.opengeoportal.harvester.mvc.bean.JsonResponse;
import org.opengeoportal.harvester.mvc.bean.JsonResponse.STATUS;
import org.opengeoportal.harvester.mvc.bean.RemoteRepositoryFormBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@SessionAttributes(types = { CustomRepositoryFormBean.class })
public class CustomRepositoryController {
	@Autowired
	private CustomRepositoryService repositoryService;
	@Autowired
	private IngestService ingestService;

	@Value("#{localSolr['localSolr.url']}")
	private String localSolrUrl;

	@Autowired
	private Validator validator;

	@RequestMapping(value = "/rest/repositories", method = RequestMethod.GET)
	@ResponseBody
	public Map<InstanceType, List<SimpleEntry<Long, String>>> getCustomRepositories() {

		ListMultimap<InstanceType, CustomRepository> multimap = repositoryService
				.getAllGroupByType();
		Map<InstanceType, List<SimpleEntry<Long, String>>> result = Maps
				.newHashMap();

		for (InstanceType instanceType : InstanceType.values()) {
			List<CustomRepository> reposOfType = multimap.get(instanceType);
			List<SimpleEntry<Long, String>> repositories = Lists.newArrayList();

			for (CustomRepository repository : reposOfType) {
				SimpleEntry<Long, String> entry = new SimpleEntry<Long, String>(
						repository.getId(), repository.getName());
				repositories.add(entry);
			}
			result.put(instanceType, repositories);
		}
		return result;
	}

	/**
	 * Create a new {@link CustomRepository} in the database. The caller user
	 * must be ADMIN.
	 * 
	 * @param repository
	 *            form bean with name, URL and instance type.
	 * @return the saved {@link CustomRepository}.
	 */
	@RequestMapping(value = "/rest/repositories", method = RequestMethod.POST)
	@Secured({ "ROLE_ADMIN" })
	@ResponseBody
	public JsonResponse saveRepository(
			@RequestBody RemoteRepositoryFormBean repository, Errors errors) {
		JsonResponse res = new JsonResponse();

		validator.validate(repository, errors);
		if (errors.hasErrors()) {
			res.setStatus(STATUS.FAIL);
			res.setResult(errors.getAllErrors());
			return res;

		}

		boolean existOther = repositoryService
				.checkExistActiveRepositoryNameAndType(repository.getName(),
						repository.getRepoType());
		if (existOther) {
			res.setStatus(STATUS.FAIL);
			errors.rejectValue("name", "ERROR_REPO_ALREADY_ADDED");
			res.setResult(errors.getAllErrors());
			return res;
		}

		CustomRepository entity = new CustomRepository();
		entity.setName(repository.getName());
		entity.setUrl(repository.getRepoUrl());
		entity.setServiceType(repository.getRepoType());

		entity = repositoryService.save(entity);

		res.setResult(entity);
		res.setStatus(STATUS.SUCCESS);

		return res;
	}

	@RequestMapping(value = "/rest/repositories/{repoId}", method = RequestMethod.DELETE)
	@Secured({ "ROLE_ADMIN" })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteRepo(@PathVariable Long repoId) {
		repositoryService.logicalDelete(repoId);

	}

	@RequestMapping("/rest/repositories/{repoId}/remoteSources")
	@ResponseBody
	public JsonResponse getRemoteRepositoriesById(
			@PathVariable Long repoId) {
		JsonResponse response = new JsonResponse();
		try {
			List<SimpleEntry<String, String>> sourcesList = repositoryService.getRemoteRepositoriesByRepoId(repoId);
			response.setStatus(STATUS.SUCCESS);
			response.setResult(sourcesList);
		} catch (GeonetworkException gne) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "ERROR_CONNECTING_TO_PREDEFINED_GEONETWORK");
			response.setResult(errorMap);
		} catch (OgpSorlException ose) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "ERROR_CONNECTING_TO_PREDEFINED_SOLR");
			response.setResult(errorMap);
		}
		return response;

	}

	@RequestMapping("/rest/repositories/{repoId}/hasScheduledIngests")
	@ResponseBody
	public JsonResponse checkRepositoryScheduledIngests(
			@PathVariable Long repoId) {
		JsonResponse response = new JsonResponse();

		CustomRepository repository = repositoryService.findById(repoId);
		if (repository == null) {
			throw new InstanceNotFoundException(
					"Cannot find a CustomRepository with id " + repoId);
		}

		response.setStatus(STATUS.SUCCESS);
		Map<String, Long> result = Maps.newHashMap();
		Long count = ingestService.countScheduledIngestsByRepo(repoId);
		result.put("ingestCount", count);
		response.setResult(result);

		return response;

	}

	/**
	 * Retrieve remote sources present in the given URL.
	 * 
	 * @param repository
	 * @return a JsonResponse with a list of remote sources.
	 */
	@RequestMapping(value = "/rest/repositoriesbyurl/remoteSources", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public JsonResponse getRemoteRepositoriesByUrl(
			@RequestBody RemoteRepositoryFormBean repository) {
		JsonResponse response = new JsonResponse();
		try {
			URL urlObj = new URL(repository.getRepoUrl());

			List<SimpleEntry<String, String>> repositories = repositoryService
					.getRemoteRepositories(repository.getRepoType(), urlObj);
			response.setStatus(STATUS.SUCCESS);
			response.setResult(repositories);
		} catch (OgpSorlException ose) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "ERROR_CONNECTING_TO_REMOTE_SOLR");
			response.setResult(errorMap);

		} catch (GeonetworkException gne) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "ERROR_CONNECTING_TO_REMOTE_GEONETWORK");
			response.setResult(errorMap);
		} 
		catch (MalformedURLException e) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "ERROR_MALFORMED_URL");
			response.setResult(errorMap);
		}

		return response;
	}

	@RequestMapping(value = "/rest/localSolr/institutions")
	@ResponseBody
	public JsonResponse getLocalSolrInstitutions() {
		JsonResponse response = new JsonResponse();
		try {
			List<SimpleEntry<String, String>> institutions = repositoryService
					.getRemoteRepositories(InstanceType.SOLR, new URL(
							localSolrUrl));
			response.setStatus(STATUS.SUCCESS);
			response.setResult(institutions);
		} catch (Exception e) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "ERROR_CONNECTING_TO_LOCAL_SOLR");
			response.setResult(errorMap);
		}

		return response;
	}

	@RequestMapping(value = "/rest/checkIfOtherRepoExist")
	@ResponseBody
	public Map<String, Object> checkExistingActiveRepositoryName(
			@RequestParam String name, @RequestParam InstanceType type) {
		Map<String, Object> resultMap = Maps.newHashMap();
		boolean exists = repositoryService
				.checkExistActiveRepositoryNameAndType(name, type);
		resultMap.put("anotherExist", exists);

		return resultMap;

	}

}
