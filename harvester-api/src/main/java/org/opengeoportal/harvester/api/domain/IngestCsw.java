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

import org.apache.commons.lang3.StringUtils;
import org.opengeoportal.harvester.api.metadata.model.BoundingBox;

import java.text.SimpleDateFormat;
import java.util.*;

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
		validRequiredFields = new HashSet<String>(Arrays.asList(new String[] {
				"geographicExtent", "themeKeyword", "placeKeyword", "topic",
				"dateOfContent", "originator", "dataType", "webServices" }));
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

	/**
	 * @return the bboxWest
	 */
	public Double getBboxWest() {
		return bboxWest;
	}

	/**
	 * @param bboxWest the bboxWest to set
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
	 * @param bboxEast the bboxEast to set
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
	 * @param bboxNorth the bboxNorth to set
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
	 * @param bboxSouth the bboxSouth to set
	 */
	public void setBboxSouth(Double bboxSouth) {
		this.bboxSouth = bboxSouth;
	}


    public String getCqlConstraint() {
        StringBuffer filter = new StringBuffer();

        List<String> propertyCqlFilters = new ArrayList<String>();

        if (StringUtils.isNotEmpty(this.freeText) && (!this.freeText.contains("%"))) this.freeText = "%"+this.freeText+"%";
        buildCqlQueryable(propertyCqlFilters, "anytext", this.freeText);
        buildCqlQueryable(propertyCqlFilters, "title", this.title);
        buildCqlQueryable(propertyCqlFilters, "subject", this.subject);
        buildDateQueryCql(propertyCqlFilters, "modified", this.dateFrom, this.dateTo);
        buildBBOXCql(propertyCqlFilters, "BoundingBox");

        if (propertyCqlFilters.size() > 0) {
            for(int i = 0; i < propertyCqlFilters.size(); i++) {
                filter.append(propertyCqlFilters.get(i));
                if (i < propertyCqlFilters.size()-1) filter.append(" AND ");
            }
        }

        return filter.toString();
    }

    public String getFilterConstraint() {
        StringBuffer filter = new StringBuffer();

        List<String> propertyFilters = new ArrayList<String>();

        filter.append("<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");

        if (StringUtils.isNotEmpty(this.freeText) && (!this.freeText.contains("%"))) this.freeText = "%"+this.freeText+"%";
        buildPropertyFilter(propertyFilters, "anytext", this.freeText);
        buildPropertyFilter(propertyFilters, "title", this.title);
        buildPropertyFilter(propertyFilters, "subject", this.subject);
        buildDateQueryFilter(propertyFilters, "modified", this.dateFrom, this.dateTo);
        buildBBOXFilter(propertyFilters, "BoundingBox");

        if (propertyFilters.size() > 0) {
            if (propertyFilters.size() > 1) filter.append("<And>");

            for(String propertyFilter: propertyFilters) {
                filter.append(propertyFilter);
            }

            if (propertyFilters.size() > 1) filter.append("</And>");
        }

        filter.append("</Filter>");

        return filter.toString();
    }

    private void buildPropertyFilter(List<String> propertyFilters, String property, String value) {
        if (StringUtils.isEmpty(value)) return;

        StringBuffer propertyFilter = new StringBuffer();


        if (value.contains("%")) {
            propertyFilter.append("<PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">");
            propertyFilter.append("        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>" + value + "</Literal>");
            propertyFilter.append("</PropertyIsLike>");
        } else {
            propertyFilter.append("<PropertyIsEqualTo>");
            propertyFilter.append("        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>" + value + "</Literal>");
            propertyFilter.append("</PropertyIsEqualTo>");
        }

        propertyFilters.add(propertyFilter.toString());

    }

    private void buildDateQueryFilter(List<String> propertyFilters, String property, Date fromValue, Date toValue) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (fromValue != null) {
            StringBuffer propertyFilter = new StringBuffer();

            propertyFilter.append("<PropertyIsGreaterThanOrEqualTo>");
            propertyFilter.append("        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>" + dateFormat.format(fromValue) + "</Literal>");
            propertyFilter.append("</PropertyIsGreaterThanOrEqualTo>");

            propertyFilters.add(propertyFilter.toString());
        }

        if (toValue != null) {
            StringBuffer propertyFilter = new StringBuffer();

            propertyFilter.append("<PropertyIsLessThanOrEqualTo>");
            propertyFilter.append("        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append("        <Literal>" + dateFormat.format(toValue) + "</Literal>");
            propertyFilter.append("</PropertyIsLessThanOrEqualTo>");

            propertyFilters.add(propertyFilter.toString());
        }


    }

    private void buildBBOXFilter(List<String> propertyFilters, String property) {
        BoundingBox bbox = new BoundingBox(this.bboxWest, this.bboxSouth, this.bboxEast, this.bboxNorth);
        if (bbox.isValid()) {
            StringBuffer propertyFilter = new StringBuffer();

            propertyFilter.append("<Intersects>");
            propertyFilter.append("        <PropertyName>" + property + "</PropertyName>");
            propertyFilter.append(bbox.generateGMLBox(4326));
            propertyFilter.append("</Intersects>");
            propertyFilters.add(propertyFilter.toString());
        }
    }

    private void buildCqlQueryable(List<String> propertyCqlFilters, String name, String value) {
        if (StringUtils.isEmpty(value)) return;

        String queryable = "";

        if (value.contains("%")) {
            queryable = name + " like '"+ value +"'";
        } else {
            queryable =  name + " = '"+ value + "'";
        }


        if (StringUtils.isNotEmpty(queryable)) propertyCqlFilters.add(queryable);
    }

    private void buildDateQueryCql(List<String> propertyCqlFilters, String name, Date fromValue, Date toValue) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (fromValue != null) {
            propertyCqlFilters.add(name + " >= '" + dateFormat.format(fromValue) + "'");
        }

        if (toValue != null) {
            propertyCqlFilters.add(name + " <= '" + dateFormat.format(toValue)  + "'");
        }

    }

    private void buildBBOXCql(List<String> propertyCqlFilters, String property) {
        BoundingBox bbox = new BoundingBox(this.bboxWest, this.bboxSouth, this.bboxEast, this.bboxNorth);
        if (bbox.isValid()) {
            propertyCqlFilters.add("INTERSECTS(ows:BoundingBox,ENVELOPE(" + bbox.toString() + "))");
        }
    }

}
