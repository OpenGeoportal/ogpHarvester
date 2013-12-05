package org.opengeoportal.harvester.api.client.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SolrJClient implements SolrClient{
	
	//private String solrUrl;
	private HttpSolrServer solrServer;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public SolrJClient(String solrUrl) {
		HttpSolrServer solr = new HttpSolrServer(solrUrl);
		this.solrServer = solr;
	}
	
	public HttpSolrServer getSolrServer(){
		return solrServer;
	}

	public Boolean commit() {
		try {
			UpdateResponse updateResponse = solrServer.commit();
			return successResponse(updateResponse);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}

	}

	private Boolean successResponse(UpdateResponse updateResponse){
		if (updateResponse.getStatus() == 200){
			return true;
		} else {
			return false;
		}
	}
	
	public String delete(String[] layerIds) throws Exception {
		//List<String> ids = Arrays.asList(layerIds);
		String query = "";
		for (String layerId : layerIds){
			query += "LayerId:" + layerId.trim();
			query += " OR ";
		}
		if (query.length() > 0){
			query = query.substring(0, query.lastIndexOf(" OR "));
		}
		//logger.info(query);
		UpdateResponse updateResponse = solrServer.deleteByQuery(query);
		//UpdateResponse updateResponse = solrServer.deleteById(ids);
		if (successResponse(updateResponse)){
			return updateResponse.toString();
		}
		return "";
	}

	public Boolean verifyIngest(String layerId) throws Exception {
		SolrQuery query = new SolrQuery();
	    query.setQuery("LayerId:" + layerId);
		QueryResponse queryResponse = solrServer.query(query);
		int numFound = Integer.getInteger(queryResponse.getResponseHeader().get("numFound").toString());
		if (numFound == 1){
			return true;
		} else {
			if (numFound > 1){
				throw new Exception("There is more than 1 layer with LayerId:" + layerId);
			} else {
				return false;
			}
		}
	}

	public int add(SolrRecord solrRecord) {
		int status = 0;
		UpdateResponse updateResponse = null;
		try {
			logger.debug("begin adding solr record");
			updateResponse = solrServer.addBean(solrRecord);
			status = updateResponse.getStatus();
			logger.debug("Status code: " + Integer.toString(status));

		} catch (IOException e) {
			logger.error("IO Exception trying to add Bean");
			e.printStackTrace();
		} catch (SolrServerException e) {
			logger.error("SolrServer Exception trying to add Bean");
			e.printStackTrace();
		} catch (SolrException e){
			logger.error("SolrException: SolrRecord values =" + solrRecord.toString());
		} catch (Exception e){
			logger.error("Unknown Exception trying to add Bean");
			e.printStackTrace();
		}
		logger.info("committing add to Solr");
		commit();
		return status;
	};
}
