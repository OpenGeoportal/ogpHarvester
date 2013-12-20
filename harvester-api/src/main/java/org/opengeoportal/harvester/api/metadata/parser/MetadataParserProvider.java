package org.opengeoportal.harvester.api.metadata.parser;


import org.w3c.dom.Document;

public interface MetadataParserProvider {
    public MetadataParser getMetadataParser(Document document) throws Exception;
}
