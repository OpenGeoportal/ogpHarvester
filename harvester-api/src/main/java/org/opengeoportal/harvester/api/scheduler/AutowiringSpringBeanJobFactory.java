/**
 * AutowiringSpringBeanJobFactory.java
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

import org.opengeoportal.harvester.api.service.IngestService;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * This JobFactory autowires automatically the created quartz bean with spring
 * <code>@Autowired</code> dependencies.
 *
 * @author jelies (thanks to <a href=
 *         "http://webcache.googleusercontent.com/search?q=cache:FH-N1i--sDgJ:blog.btmatthews.com/2011/09/24/inject-application-context-dependencies-in-quartz-job-beans/+&cd=7&hl=en&ct=clnk&gl=es"
 *         >Brian Matthews</a> )
 *
 */
public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory
        implements ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;

    /**
     * Ingest service.
     */
    @Autowired
    private IngestService ingestService;

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle)
            throws Exception {
        if (this.beanFactory == null) {
            throw new IllegalStateException(
                    "beanFactory must be initialized before calling "
                            + "createJobInstance");
        }

        Object job = super.createJobInstance(bundle);
        if (job instanceof IngestJob) {
            final IngestJob ingestJob = (IngestJob) job;
            ingestJob.setIngestService(this.ingestService);
        }

        this.beanFactory.autowireBean(job);
        job = this.beanFactory.applyBeanPostProcessorsAfterInitialization(job,
                "ingestJob");
        return job;
    }

    /**
     * @return the ingestService
     */
    public IngestService getIngestService() {
        return this.ingestService;
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) {

        this.beanFactory = context.getAutowireCapableBeanFactory();
    }

    /**
     * @param ingestService
     *            the ingestService to set
     */
    public void setIngestService(final IngestService ingestService) {
        this.ingestService = ingestService;
    }
}
