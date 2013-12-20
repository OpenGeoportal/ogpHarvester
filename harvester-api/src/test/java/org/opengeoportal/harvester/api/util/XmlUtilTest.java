package org.opengeoportal.harvester.api.util;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

public class XmlUtilTest {
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
    public void testGetMetadataType() {
        try {
            Document docFgdc = XmlUtil.load(inFgdc);
            MetadataType mdTypeFgdc = XmlUtil.getMetadataType(docFgdc);
            Assert.assertEquals(MetadataType.FGDC , mdTypeFgdc);

            Document docIso19139 = XmlUtil.load(inIso19139);
            MetadataType mdTypeIso19139 = XmlUtil.getMetadataType(docIso19139);
            Assert.assertEquals(MetadataType.ISO_19139, mdTypeIso19139);


        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }
}