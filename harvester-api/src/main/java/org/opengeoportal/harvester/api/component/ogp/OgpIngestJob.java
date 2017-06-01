package org.opengeoportal.harvester.api.component.ogp;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.client.solr.SolrSearchParams;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.OgpMetadataParser;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class OgpIngestJob extends BaseIngestJob {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void ingest() {
        try {
            boolean processFinished = false;
            int startPage = 0;
            long failedRecordsCount = 0;
            final IngestReport currentReport = this.getIngestReport();
            final String url = this.ingest.getActualUrl();
            if (StringUtils.isBlank(url)) {
                throw new SchedulerException("Ingest " + this.ingest.getId()
                        + " has not a valid URL or valid repository"
                        + " associated");
            }

            final SolrClient client = new SolrJClient(url);
            final SolrSearchParams searchParams = new SolrSearchParams(
                    (IngestOGP) this.ingest);
            if (this.logger.isInfoEnabled()) {
                this.logger.info("OgpIngestJob: search parameters "
                        + searchParams.toString());
            }

            while (!this.isInterruptRequested() && !processFinished) {
                searchParams.setPage(startPage);
                final QueryResponse searchResponse = client
                        .search(searchParams);

                final List<SolrRecord> records = searchResponse
                        .getBeans(SolrRecord.class);
                for (final SolrRecord record : records) {
                    record.setOriginalXmlMetadata(record.getFgdcText());
                }
                if (records.size() > 0) {
                    final OgpMetadataParser parser = new OgpMetadataParser();
                    final List<Metadata> metadataList = Lists
                            .newArrayListWithCapacity(records.size());
                    for (final SolrRecord record : records) {
                        try {
                            final MetadataParserResponse parseResult = parser
                                    .parse(record);
                            final Metadata metadata = parseResult.getMetadata();
                            final boolean valid = this.metadataValidator
                                    .validate(metadata, currentReport);
                            if (valid) {
                                metadataList.add(metadata);
                            } else {
                                failedRecordsCount++;
                            }
                        } catch (final Exception e) {
                            failedRecordsCount++;
                            this.saveException(e,
                                    IngestReportErrorType.SYSTEM_ERROR, record);
                        }
                    }
                    currentReport.setFailedRecordsCount(failedRecordsCount);
                    this.metadataIngester.ingest(metadataList, currentReport);

                } else {
                    processFinished = true;
                }

                startPage++;

            }
        } catch (final Exception e) {
            this.logger.error("Error in OGP Ingest: " + this.ingest.getName(),
                    e);
            this.saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        }
    }
}
