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

        Assert.assertEquals("q=*:*&fq=Institution:*&fq=Originator:originator&pf=Originator:originator&rows=40&" +
                "start=0&sort=score+desc", unencode(query.toString()));
    }

    @Test
    public void testQueryPlaceKeywords() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");
        ingest.setPlaceKeyword("place1 place2");

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        Assert.assertEquals("q=*:*&fq=Institution:*&fq=PlaceKeywords:place1+place2&fq=Originator:originator&" +
                "fq=PlaceKeywordsSynonyms:(place1+place2)+OR+LayerDisplayNameSynonyms:(place1+place2)&" +
                "pf=PlaceKeywords:%27place1+place2%27%5E9.0&pf=Originator:originator&rows=40&start=0&sort=score+desc",
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

        Assert.assertEquals("q=*:*&fq=Institution:*&fq=Originator:originator&fq=DataType:LINE+ORDataType:RASTER&" +
                "pf=Originator:originator&rows=40&start=0&sort=score+desc",
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

        Assert.assertEquals("q=*:*&fq=Institution:repo1+OR+Institution:repo1&fq=Originator:originator&" +
                "pf=Originator:originator&rows=40&start=0&sort=score+desc",
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

            Assert.assertEquals("q=*:*&fq=Institution:*&fq=ContentDate:" +
                    "[2014\\-01\\-09T23\\:00\\:00.000Z+TO+2014\\-01\\-31T23\\:00\\:00.000Z]&" +
                    "fq=Originator:originator&pf=Originator:originator&rows=40&start=0&sort=score+desc",
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

        Assert.assertEquals("q=*:*&fq=Institution:repo1+OR+Institution:repo1&fq=ThemeKeywords:theme1+theme1&" +
                "fq=PlaceKeywords:place1+place2&fq=Originator:originator&fq=PlaceKeywordsSynonyms:(place1+place2)+" +
                "OR+LayerDisplayNameSynonyms:(theme1+theme1)+OR+LayerDisplayNameSynonyms:(place1+place2)+OR+" +
                "ThemeKeywordsSynonymsLcsh:(theme1+theme1)&pf=ThemeKeywords:%27theme1+theme1%27%5E9.0&" +
                "pf=LayerDisplayName:%27theme1+theme1%27%5E9.0&pf=PlaceKeywords:%27place1+place2%27%5E9.0&" +
                "pf=Originator:originator&rows=40&start=0&sort=score+desc",
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
