package org.opengeoportal.harvester.api.component.csw;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.DOMOutputter;
import org.opengeoportal.harvester.api.client.csw.*;
import org.opengeoportal.harvester.api.client.csw.request.*;
import org.opengeoportal.harvester.api.client.csw.response.GetRecordsResponse;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestCsw;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that do the ingest retrieving the medatada from a remote CSW server and
 * storing it in the local server.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
public class CswIngestJob extends BaseIngestJob {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void ingest() {
		try {
			CswClient cswClient = new CswClient(ingest.getUrl());
			IngestCsw ingestCsw = (IngestCsw) ingest;

			GetRecordsRequest request = cswClient.setupGetRecordsRequest(
					ingestCsw.getCqlConstraint(),
					ingestCsw.getFilterConstraint());

			int start = 1;
			boolean processFinished = false;

			while (!processFinished) {
				request.setStartPosition(start + "");

				GetRecordsResponse response = cswClient.getRecords(request,
						start, cswClient.GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE);

				for (Element record : response.getResults()) {
					Document doc = new Document((Element) record.clone());

					DOMOutputter domOutputter = new DOMOutputter();
					org.w3c.dom.Document document = domOutputter.output(doc);

					MetadataParser parser = parserProvider
							.getMetadataParser(document);
					MetadataParserResponse parserResult = parser
							.parse(document);

					Metadata metadata = parserResult.getMetadata();
					metadata.setInstitution(ingest.getNameOgpRepository());

					boolean valid = metadataValidator
							.validate(metadata, report);
					if (valid) {
						metadataIngester.ingest(metadata);
					}
				}

				// --- check to see if we have to perform other searches
				int recCount = response.getNumberOfRecordsMatched();

				processFinished = (start
						+ cswClient.GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE > recCount);

				start += cswClient.GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE;
			}

		} catch (Exception ex) {
			// TODO: Add error in report
			ex.printStackTrace();
		}
	}
}
