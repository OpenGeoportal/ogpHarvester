package org.opengeoportal.harvester.api.client.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

public interface SolrClient {
	public HttpSolrServer getSolrServer();
	public Boolean commit();
	public String delete(String[] layerIds) throws Exception;
	public Boolean verifyIngest(String layerId) throws Exception;
	public int add(SolrRecord solrRecord);

}
