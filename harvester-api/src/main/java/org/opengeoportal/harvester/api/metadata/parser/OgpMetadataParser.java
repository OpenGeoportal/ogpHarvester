/**
 * OgpMetadataParser.java
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
package org.opengeoportal.harvester.api.metadata.parser;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.opengeoportal.harvester.api.metadata.model.GeometryType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.model.PlaceKeywords;
import org.opengeoportal.harvester.api.metadata.model.ThemeKeywords;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class OgpMetadataParser extends BaseMetadataParser {
    public static final String ACCESS_NOT_VALID = "ACCESS_NOT_VALID";
    public static final String BBOX_NOT_VALID = "BBOX_NOT_VALID";
    private static final String GEOMETRY_TYPE_NOT_VALID = "GEOMETRY_TYPE_NOT_VALID";

    private final IsoTopicResolver isoTopicResolver = new IsoTopicResolver();

    /**
     * @param record
     * @param response
     * @param metadata
     */
    private void handleAccess(final SolrRecord record,
            final MetadataParserResponse response, final Metadata metadata) {
        final String accessLevel = record.getAccess();
        if (StringUtils.equals(accessLevel, AccessLevel.Public.toString())) {
            metadata.setAccess(AccessLevel.Public);
        } else if (StringUtils.equals(accessLevel,
                AccessLevel.Restricted.toString())) {
            metadata.setAccess(AccessLevel.Restricted);
        } else {
            metadata.setAccessLevel(accessLevel);
            response.addError(SolrRecord.ACCESS, SolrRecord.ACCESS,
                    OgpMetadataParser.ACCESS_NOT_VALID,
                    "\"" + accessLevel + "\" is not a valid value for "
                            + SolrRecord.ACCESS + " field");
        }
    }

    /**
     * @param record
     * @param response
     * @param metadata
     */
    private void handleBounds(final SolrRecord record,
            final MetadataParserResponse response, final Metadata metadata) {
        final Double minX = record.getMinX();
        final Double minY = record.getMinY();
        final Double maxX = record.getMaxX();
        final Double maxY = record.getMaxY();
        if (this.validateBounds(minX, minY, maxX, maxY)) {
            metadata.setBounds(minX, minY, maxX, maxY);
        } else {
            response.addError("BBOX", "bbox", OgpMetadataParser.BBOX_NOT_VALID,
                    String.format(
                            "The bounding box [%s, %s, %s, %s] is not valid",
                            minX, minY, maxX, maxY));
        }

    }

    /**
     * @param record
     * @param response
     * @param metadata
     */
    private void handleGeometryType(final SolrRecord record,
            final MetadataParserResponse response, final Metadata metadata) {
        final String geometryTypeString = record.getDataType();
        final GeometryType geom = GeometryType
                .parseGeometryType(geometryTypeString);

        if (geom.equals(GeometryType.Undefined)) {
            response.addError("GeometryType", SolrRecord.DATA_TYPE,
                    OgpMetadataParser.GEOMETRY_TYPE_NOT_VALID,
                    String.format("%s is not a valid GeometryType value",
                            geometryTypeString));
        }

        metadata.setGeometryType(geom);

    }

    /**
     * @param record
     * @param response
     * @param metadata
     */
    private void handleGeoreference(final SolrRecord record,
            final MetadataParserResponse response, final Metadata metadata) {
        final Boolean isGeorerferenced = record.getGeoreferenced();
        metadata.setGeoreferenced(isGeorerferenced);
    }

    /**
     * @param record
     * @param response
     * @param metadata
     */
    private void handlePlaceKeywords(final SolrRecord record,
            final MetadataParserResponse response, final Metadata metadata) {
        final String placeKeywordsString = record.getPlaceKeywords();
        if (StringUtils.isNotBlank(placeKeywordsString)) {
            final String[] keywords = StringUtils.split(placeKeywordsString);
            final List<PlaceKeywords> placeKeywordsList = Lists.newArrayList();
            for (final String keyword : keywords) {
                final PlaceKeywords pKeywords = new PlaceKeywords();
                pKeywords.addKeyword(keyword);
                placeKeywordsList.add(pKeywords);
            }
            metadata.setPlaceKeywords(placeKeywordsList);
        }
    }

    /**
     * @param record
     * @param response
     * @param metadata
     */
    private void handleThemeKeywords(final SolrRecord record,
            final MetadataParserResponse response, final Metadata metadata) {
        final String themeKeywordsString = record.getThemeKeywords();
        if (StringUtils.isNotBlank(themeKeywordsString)) {
            final String[] keywords = StringUtils.split(themeKeywordsString);
            final List<ThemeKeywords> themeKeywordsList = Lists.newArrayList();
            for (final String keyword : keywords) {
                final ThemeKeywords tKeywords = new ThemeKeywords();
                tKeywords.addKeyword(keyword);
                themeKeywordsList.add(tKeywords);

                final String isoTopic = this.isoTopicResolver
                        .getIsoTopicKeyword(keyword);
                if (!isoTopic.isEmpty()) {
                    metadata.setTopic(isoTopic);
                }
            }
            metadata.setThemeKeywords(themeKeywordsList);
        }

    }

    /**
     * Transform the {@link SolrRecord} into a {@link MetadataParserResponse}.
     * 
     * @param record
     *            the Solr record.
     * @return an MetadataParserResponse with a {@link Metadata} inside.
     */
    public MetadataParserResponse parse(final SolrRecord record) {
        final MetadataParserResponse response = new MetadataParserResponse();
        final Metadata metadata = response.getMetadata();
        this.handleAccess(record, response, metadata);
        this.handleBounds(record, response, metadata);
        metadata.setContentDate(record.getContentDate());
        metadata.setDescription(record.getDescription());
        metadata.setFullText(record.getFgdcText());
        this.handleGeometryType(record, response, metadata);
        this.handleGeoreference(record, response, metadata);
        metadata.setId(record.getLayerId());
        metadata.setInstitution(record.getInstitution());
        metadata.setLocation(record.getLocation());
        metadata.setOriginator(record.getOriginator());
        metadata.setOwsName(record.getName());
        this.handlePlaceKeywords(record, response, metadata);
        metadata.setPublisher(record.getPublisher());
        this.handleThemeKeywords(record, response, metadata);
        metadata.setTitle(record.getLayerDisplayName());
        metadata.setWorkspaceName(record.getWorkspaceName());
        metadata.setOriginalMetadata(record.getOriginalXmlMetadata());
        metadata.setExternalId(record.getExternalLayerId());
        metadata.setCollectionId(record.getCollectionId());
        return response;
    }
}
