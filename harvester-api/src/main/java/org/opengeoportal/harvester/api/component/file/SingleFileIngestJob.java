package org.opengeoportal.harvester.api.component.file;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.BaseXmlMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.FgdcMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
import org.opengeoportal.harvester.api.util.UploadFileJob;
import org.opengeoportal.harvester.api.util.UploadJobQueue;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

/**
 * IngestJob to load metadata from an uploaded file.
 *
 */
public class SingleFileIngestJob extends BaseIngestJob {

    @Value("#{localSolr['localSolr.url']}")
    private String localSolrUrl;

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.component.BaseIngestJob#ingest()
     */
    @Override
    public void ingest() {
        try {
            long failedRecordsCount = 0;          

            List<Metadata> metadataList = Lists.newArrayListWithCapacity(1);
            UploadFileJob currentJob = UploadJobQueue.getAJob();

            while (currentJob!=null && !isInterruptRequested()) {

                Document doc = null;

                try {
                    File metadataFile = currentJob.getFile();

                    FileInputStream in = new FileInputStream(metadataFile);

                    doc = XmlUtil.load(in);

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
                    String workspace = currentJob.getWorkspace();
                    String dataSet = currentJob.getDataset();
                    String wms = currentJob.getWmsEndPoint();
                    String wcs = currentJob.getWcsEndPoint();
                           
                    metadata.setWorkspaceName(workspace);
                    metadata.setOwsName(dataSet);
                   
                    String currentLocation = metadata.getLocation();
                    if(currentLocation!= null && currentLocation.contains("{") && currentLocation.contains("}")) {
                        // Prepare JSON Object for the request
                        JSONObject request = null;
                        
                        String json = "";
                        
                        JSONParser jsonParser = new JSONParser();
                        request = (JSONObject) jsonParser.parse(json);
                        request.put("wms", wms);
                        request.put("wcs", wcs);
                        
                        currentLocation = request.toString();
                    } else {
                        JSONObject request = new JSONObject();
                        request.put("wms", wms);
                        request.put("wcs", wcs);
                        
                        currentLocation = request.toString();                        
                    }
                    
                    metadata.setLocation(currentLocation);

                    metadataList.add(metadata);    


                } catch (Exception ex) {
                    failedRecordsCount++;
                    saveException(ex,
                            IngestReportErrorType.SYSTEM_ERROR, doc);

                }
                report.setFailedRecordsCount(failedRecordsCount);
                metadataIngester.ingest(metadataList, report);

                currentJob.setCompleted(true);
                currentJob = UploadJobQueue.getAJob();

            }



        } catch (Exception e) {
            logger.error(
                    "Error in SingleFile Ingest: " + this.ingest.getName(), e);
            saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        }

    }
}
