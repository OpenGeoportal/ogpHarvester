/**
 * BaseMetadataParser.java
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

import java.util.Iterator;

import org.opengeoportal.harvester.api.metadata.model.BoundingBox;
import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;

import java.util.Collection;
import com.google.common.collect.Multimap;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class BaseMetadataParser {

	/**
	 * Constructor.
	 */
	public BaseMetadataParser() {

	}

	/**
	 * Check if the bounding box is valid.
	 * 
	 * @param minX
	 *            lower horizontal value.
	 * @param minY
	 *            lower vertical value.
	 * @param maxX
	 *            upper horizontal value.
	 * @param maxY
	 *            upper vertical value.
	 * @return <code>true</code> if passed values can be a valid bounding box
	 *         and false otherwhise.
	 */
	protected Boolean validateBounds(String minX, String minY, String maxX,
			String maxY) {
		BoundingBox bounds = new BoundingBox(minX, minY, maxX, maxY);
		return bounds.isValid();
	}

	protected Boolean validateBounds(Double minX, Double minY, Double maxX,
			Double maxY) {
		BoundingBox bounds = new BoundingBox(minX, minY, maxX, maxY);
		return bounds.isValid();
	}

	protected String buildLocationJsonFromLocationLinks(
			Multimap<LocationType, LocationLink> linksMultimap) {
		StringBuilder sb = new StringBuilder("{");

		for (Iterator<LocationType> keyIterator = linksMultimap.keySet()
				.iterator(); keyIterator.hasNext();) {
			LocationType type = keyIterator.next();
			boolean isArray = type.getIsArray();
			sb.append("\"").append(type.toString()).append("\":");
			if (isArray) {
				sb.append("[");
				for (Iterator<LocationLink> linkIterator = linksMultimap.get(
						type).iterator(); linkIterator.hasNext();) {
					LocationLink link = linkIterator.next();
					sb.append("\"").append(link.getUrl()).append("\"");
					if (linkIterator.hasNext()) {
						sb.append(", ");
					}
				}
				sb.append("]");
			} else { // not an array
				sb.append("\"");
				Collection<LocationLink> links = linksMultimap.get(type);
				if (links.size() > 0) {
					sb.append(links.iterator().next().getUrl());
				}
				sb.append("\"");
			}

			if (keyIterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}
}
