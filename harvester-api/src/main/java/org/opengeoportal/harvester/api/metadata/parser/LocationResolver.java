package org.opengeoportal.harvester.api.metadata.parser;

import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;
import org.w3c.dom.Document;

import com.google.common.collect.Multimap;

public interface LocationResolver {
    Multimap<LocationType, LocationLink> resolveLocation(Document xmlDocument);

}
