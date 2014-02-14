/**
 * IngestJobStatusServiceImpl.java
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

import javax.annotation.Resource;

import org.hibernate.loader.custom.Return;
import org.opengeoportal.harvester.api.dao.IngestJobStatusRepository;
import org.opengeoportal.harvester.api.domain.IngestJobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
@Service
public class IngestJobStatusServiceImpl implements IngestJobStatusService {

	/** IngestJobStatusRepository. */
	@Resource
	private IngestJobStatusRepository jobStatusRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.service.IngestJobStatusService#save(org
	 * .opengeoportal.harvester.api.domain.IngestJobStatus)
	 */
	@Override
	public IngestJobStatus save(IngestJobStatus jobStatus) {
		return jobStatusRepository.save(jobStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.IngestJobStatusService#
	 * getStatusesForIngest(java.lang.Long)
	 */
	@Override
	public List<IngestJobStatus> getStatusesForIngest(Long ingestId) {
		return jobStatusRepository.findByIngestId(ingestId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.service.IngestJobStatusService#
	 * findLastStatusForIngest(java.lang.Long)
	 */
	@Override
	public IngestJobStatus findLastStatusForIngest(Long id) {
		// We only want the first element, so we pass a Pageable
		// asking for the first page with one element.
		Pageable page = new PageRequest(0, 1);
		Page<IngestJobStatus> responsePage = jobStatusRepository
				.findByIngestIdAndEndTimeNotNullOrderByEndTimeDesc(id, page);
		IngestJobStatus result = null;
		if (responsePage.getNumberOfElements() > 0) {
			result = responsePage.getContent().get(0);
		}
		return result;
	}
}
