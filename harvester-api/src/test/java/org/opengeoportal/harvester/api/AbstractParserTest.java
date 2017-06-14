package org.opengeoportal.harvester.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.metadata.parser.OgpMetadataParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbarne02 on 6/14/17.
 */
public abstract class AbstractParserTest {

    protected OgpMetadataParser getParser() {
        return parser;
    }

    private OgpMetadataParser parser = new OgpMetadataParser();


    private ObjectMapper mapper = new ObjectMapper();

    private List<SolrRecord> recordList = new ArrayList();

    protected List<SolrRecord> getRecordList() {
        return recordList;
    }

    public abstract String getJsonFile();

    @Before
    public void setup() throws IOException {
        recordList = readSolrDocuments(getJsonFile());
    }


    protected List<SolrRecord> readSolrDocuments(String jsonFile) throws IOException {
        Resource resource = new ClassPathResource(jsonFile);
        List<SolrRecord> records = mapper.readValue(resource.getInputStream(), new TypeReference<List<SolrRecord>>() {
        });
        return records;
    }
}
