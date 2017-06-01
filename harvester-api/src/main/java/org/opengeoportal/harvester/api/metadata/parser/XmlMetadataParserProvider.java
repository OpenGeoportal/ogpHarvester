package org.opengeoportal.harvester.api.metadata.parser;

import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.w3c.dom.Document;

public class XmlMetadataParserProvider implements MetadataParserProvider {

    @Override
    public MetadataParser getMetadataParser(final Document document)
            throws UnsupportedMetadataType {
        final MetadataType metadataType = XmlUtil.getMetadataType(document);

        if (metadataType.equals(MetadataType.ISO_19139)) {
            return new Iso19139MetadataParser();
        } else {
            return new FgdcMetadataParser();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.metadata.parser.MetadataParserProvider
     * #getMetadataParser()
     */
    @Override
    public BaseMetadataParser getMetadataParser(final SolrRecord record) {
        return new OgpMetadataParser();
    }
}
