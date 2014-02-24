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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengeoportal.harvester.api.domain.Frequency;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.exception.InstanceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/test-data-config.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
public class IngestServiceImplTest {

	@Autowired
	private IngestService ingestService;

	@Test
	@DatabaseSetup("ingestData.xml")
	public void testFindIngest() {

		Ingest ingest = ingestService.findById(1L);
		Assert.assertNotNull(ingest);
		Assert.assertEquals("ingest1", ingest.getName());
	}

	@Test
	@DatabaseSetup("ingestData.xml")
	public void testFindAll() {
		PageRequest pageable = new PageRequest(0, 2);

		Page<Ingest> page = ingestService.findAll(pageable);
		Assert.assertNotNull(page);

		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(2, page.getNumberOfElements());
		Assert.assertEquals(3, page.getTotalElements());

		List<Ingest> ingests = page.getContent();
		Assert.assertEquals(2, ingests.size());
	}

	@Test(expected = InstanceNotFoundException.class)
	@DatabaseSetup("ingestServiceTestSaveWithRepoIdData.xml")
	public void testSaveWithRepoId() {
		Ingest ingest = new IngestOGP();
		ingest.setName("testSaveWithRepoId");
		ingest.setFrequency(Frequency.DAILY);
		ingest.setBeginDate(new Date());
		Ingest savedIngest = ingestService.save(ingest, 1L, InstanceType.SOLR);
		Assert.assertNotNull("Id is null. Ingest has not been saved",
				savedIngest.getId());

		Ingest ingest2 = new IngestOGP();
		ingest2.setName("testSaveWithRepoId");
		ingest2.setFrequency(Frequency.DAILY);
		ingest2.setBeginDate(new Date());
		// This must throw an Exception because there is no CustomRepository
		// with id = 1 and serviceType = GEONETWORK
		ingestService.save(ingest2, 1L, InstanceType.GEONETWORK);

	}
}
