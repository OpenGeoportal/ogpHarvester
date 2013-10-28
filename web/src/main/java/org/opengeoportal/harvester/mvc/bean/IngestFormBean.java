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
 * Authors:: Juan Luis Rodriguez Ponce (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author jlrodriguez
 * 
 */
public class IngestFormBean {
	private String typeOfInstance;
	private String[] requiredFields;
	private String catalogOfServices;
	private String cswUrl;
	private String extent;
	private String themeKeyword;
	private String placeKeyword;
	private String topic;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date rangeFrom;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date rangeTo;
	private String originator;
	private String[] dataType;
	private int[] dataRepository;
	private boolean excludeRestricted;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date rangeSolrFrom;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date rangeSolrTo;
	private String customSolrQuery;
	private String geonetworkUrl;
	private String ogpRepository;
	private String title;
	private String keyword;
	private String abstractText;
	private String freeText;
	private String[] geonetworkSources;
	private String url;
	private String location;
	private String subject;
	private String customCswQuery;
	private String webDavUrl;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date lastModifiedFrom;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date lastMofifiedTo;

	/**
	 * @return the dataRepository
	 */
	public int[] getDataRepository() {
		return dataRepository;
	}

	/**
	 * @param dataRepository
	 *            the dataRepository to set
	 */
	public void setDataRepository(int[] dataRepository) {
		this.dataRepository = dataRepository;
	}

	/**
	 * @return the typeOfInstance
	 */
	public String getTypeOfInstance() {
		return typeOfInstance;
	}

	/**
	 * @param typeOfInstance
	 *            the typeOfInstance to set
	 */
	public void setTypeOfInstance(String typeOfInstance) {
		this.typeOfInstance = typeOfInstance;
	}

	/**
	 * @return the requiredFields
	 */
	public String[] getRequiredFields() {
		return requiredFields;
	}

	/**
	 * @param requiredFields
	 *            the requiredFields to set
	 */
	public void setRequiredFields(String[] requiredFields) {
		this.requiredFields = requiredFields;
	}

	/**
	 * @return the catalogOfServices
	 */
	public String getCatalogOfServices() {
		return catalogOfServices;
	}

	/**
	 * @param catalogOfServices
	 *            the catalogOfServices to set
	 */
	public void setCatalogOfServices(String catalogOfServices) {
		this.catalogOfServices = catalogOfServices;
	}

	/**
	 * @return the cswUrl
	 */
	public String getCswUrl() {
		return cswUrl;
	}

	/**
	 * @param cswUrl
	 *            the cswUrl to set
	 */
	public void setCswUrl(String cswUrl) {
		this.cswUrl = cswUrl;
	}

	/**
	 * @return the extent
	 */
	public String getExtent() {
		return extent;
	}

	/**
	 * @param extent
	 *            the extent to set
	 */
	public void setExtent(String extent) {
		this.extent = extent;
	}

	/**
	 * @return the themeKeyword
	 */
	public String getThemeKeyword() {
		return themeKeyword;
	}

	/**
	 * @param themeKeyword
	 *            the themeKeyword to set
	 */
	public void setThemeKeyword(String themeKeyword) {
		this.themeKeyword = themeKeyword;
	}

	/**
	 * @return the placeKeyword
	 */
	public String getPlaceKeyword() {
		return placeKeyword;
	}

	/**
	 * @param placeKeyword
	 *            the placeKeyword to set
	 */
	public void setPlaceKeyword(String placeKeyword) {
		this.placeKeyword = placeKeyword;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic
	 *            the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * @return the rangeFrom
	 */
	public Date getRangeFrom() {
		return rangeFrom;
	}

	/**
	 * @param rangeFrom
	 *            the rangeFrom to set
	 */
	public void setRangeFrom(Date rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	/**
	 * @return the rangeTo
	 */
	public Date getRangeTo() {
		return rangeTo;
	}

	/**
	 * @param rangeTo
	 *            the rangeTo to set
	 */
	public void setRangeTo(Date rangeTo) {
		this.rangeTo = rangeTo;
	}

	/**
	 * @return the originator
	 */
	public String getOriginator() {
		return originator;
	}

	/**
	 * @param originator
	 *            the originator to set
	 */
	public void setOriginator(String originator) {
		this.originator = originator;
	}

	/**
	 * @return the dataType
	 */
	public String[] getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String[] dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the excludeRestricted
	 */
	public boolean isExcludeRestricted() {
		return excludeRestricted;
	}

	/**
	 * @param excludeRestricted
	 *            the excludeRestricted to set
	 */
	public void setExcludeRestricted(boolean excludeRestricted) {
		this.excludeRestricted = excludeRestricted;
	}

	/**
	 * @return the rangeSolrFrom
	 */
	public Date getRangeSolrFrom() {
		return rangeSolrFrom;
	}

	/**
	 * @param rangeSolrFrom
	 *            the rangeSolrFrom to set
	 */
	public void setRangeSolrFrom(Date rangeSolrFrom) {
		this.rangeSolrFrom = rangeSolrFrom;
	}

	/**
	 * @return the rangeSolrTo
	 */
	public Date getRangeSolrTo() {
		return rangeSolrTo;
	}

	/**
	 * @param rangeSolrTo
	 *            the rangeSolrTo to set
	 */
	public void setRangeSolrTo(Date rangeSolrTo) {
		this.rangeSolrTo = rangeSolrTo;
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
	 * @return the geonetworkUrl
	 */
	public String getGeonetworkUrl() {
		return geonetworkUrl;
	}

	/**
	 * @param geonetworkUrl
	 *            the geonetworkUrl to set
	 */
	public void setGeonetworkUrl(String geonetworkUrl) {
		this.geonetworkUrl = geonetworkUrl;
	}

	/**
	 * @return the ogpRepository
	 */
	public String getOgpRepository() {
		return ogpRepository;
	}

	/**
	 * @param ogpRepository
	 *            the ogpRepository to set
	 */
	public void setOgpRepository(String ogpRepository) {
		this.ogpRepository = ogpRepository;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword
	 *            the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the abstractText
	 */
	public String getAbstractText() {
		return abstractText;
	}

	/**
	 * @param abstractText
	 *            the abstractText to set
	 */
	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	/**
	 * @return the freeText
	 */
	public String getFreeText() {
		return freeText;
	}

	/**
	 * @param freeText
	 *            the freeText to set
	 */
	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	/**
	 * @return the geonetworkSources
	 */
	public String[] getGeonetworkSources() {
		return geonetworkSources;
	}

	/**
	 * @param geonetworkSources
	 *            the geonetworkSources to set
	 */
	public void setGeonetworkSources(String[] geonetworkSources) {
		this.geonetworkSources = geonetworkSources;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the customCswQuery
	 */
	public String getCustomCswQuery() {
		return customCswQuery;
	}

	/**
	 * @param customCswQuery
	 *            the customCswQuery to set
	 */
	public void setCustomCswQuery(String customCswQuery) {
		this.customCswQuery = customCswQuery;
	}

	/**
	 * @return the webDavUrl
	 */
	public String getWebDavUrl() {
		return webDavUrl;
	}

	/**
	 * @param webDavUrl the webDavUrl to set
	 */
	public void setWebDavUrl(String webDavUrl) {
		this.webDavUrl = webDavUrl;
	}

	/**
	 * @return the lastModifiedFrom
	 */
	public Date getLastModifiedFrom() {
		return lastModifiedFrom;
	}

	/**
	 * @param lastModifiedFrom the lastModifiedFrom to set
	 */
	public void setLastModifiedFrom(Date lastModifiedFrom) {
		this.lastModifiedFrom = lastModifiedFrom;
	}

	/**
	 * @return the lastMofifiedTo
	 */
	public Date getLastMofifiedTo() {
		return lastMofifiedTo;
	}

	/**
	 * @param lastMofifiedTo the lastMofifiedTo to set
	 */
	public void setLastMofifiedTo(Date lastMofifiedTo) {
		this.lastMofifiedTo = lastMofifiedTo;
	}

}
