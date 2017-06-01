package org.opengeoportal.harvester.api.client.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportError;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.exception.OgpSolrException;
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
    private final HttpSolrServer solrServer;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SolrJClient(final String solrUrl) {
        final HttpSolrServer solr = new HttpSolrServer(solrUrl);
        this.solrServer = solr;
    }

    @Override
    public boolean add(final Collection<SolrRecord> records,
            final IngestReport report) {
        boolean result = false;
        UpdateResponse updateResponse = null;
        String errMessage = "";

        try {
            this.logger.debug("Begin adding solr record batch");
            updateResponse = this.solrServer.addBeans(records);
            result = true;
            this.logger.debug("Status code: " + updateResponse.getStatus());
        } catch (final IOException e) {
            errMessage = "IO Exception trying to add a bean collection to Solr.";
            this.logger.error(errMessage, e);

        } catch (final SolrServerException e) {
            errMessage = "SolrServer Exception trying to add a bean collection";
            this.logger.error(errMessage, e);
        } catch (final SolrException e) {
            errMessage = "SolrException: ";
            this.logger.error(errMessage, e);
        } catch (final Exception e) {
            errMessage = "Unknown Exception trying to add Bean to Solr.";
            this.logger.error(errMessage, e);
        }

        if (!result) {
            final IngestReportError ire = new IngestReportError();
            ire.setType(IngestReportErrorType.SYSTEM_ERROR);
            ire.setMessage(errMessage);
            report.addError(ire);
        } else {
            this.logger.info("committing add to Solr");
            this.commit();
        }

        return result;

    }

    @Override
    public int add(final SolrRecord solrRecord) {
        int status = 0;
        UpdateResponse updateResponse = null;
        try {
            this.logger.debug("begin adding solr record");
            updateResponse = this.solrServer.addBean(solrRecord);
            status = updateResponse.getStatus();
            this.logger.debug("Status code: " + Integer.toString(status));

        } catch (final IOException e) {
            this.logger.error("IO Exception trying to add Bean", e);
        } catch (final SolrServerException e) {
            this.logger.error("SolrServer Exception trying to add Bean", e);
        } catch (final SolrException e) {
            this.logger.error("SolrException: SolrRecord values ="
                    + solrRecord.toString(), e);
        } catch (final Exception e) {
            this.logger.error("Unknown Exception trying to add Bean", e);
        }
        this.logger.info("committing add to Solr");
        this.commit();
        return status;
    }

    @Override
    public Boolean commit() {
        try {
            final UpdateResponse updateResponse = this.solrServer.commit();
            return this.successResponse(updateResponse);
        } catch (final SolrServerException e) {
            this.logger.error("Error in Solr commit", e);

            return false;

        } catch (final IOException e) {
            this.logger.error(
                    "IOExcpetion when performing Solr commit operation", e);
            return false;

        }

    }

    @Override
    public String delete(final String[] layerIds) throws Exception {
        // List<String> ids = Arrays.asList(layerIds);
        String query = "";
        for (final String layerId : layerIds) {
            query += "LayerId:" + layerId.trim();
            query += " OR ";
        }
        if (query.length() > 0) {
            query = query.substring(0, query.lastIndexOf(" OR "));
        }
        // logger.info(query);
        final UpdateResponse updateResponse = this.solrServer
                .deleteByQuery(query);
        // UpdateResponse updateResponse = solrServer.deleteById(ids);
        if (this.successResponse(updateResponse)) {
            return updateResponse.toString();
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.client.solr.SolrClient#getInstitutions()
     */
    @Override
    public List<String> getInstitutions() {
        final SolrQuery query = new SolrQuery()
                .addTermsField(SolrJClient.INSTITUTIONS_SORT_FIELD)
                .setRequestHandler("/terms").setTerms(true);
        final Set<String> institutionsSet = Sets.newTreeSet();
        try {
            final QueryResponse response = this.solrServer.query(query);
            final TermsResponse termsResponse = response.getTermsResponse();
            final List<Term> termList = termsResponse
                    .getTerms(SolrJClient.INSTITUTIONS_SORT_FIELD);
            for (final Term term : termList) {
                institutionsSet.add(term.getTerm());
            }

        } catch (final SolrServerException e) {
            this.logger.error("Error getting Solr institutions list", e);
            throw new OgpSolrException("Error getting Solr institutions list",
                    e);

        }
        return new ArrayList<String>(institutionsSet);
    }

    @Override
    public HttpSolrServer getSolrServer() {
        return this.solrServer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.client.solr.SolrClient#search(org.
     * opengeoportal.harvester.api.client.solr.SolrSearchParams)
     */
    @Override
    public QueryResponse search(final SolrSearchParams params) {
        final SolrQuery query = params.toSolrQuery();
        QueryResponse response = null;
        try {
            response = this.solrServer.query(query);
        } catch (final SolrServerException e) {
            this.logger.error("Error getting Solr records for this query: "
                    + query.getQuery(), e);
            throw new OgpSolrException(
                    "Error getting Solr records for this query: "
                            + query.getQuery(),
                    e);
        }
        return response;
    }

    private Boolean successResponse(final UpdateResponse updateResponse) {
        if (updateResponse.getStatus() == HttpStatus.SC_OK) {
            return true;
        } else {
            return false;
        }
    };

    @Override
    public Boolean verifyIngest(final String layerId) throws Exception {
        final SolrQuery query = new SolrQuery();
        query.setQuery("LayerId:" + layerId);
        final QueryResponse queryResponse = this.solrServer.query(query);
        final int numFound = Integer.getInteger(
                queryResponse.getResponseHeader().get("numFound").toString());
        if (numFound == 1) {
            return true;
        } else {
            if (numFound > 1) {
                throw new Exception(
                        "There is more than 1 layer with LayerId:" + layerId);
            } else {
                return false;
            }
        }
    }

}
