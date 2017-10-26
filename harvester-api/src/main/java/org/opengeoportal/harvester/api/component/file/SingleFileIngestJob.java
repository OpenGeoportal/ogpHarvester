package org.opengeoportal.harvester.api.component.file;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
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

            final List<Metadata> metadataList = Lists
                    .newArrayListWithCapacity(1);
            UploadFileJob currentJob = UploadJobQueue.getAJob();

            while ((currentJob != null) && !this.isInterruptRequested()) {

                Document doc = null;

                try {
                    final File metadataFile = currentJob.getFile();

                    final FileInputStream in = new FileInputStream(
                            metadataFile);

                    doc = XmlUtil.load(in);

                    final MetadataType metadataType = XmlUtil
                            .getMetadataType(doc);

                    BaseXmlMetadataParser parser;

                    if (metadataType.equals(MetadataType.ISO_19139)) {
                        parser = new Iso19139MetadataParser();
                    } else if (metadataType.equals(MetadataType.FGDC)) {
                        parser = new FgdcMetadataParser();
                    } else {
                        throw new UnsupportedMetadataType();
                    }

                    final MetadataParserResponse parsedMetadata = parser
                            .parse(doc);

                    final Metadata metadata = parsedMetadata.getMetadata();

                    // to make metadata coupled with the shape file layer
                    final String workspace = currentJob.getWorkspace();
                    final String dataSet = currentJob.getDataset();
                    final String wms = currentJob.getWmsEndPoint();
                    final String wfs = currentJob.getWfsEndPoint();

                    // values for shapefile binding
                    metadata.setWorkspaceName(workspace);
                    metadata.setOwsName(dataSet);

                    String currentLocation = metadata.getLocation();
                    if ((currentLocation != null)
                            && currentLocation.contains("{")
                            && currentLocation.contains("}")) {
                        // Prepare JSON Object for the request
                        JSONObject request = null;

                        final JSONParser jsonParser = new JSONParser();
                        request = (JSONObject) jsonParser.parse(currentLocation);
                                                
                        String[] wmsarray = (String[]) request.get("wms");
                        
                        if(wmsarray!=null && wmsarray.length>0) {
                            
                            final int n = wmsarray.length;
                            wmsarray = Arrays.copyOf(wmsarray, n + 1);
                            wmsarray[n] = wms;
                        } else {
                            wmsarray = new String[1];
                            wmsarray[0] = wms;
                        }
                        
                        request.put("wms", Arrays.toString(wmsarray));
                        request.put("wfs", wfs);

                        currentLocation = request.toString();
                    } else {
                        final JSONObject request = new JSONObject();
                        
                        String[] wmsarray = {wms};
                        
                        request.put("wms", Arrays.toString(wmsarray));
                        request.put("wfs", wfs);

                        currentLocation = request.toString();
                    }

                    // wms and wfs are always present
                    metadata.setLocation(currentLocation);
                    metadata.setGeoreferenced(true);

                    final boolean valid = this.metadataValidator
                            .validate(metadata, this.report);

                    if (valid) {
                        metadataList.add(metadata);
                    } else {
                        failedRecordsCount++;
                    }

                } catch (final Exception ex) {
                    failedRecordsCount++;
                    this.saveException(ex, IngestReportErrorType.SYSTEM_ERROR,
                            doc);

                }
                this.report.setFailedRecordsCount(failedRecordsCount);
                this.metadataIngester.ingest(metadataList, this.report);

                currentJob.setCompleted(true);
                currentJob = UploadJobQueue.getAJob();

            }

        } catch (final Exception e) {
            this.logger.error(
                    "Error in SingleFile Ingest: " + this.ingest.getName(), e);
            this.saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        }

    }
}
