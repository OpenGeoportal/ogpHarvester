package org.opengeoportal.harvester.api.client.solr;

import org.junit.Assert;
import org.junit.Test;
import org.opengeoportal.harvester.api.AbstractParserTest;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.OgpMetadataParser;

/**
 * Created by cbarne02 on 6/14/17.
 */

public class SolrRecordBuildDataTypeTest extends AbstractParserTest {

    private String jsonFile = "org/opengeoportal/harvester/api/metadata/parser/ogp_scannedmap.json";

    @Override
    public String getJsonFile() {
        return jsonFile;
    }

    /**
     * Checks to see that the DataType is parsed correctly by the OgpMetadataParser
     *
     * @param record
     * @param inputVal
     * @param toSolrVal
     */
    public void testDataTypeParsing(SolrRecord record, String inputVal, String toSolrVal) {
        Assert.assertEquals("Input String doesn't match.", inputVal, record.getDataType());
        MetadataParserResponse metadataWrapper = getParser().parse(record);
        Metadata metadata = metadataWrapper.getMetadata();
        SolrRecord toSolr = SolrRecord.build(metadata);
        String tested = toSolr.getDataType();
        Assert.assertEquals("DataType from SolrRecord.build doesn't match.", toSolrVal, tested);

    }


    @Test
    public void testMdParserDataTypePaperMap() throws Exception {
        SolrRecord record1 = getRecordList().get(0);
        testDataTypeParsing(record1, "Paper Map", "Paper Map");
    }

    @Test
    public void testMdParserDataTypeScannedMap() throws Exception {

        SolrRecord record3 = getRecordList().get(2);
        testDataTypeParsing(record3, "ScannedMap", "Scanned Map");

    }


    @Test
    public void testMdParserDataTypeScannedMapWithSpace() throws Exception {

        SolrRecord record3 = getRecordList().get(1);
        testDataTypeParsing(record3, "Scanned Map", "Scanned Map");

    }


}