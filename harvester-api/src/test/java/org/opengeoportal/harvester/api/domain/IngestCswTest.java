package org.opengeoportal.harvester.api.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;


public class IngestCswTest {
    @Test
    public void testCswCqlConstraint() {
        IngestCsw ingestCsw = new IngestCsw();

        ingestCsw.setFreeText("free text query");
        Assert.assertEquals("anytext like '%free text query%'", ingestCsw.getCqlConstraint());

        ingestCsw.setTitle("Title example");
        Assert.assertEquals("anytext like '%free text query%' AND title = 'Title example'", ingestCsw.getCqlConstraint());

        ingestCsw.setSubject("abstract%");
        Assert.assertEquals("anytext like '%free text query%' AND title = 'Title example' AND subject like 'abstract%'", ingestCsw.getCqlConstraint());

        // Cql dates
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date from = dateFormat.parse("2013-12-15");

            ingestCsw = new IngestCsw();
            ingestCsw.setDateFrom(from);
            Assert.assertEquals("modified >= '2013-12-15'", ingestCsw.getCqlConstraint());


            Date to = dateFormat.parse("2013-12-18");
            ingestCsw.setDateTo(to);
            Assert.assertEquals("modified >= '2013-12-15' AND modified <= '2013-12-18'", ingestCsw.getCqlConstraint());

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }

        //BBOX
        ingestCsw = new IngestCsw();

        ingestCsw.setBboxWest(-10.0);
        ingestCsw.setBboxEast(10.0);
        ingestCsw.setBboxNorth(5.0);
        ingestCsw.setBboxSouth(-5.0);
        Assert.assertEquals("INTERSECTS(ows:BoundingBox,ENVELOPE(-10.0,-5.0,10.0,5.0))", ingestCsw.getCqlConstraint());
    }


    @Test
    public void testCswFilterConstraint() {
        IngestCsw ingestCsw = new IngestCsw();

        StringBuffer expectedFilter = new StringBuffer();
        expectedFilter.append("<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");
        expectedFilter.append("<PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">");
        expectedFilter.append("        <PropertyName>anytext</PropertyName>");
        expectedFilter.append("        <Literal>%free text query%</Literal>");
        expectedFilter.append("</PropertyIsLike>");
        expectedFilter.append("</Filter>");

        ingestCsw.setFreeText("free text query");
        Assert.assertEquals(expectedFilter.toString(), ingestCsw.getFilterConstraint());


        expectedFilter = new StringBuffer();
        expectedFilter.append("<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");
        expectedFilter.append("<And>");
        expectedFilter.append("<PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">");
        expectedFilter.append("        <PropertyName>anytext</PropertyName>");
        expectedFilter.append("        <Literal>%free text query%</Literal>");
        expectedFilter.append("</PropertyIsLike>");
        expectedFilter.append("<PropertyIsEqualTo>");
        expectedFilter.append("        <PropertyName>title</PropertyName>");
        expectedFilter.append("        <Literal>Title example</Literal>");
        expectedFilter.append("</PropertyIsEqualTo>");
        expectedFilter.append("</And>");
        expectedFilter.append("</Filter>");

        ingestCsw.setTitle("Title example");
        Assert.assertEquals(expectedFilter.toString(), ingestCsw.getFilterConstraint());


        expectedFilter = new StringBuffer();
        expectedFilter.append("<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");
        expectedFilter.append("<And>");
        expectedFilter.append("<PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">");
        expectedFilter.append("        <PropertyName>anytext</PropertyName>");
        expectedFilter.append("        <Literal>%free text query%</Literal>");
        expectedFilter.append("</PropertyIsLike>");
        expectedFilter.append("<PropertyIsEqualTo>");
        expectedFilter.append("        <PropertyName>title</PropertyName>");
        expectedFilter.append("        <Literal>Title example</Literal>");
        expectedFilter.append("</PropertyIsEqualTo>");
        expectedFilter.append("<PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">");
        expectedFilter.append("        <PropertyName>subject</PropertyName>");
        expectedFilter.append("        <Literal>abstract%</Literal>");
        expectedFilter.append("</PropertyIsLike>");
        expectedFilter.append("</And>");
        expectedFilter.append("</Filter>");

        ingestCsw.setSubject("abstract%");
        Assert.assertEquals(expectedFilter.toString(), ingestCsw.getFilterConstraint());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date from = dateFormat.parse("2013-12-15");
            Date to = dateFormat.parse("2013-12-18");

            ingestCsw = new IngestCsw();

            expectedFilter = new StringBuffer();
            expectedFilter.append("<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");
            expectedFilter.append("<PropertyIsGreaterThanOrEqualTo>");
            expectedFilter.append("        <PropertyName>modified</PropertyName>");
            expectedFilter.append("        <Literal>2013-12-15</Literal>");
            expectedFilter.append("</PropertyIsGreaterThanOrEqualTo>");
            expectedFilter.append("</Filter>");

            ingestCsw.setDateFrom(from);
            Assert.assertEquals(expectedFilter.toString(), ingestCsw.getFilterConstraint());


            expectedFilter = new StringBuffer();
            expectedFilter.append("<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");
            expectedFilter.append("<And>");
            expectedFilter.append("<PropertyIsGreaterThanOrEqualTo>");
            expectedFilter.append("        <PropertyName>modified</PropertyName>");
            expectedFilter.append("        <Literal>2013-12-15</Literal>");
            expectedFilter.append("</PropertyIsGreaterThanOrEqualTo>");
            expectedFilter.append("<PropertyIsLessThanOrEqualTo>");
            expectedFilter.append("        <PropertyName>modified</PropertyName>");
            expectedFilter.append("        <Literal>2013-12-18</Literal>");
            expectedFilter.append("</PropertyIsLessThanOrEqualTo>");
            expectedFilter.append("</And>");
            expectedFilter.append("</Filter>");

            ingestCsw.setDateTo(to);
            Assert.assertEquals(expectedFilter.toString(), ingestCsw.getFilterConstraint());

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }

        //BBOX
        ingestCsw = new IngestCsw();

        ingestCsw.setTitle("Title example");
        ingestCsw.setBboxWest(-10.0);
        ingestCsw.setBboxEast(10.0);
        ingestCsw.setBboxNorth(5.0);
        ingestCsw.setBboxSouth(-5.0);

        expectedFilter = new StringBuffer();
        expectedFilter.append("<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">");
        expectedFilter.append("<And>");
        expectedFilter.append("<PropertyIsEqualTo>");
        expectedFilter.append("        <PropertyName>title</PropertyName>");
        expectedFilter.append("        <Literal>Title example</Literal>");
        expectedFilter.append("</PropertyIsEqualTo>");
        expectedFilter.append("<Intersects>");
        expectedFilter.append("        <PropertyName>BoundingBox</PropertyName>");
        expectedFilter.append("<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\"><gml:coordinates>-10.0,-5.0 10.0,5.0</gml:coordinates></gml:Box>");
        expectedFilter.append("</Intersects>");
        expectedFilter.append("</And>");
        expectedFilter.append("</Filter>");

        Assert.assertEquals(expectedFilter.toString(), ingestCsw.getFilterConstraint());

    }

}
