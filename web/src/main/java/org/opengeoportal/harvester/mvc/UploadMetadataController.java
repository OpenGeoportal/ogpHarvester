package org.opengeoportal.harvester.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.opengeoportal.harvester.api.client.solr.SolrJClient;
import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.Frequency;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestFileUpload;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.BaseXmlMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.FgdcMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
import org.opengeoportal.harvester.api.service.IngestService;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.opengeoportal.harvester.mvc.utils.FileConversionUtils;
import org.opengeoportal.harvester.mvc.utils.UncompressStrategyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

@Controller
public class UploadMetadataController {

    private static final String XML_EXTENSION = ".xml";

    @Value("#{localSolr['localSolr.url']}")
    private String localSolrUrl;
    
    @Resource
    private IngestService ingestService;

    @RequestMapping(value = "/rest/uploadMetadata/add", method = RequestMethod.POST)
    @ResponseBody
    public String addMetadata(@RequestPart("file") MultipartFile file, final HttpServletRequest request,
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

                addMetadataIngest(metadataFiles[0]);

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
    
    private Map<String, Object> addMetadataIngest(final File metadataFile)
            throws FileNotFoundException, Exception, UnsupportedMetadataType {
        
        Ingest ingest = new IngestFileUpload();
        
        
        ingest.setBeginDate(new Date(System.currentTimeMillis()));
        ingest.setName(metadataFile.getName()+System.currentTimeMillis());
        ingest.setNameOgpRepository(""); // In the OGP Harvester it's not active
        ingest.setFrequency(Frequency.ONCE);
        ingest.setScheduled(true);
//        if (!usesCustomRepo) {
//            ingest.setUrl(ingestFormBean.getUrl());
//            ingest.setRepository(null);
//        }

        // remove old required fields and add the new ones
//        ingest.getRequiredFields().clear();
//        for (Entry<String, Boolean> requiredField : ingestFormBean
//                .getRequiredFields().entrySet()) {
//            if (requiredField.getValue()) {
//                ingest.addRequiredField(requiredField.getKey());
//            }
//        }
        ingest.setUrl("holaaaaaaa");
        
        ((IngestFileUpload) ingest).setFile(metadataFile);        
        
        ingest = ingestService.saveAndSchedule(ingest, -99999L, InstanceType.FILE);
        
        Map<String, Object> result = Maps.newHashMap();
        result.put("success", true);
        Map<String, Object> data = Maps.newHashMap();
        data.put("id", ingest.getId());
        data.put("name", ingest.getName());
        result.put("data", data);

        return result;
        
    }


    private void saveMetadata(final File metadataFile)
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
        SolrRecord solrRecord = SolrRecord.build(metadata);

        SolrJClient solrClient = new SolrJClient(localSolrUrl);

        solrClient.add(solrRecord);

        
        
        
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



}
