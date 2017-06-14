package org.opengeoportal.harvester.api.client.solr;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.opengeoportal.harvester.api.domain.DataType;
import org.opengeoportal.harvester.api.domain.IngestOGP;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * Series of tests to make sure output query from SolrSearchParams matches expectations.
 */
public class SolrSearchParamsTest {
    @Test
    public void testQueryOriginator() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        testBaseQuery(query);

        testOriginator(query);

        String expected = "q=*:*&fq=Institution:*&fq=Originator:originator&pf=Originator:originator&rows=40&" +
                "start=0&sort=score+desc";
        lengthCheck(query, expected);
    }

    @Test
    public void testQueryPlaceKeywords() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");
        ingest.setPlaceKeyword("place1 place2");

        //TODO: add more granular test for place keywords

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        testBaseQuery(query);

        testOriginator(query);


        String expectedString = "q=*:*&fq=Institution:*&fq=PlaceKeywords:place1+place2&fq=Originator:originator&" +
                "fq=PlaceKeywordsSynonyms:(place1+place2)+OR+LayerDisplayNameSynonyms:(place1+place2)&" +
                "pf=PlaceKeywords:%27place1+place2%27%5E9.0&pf=Originator:originator&rows=40&start=0&sort=score+desc";

        lengthCheck(query, expectedString);


    }

    /**
     * a last ditch sanity check. We can't guarantee the order of terms in the Solr query, but if they are all there,
     * lengths of these 2 strings should be the same.
     *
     * @param query    complete SolrQuery
     * @param expected expected query as a String
     */
    public void lengthCheck(SolrQuery query, String expected) {
        String actualString = unencode(query.toString());
        Assert.assertTrue(expected.length() == actualString.length());
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

        testBaseQuery(query);

        List<String> dtExpected = Arrays.asList("DataType:Line", "DataType:Raster");

        testFilter(query, "DataType", dtExpected);

        testOriginator(query);

        String expected = "q=*:*&fq=Institution:*&fq=Originator:originator&fq=DataType:Line+OR+DataType:Raster&" +
                "pf=Originator:originator&rows=40&start=0&sort=score+desc";
        lengthCheck(query, expected);

    }


    /**
     * Since all tests in this class use the value "originator" for an originator, this function can be shared.
     *
     * @param query
     */
    private void testOriginator(SolrQuery query) {
        List<String> origExpected = Arrays.asList("Originator:originator");

        testFilter(query, "Originator", origExpected);

        //TODO: add test for pf clause
    }

    /**
     * Assert that the basic elements of a solr query match expected values, to make sure basic elements are
     * not affected by adding other params.
     *
     * @param query
     */
    public void testBaseQuery(SolrQuery query) {
        Assert.assertTrue(query.getRows() == 40);
        Assert.assertTrue(query.getStart() == 0);
        Assert.assertEquals(query.getQuery(), "*:*");

        List<SolrQuery.SortClause> sorts = query.getSorts();
        Assert.assertTrue("More than one 'sort' element.", sorts.size() == 1);
        SolrQuery.SortClause s = sorts.get(0);
        Assert.assertEquals(s.getOrder(), SolrQuery.ORDER.desc);
        Assert.assertEquals(s.getItem(), "score");

    }

    /**
     * Asserts whether the elements in a filter query based on a certain field matches elements in a list of
     * expected values, in any order.
     *
     * @param query
     * @param field
     * @param expected
     */
    public void testFilter(SolrQuery query, String field, List<String> expected) {
        String[] filters = query.getFilterQueries();

        //all the solr queries go through solrj, so we don't really need to test the URL encoding
        for (String f : filters) {
            // find the 'DataType' filter
            if (f.startsWith(field)) {
                // replace connector with pipe, since StringUtils.split only supports single character separator
                String testString = f.replace(" OR ", "|").replace(" AND ", "|");
                List<String> myList = new ArrayList<String>(Arrays.asList(StringUtils.split(testString, "|")));
                Assert.assertThat(myList, containsInAnyOrder(expected.toArray()));
            }
        }
    }

    @Test
    public void testQueryDataTypesPaperMap() throws UnsupportedEncodingException {
        IngestOGP ingest = new IngestOGP();
        List<DataType> dataTypes = new ArrayList<DataType>();
        dataTypes.add(DataType.LINE);
        dataTypes.add(DataType.SCANNED);
        ingest.setDataTypes(dataTypes);

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        testBaseQuery(query);

        //all the solr queries go through solrj, so we don't really need to test the URL encoding
        List<String> expected = Arrays.asList("DataType:Line", "DataType:\"Paper Map\"",
                "DataType:\"Scanned Map\"", "DataType:ScannedMap");

        testFilter(query, "DataType", expected);
    }



    @Test
    public void testQueryDataRepositories() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");
        List<String> dataRepositories = new ArrayList<String>();
        dataRepositories.add("repo1");
        dataRepositories.add("repo2");
        ingest.setDataRepositories(dataRepositories);

        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        testBaseQuery(query);

        testOriginator(query);

        List<String> expected = Arrays.asList("Institution:repo1", "Institution:repo2");

        testFilter(query, "Institution", expected);

        String expectedQuery = "q=*:*&fq=Institution:repo1+OR+Institution:repo2&fq=Originator:originator&" +
                "pf=Originator:originator&rows=40&start=0&sort=score+desc";
        lengthCheck(query, expectedQuery);

    }


    @Test
    public void testDateQuery() {
        IngestOGP ingest = new  IngestOGP();
        ingest.setOriginator("originator");

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        //TODO: add more granular tests for dates
        try {
            String dateFromString = "10-01-2014";
            ingest.setDateFrom(df.parse(dateFromString));

            String dateToString = "01-02-2014";
            ingest.setDateTo(df.parse(dateToString));

            SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
            SolrQuery query = solrSearchParams.toSolrQuery();

            testBaseQuery(query);

            testOriginator(query);

            String expected = "q=*:*&fq=Institution:*&fq=ContentDate:" +
                    "[2014\\-01\\-10T00\\:00\\:00.000Z+TO+2014\\-02\\-01T00\\:00\\:00.000Z]&" +
                    "fq=Originator:originator&pf=Originator:originator&rows=40&start=0&sort=score+desc";

            lengthCheck(query, expected);

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
        dataRepositories.add("repo2");
        ingest.setDataRepositories(dataRepositories);
        List<DataType> dataTypes = new ArrayList<DataType>();
        dataTypes.add(DataType.LINE);
        dataTypes.add(DataType.RASTER);
        ingest.setDataTypes(dataTypes);
        ingest.setPlaceKeyword("place1 place2");
        ingest.setThemeKeyword("theme1 theme2");

        //TODO: add more granular tests for theme keywords
        SolrSearchParams solrSearchParams = new SolrSearchParams(ingest);
        SolrQuery query = solrSearchParams.toSolrQuery();

        testBaseQuery(query);

        testOriginator(query);

        List<String> expected = Arrays.asList("Institution:repo1", "Institution:repo2");

        testFilter(query, "Institution", expected);

        List<String> dtExpected = Arrays.asList("DataType:Line", "DataType:Raster");

        testFilter(query, "DataType", dtExpected);

        String expectedQuery = "q=*:*&fq=Institution:repo1+OR+Institution:repo2&" +
                "fq=ThemeKeywords:theme1+theme2&fq=PlaceKeywords:place1+place2&" +
                "fq=Originator:originator&fq=DataType:Line+OR+DataType:Raster&" +
                "fq=PlaceKeywordsSynonyms:(place1+place2)+OR+LayerDisplayNameSynonyms:(theme1+theme2)+OR+" +
                "LayerDisplayNameSynonyms:(place1+place2)+OR+ThemeKeywordsSynonymsLcsh:(theme1+theme2)&" +
                "pf=ThemeKeywords:%27theme1+theme2%27%5E9.0&pf=LayerDisplayName:%27theme1+theme2%27%5E9.0&" +
                "pf=PlaceKeywords:%27place1+place2%27%5E9.0&pf=Originator:originator&rows=40&start=0&sort=score+desc";
        lengthCheck(query, expectedQuery);

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
