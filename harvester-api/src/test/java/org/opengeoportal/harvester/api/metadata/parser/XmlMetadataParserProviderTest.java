package org.opengeoportal.harvester.api.metadata.parser;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

public class XmlMetadataParserProviderTest {
    private InputStream inFgdc = null;
    private InputStream inIso19139 = null;

    @Before
    public void setup()
            throws IOException {
        inFgdc = getClass().getResourceAsStream("fgdc.xml");
        inIso19139 = getClass().getResourceAsStream("iso19139.xml");
    }

    @After
    public void teardown()
            throws IOException {
        if (inFgdc != null){
            inFgdc.close();
        }

        inFgdc = null;

        if (inIso19139 != null){
            inIso19139.close();
        }

        inIso19139 = null;
    }


    @Test
    public void testGetMetadataParser() {
        try {
            MetadataParserProvider parserProvider = new XmlMetadataParserProvider();

            Document docFgdc = XmlUtil.load(inFgdc);
            MetadataParser parserFgdc = parserProvider.getMetadataParser(docFgdc);
            Assert.assertTrue(parserFgdc instanceof FgdcMetadataParser);

            Document docIso19139 = XmlUtil.load(inIso19139);
            MetadataParser parserIso19139 = parserProvider.getMetadataParser(docIso19139);
            Assert.assertTrue(parserIso19139 instanceof Iso19139MetadataParser);


        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
}
