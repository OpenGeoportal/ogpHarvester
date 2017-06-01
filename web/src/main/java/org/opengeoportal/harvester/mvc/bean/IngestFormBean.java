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
    private boolean customRepoDeleted = false;
    private String customRepoName;
    private String serverQuery;

    /**
     * 
     */
    public IngestFormBean() {
        this.dataTypes = new DataType[0];
        this.dataRepositories = new String[0];
        this.gnSources = new String[0];
    }

    /**
     * @return the beginDate
     */
    public Date getBeginDate() {
        return this.beginDate;
    }

    /**
     * @return the catalogOfServices
     */
    public Long getCatalogOfServices() {
        return this.catalogOfServices;
    }

    /**
     * @return the contentRangeFrom
     */
    public Date getContentRangeFrom() {
        return this.contentRangeFrom;
    }

    /**
     * @return the contentRangeTo
     */
    public Date getContentRangeTo() {
        return this.contentRangeTo;
    }

    /**
     * @return the cswCustomQuery
     */
    public String getCswCustomQuery() {
        return this.cswCustomQuery;
    }

    /**
     * @return the cswFreeText
     */
    public String getCswFreeText() {
        return this.cswFreeText;
    }

    /**
     * @return the cswRangeFrom
     */
    public Date getCswRangeFrom() {
        return this.cswRangeFrom;
    }

    /**
     * @return the cswRangeTo
     */
    public Date getCswRangeTo() {
        return this.cswRangeTo;
    }

    /**
     * @return the cswSubject
     */
    public String getCswSubject() {
        return this.cswSubject;
    }

    /**
     * @return the cswTitle
     */
    public String getCswTitle() {
        return this.cswTitle;
    }

    public String getCustomRepoName() {
        return this.customRepoName;
    }

    /**
     * @return the dataRepositories
     */
    public String[] getDataRepositories() {
        return this.dataRepositories;
    }

    /**
     * @return the dataType
     */
    public DataType[] getDataTypes() {
        return this.dataTypes;
    }

    /**
     * @return the extent
     */
    public BoundingBox getExtent() {
        return this.extent;
    }

    /**
     * @return the frequency
     */
    public Frequency getFrequency() {
        return this.frequency;
    }

    /**
     * @return the gnAbstractText
     */
    public String getGnAbstractText() {
        return this.gnAbstractText;
    }

    /**
     * @return the gnFreeText
     */
    public String getGnFreeText() {
        return this.gnFreeText;
    }

    /**
     * @return the gnKeyword
     */
    public String getGnKeyword() {
        return this.gnKeyword;
    }

    /**
     * @return the gnSources
     */
    public String[] getGnSources() {
        return this.gnSources;
    }

    /**
     * @return the gnTitle
     */
    public String getGnTitle() {
        return this.gnTitle;
    }

    public Long getId() {
        return this.id;
    }

    /**
     * @return the ingestName
     */
    public String getIngestName() {
        return this.ingestName;
    }

    /**
     * @return the nameOgpRepository
     */
    public String getNameOgpRepository() {
        return this.nameOgpRepository;
    }

    /**
     * @return the originator
     */
    public String getOriginator() {
        return this.originator;
    }

    /**
     * @return the placeKeyword
     */
    public String getPlaceKeyword() {
        return this.placeKeyword;
    }

    /**
     * @return the rangeSolrFrom
     */
    public Date getRangeSolrFrom() {
        return this.rangeSolrFrom;
    }

    /**
     * @return the rangeSolrTo
     */
    public Date getRangeSolrTo() {
        return this.rangeSolrTo;
    }

    /**
     * @return the requiredFields
     */
    public Map<String, Boolean> getRequiredFields() {
        return this.requiredFields;
    }

    public Boolean getScheduled() {
        return this.scheduled;
    }

    /**
     * @return the serverQuery
     */
    public String getServerQuery() {
        return this.serverQuery;
    }

    /**
     * @return the solrCustomQuery
     */
    public String getSolrCustomQuery() {
        return this.solrCustomQuery;
    }

    /**
     * @return the themeKeyword
     */
    public String getThemeKeyword() {
        return this.themeKeyword;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return this.topic;
    }

    /**
     * @return the typeOfInstance
     */
    public InstanceType getTypeOfInstance() {
        return this.typeOfInstance;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @return the webdavFromLastModified
     */
    public Date getWebdavFromLastModified() {
        return this.webdavFromLastModified;
    }

    /**
     * @return the webdavToLastModified
     */
    public Date getWebdavToLastModified() {
        return this.webdavToLastModified;
    }

    public boolean isCustomRepoDeleted() {
        return this.customRepoDeleted;
    }

    /**
     * @return the excludeRestricted
     */
    public boolean isExcludeRestricted() {
        return this.excludeRestricted;
    }

    /**
     * @param beginDate
     *            the beginDate to set
     */
    public void setBeginDate(final Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * @param catalogOfServices
     *            the catalogOfServices to set
     */
    public void setCatalogOfServices(final Long catalogOfServices) {
        this.catalogOfServices = catalogOfServices;
    }

    /**
     * @param contentRangeFrom
     *            the contentRangeFrom to set
     */
    public void setContentRangeFrom(final Date contentRangeFrom) {
        this.contentRangeFrom = contentRangeFrom;
    }

    /**
     * @param contentRangeTo
     *            the contentRangeTo to set
     */
    public void setContentRangeTo(final Date contentRangeTo) {
        this.contentRangeTo = contentRangeTo;
    }

    /**
     * @param cswCustomQuery
     *            the cswCustomQuery to set
     */
    public void setCswCustomQuery(final String cswCustomQuery) {
        this.cswCustomQuery = cswCustomQuery;
    }

    /**
     * @param cswFreeText
     *            the cswFreeText to set
     */
    public void setCswFreeText(final String cswFreeText) {
        this.cswFreeText = cswFreeText;
    }

    /**
     * @param cswRangeFrom
     *            the cswRangeFrom to set
     */
    public void setCswRangeFrom(final Date cswRangeFrom) {
        this.cswRangeFrom = cswRangeFrom;
    }

    /**
     * @param cswRangeTo
     *            the cswRangeTo to set
     */
    public void setCswRangeTo(final Date cswRangeTo) {
        this.cswRangeTo = cswRangeTo;
    }

    /**
     * @param cswSubject
     *            the cswSubject to set
     */
    public void setCswSubject(final String cswSubject) {
        this.cswSubject = cswSubject;
    }

    /**
     * @param cswTitle
     *            the cswTitle to set
     */
    public void setCswTitle(final String cswTitle) {
        this.cswTitle = cswTitle;
    }

    public void setCustomRepoDeleted(final boolean customRepoDeleted) {
        this.customRepoDeleted = customRepoDeleted;
    }

    public void setCustomRepoName(final String customRepoName) {
        this.customRepoName = customRepoName;
    }

    /**
     * @param dataRepositories
     *            the dataRepositories to set
     */
    public void setDataRepositories(final String[] dataRepositories) {
        this.dataRepositories = dataRepositories;
    }

    /**
     * @param dataTypes
     *            the dataType to set
     */
    public void setDataTypes(final DataType[] dataTypes) {
        this.dataTypes = dataTypes;
    }

    /**
     * @param excludeRestricted
     *            the excludeRestricted to set
     */
    public void setExcludeRestricted(final boolean excludeRestricted) {
        this.excludeRestricted = excludeRestricted;
    }

    /**
     * @param extent
     *            the extent to set
     */
    public void setExtent(final BoundingBox extent) {
        this.extent = extent;
    }

    /**
     * @param frequency
     *            the frequency to set
     */
    public void setFrequency(final Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * @param gnAbstractText
     *            the gnAbstractText to set
     */
    public void setGnAbstractText(final String gnAbstractText) {
        this.gnAbstractText = gnAbstractText;
    }

    /**
     * @param gnFreeText
     *            the gnFreeText to set
     */
    public void setGnFreeText(final String gnFreeText) {
        this.gnFreeText = gnFreeText;
    }

    /**
     * @param gnKeyword
     *            the gnKeyword to set
     */
    public void setGnKeyword(final String gnKeyword) {
        this.gnKeyword = gnKeyword;
    }

    /**
     * @param gnSources
     *            the gnSources to set
     */
    public void setGnSources(final String[] gnSources) {
        this.gnSources = gnSources;
    }

    /**
     * @param gnTitle
     *            the gnTitle to set
     */
    public void setGnTitle(final String gnTitle) {
        this.gnTitle = gnTitle;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @param ingestName
     *            the ingestName to set
     */
    public void setIngestName(final String ingestName) {
        this.ingestName = ingestName;
    }

    /**
     * @param nameOgpRepository
     *            the nameOgpRepository to set
     */
    public void setNameOgpRepository(final String nameOgpRepository) {
        this.nameOgpRepository = nameOgpRepository;
    }

    /**
     * @param originator
     *            the originator to set
     */
    public void setOriginator(final String originator) {
        this.originator = originator;
    }

    /**
     * @param placeKeyword
     *            the placeKeyword to set
     */
    public void setPlaceKeyword(final String placeKeyword) {
        this.placeKeyword = placeKeyword;
    }

    /**
     * @param rangeSolrFrom
     *            the rangeSolrFrom to set
     */
    public void setRangeSolrFrom(final Date rangeSolrFrom) {
        this.rangeSolrFrom = rangeSolrFrom;
    }

    /**
     * @param rangeSolrTo
     *            the rangeSolrTo to set
     */
    public void setRangeSolrTo(final Date rangeSolrTo) {
        this.rangeSolrTo = rangeSolrTo;
    }

    /**
     * @param requiredFields
     *            the requiredFields to set
     */
    public void setRequiredFields(final Map<String, Boolean> requiredFields) {
        this.requiredFields = requiredFields;
    }

    public void setScheduled(final Boolean scheduled) {
        this.scheduled = scheduled;
    }

    /**
     * @param serverQuery
     *            the serverQuery to set
     */
    public void setServerQuery(final String serverQuery) {
        this.serverQuery = serverQuery;
    }

    /**
     * @param solrCustomQuery
     *            the solrCustomQuery to set
     */
    public void setSolrCustomQuery(final String solrCustomQuery) {
        this.solrCustomQuery = solrCustomQuery;
    }

    /**
     * @param themeKeyword
     *            the themeKeyword to set
     */
    public void setThemeKeyword(final String themeKeyword) {
        this.themeKeyword = themeKeyword;
    }

    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }

    /**
     * @param typeOfInstance
     *            the typeOfInstance to set
     */
    public void setTypeOfInstance(final InstanceType typeOfInstance) {
        this.typeOfInstance = typeOfInstance;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @param webdavFromLastModified
     *            the webdavFromLastModified to set
     */
    public void setWebdavFromLastModified(final Date webdavFromLastModified) {
        this.webdavFromLastModified = webdavFromLastModified;
    }

    /**
     * @param webdavToLastModified
     *            the webdavToLastModified to set
     */
    public void setWebdavToLastModified(final Date webdavToLastModified) {
        this.webdavToLastModified = webdavToLastModified;
    }

}
