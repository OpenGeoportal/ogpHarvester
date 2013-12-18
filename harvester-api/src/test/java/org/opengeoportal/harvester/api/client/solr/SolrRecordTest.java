package org.opengeoportal.harvester.api.client.solr;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SolrRecordTest {
    private InputStream in = null;

    @Before
    public void setup()
            throws IOException
    {
        in = getClass().getResourceAsStream("iso19139.xml");
    }

    @After
    public void teardown()
            throws IOException
    {
        if (in != null)
        {
            in.close();
        }

        in = null;
    }

    @Test
    public void testBuildFromMetadata() {
        try {
            Document doc = XmlUtil.load(in);
            Iso19139MetadataParser parser = new Iso19139MetadataParser();
            MetadataParserResponse response = parser.parse(doc);

            Metadata metadata = response.getMetadata();
            Assert.assertNotNull(metadata);

            SolrRecord solrRecord = SolrRecord.build(metadata);
            Assert.assertNotNull(solrRecord);

            Assert.assertEquals(metadata.getOwsName(), solrRecord.getName());
            Assert.assertEquals(metadata.getTitle(), solrRecord.getLayerDisplayName());
            Assert.assertEquals(metadata.getDescription(), solrRecord.getDescription());
            Assert.assertEquals(metadata.getPlaceKeywordsAsString(), solrRecord.getPlaceKeywords());
            Assert.assertEquals(metadata.getThemeKeywordsAsString(), solrRecord.getThemeKeywords());

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testGetInstitutions() {
    	SolrClient client = new SolrJClient("http://geodata.tufts.edu/solr");
    	List<String> expected = Lists.newArrayList("Berkeley", "Harvard", "MIT", "MassGIS", "Tufts");
    	List<String> result = client.getInstitutions();
    	
    	assertThat("Returned institution list does not contain the expected elements", result,
    			 equalTo(expected));
    	
    }
}
