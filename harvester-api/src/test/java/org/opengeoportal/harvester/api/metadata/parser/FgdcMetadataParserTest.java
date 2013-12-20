package org.opengeoportal.harvester.api.metadata.parser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.opengeoportal.harvester.api.metadata.model.GeometryType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

public class FgdcMetadataParserTest {
    private InputStream in = null;

    @Before
    public void setup()
            throws IOException {
        in = getClass().getResourceAsStream("fgdc.xml");
    }

    @After
    public void teardown()
            throws IOException {
        if (in != null) {
            in.close();
        }

        in = null;
    }


    @Test
    public void testParser() {
        try {
            Document doc = XmlUtil.load(in);

            FgdcMetadataParser parser = new FgdcMetadataParser();
            MetadataParserResponse response = parser.parse(doc);

            Metadata metadata = response.getMetadata();

            Assert.assertEquals("Globally threatened species of the world", metadata.getTitle());
            Assert.assertEquals("Contains information on animals and plants threatened at the global level.", metadata.getDescription());
            Assert.assertEquals("Sample test centre", metadata.getPublisher());
            Assert.assertEquals("Sample test user", metadata.getOriginator());
            Assert.assertEquals("2013", metadata.getContentDate());
            Assert.assertEquals(AccessLevel.Public, metadata.getAccess());
            Assert.assertEquals(GeometryType.Raster, metadata.getGeometryType());

            Assert.assertEquals(Boolean.TRUE, metadata.getBounds().isValid());
            Assert.assertEquals(new Double(90), metadata.getBounds().getMaxY());
            Assert.assertEquals(new Double(-90), metadata.getBounds().getMinY());
            Assert.assertEquals(new Double(180), metadata.getBounds().getMaxX());
            Assert.assertEquals(new Double(-180), metadata.getBounds().getMinX());

            Assert.assertEquals(2, metadata.getThemeKeywords().size());
            Assert.assertEquals(3, metadata.getThemeKeywords().get(0).getKeywords().size());
            Assert.assertEquals(1, metadata.getThemeKeywords().get(1).getKeywords().size());
            Assert.assertEquals(1, metadata.getPlaceKeywords().size());

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
}
