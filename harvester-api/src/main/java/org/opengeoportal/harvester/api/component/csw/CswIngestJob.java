package org.opengeoportal.harvester.api.component.csw;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.opengeoportal.harvester.api.client.csw.CswClient;
import org.opengeoportal.harvester.api.client.csw.request.GetRecordsRequest;
import org.opengeoportal.harvester.api.client.csw.response.GetRecordsResponse;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestCsw;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Class that do the ingest retrieving the medatada from a remote CSW server and
 * storing it in the local server.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class CswIngestJob extends BaseIngestJob {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void ingest() {
        try {
            final CswClient cswClient = new CswClient(
                    this.ingest.getActualUrl());
            final IngestCsw ingestCsw = (IngestCsw) this.ingest;

            final GetRecordsRequest request = cswClient.setupGetRecordsRequest(
                    ingestCsw.getCqlConstraint(),
                    ingestCsw.getFilterConstraint());

            int start = 1;
            boolean processFinished = false;
            long failedRecordsCount = 0;

            while (!(this.isInterruptRequested() || processFinished)) {
                request.setStartPosition(start + "");

                final GetRecordsResponse response = cswClient.getRecords(
                        request, start,
                        CswClient.GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE);

                final List<Metadata> metadataList = Lists
                        .newArrayListWithCapacity(response.getResults().size());

                for (final Element record : response.getResults()) {
                    org.w3c.dom.Document document = null;
                    try {
                        final Document doc = new Document(
                                (Element) record.clone());

                        final DOMOutputter domOutputter = new DOMOutputter();
                        document = domOutputter.output(doc);

                        final MetadataParser parser = this.parserProvider
                                .getMetadataParser(document);
                        final MetadataParserResponse parserResult = parser
                                .parse(document);

                        final Metadata metadata = parserResult.getMetadata();
                        metadata.setInstitution(
                                this.ingest.getNameOgpRepository());

                        final boolean valid = this.metadataValidator
                                .validate(metadata, this.report);
                        if (valid) {
                            metadataList.add(metadata);
                        } else {
                            failedRecordsCount++;
                        }
                    } catch (final JDOMException e) {
                        failedRecordsCount++;
                        this.saveException(e,
                                IngestReportErrorType.WEB_SERVICE_ERROR,
                                document);
                    } catch (final UnsupportedMetadataType e) {
                        failedRecordsCount++;
                        this.saveException(e,
                                IngestReportErrorType.WEB_SERVICE_ERROR,
                                document);
                    } catch (final Exception e) {
                        failedRecordsCount++;
                        this.saveException(e,
                                IngestReportErrorType.SYSTEM_ERROR, document);
                    }
                }

                this.report.setFailedRecordsCount(failedRecordsCount);
                this.metadataIngester.ingest(metadataList, this.report);

                // --- check to see if we have to perform other searches
                final int recCount = response.getNumberOfRecordsMatched();

                processFinished = ((start
                        + CswClient.GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE) > recCount);

                start += CswClient.GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE;
            }

        } catch (final Exception e) {
            this.logger.error("Error in CSW Ingest: " + this.ingest.getName(),
                    e);
            this.saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        }
    }
}
