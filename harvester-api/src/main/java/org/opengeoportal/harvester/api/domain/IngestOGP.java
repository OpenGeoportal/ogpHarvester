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
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;

@Entity
@DiscriminatorValue("OGP")
public class IngestOGP extends Ingest {

	private static final long serialVersionUID = 1545386542676335709L;

	@Column
	private Date dateFrom;
	@Column
	private Date dateTo;

	@Column
	private String themeKeyword;
	@Column
	private String placeKeyword;
	@Column
	private String topicCategory;

	@Column
	private String originator;

	@Column
	private String dataType;

	@ElementCollection
	@CollectionTable(name = "ingestogp_remote_sources", joinColumns = @JoinColumn(name = "ingest_id"))
	@Column(name = "repository_name")
	@OrderColumn 
	private List<String> dataRepositories;

	@Column
	private boolean excludeRestrictedData;

	@Column
	private Date fromSolrTimestamp;
	@Column
	private Date toSolrTimestamp;

	@Column
	private String customSolrQuery;

	@Column
	private Double bboxWest;
	@Column
	private Double bboxEast;
	@Column
	private Double bboxNorth;
	@Column
	private Double bboxSouth;

	public IngestOGP() {
		super();
		validRequiredFields = new HashSet<String>(Arrays.asList(new String[] {
				"geographicExtent", "themeKeyword", "placeKeyword",
				"webServices", "topic", "dateOfContent", "originator",
				"dataType", "dataRepository" }));
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

	public String getThemeKeyword() {
		return themeKeyword;
	}

	public void setThemeKeyword(String themeKeyword) {
		this.themeKeyword = themeKeyword;
	}

	public String getPlaceKeyword() {
		return placeKeyword;
	}

	public void setPlaceKeyword(String placeKeyword) {
		this.placeKeyword = placeKeyword;
	}

	public String getTopicCategory() {
		return topicCategory;
	}

	public void setTopicCategory(String topicCategory) {
		this.topicCategory = topicCategory;
	}

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isExcludeRestrictedData() {
		return excludeRestrictedData;
	}

	public void setExcludeRestrictedData(boolean excludeRestrictedData) {
		this.excludeRestrictedData = excludeRestrictedData;
	}

	public Date getFromSolrTimestamp() {
		return fromSolrTimestamp;
	}

	public void setFromSolrTimestamp(Date fromSolrTimestamp) {
		this.fromSolrTimestamp = fromSolrTimestamp;
	}

	public Date getToSolrTimestamp() {
		return toSolrTimestamp;
	}

	public void setToSolrTimestamp(Date toSolrTimestamp) {
		this.toSolrTimestamp = toSolrTimestamp;
	}

	public String getCustomSolrQuery() {
		return customSolrQuery;
	}

	public void setCustomSolrQuery(String customSolrQuery) {
		this.customSolrQuery = customSolrQuery;
	}

	public Double getBboxWest() {
		return bboxWest;
	}

	public void setBboxWest(Double bboxWest) {
		this.bboxWest = bboxWest;
	}

	public Double getBboxEast() {
		return bboxEast;
	}

	public void setBboxEast(Double bboxEast) {
		this.bboxEast = bboxEast;
	}

	public Double getBboxNorth() {
		return bboxNorth;
	}

	public void setBboxNorth(Double bboxNorth) {
		this.bboxNorth = bboxNorth;
	}

	public Double getBboxSouth() {
		return bboxSouth;
	}

	public void setBboxSouth(Double bboxSouth) {
		this.bboxSouth = bboxSouth;
	}

	/**
	 * @return the dataRepositories
	 */
	public List<String> getDataRepositories() {
		return dataRepositories;
	}

	/**
	 * @param dataRepositories
	 *            the dataRepositories to set
	 */
	public void setDataRepositories(List<String> dataRepositories) {
		this.dataRepositories = dataRepositories;
	}
}
