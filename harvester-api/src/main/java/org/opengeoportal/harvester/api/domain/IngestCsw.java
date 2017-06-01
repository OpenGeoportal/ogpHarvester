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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.opengeoportal.harvester.api.metadata.model.BoundingBox;

@Entity
@DiscriminatorValue("CSW")
public class IngestCsw extends Ingest {

    private static final long serialVersionUID = -346374812287720289L;

    @Column
    private Date dateFrom;
    @Column
    private Date dateTo;

    @Column
    private String title;
    @Column
    private String subject;
    @Column
    private String freeText;

    @Column
    private Double bboxWest;
    @Column
    private Double bboxEast;
    @Column
    private Double bboxNorth;
    @Column
    private Double bboxSouth;

    @Column
    private String customCswQuery;

    public IngestCsw() {
        super();
        this.validRequiredFields = new HashSet<String>(
                Arrays.asList(new String[] { "geographicExtent", "themeKeyword",
                        "placeKeyword", "topic", "dateOfContent", "originator",
                        "dataType", "webServices" }));
    }

    private void buildBBOXCql(final List<String> propertyCqlFilters,
            final String property) {
        final BoundingBox bbox = new BoundingBox(this.bboxWest, this.bboxSouth,
                this.bboxEast, this.bboxNorth);
        if (bbox.isValid()) {
            propertyCqlFilters.add("INTERSECTS(ows:BoundingBox,ENVELOPE("
                    + bbox.toString() + "))");
        }
    }

    private void buildBBOXFilter(final List<String> propertyFilters,
            final String property) {
        final BoundingBox bbox = new BoundingBox(this.bboxWest, this.bboxSouth,
                this.bboxEast, this.bboxNorth);
        if (bbox.isValid()) {
            final StringBuffer propertyFilter = new StringBuffer();

            propertyFilter.append("<Intersects>");
            propertyFilter.append(
                    "        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append(bbox.generateGMLBox(4326));
            propertyFilter.append("</Intersects>");
            propertyFilters.add(propertyFilter.toString());
        }
    }

    private void buildCqlQueryable(final List<String> propertyCqlFilters,
            final String name, final String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }

        String queryable = "";

        if (value.contains("%")) {
            queryable = name + " like '" + value + "'";
        } else {
            queryable = name + " = '" + value + "'";
        }

        if (StringUtils.isNotEmpty(queryable)) {
            propertyCqlFilters.add(queryable);
        }
    }

    private void buildDateQueryCql(final List<String> propertyCqlFilters,
            final String name, final Date fromValue, final Date toValue) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (fromValue != null) {
            propertyCqlFilters
                    .add(name + " >= '" + dateFormat.format(fromValue) + "'");
        }

        if (toValue != null) {
            propertyCqlFilters
                    .add(name + " <= '" + dateFormat.format(toValue) + "'");
        }

    }

    private void buildDateQueryFilter(final List<String> propertyFilters,
            final String property, final Date fromValue, final Date toValue) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (fromValue != null) {
            final StringBuffer propertyFilter = new StringBuffer();

            propertyFilter.append("<PropertyIsGreaterThanOrEqualTo>");
            propertyFilter.append(
                    "        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>"
                    + dateFormat.format(fromValue) + "</Literal>");
            propertyFilter.append("</PropertyIsGreaterThanOrEqualTo>");

            propertyFilters.add(propertyFilter.toString());
        }

        if (toValue != null) {
            final StringBuffer propertyFilter = new StringBuffer();

            propertyFilter.append("<PropertyIsLessThanOrEqualTo>");
            propertyFilter.append(
                    "        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>"
                    + dateFormat.format(toValue) + "</Literal>");
            propertyFilter.append("</PropertyIsLessThanOrEqualTo>");

            propertyFilters.add(propertyFilter.toString());
        }

    }

    private void buildPropertyFilter(final List<String> propertyFilters,
            final String property, final String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }

        final StringBuffer propertyFilter = new StringBuffer();

        if (value.contains("%")) {
            propertyFilter.append(
                    "<PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">");
            propertyFilter.append(
                    "        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>" + value + "</Literal>");
            propertyFilter.append("</PropertyIsLike>");
        } else {
            propertyFilter.append("<PropertyIsEqualTo>");
            propertyFilter.append(
                    "        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>" + value + "</Literal>");
            propertyFilter.append("</PropertyIsEqualTo>");
        }

        propertyFilters.add(propertyFilter.toString());

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

    public String getCqlConstraint() {
        final StringBuffer filter = new StringBuffer();

        final List<String> propertyCqlFilters = new ArrayList<String>();

        if (StringUtils.isNotEmpty(this.freeText)
                && (!this.freeText.contains("%"))) {
            this.freeText = "%" + this.freeText + "%";
        }
        this.buildCqlQueryable(propertyCqlFilters, "anytext", this.freeText);
        this.buildCqlQueryable(propertyCqlFilters, "title", this.title);
        this.buildCqlQueryable(propertyCqlFilters, "subject", this.subject);
        this.buildDateQueryCql(propertyCqlFilters, "modified", this.dateFrom,
                this.dateTo);
        this.buildBBOXCql(propertyCqlFilters, "BoundingBox");

        if (propertyCqlFilters.size() > 0) {
            for (int i = 0; i < propertyCqlFilters.size(); i++) {
                filter.append(propertyCqlFilters.get(i));
                if (i < (propertyCqlFilters.size() - 1)) {
                    filter.append(" AND ");
                }
            }
        }

        return filter.toString();
    }

    public String getCustomCswQuery() {
        return this.customCswQuery;
    }

    public Date getDateFrom() {
        return this.dateFrom;
    }

    public Date getDateTo() {
        return this.dateTo;
    }

    public String getFilterConstraint() {
        final StringBuffer filter = new StringBuffer();

        final List<String> propertyFilters = new ArrayList<String>();

        filter.append(
                "<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");

        if (StringUtils.isNotEmpty(this.freeText)
                && (!this.freeText.contains("%"))) {
            this.freeText = "%" + this.freeText + "%";
        }
        this.buildPropertyFilter(propertyFilters, "anytext", this.freeText);
        this.buildPropertyFilter(propertyFilters, "title", this.title);
        this.buildPropertyFilter(propertyFilters, "subject", this.subject);
        this.buildDateQueryFilter(propertyFilters, "modified", this.dateFrom,
                this.dateTo);
        this.buildBBOXFilter(propertyFilters, "BoundingBox");

        if (propertyFilters.size() > 0) {
            if (propertyFilters.size() > 1) {
                filter.append("<And>");
            }

            for (final String propertyFilter : propertyFilters) {
                filter.append(propertyFilter);
            }

            if (propertyFilters.size() > 1) {
                filter.append("</And>");
            }
        }

        filter.append("</Filter>");

        return filter.toString();
    }

    public String getFreeText() {
        return this.freeText;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getTitle() {
        return this.title;
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

    public void setCustomCswQuery(final String customCswQuery) {
        this.customCswQuery = customCswQuery;
    }

    public void setDateFrom(final Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(final Date dateTo) {
        this.dateTo = dateTo;
    }

    public void setFreeText(final String freeText) {
        this.freeText = freeText;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
