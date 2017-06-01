/*
 * GeoNetworkClient.java
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
package org.opengeoportal.harvester.api.client.geonetwork;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;

/**
 * GeoNetwork client class.
 *
 */
public class GeoNetworkClient {

    private static final Namespace GEONET_NS = Namespace.getNamespace("geonet",
            "http://www.fao.org/geonetwork");

    /**
     * GeoNetwork server url. *
     */
    private final String serverUrl;

    private final XmlRequest request;

    public GeoNetworkClient(final URL serverUrl) {
        this.serverUrl = serverUrl.toString();
        this.request = new XmlRequest(serverUrl);
    }

    /**
     * Retrieves the sources from a GeoNetwork server.
     *
     * @return A list of GeoNetwork sources (source uuid, source name).
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<AbstractMap.SimpleEntry<String, String>> getSources()
            throws Exception {
        final List<AbstractMap.SimpleEntry<String, String>> sources = new ArrayList<AbstractMap.SimpleEntry<String, String>>();

        this.request.setUrl(
                new URL(this.serverUrl + "/srv/eng/xml.info?type=sources"));

        final Element xmlResponse = this.request.execute();
        /*
         * Response xml example: <info> <sources> <source>
         * <uuid>321d0c64-24ae-460d-89fc-e425c8857331</uuid> <name>SOURCE
         * 1</name> </source> <source>
         * <uuid>f6014c39-7f47-472f-915b-920d334f4780</uuid> <name>SOURCE
         * 1</name> </source> </sources> </info>
         */

        for (final Element source : (List<Element>) xmlResponse
                .getChild("sources").getChildren()) {
            sources.add(new AbstractMap.SimpleEntry<String, String>(
                    source.getChildText("uuid"), source.getChildText("name")));
        }

        return sources;
    }

    public org.w3c.dom.Document retrieveMetadata(final int metadataId)
            throws Exception {
        this.request
                .setUrl(new URL(this.serverUrl + "/srv/eng/xml.metadata.get"));

        this.request.clearParams();
        this.request.addParam("id", metadataId);

        final Element md = this.request.execute();
        final Element info = md.getChild("info", GeoNetworkClient.GEONET_NS);

        if (info != null) {
            info.detach();
        }

        final Document doc = new Document(md);

        final DOMOutputter domOutputter = new DOMOutputter();
        final org.w3c.dom.Document document = domOutputter.output(doc);

        return document;
    }

    @SuppressWarnings("unchecked")
    public GeoNetworkSearchResponse search(
            final GeoNetworkSearchParams searchParams) throws Exception {
        final GeoNetworkSearchResponse response = new GeoNetworkSearchResponse();

        this.request.setUrl(new URL(this.serverUrl + "/srv/eng/xml.search"));

        final Element xmlResponse = this.request.execute(searchParams.toXml());

        response.setTotal(Integer.parseInt(xmlResponse.getChild("summary")
                .getAttribute("count").getValue()));

        final List<Element> records = xmlResponse.getChildren("metadata");
        for (final Element record : records) {
            final Element info = record.getChild("info",
                    GeoNetworkClient.GEONET_NS);

            if (info == null) {
                // logger.warning("Missing 'geonet:info' element in 'metadata'
                // element");

            } else {
                final GeoNetworkSearchResult metadataResult = new GeoNetworkSearchResult();
                metadataResult.setId(Integer.parseInt(info.getChildText("id")));
                metadataResult.setUuid(info.getChildText("uuid"));
                metadataResult.setSchema(info.getChildText("schema"));
                metadataResult.setChangeDate(info.getChildText("changeDate"));

                response.addMetadataSearchResult(metadataResult);
            }
        }

        return response;
    }
}
