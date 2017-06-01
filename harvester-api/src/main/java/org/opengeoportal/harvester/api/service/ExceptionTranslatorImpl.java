/**
 * ExceptionTranslatorImpl.java
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
package org.opengeoportal.harvester.api.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jdom.JDOMException;
import org.opengeoportal.harvester.api.client.geonetwork.exception.BadXmlResponseEx;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.domain.IngestReportError;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.exception.CswClientException;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author JuanLuis
 */
@Service
public class ExceptionTranslatorImpl implements ExceptionTranslator {

    private void fillIngestReportError(final IngestReportError error,
            final String field, final String message, final String originalText,
            final Exception e, final IngestReportErrorType errorType) {
        error.setType(errorType);
        final Exception ex = (Exception) e.fillInStackTrace();
        error.setField(field);
        if (message == null) {
            error.setMessage(ex.getLocalizedMessage());
        } else {
            error.setMessage(message);
        }

        if (originalText == null) {
            error.setMetadata(ExceptionUtils.getStackTrace(ex));
        } else {
            error.setMetadata(originalText);
        }
    }

    /**
     * From document get its string representation.
     *
     * @param document
     *            XML metadata document.
     * @return document string representation.
     */
    protected String getMetadataText(final Document document) {
        return XmlUtil.getFullText(document);
    }

    /**
     * From document get its string representation.
     *
     * @param record
     *            {@code SolrRecord} with metadata.
     * @return document string representation.
     */
    protected String getMetadataText(final SolrRecord record) {
        if (record == null) {
            return null;
        } else {
            return record.getFgdcText();
        }
    }

    @Override
    public IngestReportError translateException(final Exception e,
            final IngestReportErrorType errorType) {
        final String originalText = null;
        return this.translateException(e, errorType, originalText);
    }

    @Override
    public IngestReportError translateException(final Exception e,
            final IngestReportErrorType errorType, final Document document) {
        final String originalText = this.getMetadataText(document);
        return this.translateException(e, errorType, originalText);
    }

    @Override
    public IngestReportError translateException(final Exception e,
            final IngestReportErrorType errorType, final SolrRecord record) {
        final String originalText = this.getMetadataText(record);
        return this.translateException(e, errorType, originalText);
    }

    /**
     *
     * @param exception
     * @param errorType
     * @param originalText
     * @return
     */
    public IngestReportError translateException(final Exception exception,
            final IngestReportErrorType errorType, final String originalText) {
        final IngestReportError reportError = new IngestReportError();
        String message;
        String field;
        if (exception instanceof UnsupportedMetadataType) {
            message = "Metadata type not supported";
            field = UnsupportedMetadataType.class.getSimpleName();
            this.fillIngestReportError(reportError, field, message,
                    originalText, exception, errorType);
        } else if (exception instanceof MalformedURLException) {
            message = "Malformed URL: " + exception.getLocalizedMessage();
            field = MalformedURLException.class.getSimpleName();
            this.fillIngestReportError(reportError, field, message,
                    originalText, exception, errorType);
        } else if (exception instanceof JDOMException) {
            message = "Error parsing XML document";
            field = "PARSE_XML_ERROR";
            this.fillIngestReportError(reportError, field, message,
                    originalText, exception, errorType);
        } else if (exception instanceof CswClientException) {
            message = "Error connecting to CSW server";
            field = "CSW_CLIENT_ERROR";
            this.fillIngestReportError(reportError, field, message,
                    originalText, exception, errorType);
        } else if (exception instanceof SAXException) {
            message = null;
            field = "PARSE_XML_ERROR";
            this.fillIngestReportError(reportError, field, message,
                    originalText, exception, errorType);
        } else if (exception instanceof BadXmlResponseEx) {
            message = exception.getLocalizedMessage();
            field = "GN_BAD_XML_RESPONSE";
            this.fillIngestReportError(reportError, field, message,
                    originalText, exception, errorType);
        } else if (exception instanceof IOException) {
            message = exception.getLocalizedMessage();
            field = "CONNECTION_ERROR";
            this.fillIngestReportError(reportError, field, message,
                    originalText, exception, errorType);
        } else {
            field = exception.getClass().getSimpleName();
            this.fillIngestReportError(reportError, field, null, originalText,
                    exception, errorType);
        }
        return reportError;
    }
}
