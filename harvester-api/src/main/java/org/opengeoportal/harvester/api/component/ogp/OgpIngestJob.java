package org.opengeoportal.harvester.api.component.ogp;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.client.solr.SolrSearchParams;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.OgpMetadataParser;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import org.opengeoportal.harvester.api.domain.IngestReport;

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
            IngestReport currentReport = getIngestReport();
            String url = ingest.getActualUrl();
            if (StringUtils.isBlank(url)) {
                throw new SchedulerException("Ingest " + ingest.getId()
                        + " has not a valid URL or valid repository"
                        + " associated");
            }

            SolrClient client = new SolrJClient(url);
            SolrSearchParams searchParams = new SolrSearchParams(
                    (IngestOGP) ingest);
            if (logger.isInfoEnabled()) {
                logger.info("OgpIngestJob: search parameters "
                        + searchParams.toString());
            }

            while (!isInterruptRequested() && !processFinished) {
                searchParams.setPage(startPage);
                QueryResponse searchResponse = client.search(searchParams);

                List<SolrRecord> records = searchResponse
                        .getBeans(SolrRecord.class);
                for (SolrRecord record : records) {
                    record.setOriginalXmlMetadata(record.getFgdcText());
                }
                if (records.size() > 0) {
                    OgpMetadataParser parser = new OgpMetadataParser();
                    List<Metadata> metadataList = Lists
                            .newArrayListWithCapacity(records.size());
                    for (SolrRecord record : records) {
                        try {
                            MetadataParserResponse parseResult = parser
                                    .parse(record);
                            Metadata metadata = parseResult.getMetadata();
                            boolean valid = metadataValidator.validate(
                                    metadata, currentReport);
                            if (valid) {
                                metadataList.add(metadata);
                            } else {
                                failedRecordsCount++;
                            }
                        } catch (Exception e) {
                            failedRecordsCount++;
                            saveException(e,
                                    IngestReportErrorType.SYSTEM_ERROR);
                        }
                    }
                    currentReport.setFailedRecordsCount(failedRecordsCount);
                    metadataIngester.ingest(metadataList, currentReport);

                } else {
                    processFinished = true;
                }

                startPage++;

            }
        } catch (Exception e) {
            logger.error("Error in OGP Ingest: " + this.ingest.getName(), e);
            saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        }
    }
}
