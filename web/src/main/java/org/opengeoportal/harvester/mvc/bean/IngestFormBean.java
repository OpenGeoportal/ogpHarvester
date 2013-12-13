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

import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.Map;

import org.opengeoportal.harvester.api.domain.DataType;
import org.opengeoportal.harvester.api.domain.Frequency;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.mvc.utils.CustomJsonDateDeserializer;
import org.opengeoportal.harvester.mvc.utils.CustomJsonDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author jlrodriguez
 * 
 */
public class IngestFormBean {
	private Long id;
	private Boolean scheduled;
	private InstanceType typeOfInstance;
	private Long catalogOfServices;
	private String nameOgpRepository;
	private String url;
	private String themeKeyword;
	private String placeKeyword;
	private String topic;
	private String originator;
	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date contentRangeFrom;

	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date contentRangeTo;

	private DataType[] dataTypes;
	private String[] dataRepositories;
	private boolean excludeRestricted;

	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date rangeSolrFrom;

	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date rangeSolrTo;

	private Map<String, Boolean> requiredFields;
	private String gnTitle;
	private String gnKeyword;
	private String gnAbstractText;
	private String gnFreeText;
	private String[] gnSources;
	private String cswTitle;
	private String cswSubject;
	private String cswFreeText;

	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date cswRangeFrom;

	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date cswRangeTo;

	private String cswCustomQuery;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	private Date webdavFromLastModified;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	private Date webdavToLastModified;

	private String solrCustomQuery;
	private String ingestName;

	@DateTimeFormat(pattern = "MM/dd/yyyy")
	@JsonSerialize(using = CustomJsonDateSerializer.class)
	@JsonDeserialize(using = CustomJsonDateDeserializer.class)
	private Date beginDate;

	private Frequency frequency;
	private BoundingBox extent;

	/**
	 * 
	 */
	public IngestFormBean() {
		this.dataTypes = new DataType[0];
		this.dataRepositories = new String[0];
		this.gnSources = new String[0];
	}

	/**
	 * @return the typeOfInstance
	 */
	public InstanceType getTypeOfInstance() {
		return typeOfInstance;
	}

	/**
	 * @param typeOfInstance
	 *            the typeOfInstance to set
	 */
	public void setTypeOfInstance(InstanceType typeOfInstance) {
		this.typeOfInstance = typeOfInstance;
	}

	/**
	 * @return the catalogOfServices
	 */
	public Long getCatalogOfServices() {
		return catalogOfServices;
	}

	/**
	 * @param catalogOfServices
	 *            the catalogOfServices to set
	 */
	public void setCatalogOfServices(Long catalogOfServices) {
		this.catalogOfServices = catalogOfServices;
	}

	/**
	 * @return the nameOgpRepository
	 */
	public String getNameOgpRepository() {
		return nameOgpRepository;
	}

