/**
 * IngestReportErrorService.java
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

import java.util.Map;

import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportError;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public interface IngestReportErrorService {
	/**
	 * Save an {@link IngestReportError}.
	 * 
	 * @param reportError
	 *            the {@link IngestReportError}.
	 * @return the saved error report.
	 */
	IngestReportError save(IngestReportError reportError);

	/**
	 * Gets the count of each errors group by error type for a given
	 * {@link IngestReport}.
	 * 
	 * @param reportId
	 *            the ingest report identifier.
	 * @return a Map with the count of errors for each
	 *         {@link IngestReportErrorType}.
	 */
	Map<IngestReportErrorType, Long> getCountErrorTypesByReportId(Long reportId);

	/**
	 * Return the count of type field not found errors categorized by the name
	 * of the field for a given ingest report.
	 * 
	 * @param id
	 *            the ingest report identifier.
	 * @param errorType
	 *            main error category.
	 * @return a Map with the field name as a key and the count of this error
	 *         like value.
	 */
	Map<String, Long> getCountErrorsByReportId(Long id,
			IngestReportErrorType errorType);

}
