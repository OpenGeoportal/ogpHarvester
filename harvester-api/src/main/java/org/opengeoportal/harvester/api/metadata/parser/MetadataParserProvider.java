package org.opengeoportal.harvester.api.metadata.parser;

import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.w3c.dom.Document;

/**
 * Interface implemented by Metadata Parser factory classes.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public interface MetadataParserProvider {
	/**
	 * Create a new MetadataParser based on the nature of the document.
	 * 
	 * @param document
	 *            DOM document.
	 * @return a new instance of MetadataParser.
	 * @throws Exception
	 *             if Metadata Type is not supported.
	 */
	MetadataParser getMetadataParser(Document document) throws Exception;

	/**
	 * Create a new MetadataParser capable of read Solr records.
	 * 
	 * @param record
	 *            a SolrRecord.
	 * @return a new MetadataParser instance.
	 */
	BaseMetadataParser getMetadataParser(SolrRecord record);
}
