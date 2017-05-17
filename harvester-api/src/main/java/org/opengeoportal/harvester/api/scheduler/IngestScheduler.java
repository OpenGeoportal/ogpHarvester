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

import com.google.common.collect.Sets;

import ch.qos.logback.core.net.SyslogOutputStream;

import org.opengeoportal.harvester.api.domain.Frequency;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.quartz.*;
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

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 */
@Service
public class IngestScheduler implements
org.opengeoportal.harvester.api.scheduler.Scheduler {
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
            scheduled = false;
            rollbackTransaction(transactionStatus, se);

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
    public boolean scheduleAnImmediateIngest(Ingest ingest) {

        boolean scheduled = true;
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = this.transactionManager
                    .getTransaction(new DefaultTransactionDefinition());

            //Create the Job and the trigger
            JobDetail job = createJobDetail(ingest);            
            Trigger trigger = createTrigger(ingest.getId(), new Date(System.currentTimeMillis()+10000), ingest.getFrequency(), job);
            
            if(schedulerFactoryBean.getScheduler().checkExists(job.getKey())) {
                schedulerFactoryBean.getScheduler().deleteJob(job.getKey());               
            }
            
            schedulerFactoryBean.getScheduler().scheduleJob(job, trigger);              
            
            printLogInfos(job, true);

        } catch (SchedulerException se) {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot scheduled the ingest " + ingest.getId(), se);
            }
            scheduled = false;
            rollbackTransaction(transactionStatus, se);

        }

        if (transactionStatus != null) {
            this.transactionManager.commit(transactionStatus);
        }

        return scheduled;

    }

    private void printLogInfos(JobDetail job, boolean debug) throws SchedulerException {
        if(debug) {
            System.out.println("JOB KEY --- " + job.getKey().getGroup() + job.getKey().getName());
            for (String groupName : schedulerFactoryBean.getScheduler().getJobGroupNames()) {

                for (JobKey jobKey : schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) schedulerFactoryBean.getScheduler().getTriggersOfJob(jobKey);

                    if(!triggers.isEmpty()) {
                        Date nextFireTime = triggers.get(0).getNextFireTime();

                        String triggerName = triggers.get(0).getJobKey().getName();
                        String triggerGroup = triggers.get(0).getJobKey().getGroup();

                        System.out.println("[jobName] : " + jobName + " [groupName] : "
                                + jobGroup + " - " + "[triggerName] : " + triggerName + " [triggerrGroupName] : "
                                + triggerGroup + " - " + nextFireTime);
                    } else {
                        System.out.println("[jobName] : " + jobName + " [groupName] : "
                                + jobGroup);
                    }

                }
            }
        }
    }

    private void rollbackTransaction(TransactionStatus transactionStatus, SchedulerException se) {
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
        boolean unscheduled = true;
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = this.transactionManager
                    .getTransaction(new DefaultTransactionDefinition());
            unscheduled = schedulerFactoryBean.getScheduler().deleteJob(
                    jobKey(JOB_PREFIX + ingest.getId()));
        } catch (SchedulerException e) {
            if (logger.isWarnEnabled()) {
                logger.warn(
                        "Cannot unschedule ingest with id " + ingest.getId(), e);
            }
            unscheduled = false;
            rollbackTransaction(transactionStatus, e);
        }
        if (transactionStatus != null) {
            this.transactionManager.commit(transactionStatus);
        }
        return unscheduled;
    }


    @Override
    @Transactional
    public boolean interrupt(Ingest ingest) throws SchedulerException {
        boolean cancelled = true;
        TransactionStatus transactionStatus = null;
        try {
            transactionStatus = this.transactionManager
                    .getTransaction(new DefaultTransactionDefinition());
            schedulerFactoryBean.getScheduler().interrupt(jobKey(JOB_PREFIX + ingest.getId()));

        } catch (UnableToInterruptJobException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot cancel ingest with id " + ingest.getId(), e);
            }
            cancelled = false;
            rollbackTransaction(transactionStatus, e);
        }
        if (transactionStatus != null) {
            this.transactionManager.commit(transactionStatus);
        }
        return cancelled;
    }

    /**
     * Create a trigger for jobDetail with the given frequency and start date.
     *
     * @param ingestId  ingest identifier.
     * @param startDate start date.
     * @param frequency frequency.
     * @param jobDetail jobDetail to be launched when the trigger fire.
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
        case EVERYXMINUTES:
            triggerBuilder.withSchedule(calendarIntervalSchedule()
                    .withIntervalInMinutes(1));
            break;
        default:
            break;
        }
        return triggerBuilder.build();
    }

    /**
     * @param ingestId ingest identifier.
     * @return a key for the passed identifier.
     */
    private String generateTriggerIdentity(Long ingestId) {
        return TRIGGER_PREFIX + ingestId;
    }

    /**
     * Create a {@link JobDetail} based on the ingest passed as parameter.
     *
     * @param ingest the ingest to be scheduled.
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

    /**
     * Create a {@link JobDetail} based on the ingest passed as parameter.
     *
     * @param ingest the ingest to be scheduled.
     * @param isDurable for durabilty
     * @return a JobDetail base on the ingest passed.
     */
    private JobDetail createImmediateJobDetail(Ingest ingest) {
        JobDetailFactoryBean jdFactory = new JobDetailFactoryBean();
        jdFactory.setName(generateJobName(ingest));
        jdFactory.setDurability(true);
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
        org.quartz.Scheduler scheduler = schedulerFactoryBean.getScheduler();
        String jobName = generateJobName(ingest);
        JobKey jobKey = new JobKey(jobName, org.quartz.Scheduler.DEFAULT_GROUP);
        TriggerKey triggerKey = new TriggerKey(
                generateTriggerIdentity(ingest.getId()),
                org.quartz.Scheduler.DEFAULT_GROUP);
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
        org.quartz.Scheduler scheduler = schedulerFactoryBean.getScheduler();
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
