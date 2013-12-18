package org.opengeoportal.harvester.api.client.solr;

import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

public interface SolrClient {
	public HttpSolrServer getSolrServer();

	public Boolean commit();

	public String delete(String[] layerIds) throws Exception;

	public Boolean verifyIngest(String layerId) throws Exception;

	public int add(SolrRecord solrRecord);

	/**
	 * Query the server for the institutions with metadata.
	 * 
	 * @return the list of institutions with metadata indexed in the server.
	 */
	public List<String> getInstitutions();

}
