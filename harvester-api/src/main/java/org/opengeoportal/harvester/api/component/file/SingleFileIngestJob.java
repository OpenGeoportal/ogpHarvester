package org.opengeoportal.harvester.api.component.file;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.BaseXmlMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.FgdcMetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
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
            boolean processFinished = false;
            int page = 0;
            long failedRecordsCount = 0;

            File metadataFile = new File("");
            
            List<Metadata> metadataList = Lists
                    .newArrayListWithCapacity(1);

            while (!(isInterruptRequested() || processFinished)) {
                
                Document doc = null;

                    try {
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

                        metadataList.add(metadata);

                    } catch (Exception ex) {
                        failedRecordsCount++;
                        saveException(ex,
                                IngestReportErrorType.SYSTEM_ERROR, doc);

                    }
                report.setFailedRecordsCount(failedRecordsCount);
                metadataIngester.ingest(metadataList, report);


            }

        } catch (Exception e) {
            logger.error(
                    "Error in SingleFile Ingest: " + this.ingest.getName(), e);
            saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        }

    }
}
