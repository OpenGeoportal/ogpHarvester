package org.opengeoportal.harvester.api.component;

import java.util.List;

import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.metadata.model.Metadata;

import com.google.common.collect.Lists;

public class SolrMetadataIngester implements MetadataIngester {
	private SolrClient solrClient;

	/**
	 * Create a new instance of SolrMetadataIngester.
	 * 
	 * @param solrUrl
	 *            destination server where metadata will be stored.
	 */
	public SolrMetadataIngester(String solrUrl) {
		this.solrClient = new SolrJClient(solrUrl);

	}

	@Override
	public void ingest(Metadata metadata) {
		SolrRecord solrRecord = SolrRecord.build(metadata);

		solrClient.add(solrRecord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opengeoportal.harvester.api.component.MetadataIngester#ingest(java
	 * .util.List)
	 */
	@Override
	public void ingest(List<Metadata> metadataList) {
		List<SolrRecord> solrRecordList = Lists
				.newArrayListWithCapacity(metadataList.size());
		if (metadataList.size() > 0) {
			for (Metadata metadata : metadataList) {
				solrRecordList.add(SolrRecord.build(metadata));
			}
			solrClient.add(solrRecordList);
		}
	}

}
