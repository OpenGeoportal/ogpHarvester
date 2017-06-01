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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;

import com.google.common.collect.Lists;

@Entity
@DiscriminatorValue("SOLR")
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ingestogp_data_type", joinColumns = @JoinColumn(name = "ingest_id"))
    @OrderColumn
    @Column
    @Enumerated(EnumType.STRING)
    private List<DataType> dataTypes = Lists.newArrayList();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ingestogp_remote_sources", joinColumns = @JoinColumn(name = "ingest_id"))
    @Column(name = "repository_name")
    @OrderColumn
    private List<String> dataRepositories = Lists.newArrayList();

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
    @Column
    @Lob
    private String serverQuery;

    public IngestOGP() {
        super();
        this.validRequiredFields = new HashSet<String>(
                Arrays.asList(new String[] { "geographicExtent", "themeKeyword",
                        "placeKeyword", "webServices", "topic", "dateOfContent",
                        "originator", "dataType", "dataRepository" }));
    }

    public Double getBboxEast() {
        return this.bboxEast;
    }

    public Double getBboxNorth() {
        return this.bboxNorth;
    }

    public Double getBboxSouth() {
        return this.bboxSouth;
    }

    public Double getBboxWest() {
        return this.bboxWest;
    }

    public String getCustomSolrQuery() {
        return this.customSolrQuery;
    }

    /**
     * @return the dataRepositories
     */
    public List<String> getDataRepositories() {
        return this.dataRepositories;
    }

    public List<DataType> getDataTypes() {
        return this.dataTypes;
    }

    public Date getDateFrom() {
        return this.dateFrom;
    }

    public Date getDateTo() {
        return this.dateTo;
    }

    public Date getFromSolrTimestamp() {
        return this.fromSolrTimestamp;
    }

    public String getOriginator() {
        return this.originator;
    }

    public String getPlaceKeyword() {
        return this.placeKeyword;
    }

    /**
     * @return the serverQuery
     */
    public String getServerQuery() {
        return this.serverQuery;
    }

    public String getThemeKeyword() {
        return this.themeKeyword;
    }

    public String getTopicCategory() {
        return this.topicCategory;
    }

    public Date getToSolrTimestamp() {
        return this.toSolrTimestamp;
    }

    public boolean isExcludeRestrictedData() {
        return this.excludeRestrictedData;
    }

    public void setBboxEast(final Double bboxEast) {
        this.bboxEast = bboxEast;
    }

    public void setBboxNorth(final Double bboxNorth) {
        this.bboxNorth = bboxNorth;
    }

    public void setBboxSouth(final Double bboxSouth) {
        this.bboxSouth = bboxSouth;
    }

    public void setBboxWest(final Double bboxWest) {
        this.bboxWest = bboxWest;
    }

    public void setCustomSolrQuery(final String customSolrQuery) {
        this.customSolrQuery = customSolrQuery;
    }

    /**
     * @param dataRepositories
     *            the dataRepositories to set
     */
    public void setDataRepositories(final List<String> dataRepositories) {
        this.dataRepositories = dataRepositories;
    }

    public void setDataTypes(final List<DataType> dataType) {
        this.dataTypes = dataType;
    }

    public void setDateFrom(final Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(final Date dateTo) {
        this.dateTo = dateTo;
    }

    public void setExcludeRestrictedData(final boolean excludeRestrictedData) {
        this.excludeRestrictedData = excludeRestrictedData;
    }

    public void setFromSolrTimestamp(final Date fromSolrTimestamp) {
        this.fromSolrTimestamp = fromSolrTimestamp;
    }

    public void setOriginator(final String originator) {
        this.originator = originator;
    }

    public void setPlaceKeyword(final String placeKeyword) {
        this.placeKeyword = placeKeyword;
    }

    /**
     * @param serverQuery
     *            the serverQuery to set
     */
    public void setServerQuery(final String serverQuery) {
        this.serverQuery = serverQuery;
    }

    public void setThemeKeyword(final String themeKeyword) {
        this.themeKeyword = themeKeyword;
    }

    public void setTopicCategory(final String topicCategory) {
        this.topicCategory = topicCategory;
    }

    public void setToSolrTimestamp(final Date toSolrTimestamp) {
        this.toSolrTimestamp = toSolrTimestamp;
    }
}
