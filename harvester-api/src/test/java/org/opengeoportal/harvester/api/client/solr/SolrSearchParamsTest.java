package org.opengeoportal.harvester.api.client.solr;

import org.junit.Assert;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.opengeoportal.harvester.api.domain.DataType;
import org.opengeoportal.harvester.api.domain.IngestOGP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SolrSearchParamsTest {
    @Test
    public void testQueryOriginator() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        Assert.assertEquals("q=Institution:*+AND+(Originator:*originator*)&start=0&rows=40", unencode(query.toString()));
    }

    @Test
    public void testQueryPlaceKeywords() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");
        ingest.setPlaceKeyword("place1 place2");

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        Assert.assertEquals("q=Institution:*+AND+(PlaceKeywords:*place1*+OR+PlaceKeywords:*place2*)+" +
                "AND+(Originator:*originator*)&start=0&rows=40",
                unencode(query.toString()));


    }

    @Test
    public void testQueryDataTypes() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");
        List<DataType> dataTypes = new ArrayList<DataType>();
        dataTypes.add(DataType.LINE);
        dataTypes.add(DataType.RASTER);
        ingest.setDataTypes(dataTypes);

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        Assert.assertEquals("q=Institution:*+AND+(Originator:*originator*)+AND+" +
                "(DataType:LINE+OR+DataType:RASTER)&start=0&rows=40",
                unencode(query.toString()));

    }


    @Test
    public void testQueryDataRepositories() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");
        List<String> dataRepositories = new ArrayList<String>();
        dataRepositories.add("repo1");
        dataRepositories.add("repo1");
        ingest.setDataRepositories(dataRepositories);

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        Assert.assertEquals("q=(Institution:repo1+OR+Institution:repo1)+AND+" +
                "(Originator:*originator*)&start=0&rows=40",
                unencode(query.toString()));

    }


    @Test
    public void testDateQuery() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        try {
            String dateFromString = "10-01-2014";
            ingest.setDateFrom(df.parse(dateFromString));

            String dateToString = "01-02-2014";
            ingest.setDateTo(df.parse(dateToString));

            SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
            SolrQuery query = solrSearchParams.toSolrQuery();

            Assert.assertEquals("q=Institution:*+AND+" +
                    "ContentDate:[2014\\-01\\-09T23\\:00\\:00.000Z+TO+2014\\-01\\-31T23\\:00\\:00.000Z]+AND+" +
                    "Originator:*originator*)&start=0&rows=40",
                    unencode(query.toString()));

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void testComplexQuery() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");
        List<String> dataRepositories = new ArrayList<String>();
        dataRepositories.add("repo1");
        dataRepositories.add("repo1");
        ingest.setDataRepositories(dataRepositories);
        List<DataType> dataTypes = new ArrayList<DataType>();
        dataTypes.add(DataType.LINE);
        dataTypes.add(DataType.RASTER);
        ingest.setPlaceKeyword("place1 place2");
        ingest.setThemeKeyword("theme1 theme1");

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        Assert.assertEquals("q=(Institution:repo1+OR+Institution:repo1)+AND+" +
                "(ThemeKeywords:*theme1*+OR+ThemeKeywords:*theme1*)+AND+" +
                "(PlaceKeywords:*place1*+OR+PlaceKeywords:*place2*)+AND+(Originator:*originator*)&start=0&rows=40",
                unencode(query.toString()));

    }

    private String unencode(String query) {
        String result = query.replaceAll("%3A", ":");
        result = result.replaceAll("%28", "(");
        result = result.replaceAll("%29", ")");
        result = result.replaceAll("%5C", "\\\\");
        result = result.replaceAll("%5B", "[");
        result = result.replaceAll("%5D", "]");

        return result;
    }
}
