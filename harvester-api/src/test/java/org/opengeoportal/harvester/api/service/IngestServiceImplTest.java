package org.opengeoportal.harvester.api.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/test-data-config.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
public class IngestServiceImplTest {

    @Autowired
    private IngestService ingestService;

    @Test
    @DatabaseSetup("ingestData.xml")
    public void testFindIngest() {

        Ingest ingest = ingestService.findByName("ingest1");
        Assert.assertNotNull(ingest);
        Assert.assertEquals("ingest1", ingest.getName());
    }
}
