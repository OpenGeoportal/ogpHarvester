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
package org.opengeoportal.harvester.api.client.geonetwork;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * GeoNetwork client class.
 *
 */
public class GeoNetworkClient {
    private static final Namespace GEONET_NS = Namespace.getNamespace("geonet",
    		"http://www.fao.org/geonetwork");

    /** GeoNetwork server url. **/
    private String serverUrl;

    private XmlRequest request;

    public GeoNetworkClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.request = new XmlRequest(serverUrl);
    }

    public GeoNetworkClient(URL serverUrl) {
        this(serverUrl.toString());
    }

    /**
     * Retrieves the sources from a GeoNetwork server.
     *
     * @return A list of GeoNetwork sources (source uuid, source name).
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public List<AbstractMap.SimpleEntry<String, String>> getSources() throws Exception {
        List<AbstractMap.SimpleEntry<String, String>> sources = new ArrayList<AbstractMap.SimpleEntry<String, String>>();

        request.setAddress(serverUrl + "/srv/eng/xml.info?type=sources");

        Element xmlResponse = request.execute();
        /* Response xml example:
            <info>
                <sources>
                    <source>
                        <uuid>321d0c64-24ae-460d-89fc-e425c8857331</uuid>
                        <name>SOURCE 1</name>
                    </source>
                    <source>
                        <uuid>f6014c39-7f47-472f-915b-920d334f4780</uuid>
                        <name>SOURCE 1</name>
                    </source>
                </sources>
            </info>
         */

        for(Element source: (List<Element>) xmlResponse.getChild("sources").getChildren()) {
            sources.add(new AbstractMap.SimpleEntry<String, String>(source.getChildText("uuid"), source.getChildText("name")));
        }

        return sources;
    }

    public org.w3c.dom.Document retrieveMetadata(int metadataId) throws Exception {
        request.setAddress(serverUrl + "/srv/eng/xml.metadata.get");
        request.clearParams();
        request.addParam("id", metadataId);

        try {
            Element md   = request.execute();
            Element info = md.getChild("info", GEONET_NS);

            if (info != null) { info.detach(); }


            Document doc = new Document(md);

            DOMOutputter domOutputter = new DOMOutputter();
            org.w3c.dom.Document document =  domOutputter.output(doc);

            return document;
        } catch(Exception e) {
            // TODO: Log error in ingest report

            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public GeoNetworkSearchResponse search(GeoNetworkSearchParams searchParams) throws Exception {
        GeoNetworkSearchResponse response = new GeoNetworkSearchResponse();

        request.setAddress(serverUrl + "/srv/eng/xml.search");

        Element xmlResponse = request.execute(searchParams.toXml());

        response.setTotal(Integer.parseInt(xmlResponse.getChild("summary").getAttribute("count").getValue()));

        List<Element> records = xmlResponse.getChildren("metadata");
        for(Element record : records) {
            Element info = record.getChild("info", GEONET_NS);

            if (info == null) {
                //logger.warning("Missing 'geonet:info' element in 'metadata' element");

            } else {
                GeoNetworkSearchResult metadataResult = new GeoNetworkSearchResult();
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
