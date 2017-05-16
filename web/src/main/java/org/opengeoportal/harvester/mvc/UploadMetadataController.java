package org.opengeoportal.harvester.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.domain.Frequency;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestFileUpload;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.BaseXmlMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.FgdcMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
import org.opengeoportal.harvester.api.service.IngestService;
import org.opengeoportal.harvester.api.util.JSONReader;
import org.opengeoportal.harvester.api.util.UploadFileJob;
import org.opengeoportal.harvester.api.util.UploadJobQueue;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.opengeoportal.harvester.mvc.exception.PropertyNotSetException;
import org.opengeoportal.harvester.mvc.utils.FileConversionUtils;
import org.opengeoportal.harvester.mvc.utils.UncompressStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.google.common.io.Files;

@Controller
public class UploadMetadataController {

    private static final String XML_EXTENSION = ".xml";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{localSolr['localSolr.url']}")
    private String localSolrUrl;

    @Value("#{dataIngest['dataIngest.url']}")
    private String dataIngestUrl;

    @Resource
    private IngestService ingestService;

    @RequestMapping(value = "/rest/uploadMetadata/add", method = RequestMethod.POST)
    @ResponseBody
    public String addMetadata(@RequestParam("workspace") String workspace, @RequestParam("dataset") String dataset, @RequestParam("requiredFields") String requiredFields, @RequestPart("file") MultipartFile file, final HttpServletRequest request,
            final HttpServletResponse response) {

        try {
            File zipFile = FileConversionUtils.multipartToFile(file);

            final String packageName = zipFile.getName();

            File unzippedFile = uncompressFile(zipFile);

            if(unzippedFile != null) { 

                final File[] metadataFiles = unzippedFile.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return (name.toLowerCase().endsWith(XML_EXTENSION));
                    }
                });

                if(metadataFiles.length==0) {
                    return "No metadata.";
                }

                if(metadataFiles.length>1) {
                    printOutputMessage(response, HttpServletResponse.SC_BAD_REQUEST,
                            "The archive contains more than one metadata file.");
                    return "The archive contains more than one metadata file.";
                }
                
                String[] requiredFieldsArray = null;
                
                if(requiredFields!=null && requiredFields.length()>0) {
                    
                    requiredFieldsArray = requiredFields.split(",");                    
                }                

