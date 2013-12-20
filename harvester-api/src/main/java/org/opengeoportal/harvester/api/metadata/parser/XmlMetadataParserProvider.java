package org.opengeoportal.harvester.api.metadata.parser;

import org.opengeoportal.harvester.api.util.XmlUtil;
import org.w3c.dom.Document;

public class XmlMetadataParserProvider implements MetadataParserProvider {

    @Override
    public MetadataParser getMetadataParser(Document document) throws Exception {
        MetadataType metadataType = XmlUtil.getMetadataType(document);

        if (metadataType.equals(MetadataType.ISO_19139)) {
            return new Iso19139MetadataParser();
        } else {
            return new FgdcMetadataParser();
        }
    }
}
