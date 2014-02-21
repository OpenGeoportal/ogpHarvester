package org.opengeoportal.harvester.api.component.ogp;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.client.solr.SolrSearchParams;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.OgpMetadataParser;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sardine.model.Response;
import com.google.common.collect.Lists;

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
			String url = ingest.getUrl();
			if (StringUtils.isBlank(url)) {
				CustomRepository cRepository = ingest.getRepository();
				if (cRepository != null
						&& cRepository.getServiceType() == InstanceType.SOLR) {
					url = cRepository.getUrl();
				} else {
					throw new SchedulerException(
							"Ingest "
									+ ingest.getId()
									+ " has not a valid URL or valid SOLR repository associated");
				}
			}

			SolrClient client = new SolrJClient(url);
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
				for (int i = 0; i < records.size(); i++) {
					SolrDocument record = searchResponse.getResults().get(i);
					SolrInputDocument inputDocument = ClientUtils
							.toSolrInputDocument(record);
					String xmlString = ClientUtils.toXML(inputDocument);
					records.get(i).setOriginalXmlMetadata(xmlString);

				}
				if (records.size() > 0) {
					OgpMetadataParser parser = new OgpMetadataParser();
					List<Metadata> metadataList = Lists
							.newArrayListWithCapacity(records.size());
					for (SolrRecord record : records) {
						MetadataParserResponse parseResult = parser
								.parse(record);
						Metadata metadata = parseResult.getMetadata();
						boolean valid = metadataValidator.validate(metadata,
								getIngestReport());
						if (valid) {
							metadataList.add(metadata);
						}
					}
					metadataIngester.ingest(metadataList, getIngestReport());

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
