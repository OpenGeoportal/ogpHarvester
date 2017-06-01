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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.opengeoportal.harvester.api.domain.DataType;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.springframework.data.solr.core.DefaultQueryParser;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * This class contains the fields values used to filter Solr medatada.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class SolrSearchParams {
    /**
     * The default operator for parsing queries.
     */
    public static enum Operator {
        /** OR operator. */
        OR,
        /** AND operator. */
        AND
    }

    /**
     * 
     */
    private static final String PF = "pf";

    /**
     * Number of result records requested per page.
     */
    public static final int NUMBER_OF_RESULTS_PER_PAGE = 40;
    /** Content range date page. */
    private Date dateFrom;
    /** Content range date to. */
    private Date dateTo;
    /** Number of the page to retrieve (zero based). */
    private int page = 0;
    /** Data originator. */
    private String originator;
    /** Number of elements in one page. */
    private int pageSize = SolrSearchParams.NUMBER_OF_RESULTS_PER_PAGE;
    /** Place keyworkds. */
    private String placeKeyword;
    /** Theme keywords. */
    private String themeKeyword;
    /** ISO topic category. */
    private String topicCategory;
    /** Data types (Line, Point, Polygon, ...). */
    private List<DataType> dataTypes;
    /** Remote repositories. */
    private List<String> dataRepositories;
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
        this(ingest, SolrSearchParams.NUMBER_OF_RESULTS_PER_PAGE);
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
            throw new IllegalArgumentException(
                    "ingest parameter cannot be null when"
                            + " creating a new SolrSearchParams");
        }
        this.pageSize = numberOfElementsPerPage;
        this.bboxEast = ingest.getBboxEast();
        this.bboxNorth = ingest.getBboxNorth();
        this.bboxSouth = ingest.getBboxSouth();
        this.bboxWest = ingest.getBboxWest();
        this.customSolrQuery = ingest.getCustomSolrQuery();
        this.dataRepositories = Lists
                .newArrayList(ingest.getDataRepositories());
        this.dataTypes = Lists.newArrayList(ingest.getDataTypes());
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

    private void buildBoundingBoxQuery(final SolrQuery query) {
        if (this.isValidBBox()) {
            final String fqParam = "{!frange l=0 incl=false cache=false}$intx";
            query.addFilterQuery(fqParam);
            // @formatter:off
            // product(
            // max(
            // 0,
            // sub(
            // min(180,MaxX),
            // max(-180,MinX)
            // )
            // ),
            // max(
            // 0,
            // sub(
            // min(41.902277040963,MaxY),
            // max(-86.30131338825,MinY)
            // )
            // )
            // )
            // @formatter:on
            final String intxTemplateString = "product(max(0,sub(min(%f,MaxX),"
                    + "max(%f,MinX))),max(0,sub(min(%f,MaxY),"
                    + "max(%f,MinY))))";
            final String intxParam = String.format(Locale.ENGLISH,
                    intxTemplateString, this.bboxEast, this.bboxWest,
                    this.bboxNorth, this.bboxSouth);
            query.setParam("intx", intxParam);
        }
    }

    /**
     * @return
     */
    private String generateSynonymsQuery() {
        final ListMultimap<String, String> fieldList = ArrayListMultimap
                .create();
        if (StringUtils.isNotBlank(this.themeKeyword)) {
            fieldList.put(SolrRecord.LAYER_DISPLAY_NAME_SYNONYMS,
                    "(" + this.themeKeyword + ")");
            fieldList.put(SolrRecord.THEME_KEYWORDS_SYNONYMS_LCSH,
                    "(" + this.themeKeyword + ")");
        }
        if (StringUtils.isNotBlank(this.placeKeyword)) {
            fieldList.put(SolrRecord.PLACE_KEYWORDS_SYNONYMS,
                    "(" + this.placeKeyword + ")");
            fieldList.put(SolrRecord.LAYER_DISPLAY_NAME_SYNONYMS,
                    "(" + this.placeKeyword + ")");
        }
        final StringBuilder synonymsQuery = new StringBuilder();
        final Iterator<Entry<String, String>> entryIterator = fieldList
                .entries().iterator();
        while (entryIterator.hasNext()) {
            final Entry<String, String> entry = entryIterator.next();
            synonymsQuery.append(entry.getKey()).append(":")
                    .append(entry.getValue());

            if (entryIterator.hasNext()) {
                synonymsQuery.append(" OR ");
            }

        }
        return synonymsQuery.toString();
    }

    /**
     * @return the bboxEast
     */
    public Double getBboxEast() {
        return this.bboxEast;
    }

    /**
     * @return the bboxNorth
     */
    public Double getBboxNorth() {
        return this.bboxNorth;
    }

    /**
     * @return the bboxSouth
     */
    public Double getBboxSouth() {
        return this.bboxSouth;
    }

    /**
     * @return the bboxWest
     */
    public Double getBboxWest() {
        return this.bboxWest;
    }

    /**
     * @return the customSolrQuery
     */
    public String getCustomSolrQuery() {
        return this.customSolrQuery;
    }

    /**
     * @return the dataRepositories
     */
    public List<String> getDataRepositories() {
        return this.dataRepositories;
    }

    /**
     * @return the dataTypes
     */
    public List<DataType> getDataTypes() {
        return this.dataTypes;
    }

    /**
     * @return the dateFrom
     */
    public Date getDateFrom() {
        return this.dateFrom;
    }

    /**
     * @return the dateTo
     */
    public Date getDateTo() {
        return this.dateTo;
    }

    /**
     * @return the fromSolrTimestamp
     */
    public Date getFromSolrTimestamp() {
        return this.fromSolrTimestamp;
    }

    /**
     * @return the originator
     */
    public String getOriginator() {
        return this.originator;
    }

    /**
     * @return the page.
     */
    public int getPage() {
        return this.page;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * @return the placeKeyword
     */
    public String getPlaceKeyword() {
        return this.placeKeyword;
    }

    /**
     * @return the themeKeyword
     */
    public String getThemeKeyword() {
        return this.themeKeyword;
    }

    /**
     * @return the topicCategory
     */
    public String getTopicCategory() {
        return this.topicCategory;
    }

    /**
     * @return the toSolrTimestamp
     */
    public Date getToSolrTimestamp() {
        return this.toSolrTimestamp;
    }

    /**
     * @return the excludeRestrictedData
     */
    public boolean isExcludeRestrictedData() {
        return this.excludeRestrictedData;
    }

    /**
     * Check if bounding box coordinates are valid. Bounding Box is valid if its
     * four components are not null and west coordinate is lesser than east
     * coordinate and south coord is lesser than north coord.
     * 
     * @return <code>true</code> if bounding box is valid; otherwise return
     *         <code>false</code>.
     */
    private boolean isValidBBox() {
        boolean valid = false;

        if ((this.bboxWest != null) && (this.bboxEast != null)
                && (this.bboxNorth != null) && (this.bboxSouth != null)) {
            valid = (this.bboxWest < this.bboxEast)
                    && (this.bboxSouth < this.bboxNorth);
        }

        return valid;
    }

    /**
     * @param bboxEast
     *            the bboxEast to set
     */
    public void setBboxEast(final Double bboxEast) {
        this.bboxEast = bboxEast;
    }

    /**
     * @param bboxNorth
     *            the bboxNorth to set
     */
    public void setBboxNorth(final Double bboxNorth) {
        this.bboxNorth = bboxNorth;
    }

    /**
     * @param bboxSouth
     *            the bboxSouth to set
     */
    public void setBboxSouth(final Double bboxSouth) {
        this.bboxSouth = bboxSouth;
    }

    /**
     * @param bboxWest
     *            the bboxWest to set
     */
    public void setBboxWest(final Double bboxWest) {
        this.bboxWest = bboxWest;
    }

    /**
     * @param customSolrQuery
     *            the customSolrQuery to set
     */
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

    /**
     * @param dataTypes
     *            the dataTypes to set
     */
    public void setDataTypes(final List<DataType> dataTypes) {
        this.dataTypes = dataTypes;
    }

    /**
     * @param dateFrom
     *            the dateFrom to set
     */
    public void setDateFrom(final Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * @param dateTo
     *            the dateTo to set
     */
    public void setDateTo(final Date dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * @param excludeRestrictedData
     *            the excludeRestrictedData to set
     */
    public void setExcludeRestrictedData(final boolean excludeRestrictedData) {
        this.excludeRestrictedData = excludeRestrictedData;
    }

    /**
     * @param fromSolrTimestamp
     *            the fromSolrTimestamp to set
     */
    public void setFromSolrTimestamp(final Date fromSolrTimestamp) {
        this.fromSolrTimestamp = fromSolrTimestamp;
    }

    /**
     * @param originator
     *            the originator to set
     */
    public void setOriginator(final String originator) {
        this.originator = originator;
    }

    /**
     * @param from
     *            the page to set
     */
    public void setPage(final int from) {
        this.page = from;
    }

    /**
     * @param pageSize
     *            the pageSize to set
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @param placeKeyword
     *            the placeKeyword to set
     */
    public void setPlaceKeyword(final String placeKeyword) {
        this.placeKeyword = placeKeyword;
    }

    /**
     * @param themeKeyword
     *            the themeKeyword to set
     */
    public void setThemeKeyword(final String themeKeyword) {
        this.themeKeyword = themeKeyword;
    }

    /**
     * @param topicCategory
     *            the topicCategory to set
     */
    public void setTopicCategory(final String topicCategory) {
        this.topicCategory = topicCategory;
    }

    /**
     * @param toSolrTimestamp
     *            the toSolrTimestamp to set
     */
    public void setToSolrTimestamp(final Date toSolrTimestamp) {
        this.toSolrTimestamp = toSolrTimestamp;
    }

    /**
     * @param operator
     * @param fieldName
     * @param fieldContent
     * @return
     */
    private String splitAndConcatenateUsingOperator(final Operator operator,
            final String fieldName, final String fieldContent) {
        final String[] contentSplitted = StringUtils.split(fieldContent);
        final StringBuilder sb = new StringBuilder();
        final int length = contentSplitted.length;
        for (int i = 0; i < length; i++) {
            sb.append(fieldName).append(":").append(contentSplitted[i]);
            if (i != (length - 1)) {
                sb.append(" ").append(operator.toString()).append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Transform the record in {@link SolrQuery} executable by an
     * {@link org.apache.solr.client.solrj.impl.HttpSolrServer}.
     * 
     * @return the {@link SolrQuery} built with the data page this.
     */
    public SolrQuery toSolrQuery() {
        final SolrQuery solrQuery = new SolrQuery();

        if (StringUtils.isNotBlank(this.customSolrQuery)) {
            solrQuery.setQuery(this.customSolrQuery);
        } else {
            solrQuery.setQuery("*:*");
            // data repositories
            if ((this.dataRepositories != null)
                    && (this.dataRepositories.size() > 0)) {
                Criteria institutionCriteria = null;
                for (final String institution : this.dataRepositories) {
                    if (institutionCriteria == null) {
                        institutionCriteria = new Criteria(
                                SolrRecord.INSTITUTION).is(institution);
                    } else {
                        institutionCriteria = institutionCriteria
                                .or(new Criteria(SolrRecord.INSTITUTION)
                                        .is(institution));
                    }
                }

                final SimpleQuery query = new SimpleQuery(institutionCriteria);
                final DefaultQueryParser parser = new DefaultQueryParser();
                final String queryString = parser.getQueryString(query);
                solrQuery.addFilterQuery(queryString);
            } else {
                solrQuery.addFilterQuery(SolrRecord.INSTITUTION + ":*");
            }

            // theme keywords
            if (StringUtils.isNotBlank(this.themeKeyword)) {
                solrQuery.addFilterQuery(
                        SolrRecord.THEME_KEYWORDS + ":" + this.themeKeyword);
                solrQuery.add(SolrSearchParams.PF, SolrRecord.THEME_KEYWORDS
                        + ":'" + this.themeKeyword + "'^9.0");
                solrQuery.add(SolrSearchParams.PF, SolrRecord.LAYER_DISPLAY_NAME
                        + ":'" + this.themeKeyword + "'^9.0");
            }
            if (StringUtils.isNotBlank(this.placeKeyword)) {
                solrQuery.addFilterQuery(
                        SolrRecord.PLACE_KEYWORDS + ":" + this.placeKeyword);
                solrQuery.add(SolrSearchParams.PF, SolrRecord.PLACE_KEYWORDS
                        + ":'" + this.placeKeyword + "'^9.0");
            }
            if (StringUtils.isNotBlank(this.topicCategory)) {
                solrQuery.addFilterQuery(SolrRecord.ISO_TOPIC_CATEGORY + ":"
                        + this.topicCategory);

            }

            if ((this.dateFrom != null) || (this.dateTo != null)) {
                final Criteria contentDateCriteria = Criteria
                        .where(SolrRecord.CONTENT_DATE)
                        .between(this.dateFrom, this.dateTo);
                final SimpleQuery query = new SimpleQuery(contentDateCriteria);
                final DefaultQueryParser parser = new DefaultQueryParser();
                final String queryString = parser.getQueryString(query);
                solrQuery.addFilterQuery(queryString);

            }
            if (StringUtils.isNotBlank(this.originator)) {
                final String originatorCriteria = this
                        .splitAndConcatenateUsingOperator(Operator.AND,
                                SolrRecord.ORIGINATOR, this.originator);
                solrQuery.addFilterQuery(originatorCriteria);
                solrQuery.add(SolrSearchParams.PF,
                        SolrRecord.ORIGINATOR + ":" + this.originator);
            }
            if ((this.dataTypes != null) && (this.dataTypes.size() > 0)) {
                final StringBuilder concatenatedType = new StringBuilder();
                for (final DataType dType : this.dataTypes) {
                    concatenatedType.append(dType.toString().replace(" ", "+"))
                            .append(" ");
                }
                final String dataTypeCriteria = this
                        .splitAndConcatenateUsingOperator(Operator.OR,
                                SolrRecord.DATA_TYPE,
                                concatenatedType.toString());
                solrQuery.add("fq", dataTypeCriteria);
            }

            if (this.excludeRestrictedData) {
                solrQuery.addFilterQuery(
                        SolrRecord.ACCESS + ":" + AccessLevel.Public);
            }

            if ((this.fromSolrTimestamp != null)
                    || (this.toSolrTimestamp != null)) {
                final Criteria solrTimestampCriteria = Criteria
                        .where(SolrRecord.TIMESTAMP)
                        .between(this.fromSolrTimestamp, this.toSolrTimestamp);
                final SimpleQuery query = new SimpleQuery(
                        solrTimestampCriteria);
                final DefaultQueryParser parser = new DefaultQueryParser();
                final String queryString = parser.getQueryString(query);
                solrQuery.addFilterQuery(queryString);
            }
            // Add bbox filter only if user has not specified a custom solr
            // query.
            this.buildBoundingBoxQuery(solrQuery);

            final String synonymsFilter = this.generateSynonymsQuery();
            if (StringUtils.isNotBlank(synonymsFilter)) {
                solrQuery.addFilterQuery(synonymsFilter);
            }

        }

        solrQuery.setRows(this.pageSize);
        solrQuery.setStart(this.page * this.pageSize);
        solrQuery.addSort(SortClause.desc("score"));

        return solrQuery;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SolrSearchParams [");
        if (this.dateFrom != null) {
            builder.append("dateFrom=");
            builder.append(this.dateFrom);
            builder.append(", ");
        }
        if (this.dateTo != null) {
            builder.append("dateTo=");
            builder.append(this.dateTo);
            builder.append(", ");
        }
        builder.append("page=");
        builder.append(this.page);
        builder.append(", ");
        if (this.originator != null) {
            builder.append("originator=");
            builder.append(this.originator);
            builder.append(", ");
        }
        builder.append("pageSize=");
        builder.append(this.pageSize);
        builder.append(", ");
        if (this.placeKeyword != null) {
            builder.append("placeKeyword=");
            builder.append(this.placeKeyword);
            builder.append(", ");
        }
        if (this.themeKeyword != null) {
            builder.append("themeKeyword=");
            builder.append(this.themeKeyword);
            builder.append(", ");
        }
        if (this.topicCategory != null) {
            builder.append("topicCategory=");
            builder.append(this.topicCategory);
            builder.append(", ");
        }
        if (this.dataTypes != null) {
            builder.append("dataTypes=");
            builder.append(this.dataTypes);
            builder.append(", ");
        }
        if (this.dataRepositories != null) {
            builder.append("dataRepositories=");
            builder.append(this.dataRepositories);
            builder.append(", ");
        }
        builder.append("excludeRestrictedData=");
        builder.append(this.excludeRestrictedData);
        builder.append(", ");
        if (this.fromSolrTimestamp != null) {
            builder.append("fromSolrTimestamp=");
            builder.append(this.fromSolrTimestamp);
            builder.append(", ");
        }
        if (this.toSolrTimestamp != null) {
            builder.append("toSolrTimestamp=");
            builder.append(this.toSolrTimestamp);
            builder.append(", ");
        }
        if (this.customSolrQuery != null) {
            builder.append("customSolrQuery=");
            builder.append(this.customSolrQuery);
            builder.append(", ");
        }
        if (this.bboxWest != null) {
            builder.append("bboxWest=");
            builder.append(this.bboxWest);
            builder.append(", ");
        }
        if (this.bboxEast != null) {
            builder.append("bboxEast=");
            builder.append(this.bboxEast);
            builder.append(", ");
        }
        if (this.bboxNorth != null) {
            builder.append("bboxNorth=");
            builder.append(this.bboxNorth);
            builder.append(", ");
        }
        if (this.bboxSouth != null) {
            builder.append("bboxSouth=");
            builder.append(this.bboxSouth);
        }
        builder.append("]");
        return builder.toString();
    }

}
