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

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.opengeoportal.harvester.api.domain.Frequency;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.matchers.GroupMatcher;
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

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 */
@Service
public class IngestScheduler
        implements org.opengeoportal.harvester.api.scheduler.Scheduler {
    /**
     *
     */
    public static final String TRIGGER_PREFIX = "JobTrigger_for_job_";
    /**
     *
     */
    private static final String JOB_PREFIX = "IngestJob_for_ingest_";
    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory
            .getLogger(IngestScheduler.class);

    /**
     * Factory used to schedule jobs.
     */
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * Create a {@link JobDetail} based on the ingest passed as parameter.
     *
     * @param ingest
     *            the ingest to be scheduled.
     * @return a JobDetail base on the ingest passed.
     */
    private JobDetail createJobDetail(final Ingest ingest) {
        final JobDetailFactoryBean jdFactory = new JobDetailFactoryBean();
        jdFactory.setName(this.generateJobName(ingest));
        jdFactory.setJobClass(IngestJob.class);
        jdFactory.getJobDataMap().put(IngestJob.INGEST_ID,
                ingest.getId().toString());
        jdFactory.afterPropertiesSet();
        return jdFactory.getObject();
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
    private Trigger createTrigger(final Long ingestId, final Date startDate,
            final Frequency frequency, final JobDetail jobDetail) {
        final TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder
                .newTrigger();
        triggerBuilder.withIdentity(this.generateTriggerIdentity(ingestId))
                .forJob(jobDetail).startAt(startDate);
        switch (frequency) {
        case ONCE:
            // no schedule needed
            break;
        case DAILY:
            triggerBuilder.withSchedule(CalendarIntervalScheduleBuilder
                    .calendarIntervalSchedule().withIntervalInDays(1));
            break;
        case WEEKLY:
            triggerBuilder.withSchedule(CalendarIntervalScheduleBuilder
                    .calendarIntervalSchedule().withIntervalInWeeks(1));
            break;
        case MONTHLY:
            triggerBuilder.withSchedule(CalendarIntervalScheduleBuilder
                    .calendarIntervalSchedule().withIntervalInMonths(1));
            break;
        case EVERYXMINUTES:
            triggerBuilder.withSchedule(CalendarIntervalScheduleBuilder
                    .calendarIntervalSchedule().withIntervalInMinutes(1));
            break;
        default:
            break;
        }
        return triggerBuilder.build();
    }

    private String generateJobName(final Ingest ingest) {
        return IngestScheduler.JOB_PREFIX + ingest.getId();
    }

    /**
     * @param ingestId
     *            ingest identifier.
     * @return a key for the passed identifier.
     */
    private String generateTriggerIdentity(final Long ingestId) {
        return IngestScheduler.TRIGGER_PREFIX + ingestId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opengeoportal.harvester.api.scheduler.Scheduler#
     * getCurrentlyExecutingJobs ()
     */
    @Override
    public SortedSet<Long> getCurrentlyExecutingJobs() {
        final org.quartz.Scheduler scheduler = this.schedulerFactoryBean
                .getScheduler();
        final SortedSet<Long> ingestIdList = Sets.newTreeSet();
        try {
            final List<JobExecutionContext> jobList = scheduler
                    .getCurrentlyExecutingJobs();
            for (final JobExecutionContext jec : jobList) {
                final Long ingestId = Long.valueOf((String) jec
                        .getMergedJobDataMap().get(IngestJob.INGEST_ID));
                ingestIdList.add(ingestId);
            }

        } catch (final SchedulerException e) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error("Error getting currently executing ingests",
                        e);
            }
        }
        return ingestIdList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opengeoportal.harvester.api.scheduler.Scheduler#getNextRun(org.
     * opengeoportal.harvester.api.domain.Ingest)
     */
    @Override
    public Date getNextRun(final Ingest ingest) {
        Date nextFireTimeUtc = null;
        final org.quartz.Scheduler scheduler = this.schedulerFactoryBean
                .getScheduler();
        final String jobName = this.generateJobName(ingest);
        new JobKey(jobName, org.quartz.Scheduler.DEFAULT_GROUP);
        final TriggerKey triggerKey = new TriggerKey(
                this.generateTriggerIdentity(ingest.getId()),
                org.quartz.Scheduler.DEFAULT_GROUP);
        boolean isTriggerExisting;
        try {
            isTriggerExisting = scheduler.checkExists(triggerKey);

            if (isTriggerExisting) {
                final Trigger trigger = scheduler.getTrigger(triggerKey);
                nextFireTimeUtc = trigger.getNextFireTime();
            }
        } catch (final SchedulerException e) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error("Error getting next fire time for ingest: "
                        + ingest.getId(), e);
            }
        }

        return nextFireTimeUtc;
    }

    @Override
    @Transactional
    public boolean interrupt(final Ingest ingest) throws SchedulerException {
        boolean cancelled = true;
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = this.transactionManager
                    .getTransaction(new DefaultTransactionDefinition());
            this.schedulerFactoryBean.getScheduler().interrupt(
                    JobKey.jobKey(IngestScheduler.JOB_PREFIX + ingest.getId()));

        } catch (final UnableToInterruptJobException e) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn(
                        "Cannot cancel ingest with id " + ingest.getId(), e);
            }
            cancelled = false;
            this.rollbackTransaction(transactionStatus, e);
        }
        if (transactionStatus != null) {
            this.transactionManager.commit(transactionStatus);
        }
        return cancelled;
    }

    private void printLogInfos(final JobDetail job, final boolean debug)
            throws SchedulerException {
        if (debug) {
            System.out.println("JOB KEY --- " + job.getKey().getGroup()
                    + job.getKey().getName());
            for (final String groupName : this.schedulerFactoryBean
                    .getScheduler().getJobGroupNames()) {

                for (final JobKey jobKey : this.schedulerFactoryBean
                        .getScheduler()
                        .getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    final String jobName = jobKey.getName();
                    final String jobGroup = jobKey.getGroup();

                    // get job's trigger
                    final List<Trigger> triggers = (List<Trigger>) this.schedulerFactoryBean
                            .getScheduler().getTriggersOfJob(jobKey);

                    if (!triggers.isEmpty()) {
                        final Date nextFireTime = triggers.get(0)
                                .getNextFireTime();

                        final String triggerName = triggers.get(0).getJobKey()
                                .getName();
                        final String triggerGroup = triggers.get(0).getJobKey()
                                .getGroup();

                        System.out.println("[jobName] : " + jobName
                                + " [groupName] : " + jobGroup + " - "
                                + "[triggerName] : " + triggerName
                                + " [triggerrGroupName] : " + triggerGroup
                                + " - " + nextFireTime);
                    } else {
                        System.out.println("[jobName] : " + jobName
                                + " [groupName] : " + jobGroup);
                    }

                }
            }
        }
    }

    private void rollbackTransaction(final TransactionStatus transactionStatus,
            final SchedulerException se) {
        if (transactionStatus != null) {
            try {
                this.transactionManager.rollback(transactionStatus);
            } catch (final TransactionException tex) {
                this.logger.error(
                        "Job registration exception overridden by rollback exception",
                        se);
                throw tex;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opengeoportal.harvester.api.scheduler.Scheduler#scheduleIngest(org
     * .opengeoportal.harvester.api.domain.Ingest)
     */
    @Override
    @Transactional
    public boolean scheduleAnImmediateIngest(final Ingest ingest) {

        boolean scheduled = true;
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = this.transactionManager
                    .getTransaction(new DefaultTransactionDefinition());

            // Create the Job and the trigger
            final JobDetail job = this.createJobDetail(ingest);
            final Trigger trigger = this.createTrigger(ingest.getId(),
                    new Date(System.currentTimeMillis() + 10000),
                    ingest.getFrequency(), job);

            if (this.schedulerFactoryBean.getScheduler()
                    .checkExists(job.getKey())) {
                this.schedulerFactoryBean.getScheduler()
                        .deleteJob(job.getKey());
            }

            this.schedulerFactoryBean.getScheduler().scheduleJob(job, trigger);

            this.printLogInfos(job, true);

        } catch (final SchedulerException se) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn(
                        "Cannot scheduled the ingest " + ingest.getId(), se);
            }
            scheduled = false;
            this.rollbackTransaction(transactionStatus, se);

        }

        if (transactionStatus != null) {
            this.transactionManager.commit(transactionStatus);
        }

        return scheduled;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opengeoportal.harvester.api.scheduler.Scheduler#scheduleIngest(org
     * .opengeoportal.harvester.api.domain.Ingest)
     */
    @Override
    @Transactional
    public boolean scheduleIngest(final Ingest ingest) {

        boolean scheduled = true;
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = this.transactionManager
                    .getTransaction(new DefaultTransactionDefinition());
            this.unschedule(ingest);
            final Date startDate = ingest.getBeginDate();
            final Frequency frequency = ingest.getFrequency();

            final JobDetail jobDetail = this.createJobDetail(ingest);
            final Trigger trigger = this.createTrigger(ingest.getId(),
                    startDate, frequency, jobDetail);

            this.schedulerFactoryBean.getScheduler().scheduleJob(jobDetail,
                    trigger);
        } catch (final SchedulerException se) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn(
                        "Cannot scheduled the ingest " + ingest.getId(), se);
            }
            scheduled = false;
            this.rollbackTransaction(transactionStatus, se);

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
    public boolean unschedule(final Ingest ingest) throws SchedulerException {
        boolean unscheduled = true;
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = this.transactionManager
                    .getTransaction(new DefaultTransactionDefinition());
            unscheduled = this.schedulerFactoryBean.getScheduler().deleteJob(
                    JobKey.jobKey(IngestScheduler.JOB_PREFIX + ingest.getId()));
        } catch (final SchedulerException e) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn(
                        "Cannot unschedule ingest with id " + ingest.getId(),
                        e);
            }
            unscheduled = false;
            this.rollbackTransaction(transactionStatus, e);
        }
        if (transactionStatus != null) {
            this.transactionManager.commit(transactionStatus);
        }
        return unscheduled;
    }

}
