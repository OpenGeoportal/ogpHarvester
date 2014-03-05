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
	/** Logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void ingest() {
		Sardine sardine = null;
		try {
			sardine = SardineFactory.begin();
			processWebdavFolder(sardine, ingest.getActualUrl());
		} catch (Exception e) {
			saveException(e, IngestReportErrorType.SYSTEM_ERROR);
		} finally {
			if (sardine != null) {
				sardine.shutdown();
			}
		}
	}

	/**
	 * Process the metadata files in the webdav server, recursing the
	 * subfolders.
	 * 
	 * @param sardine
	 *            {@link Sardine} instance.
	 * @param url
	 *            WebDAV folder URL.
	 */
	private void processWebdavFolder(Sardine sardine, String url) {
		if (isInterruptRequested()) {
			return;
		}
		if (!url.endsWith("/")) {
			url += "/";
		}
		List<DavResource> resources = null;
		try {
			resources = sardine.list(url);

		} catch (IOException e) {
			logger.error("Error in Webdav Ingest " + this.ingest.getName()
					+ " (getting resources)", e);

			saveException(e, IngestReportErrorType.WEB_SERVICE_ERROR);
			return;
		}

		for (DavResource res : resources) {
			if (isInterruptRequested()) {
				return;
			}
			if (res.isDirectory()) {
				// If it's not the current folder, process the files inside
				if (!url.endsWith(res.getPath())) {
					if (logger.isDebugEnabled()) {
						logger.debug("Processing webdav folder resource: "
								+ res.toString());
					}
					try {
						String absoluteHrefPath = getResourceAbsoluteUrl(res,
								url);
						processWebdavFolder(sardine, absoluteHrefPath);

					} catch (MalformedURLException e) {
						logger.error(
								"Error in Webdav Ingest: "
										+ this.ingest.getName()
										+ " (malformed url)", e);
						saveException(e,
								IngestReportErrorType.WEB_SERVICE_ERROR);
					}
				}
			} else {
				if (hasToProcessFile(res)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Processing webdav resource: "
								+ res.toString());
					}
					processFile(res, url);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Ignoring webdav resource: "
								+ res.toString());
					}
				}
			}
		}
	}

	/**
	 * Process the remote WebDav file, validating it and ingesting in the
	 * system.
	 * 
	 * @param res
	 *            WebDav resource.
	 * @param baseUrl
	 *            Base url of the WebDav resource.
	 */
	private void processFile(DavResource res, String baseUrl) {
		try {
			// Retrieve file content
			String absoluteHrefPath = getResourceAbsoluteUrl(res, baseUrl);

			Document document = XmlUtil.load(absoluteHrefPath);
			MetadataParser parser = parserProvider.getMetadataParser(document);
			MetadataParserResponse parserResult = parser.parse(document);

			Metadata metadata = parserResult.getMetadata();
			metadata.setInstitution(ingest.getNameOgpRepository());

			boolean valid = metadataValidator.validate(metadata, report);
			if (valid) {
				metadataIngester.ingest(ImmutableList.of(metadata),
						getIngestReport());
			}

		} catch (Exception e) {
			logger.error("Error in Webdav Ingest: " + this.ingest.getName()
					+ " (processing file:" + baseUrl + ")", e);
			saveException(e, IngestReportErrorType.SYSTEM_ERROR);
		}
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
	private boolean hasToProcessFile(DavResource res) {
		if (res.isDirectory()) {
			return false;
		}
		if (!res.getContentType().equalsIgnoreCase("application/xml")) {
			return false;
		}

		IngestWebDav ingestWebdav = (IngestWebDav) ingest;
		Date beginFilterDate = ingestWebdav.getDateFrom();
		Date endFilterDate = ingestWebdav.getDateTo();
		Date resourceDate = res.getModified();

		if ((beginFilterDate != null) && (resourceDate.before(beginFilterDate))) {
			return false;
		}
		if ((endFilterDate != null) && (resourceDate.after(endFilterDate))) {
			return false;
		}

		return true;
	}

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
	private String getResourceAbsoluteUrl(DavResource res, String baseUrl)
			throws MalformedURLException {
		URL urlR = new URL(baseUrl);

		return urlR + res.getHref().toString().replace(urlR.getPath(), "");
	}
}
