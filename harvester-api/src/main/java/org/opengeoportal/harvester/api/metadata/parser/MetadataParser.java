package org.opengeoportal.harvester.api.metadata.parser;

import org.w3c.dom.Document;

public interface MetadataParser {
    MetadataParserResponse parse(Document document);
}
