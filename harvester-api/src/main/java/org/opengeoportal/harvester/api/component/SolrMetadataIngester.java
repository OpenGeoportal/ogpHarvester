package org.opengeoportal.harvester.api.component;


import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.metadata.model.Metadata;

public class SolrMetadataIngester implements MetadataIngester {
    private SolrClient solrClient;

    public SolrMetadataIngester(String solrUrl) {
        this.solrClient = new SolrJClient(solrUrl);

    }

    @Override
    public void ingest(Metadata metadata) {
        SolrRecord solrRecord =  SolrRecord.build(metadata);

        solrClient.add(solrRecord);
    }
}
