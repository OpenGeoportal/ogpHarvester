package org.opengeoportal.harvester.api.component.ogp;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.client.solr.SolrSearchParams;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.OgpMetadataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class OgpIngestJob extends BaseIngestJob {
	/** Logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void ingest() {
		try {
			boolean processFinished = false;
			int startPage = 0;

			SolrClient client = new SolrJClient(ingest.getUrl());
			SolrSearchParams searchParams = new SolrSearchParams(
					(IngestOGP) ingest);
			if (logger.isInfoEnabled()) {
				logger.info("OgpIngestJob: search parameters "
						+ searchParams.toString());
			}

			while (!processFinished) {
				searchParams.setPage(startPage);
				QueryResponse searchResponse = client.search(searchParams);
				// long numFound = searchResponse.getResults().getNumFound();
				List<SolrRecord> records = searchResponse
						.getBeans(SolrRecord.class);
				if (records.size() > 0) {
					OgpMetadataParser parser = new OgpMetadataParser();
					for (SolrRecord record : records) {
						MetadataParserResponse parseResult = parser
								.parse(record);
						Metadata metadata = parseResult.getMetadata();
						boolean valid = metadataValidator.validate(metadata,
								report);
						if (valid) {
							metadataIngester.ingest(metadata);
						}

					}

				} else {
					processFinished = true;
				}

				startPage++;

			}
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Error getting remote records", e);
			}
			// LOG exception
		}

	}
}
