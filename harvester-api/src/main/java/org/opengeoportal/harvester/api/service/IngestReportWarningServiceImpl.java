/**
 * IngestReportErrorServiceImpl.java
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
package org.opengeoportal.harvester.api.service;

import java.util.List;
import java.util.Map;

import org.opengeoportal.harvester.api.dao.IngestReportWarningRepository;
import org.opengeoportal.harvester.api.domain.IngestReportWarning;
import org.opengeoportal.harvester.api.domain.IngestReportWarningType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * @author cbarne02
 */
@Service
public class IngestReportWarningServiceImpl
        implements IngestReportWarningsService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Report error repository. */
    @Autowired
    private IngestReportWarningRepository reportWarningRepository;

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.service.IngestReportErrorService#
     * getCountFieldErrorsByReportId(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCountWarningsByReportId(final Long id) {

        final List<Object[]> errorList = this.reportWarningRepository
                .getCountWarningsByReportId(id,
                        IngestReportWarningType.UNREQUIRED_FIELD_WARNING);
        final Map<String, Long> result = Maps.newTreeMap();
        for (final Object[] fieldError : errorList) {
            final String fieldName = (String) fieldError[0];
            final Long errorCount = (Long) fieldError[1];

            result.put(fieldName, errorCount);

        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.service.IngestReportErrorService#
     * getCountErrorTypesByReportId(java.lang.Long)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<IngestReportWarningType, Long> getCountWarningTypesByReportId(
            final Long reportId) {
        final List<Object[]> items = this.reportWarningRepository
                .getCountWarningTypesByReportId(reportId);
        final List<IngestReportWarningType> remainingTypes = Lists
                .newArrayList(IngestReportWarningType.values());
        final Map<IngestReportWarningType, Long> result = Maps.newHashMap();
        for (final Object item : items) {
            final Object[] tuple = (Object[]) item;
            final IngestReportWarningType warningType = (IngestReportWarningType) tuple[0];
            final Long count = (Long) tuple[1];
            result.put(warningType, count);
            remainingTypes.remove(warningType);
        }

        // Set a default value for error types not returned by the repository
        for (final IngestReportWarningType warningType : remainingTypes) {
            result.put(warningType, 0L);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.service.IngestReportErrorService#save
     * (org.opengeoportal.harvester.api.domain.IngestReportError)
     */
    @Override
    @Transactional
    public IngestReportWarning save(final IngestReportWarning reportWarning) {
        return this.reportWarningRepository.save(reportWarning);
    }

}
