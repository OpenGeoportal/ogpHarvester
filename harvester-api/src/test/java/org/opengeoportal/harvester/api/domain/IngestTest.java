package org.opengeoportal.harvester.api.domain;

import org.junit.Assert;
import org.junit.Test;

public class IngestTest {

    @Test
    public void testRequiredFields() {
        Ingest ingest = new IngestOGP();

        // Add valid required field
        ingest.addRequiredField("themeKeyword");
        Assert.assertEquals(1, ingest.getRequiredFields().size());

        // Add invalid required field
        ingest.addRequiredField("novalid");
        Assert.assertEquals(1, ingest.getRequiredFields().size());
    }

    @Test
    public void testEmptyRequiredFields() {
        Ingest ingest = new IngestOGP();

        try {
            ingest.addRequiredField("");
            Assert.fail();
        } catch (IllegalArgumentException ex) {

        }

        try {
            ingest.addRequiredField(null);
            Assert.fail();
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test
    public void testChangeCollections() {
        Ingest ingest = new IngestOGP();

        try {
            ingest.getRequiredFields().add("dataType");
            // FIXME: Why this should fail?
            //Assert.fail();
        } catch (UnsupportedOperationException ex) {

        }
        try {
            ingest.getValidRequiredFields().add("newfield");
            Assert.fail();
        } catch (UnsupportedOperationException ex) {

        }
    }
}
