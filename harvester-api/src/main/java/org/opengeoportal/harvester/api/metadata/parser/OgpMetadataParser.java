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

	/**
	 * Transform the {@link SolrRecord} into a {@link MetadataParserResponse}.
	 * 
	 * @param record
	 *            the Solr record.
	 * @return an MetadataParserResponse with a {@link Metadata} inside.
	 */
	public MetadataParserResponse parse(SolrRecord record) {
		MetadataParserResponse response = new MetadataParserResponse();
		Metadata metadata = response.getMetadata();
		handleAccess(record, response, metadata);
		handleBounds(record, response, metadata);
		metadata.setContentDate(record.getContentDate());
		metadata.setDescription(record.getDescription());
		metadata.setFullText(record.getFgdcText());
		handleGeometryType(record, response, metadata);
		handleGeoreference(record, response, metadata);
		metadata.setId(record.getLayerId());
		metadata.setInstitution(record.getInstitution());
		metadata.setLocation(record.getLocation());
		metadata.setOriginator(record.getOriginator());
		metadata.setOwsName(record.getName());
		handlePlaceKeywords(record, response, metadata);
		metadata.setPublisher(record.getPublisher());
		handleThemeKeywords(record, response, metadata);
		metadata.setTitle(record.getLayerDisplayName());
		metadata.setTopic(record.getTopicCategory());
		metadata.setWorkspaceName(record.getWorkspaceName());
		metadata.setOriginalMetadata(record.getOriginalXmlMetadata());

		return response;
	}

	/**
	 * @param record
	 * @param response
	 * @param metadata
	 */
	private void handleThemeKeywords(SolrRecord record,
			MetadataParserResponse response, Metadata metadata) {
		String themeKeywordsString = record.getThemeKeywords();
		if (StringUtils.isNotBlank(themeKeywordsString)) {
			String[] keywords = StringUtils.split(themeKeywordsString);
			List<ThemeKeywords> themeKeywordsList = Lists.newArrayList();
			for (String keyword : keywords) {
				ThemeKeywords tKeywords = new ThemeKeywords();
				tKeywords.addKeyword(keyword);
				themeKeywordsList.add(tKeywords);
			}
			metadata.setThemeKeywords(themeKeywordsList);
		}

	}

	/**
	 * @param record
	 * @param response
	 * @param metadata
	 */
	private void handlePlaceKeywords(SolrRecord record,
			MetadataParserResponse response, Metadata metadata) {
		String placeKeywordsString = record.getPlaceKeywords();
		if (StringUtils.isNotBlank(placeKeywordsString)) {
			String[] keywords = StringUtils.split(placeKeywordsString);
			List<PlaceKeywords> placeKeywordsList = Lists.newArrayList();
			for (String keyword : keywords) {
				PlaceKeywords pKeywords = new PlaceKeywords();
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
	private void handleGeoreference(SolrRecord record,
			MetadataParserResponse response, Metadata metadata) {
		Boolean isGeorerferenced = record.getGeoreferenced();
		metadata.setGeoreferenced(isGeorerferenced);
	}

	/**
	 * @param record
	 * @param response
	 * @param metadata
	 */
	private void handleGeometryType(SolrRecord record,
			MetadataParserResponse response, Metadata metadata) {
		String geometryTypeString = record.getDataType();
		if (StringUtils.isNotBlank(geometryTypeString)) {
			boolean found = false;
			GeometryType[] types = GeometryType.values();
			for (int i = 0; i < types.length && !found; i++) {
				if (StringUtils.equalsIgnoreCase(types[i].toString(),
						geometryTypeString)) {
					metadata.setGeometryType(types[i]);
					found = true;
				}
			}

			if (!found) {
                // Set a default value if no found: Undefined
                metadata.setGeometryType(GeometryType.Undefined);

				response.addError("GeometryType", SolrRecord.DATA_TYPE,
						GEOMETRY_TYPE_NOT_VALID, String.format(
								"%s is not a valid GeometryType value",
								geometryTypeString));
			}
		} else {
            // Set a default value if no found: Undefined
            metadata.setGeometryType(GeometryType.Undefined);

            response.addError("GeometryType", SolrRecord.DATA_TYPE,
                    GEOMETRY_TYPE_NOT_VALID,
                    "No GeometryType value found");
        }

	}

	/**
	 * @param record
	 * @param response
	 * @param metadata
	 */
	private void handleBounds(SolrRecord record,
			MetadataParserResponse response, Metadata metadata) {
		Double minX = record.getMinX();
		Double minY = record.getMinY();
		Double maxX = record.getMaxX();
		Double maxY = record.getMaxY();
		if (validateBounds(minX, minY, maxX, maxY)) {
			metadata.setBounds(minX, minY, maxX, maxY);
		} else {
			response.addError("BBOX", "bbox", BBOX_NOT_VALID, String.format(
					"The bounding box [%s, %s, %s, %s] is not valid", minX,
					minY, maxX, maxY));
		}

	}

	/**
	 * @param record
	 * @param response
	 * @param metadata
	 */
	private void handleAccess(SolrRecord record,
			MetadataParserResponse response, Metadata metadata) {
		String accessLevel = record.getAccess();
		if (StringUtils.equals(accessLevel, AccessLevel.Public.toString())) {
			metadata.setAccess(AccessLevel.Public);
		} else if (StringUtils.equals(accessLevel,
				AccessLevel.Restricted.toString())) {
			metadata.setAccess(AccessLevel.Restricted);
		} else {
			metadata.setAccessLevel(accessLevel);
			response.addError(SolrRecord.ACCESS, SolrRecord.ACCESS,
					ACCESS_NOT_VALID, "\"" + accessLevel
							+ "\" is not a valid value for "
							+ SolrRecord.ACCESS + " field");
		}
	}
}
