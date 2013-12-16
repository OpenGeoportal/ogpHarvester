package org.opengeoportal.harvester.api.metadata.parser;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

public class Iso19139MetadataParserTest {
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
    public void testParser() {
        try {
            Document doc = XmlUtil.load(in);
            Iso19139MetadataParser parser = new Iso19139MetadataParser();
            MetadataParserResponse response = parser.parse(doc);

            Metadata metadata = response.getMetadata();

            Assert.assertEquals("Test metadata", metadata.getTitle());
            Assert.assertEquals("Test metadata abstract", metadata.getDescription());
            Assert.assertEquals("32320C9D-F227-4027-8396-A6257B728424", metadata.getOwsName());
            Assert.assertEquals(AccessLevel.Public, metadata.getAccess());

            Assert.assertEquals(Boolean.TRUE, metadata.getBounds().isValid());
            Assert.assertEquals(new Double(51.757), metadata.getBounds().getMaxY());
            Assert.assertEquals(new Double(51.752), metadata.getBounds().getMinY());
            Assert.assertEquals(new Double(4.839), metadata.getBounds().getMaxX());
            Assert.assertEquals(new Double(4.816), metadata.getBounds().getMinX());

            Assert.assertEquals(1, metadata.getThemeKeywords().size());
            Assert.assertTrue(metadata.getThemeKeywords().get(0).getKeywords().contains("elevation"));
            Assert.assertEquals("farming", metadata.getTopic());
            Assert.assertEquals("2008", metadata.getContentDate());

            Assert.assertEquals("Author organisation", metadata.getOriginator());
            Assert.assertEquals("Publisher organisation", metadata.getPublisher());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
}
