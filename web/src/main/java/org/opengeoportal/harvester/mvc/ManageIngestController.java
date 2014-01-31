/*
 * ManageIngestController.java
 * 
 * Copyright (C) 2013
 * 
 * This file is part of Open Geoportal Harvester
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
 * Authors:: Juan Luis Rodriguez Ponce (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.service.IngestService;
import org.opengeoportal.harvester.mvc.bean.IngestListItem;
import org.opengeoportal.harvester.mvc.bean.PageWrapper;
import org.opengeoportal.harvester.mvc.exception.ItemNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 * 
 */
@Controller
public class ManageIngestController {
	@Resource
	private IngestService ingestService;

	@RequestMapping("/manageIngests")
	public String test(ModelMap model) {
		return "ngView";
	}

	@RequestMapping("/rest/ingests")
	@ResponseBody
	public Map<String, Object> getAllIngests(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize, ModelMap model) {
		if (page < 1) {
			page = 1;
		}

		// Pages are zero base. We need to subtract 1 to the received page
		page = page - 1;
		Pageable pageable = new PageRequest(page, pageSize);
		Page<Ingest> resultPage = ingestService.findAll(pageable);
		PageWrapper pageDetails = new PageWrapper(resultPage);

		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("pageDetails", pageDetails);

		List<IngestListItem> resultList = Lists
				.newArrayListWithCapacity(resultPage.getNumberOfElements());
		Set<Long> executingJobs = ingestService.getCurrentlyExecutingJobs();
		for (Ingest ingest : resultPage) {
			IngestListItem ingestListItem = new IngestListItem(ingest);
			Date nextRun = ingestService.getNextRun(ingest);
			ingestListItem.setNextRun(nextRun);
			ingestListItem
					.setInProgress(executingJobs.contains(ingest.getId()));

			resultList.add(ingestListItem);
		}
		resultMap.put("elements", resultList);

		return resultMap;
	}

	@RequestMapping("/rest/ingests/{id}")
	@ResponseBody
	public Map<String, Object> ingestDetails(ModelMap model,
			@PathVariable Long id) {

		Ingest ingest = ingestService.findById(id);
		if (ingest == null) {
			throw new ItemNotFoundException("Ingest with id=" + id
					+ "does not exist");
		}

		Map<String, Object> ingestMap = Maps.newHashMap();
		ingestMap.put("id", id);
		ingestMap.put("name", ingest.getName());
		ingestMap.put("lastRun", ingest.getLastRun());

		Map<String, Object> passed = new HashMap<String, Object>();
		passed.put("restrictedRecords", 745);
		passed.put("publicRecords", 120);
		passed.put("vectorRecords", 850);
		passed.put("rasterRecords", 845);
		ingestMap.put("passed", passed);

		Map<String, Object> warning = new HashMap<String, Object>();
		warning.put("unrequiredFields", 345);
		warning.put("webserviceWarnings", 200);
		ingestMap.put("warning", warning);

		Map<String, Object> errorsMap = new HashMap<String, Object>();
		errorsMap.put("requiredFields", 745);
		errorsMap.put("webServiceErrors", 120);
		errorsMap.put("systemErrors", 58);
		List<SimpleEntry<String, Integer>> requiredFieldSubcat = new ArrayList<SimpleEntry<String, Integer>>();
		requiredFieldSubcat
				.add(new SimpleEntry<String, Integer>("extent", 245));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>(
				"themeKeyword", 105));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>(
				"placeKeyword", 200));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("topic", 125));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>(
				"dateOfContent", 400));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("originator",
				32));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("dataType",
				400));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>(
				"dataRepository", 320));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("editDate",
				126));

		errorsMap.put("requiredFieldsList", requiredFieldSubcat);

		List<SimpleEntry<String, Integer>> webServiceErrorList = new ArrayList<SimpleEntry<String, Integer>>();
		webServiceErrorList
				.add(new SimpleEntry<String, Integer>("error1", 120));
		webServiceErrorList
				.add(new SimpleEntry<String, Integer>("error2", 120));
		webServiceErrorList
				.add(new SimpleEntry<String, Integer>("error3", 120));
		webServiceErrorList
				.add(new SimpleEntry<String, Integer>("error4", 120));
		webServiceErrorList
				.add(new SimpleEntry<String, Integer>("error5", 120));
		webServiceErrorList
				.add(new SimpleEntry<String, Integer>("error6", 120));
		errorsMap.put("webServiceErrorList", webServiceErrorList);

		List<SimpleEntry<String, Integer>> systemErrorList = new ArrayList<SimpleEntry<String, Integer>>();
		systemErrorList.add(new SimpleEntry<String, Integer>("serror1", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror2", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror3", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror4", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror5", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror6", 120));
		errorsMap.put("systemErrorList", systemErrorList);

		ingestMap.put("error", errorsMap);

		return ingestMap;
	}

	@RequestMapping("/rest/ingests/{id}/metadata")
	public void downloadMetadata(@PathVariable String id, String[] categories,
			Writer writer, HttpServletResponse response) {
		response.setHeader("Content-Type", "text/plain; charset=utf-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=metadata_" + id + ".txt");

		PrintWriter outputWriter = new PrintWriter(writer);

		for (String category : categories) {
			outputWriter.println("Category " + category);
		}
	}

}
