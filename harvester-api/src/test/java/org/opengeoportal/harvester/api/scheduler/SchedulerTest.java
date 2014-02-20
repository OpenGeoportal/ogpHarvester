/**
 * SchedulerTest.java
 *
 * Copyright (C) 2014
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
 * Authors:: Juan Luis Rodríguez (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.api.scheduler;

import static org.quartz.TriggerBuilder.newTrigger;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.component.IngestJobFactory;
import org.opengeoportal.harvester.api.component.MetadataIngester;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.service.IngestService;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Maps;

/**
 * Test the scheduler.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/test-data-config.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
public class SchedulerTest {
	/**
	 * Factory in charge of create jobs.
	 */
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory;

	@Autowired
	private IngestService ingestService;

	/**
	 * Dummy metadata ingester.
	 */
	@Autowired
	private MetadataIngester metadataIngester;

	/**
	 * Test if Job instances are autowired with the required beans and the
	 * method ingest is called at least once (the job has been executed).
	 * 
	 * @throws SchedulerException
	 *             if there is any problem launching the job.
	 * @throws InterruptedException
	 *             if test is interrupted while waiting for the job to start.
	 */
	@Test
	@DatabaseSetup("schedulerData.xml")
	public void autowireJobTest() throws SchedulerException,
			InterruptedException {

		JobDetailFactoryBean jdFactory = new JobDetailFactoryBean();
		jdFactory.setName("jdFactory");
		jdFactory.setJobClass(IngestJob.class);
		Map<String, Object> jobDataMap = Maps.newHashMap();
		jobDataMap.put(IngestJob.INGEST_ID, Long.toString(10L));
		jdFactory.setJobDataAsMap(jobDataMap);
		jdFactory.afterPropertiesSet();
		JobDetail jobDetail = jdFactory.getObject();

		SimpleTrigger trigger = (SimpleTrigger) newTrigger()
				.withIdentity("jdFactory", "DEFAULT").forJob(jobDetail).build();

		schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
		Thread.sleep(5000L);
		// verify(metadataIngester, atLeastOnce()).ingest(any(Metadata.class));
	}

	@Test
	@DatabaseSetup("schedulerData.xml")
	public void lastRunSavedWhenExceptionTest() throws Exception {
		Long ingestId = 10L;
		Ingest ingest = ingestService.findById(ingestId);
		ingest.setLastRun(null);
		ingestService.save(ingest);
		JobDetailFactoryBean jdFactory = new JobDetailFactoryBean();
		jdFactory.setName("jdFactory");
		jdFactory.setJobClass(IngestJob.class);
		Map<String, Object> jobDataMap = Maps.newHashMap();
		jobDataMap.put(IngestJob.INGEST_ID, Long.toString(ingestId));
		jdFactory.setJobDataAsMap(jobDataMap);
		jdFactory.afterPropertiesSet();
		JobDetail jobDetail = jdFactory.getObject();

		SimpleTriggerImpl trigger = (SimpleTriggerImpl) SimpleScheduleBuilder
				.simpleSchedule().withRepeatCount(0).build();
		TriggerFiredBundle bundle = mock(TriggerFiredBundle.class);
		when(bundle.getJobDetail()).thenReturn(jobDetail);
		when(bundle.getTrigger()).thenReturn(trigger);
		Object job = autowiringSpringBeanJobFactory.createJobInstance(bundle);

		RuntimeException mockedException = new RuntimeException();
		BaseIngestJob baseIngestJobMock = mock(BaseIngestJob.class);
		doThrow(mockedException).when(baseIngestJobMock).run();

		IngestJobFactory jobFactoryMock = mock(IngestJobFactory.class);
		when(jobFactoryMock.newIngestJob(any(Ingest.class))).thenReturn(
				baseIngestJobMock);

		Method setJobFactoryMethod = job.getClass().getMethod(
				"setIngestJobFactory", IngestJobFactory.class);
		ReflectionUtils.invokeJdbcMethod(setJobFactoryMethod, job,
				jobFactoryMock);

		JobExecutionContext context = mock(JobExecutionContext.class);
		when(context.getJobDetail()).thenReturn(jobDetail);
		Method executeMethod = job.getClass().getMethod("execute",
				JobExecutionContext.class);
		try {
			ReflectionUtils.invokeMethod(executeMethod, job, context);
		} catch (UndeclaredThrowableException ute) {
			Assert.assertEquals(
					"Cause exception must be the same we created in test method",
					mockedException, ute.getUndeclaredThrowable().getCause());
			ingest = ingestService.findById(ingestId);
			Assert.assertNotNull("LastRun must not be null",
					ingest.getLastRun());

		}

		// verify(metadataIngester, atLeastOnce()).ingest(any(Metadata.class));
	}
}
