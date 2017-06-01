/**
 * IngestReportErrorRepository.java
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
package org.opengeoportal.harvester.api.dao;

import java.util.List;

import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportError;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public interface IngestReportErrorRepository
        extends JpaRepository<IngestReportError, Long> {

    /**
     * @param reportId
     * @param requiredFieldError
     * @param requiredField
     * @param pageRequest
     * @return
     */
    Page<IngestReportError> findByReportIdAndTypeAndField(Long reportId,
            IngestReportErrorType requiredFieldError, String requiredField,
            Pageable pageRequest);

    /**
     * Return the count errors of the passed type categorized by the error
     * subtype for a given ingest report.
     * 
     * @param id
     *            the ingest report identifier.
     * @return A list of Object[]. Each Object[] has these elements:
     *         <ul>
     *         <li>Object[0]: the field name.</li>
     *         <li>Object[1]: errors count for that field.</li>
     *         </ul>
     */
    @Query(value = "select r.field, count(r) from IngestReportError r where "
            + "r.report.id=:id and " + "r.type=:errorType group by r.field")
    List<Object[]> getCountErrorsByReportId(@Param("id") Long id,
            @Param("errorType") IngestReportErrorType errorType);

    /**
     * Gets the count of each errors group by error type for a given
     * {@link IngestReport}.
     * 
     * @param reportId
     *            the ingest report identifier.
     * @return A list of Object[]. Each Object[] has these elements:
     *         <ul>
     *         <li>Object[0]: the error type.</li>
     *         <li>Object[1]: errors count for that type.</li>
     *         </ul>
     */
    @Query(value = "select r.type, count(r) from IngestReportError r where "
            + "r.report.id=:id group by r.type")
    List<Object[]> getCountErrorTypesByReportId(@Param("id") Long reportId);

}
