package org.opengeoportal.harvester.api.metadata.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengeoportal.harvester.api.AbstractParserTest;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.metadata.model.GeometryType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbarne02 on 6/14/17.
 */

public class OgpMetadataParserDataTypeTest extends AbstractParserTest {

    private String jsonFile = "org/opengeoportal/harvester/api/metadata/parser/ogp_scannedmap.json";

    @Override
    public String getJsonFile() {
        return jsonFile;
    }


    /**
     * Checks to see that the DataType is parsed correctly by the OgpMetadataParser
     *
     * @param record
     * @param solrVal
     * @param expectedGeometry
     */
    public void testDataTypeParsing(SolrRecord record, String solrVal, GeometryType expectedGeometry) {
        Assert.assertEquals("String doesn't match.", solrVal, record.getDataType());
        MetadataParserResponse metadataWrapper = getParser().parse(record);
        Metadata metadata = metadataWrapper.getMetadata();
        GeometryType tested = metadata.getGeometryType();
        Assert.assertEquals("GeometryType from metadata parser doesn't match.", expectedGeometry, tested);

    }

    /**
     * Checks to see that the DataType is parsed correctly by GeometryType.parseGeometryType
     *
     * @param record
     * @param solrVal
     * @param expectedGeometry
     */
    public void testGeometryTypeParse(SolrRecord record, String solrVal, GeometryType expectedGeometry) {
        Assert.assertEquals("String doesn't match.", solrVal, record.getDataType());
        GeometryType tested = GeometryType.parseGeometryType(record.getDataType());
        Assert.assertEquals("GeometryType parse doesn't match.", expectedGeometry, tested);
    }

    @Test
    public void testMdParserDataTypePaperMap() throws Exception {
        SolrRecord record1 = getRecordList().get(0);
        testDataTypeParsing(record1, "Paper Map", GeometryType.PaperMap);
    }

    @Test
    public void testGeometryTypeParsePaperMap() throws Exception {
        SolrRecord record1 = getRecordList().get(0);
        testGeometryTypeParse(record1, "Paper Map", GeometryType.PaperMap);
    }

    @Test
    public void testMdParserDataTypeScannedMap() throws Exception {

        SolrRecord record3 = getRecordList().get(2);
        testDataTypeParsing(record3, "ScannedMap", GeometryType.ScannedMap);

    }

    @Test
    public void testGeometryTypeParseScannedMap() throws Exception {

        SolrRecord record3 = getRecordList().get(2);
        testGeometryTypeParse(record3, "ScannedMap", GeometryType.ScannedMap);
    }

    @Test
    public void testMdParserDataTypeScannedMapWithSpace() throws Exception {

        SolrRecord record3 = getRecordList().get(1);
        testDataTypeParsing(record3, "Scanned Map", GeometryType.ScannedMap);

    }

    @Test
    public void testGeometryTypeParseScannedMapWithSpace() throws Exception {

        SolrRecord record3 = getRecordList().get(1);
        testGeometryTypeParse(record3, "Scanned Map", GeometryType.ScannedMap);
    }

}