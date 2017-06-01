package org.opengeoportal.harvester.api.component;

import java.util.List;

import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.opengeoportal.harvester.api.metadata.model.GeometryType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;

import com.google.common.collect.Lists;

public class SolrMetadataIngester implements MetadataIngester {
    private final SolrClient solrClient;

    /**
     * Create a new instance of SolrMetadataIngester.
     * 
     * @param solrUrl
     *            destination server where metadata will be stored.
     */
    public SolrMetadataIngester(final String solrUrl) {
        this.solrClient = new SolrJClient(solrUrl);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.component.MetadataIngester#ingest(java
     * .util.List)
     */
    @Override
    public void ingest(final List<Metadata> metadataList,
            final IngestReport report) {
        final List<SolrRecord> solrRecordList = Lists
                .newArrayListWithCapacity(metadataList.size());
        if (metadataList.size() > 0) {
            long publicRecords = 0L;
            long restrictedRecords = 0L;
            long rasterRecords = 0L;
            long vectorRecords = 0L;

            for (final Metadata metadata : metadataList) {
                if (metadata.getAccess() == AccessLevel.Restricted) {
                    restrictedRecords++;
                } else if (metadata.getAccess() == AccessLevel.Public) {
                    publicRecords++;
                }

                if (GeometryType.isRaster(metadata.getGeometryType())) {
                    rasterRecords++;
                } else if (GeometryType.isVector(metadata.getGeometryType())) {
                    vectorRecords++;
                }

                solrRecordList.add(SolrRecord.build(metadata));
            }
            final boolean added = this.solrClient.add(solrRecordList, report);
            if (added) {
                report.setRestrictedRecords(
                        report.getRestrictedRecords() + restrictedRecords);
                report.setPublicRecords(
                        report.getPublicRecords() + publicRecords);
                report.setRasterRecords(
                        report.getRasterRecords() + rasterRecords);
                report.setVectorRecords(
                        report.getVectorRecords() + vectorRecords);
            }
        }
    }

    @Override
    public void ingest(final Metadata metadata) {
        final SolrRecord solrRecord = SolrRecord.build(metadata);

        this.solrClient.add(solrRecord);
    }

}
