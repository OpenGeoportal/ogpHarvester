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
import java.util.HashSet;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;

import com.google.common.collect.Lists;

@Entity
@DiscriminatorValue("GN")
public class IngestGeonetwork extends Ingest {

    private static final long serialVersionUID = 2521267747992995326L;

    @Column
    private String title;
    @Column
    private String keyword;
    @Column
    private String abstractText;
    @Column
    private String freeText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ingestgn_gn_remote_sources", joinColumns = @JoinColumn(name = "ingest_id"))
    @OrderColumn
    @Column
    private List<String> geonetworkSources = Lists.newArrayList();

    public IngestGeonetwork() {
        super();
        this.validRequiredFields = new HashSet<String>(
                Arrays.asList(new String[] { "geographicExtent", "themeKeyword",
                        "placeKeyword", "topic", "dateOfContent", "originator",
                        "dataType", "webServices" }));
    }

    public String getAbstractText() {
        return this.abstractText;
    }

    public String getFreeText() {
        return this.freeText;
    }

    public List<String> getGeonetworkSources() {
        return this.geonetworkSources;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public String getTitle() {
        return this.title;
    }

    public void setAbstractText(final String abstractText) {
        this.abstractText = abstractText;
    }

    public void setFreeText(final String freeText) {
        this.freeText = freeText;
    }

    public void setGeonetworkSource(final List<String> geonetworkSource) {
        this.geonetworkSources = geonetworkSource;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
