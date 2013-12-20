/**
 * SolrSearchParams.java
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
 * Authors:: Juan Luis Rodríguez (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.api.client.solr;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.opengeoportal.harvester.api.domain.DataType;
import org.opengeoportal.harvester.api.domain.IngestOGP;

import com.google.common.collect.Lists;

/**
 * This class contains the fields values used to filter Solr medatada.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class SolrSearchParams {
	/**
	 * Number of result records requested per page.
	 */
	public static final int NUMBER_OF_RESULTS_PER_PAGE = 40;
	/** Content range date from. */
	private Date dateFrom;
	/** Content range date to. */
	private Date dateTo;
	/** Number of the page to retrieve. */
	private int from = 1;
	/** Data originator. */
	private String originator;
	/** Number of elements in one page. */
	private int pageSize = NUMBER_OF_RESULTS_PER_PAGE;
	/** Place keyworkds. */
	private String placeKeyword;
	/** Theme keywords. */
	private String themeKeyword;
	/** ISO topic category. */
	private String topicCategory;
	/** Data types (Line, Point, Polygon, ...). */
	private List<DataType> dataTypes = Lists.newArrayList();
	/** Remote repositories. */
	private List<String> dataRepositories = Lists.newArrayList();
	/** <code>true</code> if restricted data should be excluded. */
	private boolean excludeRestrictedData;
	/** Returned metadata can not ha a solr timestamp before this date. */
	private Date fromSolrTimestamp;
	/** Returned metadata can not ha a solr timestamp after this date. */
	private Date toSolrTimestamp;
	/** Custom Sorl Query. */
	private String customSolrQuery;
	/** Bounding box West coordinte. */
	private Double bboxWest;
	/** Bounding box East coordinate. */
	private Double bboxEast;
	/** Bounding box North coordinate. */
	private Double bboxNorth;
	/** Bounding box South coordinate. */
	private Double bboxSouth;

	/**
	 * Construct a new <code>SolrSearchParams</code> with the data contained in
	 * ingest configuration and
	 * {@link SolrSearchParams#NUMBER_OF_RESULTS_PER_PAGE} page size.
	 * 
	 * @param ingest
	 *            ingest configuration.
	 */
	public SolrSearchParams(final IngestOGP ingest) {
		this(ingest, NUMBER_OF_RESULTS_PER_PAGE);
	}

	/**
	 * Construct a new <code>SolrSearchParams</code> with the data contained in
	 * ingest configuration and
	 * {@link SolrSearchParams#NUMBER_OF_RESULTS_PER_PAGE} page size.
	 * 
	 * @param ingest
	 *            ingest configuration.
	 * 
	 * @param numberOfElementsPerPage
	 *            number of elements to be retrieved in one page.
	 */
	public SolrSearchParams(final IngestOGP ingest,
			final int numberOfElementsPerPage) {
		if (ingest == null) {
			throw new InvalidParameterException(
					"ingest parameter cannot be null when"
							+ " creating a new SolrSearchParams");
		}
		this.pageSize = numberOfElementsPerPage;
		this.bboxEast = ingest.getBboxEast();
		this.bboxNorth = ingest.getBboxNorth();
		this.bboxSouth = ingest.getBboxSouth();
		this.bboxWest = ingest.getBboxWest();
		this.customSolrQuery = ingest.getCustomSolrQuery();
		Collections.copy(this.dataRepositories, ingest.getDataRepositories());
		Collections.copy(this.dataTypes, ingest.getDataTypes());
		this.dateFrom = ingest.getDateFrom();
		this.dateTo = ingest.getDateTo();
		this.excludeRestrictedData = ingest.isExcludeRestrictedData();
		this.fromSolrTimestamp = ingest.getFromSolrTimestamp();
		this.originator = ingest.getOriginator();
		this.placeKeyword = ingest.getPlaceKeyword();
		this.themeKeyword = ingest.getThemeKeyword();
		this.topicCategory = ingest.getTopicCategory();
		this.toSolrTimestamp = ingest.getToSolrTimestamp();
	}
	
	SolrQuery toSolrQuery() {
		SolrQuery query = null;
		if (StringUtils.isNotBlank(customSolrQuery)) {
			query = new SolrQuery(customSolrQuery);
		} else {
			query = new SolrQuery();
		}
		if (dateFrom != null) {
			//query.addDateRangeFacet(field, start, end, gap)
			
		}
		
		
		return query;
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @return the from.
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * @return the originator
	 */
	public String getOriginator() {
		return originator;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the placeKeyword
	 */
	public String getPlaceKeyword() {
		return placeKeyword;
	}

	/**
	 * @return the themeKeyword
	 */
	public String getThemeKeyword() {
		return themeKeyword;
	}

	/**
	 * @return the topicCategory
	 */
	public String getTopicCategory() {
		return topicCategory;
	}

	/**
	 * @param dateFrom
	 *            the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * @param dateTo
	 *            the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(int from) {
		this.from = from;
	}

	/**
	 * @param originator
	 *            the originator to set
	 */
	public void setOriginator(String originator) {
		this.originator = originator;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @param placeKeyword
	 *            the placeKeyword to set
	 */
	public void setPlaceKeyword(String placeKeyword) {
		this.placeKeyword = placeKeyword;
	}

	/**
	 * @param themeKeyword
	 *            the themeKeyword to set
	 */
	public void setThemeKeyword(String themeKeyword) {
		this.themeKeyword = themeKeyword;
	}

	/**
	 * @param topicCategory
	 *            the topicCategory to set
	 */
	public void setTopicCategory(String topicCategory) {
		this.topicCategory = topicCategory;
	}

	/**
	 * @return the dataTypes
	 */
	public List<DataType> getDataTypes() {
		return dataTypes;
	}

	/**
	 * @param dataTypes
	 *            the dataTypes to set
	 */
	public void setDataTypes(List<DataType> dataTypes) {
		this.dataTypes = dataTypes;
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

	/**
	 * @return the excludeRestrictedData
	 */
	public boolean isExcludeRestrictedData() {
		return excludeRestrictedData;
	}

	/**
	 * @param excludeRestrictedData
	 *            the excludeRestrictedData to set
	 */
	public void setExcludeRestrictedData(boolean excludeRestrictedData) {
		this.excludeRestrictedData = excludeRestrictedData;
	}

	/**
	 * @return the fromSolrTimestamp
	 */
	public Date getFromSolrTimestamp() {
		return fromSolrTimestamp;
	}

	/**
	 * @param fromSolrTimestamp
	 *            the fromSolrTimestamp to set
	 */
	public void setFromSolrTimestamp(Date fromSolrTimestamp) {
		this.fromSolrTimestamp = fromSolrTimestamp;
	}

	/**
	 * @return the toSolrTimestamp
	 */
	public Date getToSolrTimestamp() {
		return toSolrTimestamp;
	}

	/**
	 * @param toSolrTimestamp
	 *            the toSolrTimestamp to set
	 */
	public void setToSolrTimestamp(Date toSolrTimestamp) {
		this.toSolrTimestamp = toSolrTimestamp;
	}

	/**
	 * @return the customSolrQuery
	 */
	public String getCustomSolrQuery() {
		return customSolrQuery;
	}

	/**
	 * @param customSolrQuery
	 *            the customSolrQuery to set
	 */
	public void setCustomSolrQuery(String customSolrQuery) {
		this.customSolrQuery = customSolrQuery;
	}

	/**
	 * @return the bboxWest
	 */
	public Double getBboxWest() {
		return bboxWest;
	}

	/**
	 * @param bboxWest
	 *            the bboxWest to set
	 */
	public void setBboxWest(Double bboxWest) {
		this.bboxWest = bboxWest;
	}

	/**
	 * @return the bboxEast
	 */
	public Double getBboxEast() {
		return bboxEast;
	}

	/**
	 * @param bboxEast
	 *            the bboxEast to set
	 */
	public void setBboxEast(Double bboxEast) {
		this.bboxEast = bboxEast;
	}

	/**
	 * @return the bboxNorth
	 */
	public Double getBboxNorth() {
		return bboxNorth;
	}

	/**
	 * @param bboxNorth
	 *            the bboxNorth to set
	 */
	public void setBboxNorth(Double bboxNorth) {
		this.bboxNorth = bboxNorth;
	}

	/**
	 * @return the bboxSouth
	 */
	public Double getBboxSouth() {
		return bboxSouth;
	}

	/**
	 * @param bboxSouth
	 *            the bboxSouth to set
	 */
	public void setBboxSouth(Double bboxSouth) {
		this.bboxSouth = bboxSouth;
	}

}
