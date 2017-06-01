package org.opengeoportal.harvester.api.component.webdav;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.domain.IngestWebDav;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.google.common.collect.ImmutableList;

/**
 * Ingest Job capable of process a remote WebDAV folder.
 *
 * @author <a href="mailto:jose.garcia@geocat.net">Jose García</a>.
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class WebdavIngestJob extends BaseIngestJob {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * DavResource contains only the relative path. This method uses the base
     * url to return the absolute url of the resource.
     *
     * @param res
     *            WebDav resource.
     * @param baseUrl
     *            Base url of the WebDav resource.
     * @return Absolute url of the WebDav resource.
     * @throws MalformedURLException
     *             thrown when baseUrl param is nota valid URL.
     */
    private String getResourceAbsoluteUrl(final DavResource res,
            final String baseUrl) throws MalformedURLException {
        final URL urlR = new URL(baseUrl);

        return urlR + res.getHref().toString().replace(urlR.getPath(), "");
    }

    /**
     * Checks if the file has to be processed, verifying the content type
     * (application/xml) and the date filter configured in the the Ingest.
     *
     * @param res
     *            WebDav resource.
     * @return <code>true</code> is the file has to be processed,
     *         <code>false</code> otherwise.
     */
    private boolean hasToProcessFile(final DavResource res) {
        if (res.isDirectory()) {
            return false;
        }
        if (!res.getContentType().equalsIgnoreCase("application/xml")) {
            return false;
        }

        final IngestWebDav ingestWebdav = (IngestWebDav) this.ingest;
        final Date beginFilterDate = ingestWebdav.getDateFrom();
        final Date endFilterDate = ingestWebdav.getDateTo();
        final Date resourceDate = res.getModified();

        if ((beginFilterDate != null)
                && (resourceDate.before(beginFilterDate))) {
            return false;
        }
        return (endFilterDate == null) || (resourceDate.after(endFilterDate));
    }

    @Override
    public void ingest() {
        long failedRecordsCount = 0;
        Sardine sardine = null;
        try {
            sardine = SardineFactory.begin();
            failedRecordsCount = this.processWebdavFolder(sardine,
                    this.ingest.getActualUrl());
        } catch (final Exception e) {
            this.saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        } finally {
            if (sardine != null) {
                sardine.shutdown();
            }
        }
        this.report.setFailedRecordsCount(failedRecordsCount);
    }

    /**
     * Process the remote WebDav file, validating it and ingesting in the
     * system.
     *
     * @param res
     *            WebDav resource.
     * @param baseUrl
     *            Base url of the WebDav resource.
     * @return count of invalid records processed.
     */
    private long processFile(final DavResource res, final String baseUrl) {
        long failedRecordsCount = 0;
        Document document = null;
        try {
            // Retrieve file content
            final String absoluteHrefPath = this.getResourceAbsoluteUrl(res,
                    baseUrl);

            document = XmlUtil.load(absoluteHrefPath);
            final MetadataParser parser = this.parserProvider
                    .getMetadataParser(document);
            final MetadataParserResponse parserResult = parser.parse(document);

            final Metadata metadata = parserResult.getMetadata();
            metadata.setInstitution(this.ingest.getNameOgpRepository());

            final boolean valid = this.metadataValidator.validate(metadata,
                    this.report);
            if (valid) {
                this.metadataIngester.ingest(ImmutableList.of(metadata),
                        this.getIngestReport());
            } else {
                failedRecordsCount++;
            }

        } catch (final Exception e) {
            failedRecordsCount++;
            this.logger.error("Error in Webdav Ingest: " + this.ingest.getName()
                    + " (processing file:" + baseUrl + ")", e);
            this.saveException(e, IngestReportErrorType.SYSTEM_ERROR, document);
        }
        return failedRecordsCount;
    }

    /**
     * Process the metadata files in the webdav server, recursing the
     * subfolders.
     *
     * @param sardine
     *            {@link Sardine} instance.
     * @param url
     *            WebDAV folder URL.
     * @return count of invalid records processed.
     */
    private long processWebdavFolder(final Sardine sardine, String url) {
        long failedRecordsCount = 0;
        if (this.isInterruptRequested()) {
            return failedRecordsCount;
        }
        if (!url.endsWith("/")) {
            url += "/";
        }
        List<DavResource> resources;
        try {
            resources = sardine.list(url);
        } catch (final IOException e) {
            this.logger.error("Error in Webdav Ingest " + this.ingest.getName()
                    + " (getting resources)", e);

            this.saveException(e, IngestReportErrorType.SYSTEM_ERROR);
            return failedRecordsCount;
        }

        for (final DavResource res : resources) {
            if (this.isInterruptRequested()) {
                return failedRecordsCount;
            }
            if (res.isDirectory()) {
                // If it's not the current folder, process the files inside
                if (!url.endsWith(res.getPath())) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Processing webdav folder resource: "
                                + res.toString());
                    }
                    try {
                        final String absoluteHrefPath = this
                                .getResourceAbsoluteUrl(res, url);
                        failedRecordsCount += this.processWebdavFolder(sardine,
                                absoluteHrefPath);

                    } catch (final MalformedURLException e) {
                        this.logger.error("Error in Webdav Ingest: "
                                + this.ingest.getName() + " (malformed url)",
                                e);
                        this.saveException(e,
                                IngestReportErrorType.SYSTEM_ERROR);
                    }
                }
            } else {
                if (this.hasToProcessFile(res)) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Processing webdav resource: "
                                + res.toString());
                    }
                    failedRecordsCount += this.processFile(res, url);
                } else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug(
                                "Ignoring webdav resource: " + res.toString());
                    }
                }
            }
        }
        return failedRecordsCount;
    }
}
