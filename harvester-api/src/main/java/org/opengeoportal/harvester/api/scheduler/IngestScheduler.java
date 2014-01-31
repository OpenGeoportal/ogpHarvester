/**
 * IngestScheduler.java
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

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.JobKey.*;
import static org.quartz.TriggerKey.*;
import static org.quartz.DateBuilder.*;
import static org.quartz.impl.matchers.KeyMatcher.*;
import static org.quartz.impl.matchers.GroupMatcher.*;
import static org.quartz.impl.matchers.AndMatcher.*;
import static org.quartz.impl.matchers.OrMatcher.*;
import static org.quartz.impl.matchers.EverythingMatcher.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.opengeoportal.harvester.api.domain.Frequency;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
@Service
public class IngestScheduler implements
		org.opengeoportal.harvester.api.scheduler.Scheduler {
	/**
	 * 
	 */
	private static final String JOB_PREFIX = "IngestJob_for_ingest_";

	/**
	 * 
	 */
	public static final String TRIGGER_PREFIX = "JobTrigger_for_job_";

	/** Logger. */
	private final Logger logger = LoggerFactory
			.getLogger(IngestScheduler.class);

	/**
	 * Factory used to schedule jobs.
	 */
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private PlatformTransactionManager transactionManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.scheduler.Scheduler#scheduleIngest(org
	 * .opengeoportal.harvester.api.domain.Ingest)
	 */
	@Override
	@Transactional
	public boolean scheduleIngest(Ingest ingest) {

		boolean scheduled = true;
		TransactionStatus transactionStatus = null;
		try {
			transactionStatus = this.transactionManager
					.getTransaction(new DefaultTransactionDefinition());
			unschedule(ingest);
			Date startDate = ingest.getBeginDate();
			Frequency frequency = ingest.getFrequency();

			JobDetail jobDetail = createJobDetail(ingest);
			Trigger trigger = createTrigger(ingest.getId(), startDate,
					frequency, jobDetail);

			schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
		} catch (SchedulerException se) {
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot scheduled the ingest " + ingest.getId(), se);
			}
			if (transactionStatus != null) {
				try {
					this.transactionManager.rollback(transactionStatus);
				} catch (TransactionException tex) {
					logger.error(
							"Job registration exception overridden by rollback exception",
							se);
					throw tex;
				}
			}
			scheduled = false;
		}

		if (transactionStatus != null) {
			this.transactionManager.commit(transactionStatus);
		}

		return scheduled;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.scheduler.Scheduler#unschedule(org.
	 * opengeoportal.harvester.api.domain.Ingest)
	 */
	@Override
	@Transactional
	public boolean unschedule(Ingest ingest) throws SchedulerException {
		boolean unscheduled = false;
		try {
			unscheduled = schedulerFactoryBean.getScheduler().deleteJob(
					jobKey(JOB_PREFIX + ingest.getId()));
		} catch (SchedulerException e) {
			if (logger.isWarnEnabled()) {
				logger.warn(
						"Cannot unschedule ingest with id " + ingest.getId(), e);
			}
			throw e;
		}
		return unscheduled;
	}

	/**
	 * Create a trigger for jobDetail with the given frequency and start date.
	 * 
	 * @param ingestId
	 *            ingest identifier.
	 * @param startDate
	 *            start date.
	 * @param frequency
	 *            frequency.
	 * @param jobDetail
	 *            jobDetail to be launched when the trigger fire.
	 * @return a trigger.s
	 */
	private Trigger createTrigger(Long ingestId, Date startDate,
			Frequency frequency, JobDetail jobDetail) {
		TriggerBuilder<Trigger> triggerBuilder = newTrigger();
		triggerBuilder.withIdentity(generateTriggerIdentity(ingestId))
				.forJob(jobDetail).startAt(startDate);
		switch (frequency) {
		case ONCE:
			// no schedule needed
			break;
		case DAILY:
			triggerBuilder.withSchedule(calendarIntervalSchedule()
					.withIntervalInDays(1));
			break;
		case WEEKLY:
			triggerBuilder.withSchedule(calendarIntervalSchedule()
					.withIntervalInWeeks(1));
			break;
		case MONTHLY:
			triggerBuilder.withSchedule(calendarIntervalSchedule()
					.withIntervalInMonths(1));
			break;
		default:
			break;
		}
		return triggerBuilder.build();
	}

	/**
	 * @param ingestId
	 * @return
	 */
	private String generateTriggerIdentity(Long ingestId) {
		return TRIGGER_PREFIX + ingestId;
	}

	/**
	 * Create a {@link JobDetail} based on the ingest passed as parameter.
	 * 
	 * @param ingest
	 *            the ingest to be scheduled.
	 * @return a JobDetail base on the ingest passed.
	 */
	private JobDetail createJobDetail(Ingest ingest) {
		JobDetailFactoryBean jdFactory = new JobDetailFactoryBean();
		jdFactory.setName(generateJobName(ingest));
		jdFactory.setJobClass(IngestJob.class);
		jdFactory.getJobDataMap().put(IngestJob.INGEST_ID,
				ingest.getId().toString());
		jdFactory.afterPropertiesSet();
		return jdFactory.getObject();
	}

	private String generateJobName(Ingest ingest) {
		return JOB_PREFIX + ingest.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.scheduler.Scheduler#getNextRun(org.
	 * opengeoportal.harvester.api.domain.Ingest)
	 */
	@Override
	public Date getNextRun(Ingest ingest) {
		Date nextFireTimeUtc = null;
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		String jobName = generateJobName(ingest);
		JobKey jobKey = new JobKey(jobName, Scheduler.DEFAULT_GROUP);
		TriggerKey triggerKey = new TriggerKey(
				generateTriggerIdentity(ingest.getId()),
				Scheduler.DEFAULT_GROUP);
		boolean isTriggerExisting;
		try {
			isTriggerExisting = scheduler.checkExists(triggerKey);

			if (isTriggerExisting) {
				Trigger trigger = scheduler.getTrigger(triggerKey);
				nextFireTimeUtc = trigger.getNextFireTime();
			}
		} catch (SchedulerException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error getting next fire time for ingest: "
						+ ingest.getId(), e);
			}
		}

		return nextFireTimeUtc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.scheduler.Scheduler#getCurrentlyExecutingJobs
	 * ()
	 */
	@Override
	public SortedSet<Long> getCurrentlyExecutingJobs() {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		SortedSet<Long> ingestIdList = Sets.newTreeSet();
		try {
			List<JobExecutionContext> jobList = scheduler
					.getCurrentlyExecutingJobs();
			for (JobExecutionContext jec : jobList) {
				Long ingestId = Long.valueOf((String) jec.getMergedJobDataMap()
						.get(IngestJob.INGEST_ID));
				ingestIdList.add(ingestId);
			}

		} catch (SchedulerException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error getting currently executing ingests", e);
			}
		}
		return ingestIdList;
	}

}
