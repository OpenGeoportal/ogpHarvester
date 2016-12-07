package org.opengeoportal.harvester.api.client.solr;

import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.opengeoportal.harvester.api.domain.IngestReport;

public interface SolrClient {
	HttpSolrServer getSolrServer();

	Boolean commit();

	String delete(String[] layerIds) throws Exception;

	Boolean verifyIngest(String layerId) throws Exception;

	int add(SolrRecord solrRecord);

	/**
	 * Query the server for the institutions with metadata.
	 * 
	 * @return the list of institutions with metadata indexed in the server.
	 */
	List<String> getInstitutions();
	
	QueryResponse search(SolrSearchParams params);

	/**
	 * @param records
	 * @param report 
	 * @return
	 */
	boolean add(Collection<SolrRecord> records, IngestReport report);

}
