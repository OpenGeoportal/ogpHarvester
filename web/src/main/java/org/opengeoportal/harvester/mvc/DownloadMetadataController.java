package org.opengeoportal.harvester.mvc;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.opengeoportal.harvester.api.client.solr.SolrClient;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.exception.OgpSolrException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by joana on 11/05/17.
 */
@Controller
public class DownloadMetadataController {

    @Value("#{localSolr['localSolr.url']}")
    private String localSolrUrl;

    @RequestMapping(value = "/rest/workspaces/{workspace}/datasets/{dataset}/downloadMetadata/", method =
            RequestMethod.GET)
    @ResponseBody
    public final void download(
            @PathVariable(value = "workspace") final String workspace,
            @PathVariable(value = "dataset") final String dataset,
            final HttpServletResponse response) throws Exception {

        File file = null;
        StreamResult in;

        try {
            createXmlFileFromTypeName(workspace, dataset);

        } catch (final Exception ex) {
             ex.printStackTrace();
            throw ex;
        }
    }

    private void createXmlFileFromTypeName(String WorkspaceName, String Name)
            throws SolrServerException, ParserConfigurationException, IOException, SAXException,
            TransformerException, SolrServerException, OgpSolrException {

        SolrClient solrClient = new SolrJClient(localSolrUrl);
        QueryResponse qr = solrClient.searchForDataset("cite",
                "SDE2.MATWN_3764_B6N44_1852_P5"); // TODO: Update this to Production
        SolrDocumentList docs = qr.getResults();
        String str = docs.get(0).getFieldValue("FgdcText").toString();

        //Fix, until we find a better solution for reading the contents of the solr doc
        StringBuilder sb = new StringBuilder(str);
        sb.deleteCharAt(0);
        sb.deleteCharAt(str.length()-2);
        str = sb.toString();

        System.out.println(str);

        // Just in case, check that the xml is correct: remove from Production?
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(str)));

        // Write the parsed document to an xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        String strPath = System.getProperty("java.io.tmpdir");

        StreamResult result = new StreamResult(new File(strPath + "/" + Name + ".xml"));
        transformer.transform(source, result);
    }
}