	/**
	 * @param nameOgpRepository
	 *            the nameOgpRepository to set
	 */
	public void setNameOgpRepository(String nameOgpRepository) {
		this.nameOgpRepository = nameOgpRepository;
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
	public DataType[] getDataTypes() {
		return dataTypes;
	}

	/**
	 * @param dataTypes
	 *            the dataType to set
	 */
	public void setDataTypes(DataType[] dataTypes) {
		this.dataTypes = dataTypes;
	}

	/**
	 * @return the dataRepositories
	 */
	public String[] getDataRepositories() {
		return dataRepositories;
	}

	/**
	 * @param dataRepositories
	 *            the dataRepositories to set
	 */
	public void setDataRepositories(String[] dataRepositories) {
		this.dataRepositories = dataRepositories;
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
	 * @return the requiredFields
	 */
	public Map<String, Boolean> getRequiredFields() {
		return requiredFields;
	}

	/**
	 * @param requiredFields
	 *            the requiredFields to set
	 */
	public void setRequiredFields(Map<String, Boolean> requiredFields) {
		this.requiredFields = requiredFields;
	}

	/**
	 * @return the gnTitle
	 */
	public String getGnTitle() {
		return gnTitle;
	}

	/**
	 * @param gnTitle
	 *            the gnTitle to set
	 */
	public void setGnTitle(String gnTitle) {
		this.gnTitle = gnTitle;
	}

	/**
	 * @return the gnKeyword
	 */
	public String getGnKeyword() {
		return gnKeyword;
	}

	/**
	 * @param gnKeyword
	 *            the gnKeyword to set
	 */
	public void setGnKeyword(String gnKeyword) {
		this.gnKeyword = gnKeyword;
	}

	/**
	 * @return the gnAbstractText
	 */
	public String getGnAbstractText() {
		return gnAbstractText;
	}

	/**
	 * @param gnAbstractText
	 *            the gnAbstractText to set
	 */
	public void setGnAbstractText(String gnAbstractText) {
		this.gnAbstractText = gnAbstractText;
	}

	/**
	 * @return the gnFreeText
	 */
	public String getGnFreeText() {
		return gnFreeText;
	}

	/**
	 * @param gnFreeText
	 *            the gnFreeText to set
	 */
	public void setGnFreeText(String gnFreeText) {
		this.gnFreeText = gnFreeText;
	}

	/**
	 * @return the gnSources
	 */
	public String[] getGnSources() {
		return gnSources;
	}

	/**
	 * @param gnSources
	 *            the gnSources to set
	 */
	public void setGnSources(String[] gnSources) {
		this.gnSources = gnSources;
	}

	/**
	 * @return the cswTitle
	 */
	public String getCswTitle() {
		return cswTitle;
	}

	/**
	 * @param cswTitle
	 *            the cswTitle to set
	 */
	public void setCswTitle(String cswTitle) {
		this.cswTitle = cswTitle;
	}

	/**
	 * @return the cswSubject
	 */
	public String getCswSubject() {
		return cswSubject;
	}

	/**
	 * @param cswSubject
	 *            the cswSubject to set
	 */
	public void setCswSubject(String cswSubject) {
		this.cswSubject = cswSubject;
	}

	/**
	 * @return the cswFreeText
	 */
	public String getCswFreeText() {
		return cswFreeText;
	}

	/**
	 * @param cswFreeText
	 *            the cswFreeText to set
	 */
	public void setCswFreeText(String cswFreeText) {
		this.cswFreeText = cswFreeText;
	}

	/**
	 * @return the cswRangeFrom
	 */
	public Date getCswRangeFrom() {
		return cswRangeFrom;
	}

	/**
	 * @param cswRangeFrom
	 *            the cswRangeFrom to set
	 */
	public void setCswRangeFrom(Date cswRangeFrom) {
		this.cswRangeFrom = cswRangeFrom;
	}

	/**
	 * @return the cswRangeTo
	 */
	public Date getCswRangeTo() {
		return cswRangeTo;
	}

	/**
	 * @param cswRangeTo
	 *            the cswRangeTo to set
	 */
	public void setCswRangeTo(Date cswRangeTo) {
		this.cswRangeTo = cswRangeTo;
	}

	/**
	 * @return the cswCustomQuery
	 */
	public String getCswCustomQuery() {
		return cswCustomQuery;
	}

	/**
	 * @param cswCustomQuery
	 *            the cswCustomQuery to set
	 */
	public void setCswCustomQuery(String cswCustomQuery) {
		this.cswCustomQuery = cswCustomQuery;
	}

	/**
	 * @return the webdavFromLastModified
	 */
	public Date getWebdavFromLastModified() {
		return webdavFromLastModified;
	}

	/**
	 * @param webdavFromLastModified
	 *            the webdavFromLastModified to set
	 */
	public void setWebdavFromLastModified(Date webdavFromLastModified) {
		this.webdavFromLastModified = webdavFromLastModified;
	}

	/**
	 * @return the webdavToLastModified
	 */
	public Date getWebdavToLastModified() {
		return webdavToLastModified;
	}

	/**
	 * @param webdavToLastModified
	 *            the webdavToLastModified to set
	 */
	public void setWebdavToLastModified(Date webdavToLastModified) {
		this.webdavToLastModified = webdavToLastModified;
	}

	/**
	 * @return the solrCustomQuery
	 */
	public String getSolrCustomQuery() {
		return solrCustomQuery;
	}

	/**
	 * @param solrCustomQuery
	 *            the solrCustomQuery to set
	 */
	public void setSolrCustomQuery(String solrCustomQuery) {
		this.solrCustomQuery = solrCustomQuery;
	}

	/**
	 * @return the ingestName
	 */
	public String getIngestName() {
		return ingestName;
	}

	/**
	 * @param ingestName
	 *            the ingestName to set
	 */
	public void setIngestName(String ingestName) {
		this.ingestName = ingestName;
	}

	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate
	 *            the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return the frequency
	 */
	public Frequency getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the extent
	 */
	public BoundingBox getExtent() {
		return extent;
	}

	/**
	 * @param extent
	 *            the extent to set
	 */
	public void setExtent(BoundingBox extent) {
		this.extent = extent;
	}

	/**
	 * @return the contentRangeFrom
	 */
	public Date getContentRangeFrom() {
		return contentRangeFrom;
	}

	/**
	 * @param contentRangeFrom
	 *            the contentRangeFrom to set
	 */
	public void setContentRangeFrom(Date contentRangeFrom) {
		this.contentRangeFrom = contentRangeFrom;
	}

	/**
	 * @return the contentRangeTo
	 */
	public Date getContentRangeTo() {
		return contentRangeTo;
	}

	/**
	 * @param contentRangeTo
	 *            the contentRangeTo to set
	 */
	public void setContentRangeTo(Date contentRangeTo) {
		this.contentRangeTo = contentRangeTo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
	}

}