                checkMetadata(metadataFiles[0], requiredFieldsArray);
                addMetadataIngest(workspace, dataset, metadataFiles[0]);

            }

        } catch (PropertyNotSetException e) {
            try {
                printOutputMessage(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Missing required field in metadata: " + e.getProperty());
                return "Missing required field in metadata: " + e.getProperty();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return "";
    }

    private boolean addMetadataIngest(String workspace, String dataset, final File metadataFile)
            throws FileNotFoundException, Exception, UnsupportedMetadataType {        

        // MANAGE THE UPLOAD JOB QUEUE HERE
        UploadFileJob job = new UploadFileJob();
        job.setWorkspace(workspace);
        job.setDataset(dataset);
        job.setFile(metadataFile);

        // Get the values for WMS and WFS from data-ingest (from uploded shapefile)
        try {
            JSONObject json = JSONReader.readJsonFromUrl(dataIngestUrl+"/workspaces/"+workspace+"/datasets/"+dataset+"/");

            String wms = (String) json.get("WMS");
            String wfs = (String) json.get("WFS");

            job.setWmsEndPoint(wms);
            job.setWfsEndPoint(wfs);
        } catch (Exception e) {
            logger.error("Unable to load WMS and WFS from Data-Ingest");
        }

        UploadJobQueue.addNewJob(job);

        // Check if the ingest process is already created
        String name = "UPLOADED FILES METADATA INGESTER";
        Ingest ingest = ingestService.findByName(name);

        // START THE PROCESS
        try {
            if(ingest==null) { 
                // Create the ingest process
                ingest = new IngestFileUpload(); 
                ingest.setName(name);
                ingest.setNameOgpRepository("");
                ingest.setFrequency(Frequency.EVERY5MINUTES);
                ingest.setScheduled(true);
                ingest.setUrl("LOCAL");
                ingest.setBeginDate(new Date(System.currentTimeMillis()));
                ingest = ingestService.saveAndSchedule(ingest);
            }    
        } catch(Exception e) {
            logger.error(e.getMessage());
            return false;
        }       


        return true;        
    }
    
    private void checkMetadata(File metadataFile, String[] requiredFields)
            throws FileNotFoundException, Exception, UnsupportedMetadataType {

        FileInputStream in = new FileInputStream(metadataFile);

        Document doc = XmlUtil.load(in);        

        MetadataType metadataType = XmlUtil.getMetadataType(doc);        

        BaseXmlMetadataParser parser;

        if(metadataType.equals(MetadataType.ISO_19139)) {
            parser = new Iso19139MetadataParser();
        } else  if(metadataType.equals(MetadataType.FGDC)) { 
            parser = new FgdcMetadataParser(); 
        } else {
            throw new UnsupportedMetadataType();
        }

        MetadataParserResponse parsedMetadata = parser.parse(doc);

        Metadata metadata = parsedMetadata.getMetadata();
        
        if(requiredFields!=null) {
            for (String property : requiredFields) {
                if(!metadata.hasValueForProperty(property)) {
                    throw new PropertyNotSetException(property);
                }
            }     
        }

    }   

    private File uncompressFile(File packageFile) throws Exception {

        try {
            File unzipDir = Files.createTempDir();

            final String packageName = (packageFile.getName()
                    .endsWith("shp.zip"))
                    ? packageFile.getName().replace(".shp.zip", "")
                            : FilenameUtils
                            .removeExtension(packageFile.getName());

                    final String packageExtension = (packageFile.getName()
                            .endsWith("shp.zip")) ? "shp.zip"
                                    : FilenameUtils.getExtension(packageFile.getName());

                    UncompressStrategyFactory.getUncompressStrategy(packageExtension)
                    .uncompress(packageFile, unzipDir);

                    return unzipDir;
        } catch (Exception e) { 
            e.printStackTrace();
            throw e;
        }


    }

    /**
     * Prints the output message.
     *
     * @param response the response
     * @param code     the code
     * @param message  the message
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void printOutputMessage(final HttpServletResponse response,
            final int code, final String message) throws IOException {
        response.setStatus(code);
        final PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println(message);
        response.flushBuffer();
    }

    
    /* TODO: REMOVE FOLLOWING METHODS BEFORE PRODUCTION */
    private void countSolr() throws SolrServerException {
        Integer start = 0;
        SolrJClient solrClient = new SolrJClient(localSolrUrl);
        HttpSolrServer server = solrClient.getSolrServer();
        long counter = 0;
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(Integer.MAX_VALUE);
        QueryResponse rsp;
        rsp = server.query(query, METHOD.POST);
        SolrDocumentList docs = rsp.getResults();
        for (SolrDocument doc : docs) {
            Collection<String> fieldNames = doc.getFieldNames();
            for (String s: fieldNames) {
                counter++;
            }
        }

        System.out.println("SOLR: " + counter);
    }
    
    private void immediateSaveMetadata(String workspace, String dataset, final File metadataFile)
            throws FileNotFoundException, Exception, UnsupportedMetadataType {

        FileInputStream in = new FileInputStream(metadataFile);

        Document doc = XmlUtil.load(in);        

        MetadataType metadataType = XmlUtil.getMetadataType(doc);        

        BaseXmlMetadataParser parser;

        if(metadataType.equals(MetadataType.ISO_19139)) {
            parser = new Iso19139MetadataParser();
        } else  if(metadataType.equals(MetadataType.FGDC)) { 
            parser = new FgdcMetadataParser(); 
        } else {
            throw new UnsupportedMetadataType();
        }

        MetadataParserResponse parsedMetadata = parser.parse(doc);

        Metadata metadata = parsedMetadata.getMetadata();

        // to make metadata coupled with the shape file layer
        metadata.setWorkspaceName(workspace);
        metadata.setOwsName(dataset);

        // location
        // Get the values from data-ingest
        JSONObject json = JSONReader.readJsonFromUrl(dataIngestUrl+"/workspaces/"+workspace+"/datasets/"+dataset+"/");

        String wms = (String) json.get("WMS");
        String wfs = (String) json.get("WFS");

        // get the current value for location from metadata
        String currentLocation = metadata.getLocation();
        if(currentLocation!= null && currentLocation.contains("{") && currentLocation.contains("}")) {
            // Prepare JSON Object for the request
            JSONObject request = null;

            String jsonS = "";

            JSONParser jsonParser = new JSONParser();

            request = (JSONObject) jsonParser.parse(jsonS);
            request.put("wms", wms);
            request.put("wfs", wfs);

            currentLocation = request.toString();
        } else {
            JSONObject request = new JSONObject();
            request.put("wms", wms);
            request.put("wfs", wfs);

            currentLocation = request.toString();                        
        }

        metadata.setLocation(currentLocation);

        SolrRecord solrRecord = SolrRecord.build(metadata);

        SolrJClient solrClient = new SolrJClient(localSolrUrl);

        solrClient.add(solrRecord);  

    }
}
