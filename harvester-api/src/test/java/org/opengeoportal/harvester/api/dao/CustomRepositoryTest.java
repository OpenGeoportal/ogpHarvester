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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/test-data-config.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@Transactional
public class CustomRepositoryTest {

    @Autowired
    private CustomRepositoryRepository customRepositoryRepository;

    @Test
    @DatabaseSetup("customRepositoryData.xml")
    public void testFindCustomRepository() {
        CustomRepository customRepository1 = new CustomRepository();
        customRepository1.setUrl("");
        CustomRepository customRepository = customRepositoryRepository.findOne(1L);
        Assert.assertNotNull(customRepository);
        Assert.assertEquals("repo1", customRepository.getName());
    }

    @Test
    @DatabaseSetup("customRepositoryData.xml")
    public void testDeleteCustomRepository() {
        customRepositoryRepository.delete(1L);

        List<CustomRepository> ingests = customRepositoryRepository.findAll();
        Assert.assertEquals(1, ingests.size());
    }

    @Test
    @DatabaseSetup("customRepositoryData.xml")
    public void testUpdateCustomRepository() {

        CustomRepository customRepository = customRepositoryRepository.findByName("repo2");
        customRepository.setName("repo2 changed");
        CustomRepository customRepositorySaved = customRepositoryRepository.save(customRepository);

        Assert.assertEquals("repo2 changed", customRepositorySaved.getName());
    }

    @Test
    public void testInsertCustomRepository() {
        CustomRepository customRepository = new CustomRepository();
        customRepository.setName("repo3");
        customRepository.setUrl("http://repo3");
        customRepository.setServiceType("OGP");

        CustomRepository customRepositoryCreated = customRepositoryRepository.save(customRepository);

        Assert.assertNotNull(customRepositoryCreated);

        CustomRepository customRepositoryRetrieved = customRepositoryRepository.findByName("repo3");

        Assert.assertEquals(customRepositoryCreated, customRepositoryRetrieved);
    }
}
