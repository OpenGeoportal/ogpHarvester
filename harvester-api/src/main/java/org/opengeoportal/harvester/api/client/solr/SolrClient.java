package org.opengeoportal.harvester.api.client.solr;

import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.opengeoportal.harvester.api.domain.IngestReport;

public interface SolrClient {
    /**
     * @param records
     * @param report
     * @return
     */
    boolean add(Collection<SolrRecord> records, IngestReport report);

    int add(SolrRecord solrRecord);

    Boolean commit();

    String delete(String[] layerIds) throws Exception;

    /**
     * Query the server for the institutions with metadata.
     * 
     * @return the list of institutions with metadata indexed in the server.
     */
    List<String> getInstitutions();

    HttpSolrServer getSolrServer();

    QueryResponse search(SolrSearchParams params);

    Boolean verifyIngest(String layerId) throws Exception;

}
