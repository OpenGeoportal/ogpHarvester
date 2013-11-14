/*
 * IngestFormBean.java
 *
 * Copyright (C) 2013
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
 * Authors:: Jose García (mailto:jose.garcia@geocat.net)
 */
package org.opengeoportal.harvester.api.domain;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CSW")
public class IngestCsw extends Ingest {

	private static final long serialVersionUID = -346374812287720289L;

	@Column
	private Date dateFrom;
	@Column
	private Date dateTo;

	@Column
	private String location;
	@Column
	private String title;
	@Column
	private String subject;
	@Column
	private String freeText;

	@Column
	private String customCswQuery;

	public IngestCsw() {
		super();
		validRequiredFields = new HashSet<String>(Arrays.asList(new String[] {
				"geographicExtent", "themeKeyword", "placeKeyword", "topic",
				"dateOfContent", "originator", "dataType", "dataRepository" }));
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public String getCustomCswQuery() {
		return customCswQuery;
	}

	public void setCustomCswQuery(String customCswQuery) {
		this.customCswQuery = customCswQuery;
	}
}
