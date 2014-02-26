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

import static org.junit.Assert.*;
import ch.qos.logback.core.boolex.Matcher;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Lists;

import org.hamcrest.MatcherAssert;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengeoportal.harvester.api.domain.*;
import org.opengeoportal.harvester.api.service.IngestJobStatusService;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/test-data-config.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@TransactionConfiguration(defaultRollback = true)
public class IngestRepositoryTest {
	@Autowired
	private IngestRepository ingestRepository;
	@Autowired
	private IngestJobStatusService jobStatusService;
	@Autowired
	private IngestReportErrorService reportErrorService;
	@Autowired
	private IngestReportService reportService;

	@Test
	@DatabaseSetup("ingestData.xml")
	public void testFindIngest() {

		Ingest ingest = ingestRepository.findOne(1L);
		Assert.assertNotNull(ingest);
		Assert.assertEquals("ingest1", ingest.getName());
	}

	@Test
	@DatabaseSetup("ingestData.xml")
	public void testDeleteIngest() {
		ingestRepository.delete(1L);

		List<Ingest> ingests = ingestRepository.findAll();
		Assert.assertEquals(1, ingests.size());
	}

	@Test
	@DatabaseSetup("ingestData.xml")
	public void testUpdateIngest() {

		Ingest ingest = ingestRepository.findOne(2L);
		ingest.setName("ingest2 changed");
		Ingest ingestSaved = ingestRepository.save(ingest);

		Assert.assertEquals("ingest2 changed", ingestSaved.getName());
	}

	@Test
	public void testInsertIngest() {
		Ingest ingest = new IngestOGP();
		ingest.setName("ingest3");
		ingest.setUrl("http://ingest3");
		ingest.setBeginDate(new Date());
		ingest.setFrequency(Frequency.DAILY);
		ingest.addRequiredField("themeKeyword");

		Ingest ingestCreated = ingestRepository.save(ingest);

		Assert.assertNotNull(ingestCreated);

		Ingest ingestRetrieved = ingestRepository.findOne(ingestCreated.getId());

		Assert.assertEquals(ingestCreated, ingestRetrieved);
	}

	@Test
	@DatabaseSetup("ingestData.xml")
	public void testUpdateIngestWithJob() {
		Ingest ingest = ingestRepository.findOne(2L);

		IngestJobStatus jobStatus = new IngestJobStatus();
		jobStatus.setStatus(IngestJobStatusValue.SUCCESSED);
		jobStatus.setStartTime(new Date());
		jobStatus.setEndTime(new Date());
		jobStatus.setIngest(ingest);
		jobStatus = jobStatusService.save(jobStatus);
 
		IngestReport report = new IngestReport();
		report.setPublicRecords(100);
		report.setRestrictedRecords(20);
		report.setRasterRecords(70);
		report.setVectorRecords(50);

		report.setWebServiceWarnings(20);
		report.setUnrequiredFieldWarnings(60);
		report.setJobStatus(jobStatus);
		report = reportService.save(report);
		jobStatus.setIngestReport(report);
		jobStatus = jobStatusService.save(jobStatus);

		IngestReportError reportError = new IngestReportError();
		reportError.setField("title");
		reportError.setType(IngestReportErrorType.REQUIRED_FIELD_ERROR);
		reportError.setReport(report);
		reportError = reportErrorService.save(reportError);
	
		Ingest ingestUpdated = ingestRepository.save(ingest);

		Assert.assertNotNull(ingestUpdated);

		Ingest ingestRetrieved = ingestRepository.findOne(2L);
		Assert.assertEquals(ingestUpdated, ingestRetrieved);

		List<IngestJobStatus> jobStatuses = jobStatusService.getStatusesForIngest(ingest.getId());
		Assert.assertEquals(1, jobStatuses.size());

		IngestReport reportRetrieved = jobStatuses.get(0).getIngestReport();
		Assert.assertEquals(reportRetrieved, report);

		Assert.assertEquals(IngestJobStatusValue.SUCCESSED, jobStatuses.get(0).getStatus());
		Assert.assertEquals(100, reportRetrieved.getPublicRecords());
		Assert.assertEquals(20, reportRetrieved.getRestrictedRecords());
	}

	@Test
	public void testIngestSaveCascade() {
		Ingest ingest = new IngestOGP();
		ingest.setBeginDate(new Date());
		ingest.setFrequency(Frequency.ONCE);
		ingest.setLastRun(new Date());
		ingest.setNameOgpRepository("Name Ogp Repository");
		ingest.setUrl("http://geodata.tufts.edu");
		ingest.setScheduled(true);
		ingest.setName("Test name");

		ingest = ingestRepository.save(ingest);

		assertNotNull(ingest.getId());

		IngestJobStatus jStatus = new IngestJobStatus();
		jStatus.setStatus(IngestJobStatusValue.SUCCESSED);
		jStatus.setStartTime(new Date());
		jStatus.setEndTime(new Date());
		jStatus.setIngest(ingest);
		jStatus = jobStatusService.save(jStatus);

		long numRecords = 1000L;
		ingest = ingestRepository.save(ingest);
		IngestReport iReport = new IngestReport();
		iReport.setPublicRecords(numRecords);
		iReport.setRasterRecords(numRecords);
		iReport.setRestrictedRecords(numRecords);
		iReport.setUnrequiredFieldWarnings(numRecords);
		iReport.setVectorRecords(numRecords);
		iReport.setWebServiceWarnings(numRecords);
		iReport.setJobStatus(jStatus);
		iReport = reportService.save(iReport);

		for (int i = 0; i < numRecords; i++) {
			IngestReportError error = new IngestReportError();
			error.setField("my_field");
			error.setMessage("My error message");
			error.setMetadata("My metadata");
			error.setType(IngestReportErrorType.REQUIRED_FIELD_ERROR);
			error.setReport(iReport);
			error = reportErrorService.save(error);
		}

		ingest.setLastRun(new Date());
		ingest = ingestRepository.save(ingest);
	}

}
