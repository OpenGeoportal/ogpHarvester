/*
 * IngestListItem.java
 * 
 * Copyright (C) 2013
 * 
 * This file is part of Open Geoportal Harvester
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
 * Authors:: Juan Luis Rodriguez Ponce (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc.bean;

import java.util.Date;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestJobStatus;
import org.opengeoportal.harvester.api.domain.IngestJobStatusValue;

/**
 * @author jlrodriguez
 * 
 */
public class IngestListItem {
	private Ingest ingest;
	private Date nextRun;
	private boolean inProgress = false;
	private IngestJobStatus status;

	public IngestListItem(Ingest ingest, IngestJobStatus lastStatus) {
		this.ingest = ingest;
		this.status = lastStatus;

	}

	public Long getId() {
		return ingest.getId();
	}

	public String getName() {
		return ingest.getName();
	}

	public Date getLastRun() {
		return ingest.getLastRun();
	}

	public String getNameOgpRepository() {
		return ingest.getNameOgpRepository();
	}

	public Date getNextRun() {
		return nextRun;
	}

	public void setNextRun(Date nextRun) {
		this.nextRun = nextRun;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	
	public IngestJobStatusValue getStatus() {
		if (status != null) {
			return status.getStatus();
		} else {
			return null;
		}
	}

}
