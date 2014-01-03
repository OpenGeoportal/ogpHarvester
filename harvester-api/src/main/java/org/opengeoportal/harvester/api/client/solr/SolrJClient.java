package org.opengeoportal.harvester.api.client.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.opengeoportal.harvester.api.exception.OgpSorlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Class for retrieve info from a remote Solr instance.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class SolrJClient implements SolrClient {

	/**
	 * 
	 */
	private static final String INSTITUTIONS_SORT_FIELD = "InstitutionSort";

	// private String solrUrl;
	private HttpSolrServer solrServer;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public SolrJClient(String solrUrl) {
		HttpSolrServer solr = new HttpSolrServer(solrUrl);
		this.solrServer = solr;
	}

	public HttpSolrServer getSolrServer() {
		return solrServer;
	}

	public Boolean commit() {
		try {
			UpdateResponse updateResponse = solrServer.commit();
			return successResponse(updateResponse);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			logger.error("Error in Sorl commit", e);

			return false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOExcpetion when performing Solr commit operation", e);
			return false;

		}

	}

	private Boolean successResponse(UpdateResponse updateResponse) {
		if (updateResponse.getStatus() == HttpStatus.SC_OK) {
			return true;
		} else {
			return false;
		}
	}

	public String delete(String[] layerIds) throws Exception {
		// List<String> ids = Arrays.asList(layerIds);
		String query = "";
		for (String layerId : layerIds) {
			query += "LayerId:" + layerId.trim();
			query += " OR ";
		}
		if (query.length() > 0) {
			query = query.substring(0, query.lastIndexOf(" OR "));
		}
		// logger.info(query);
		UpdateResponse updateResponse = solrServer.deleteByQuery(query);
		// UpdateResponse updateResponse = solrServer.deleteById(ids);
		if (successResponse(updateResponse)) {
			return updateResponse.toString();
		}
		return "";
	}

	public Boolean verifyIngest(String layerId) throws Exception {
		SolrQuery query = new SolrQuery();
		query.setQuery("LayerId:" + layerId);
		QueryResponse queryResponse = solrServer.query(query);
		int numFound = Integer.getInteger(queryResponse.getResponseHeader()
				.get("numFound").toString());
		if (numFound == 1) {
			return true;
		} else {
			if (numFound > 1) {
				throw new Exception("There is more than 1 layer with LayerId:"
						+ layerId);
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
			logger.error("IO Exception trying to add Bean", e);
		} catch (SolrServerException e) {
			logger.error("SolrServer Exception trying to add Bean", e);
		} catch (SolrException e) {
			logger.error(
					"SolrException: SolrRecord values ="
							+ solrRecord.toString(), e);
		} catch (Exception e) {
			logger.error("Unknown Exception trying to add Bean", e);
		}
		logger.info("committing add to Solr");
		commit();
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.client.solr.SolrClient#getInstitutions()
	 */
	@Override
	public List<String> getInstitutions() {
		SolrQuery query = new SolrQuery()
				.addTermsField(INSTITUTIONS_SORT_FIELD)
				.setRequestHandler("/terms").setTerms(true);
		Set<String> institutionsSet = Sets.newTreeSet();
		try {
			QueryResponse response = solrServer.query(query);
			TermsResponse termsResponse = response.getTermsResponse();
			List<Term> termList = termsResponse
					.getTerms(INSTITUTIONS_SORT_FIELD);
			for (Term term : termList) {
				institutionsSet.add(term.getTerm());
			}

		} catch (SolrServerException e) {
			logger.error("Error getting Solr institutions list", e);
			throw new OgpSorlException("Error getting Solr institutions list",
					e);

		}
		return new ArrayList<String>(institutionsSet);
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opengeoportal.harvester.api.client.solr.SolrClient#search(org.
	 * opengeoportal.harvester.api.client.solr.SolrSearchParams)
	 */
	@Override
	public QueryResponse search(SolrSearchParams params) {
		SolrQuery query = params.toSolrQuery();
		QueryResponse response = null;
		try {
			response = solrServer.query(query);
		} catch (SolrServerException e) {
			logger.error(
					"Error getting Solr records for this query: "
							+ query.getQuery(), e);
			throw new OgpSorlException(
					"Error getting Solr records for this query: "
							+ query.getQuery(), e);
		}
		return response;
	}
}
