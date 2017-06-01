/**
 * SimpleIngestJobFactory.java
 *
 * Copyright (C) 2014
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
package org.opengeoportal.harvester.api.component;

import org.opengeoportal.harvester.api.component.csw.CswIngestJob;
import org.opengeoportal.harvester.api.component.file.SingleFileIngestJob;
import org.opengeoportal.harvester.api.component.geonetwork.GeonetworkIngestJob;
import org.opengeoportal.harvester.api.component.ogp.OgpIngestJob;
import org.opengeoportal.harvester.api.component.webdav.WebdavIngestJob;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestCsw;
import org.opengeoportal.harvester.api.domain.IngestFileUpload;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.IngestWebDav;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
@Component
public class SimpleIngestJobFactory implements IngestJobFactory {

    /**
     * Create a new {@link SimpleIngestJobFactory} instance.
     */
    public SimpleIngestJobFactory() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.component.IngestJobFactory#newIngestJob
     * (org.opengeoportal.harvester.api.domain.Ingest)
     */
    @Override
    public BaseIngestJob newIngestJob(final Ingest ingest) {
        BaseIngestJob newInstance;
        if (ingest == null) {
            throw new IllegalArgumentException("ingest cannot be null");
        }

        if (ingest instanceof IngestCsw) {
            newInstance = new CswIngestJob();
        } else if (ingest instanceof IngestGeonetwork) {
            newInstance = new GeonetworkIngestJob();
        } else if (ingest instanceof IngestOGP) {
            newInstance = new OgpIngestJob();
        } else if (ingest instanceof IngestWebDav) {
            newInstance = new WebdavIngestJob();
        } else if (ingest instanceof IngestFileUpload) {
            newInstance = new SingleFileIngestJob();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find a suitable Job class " + "for processing a "
                            + ingest.getClass().getName() + "class");
        }

        return newInstance;
    }
}
