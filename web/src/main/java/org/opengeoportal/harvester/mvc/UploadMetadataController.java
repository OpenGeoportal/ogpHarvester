package org.opengeoportal.harvester.mvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
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

/**
 * The Class UploadMetadataController.
 */
@Controller
public class UploadMetadataController {

    /** The Constant XML_EXTENSION. */
    private static final String XML_EXTENSION = ".xml";

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** The local solr url. */
    @Value("#{localSolr['localSolr.url']}")
    private String localSolrUrl;

    /** The data ingest url. */
    @Value("#{dataIngest['dataIngest.url']}")
    private String dataIngestUrl;

    /** The ingest service. */
    @Resource
    private IngestService ingestService;

    /**
     * Adds the metadata.
     *
     * @param workspace the workspace
     * @param dataset the dataset
     * @param requiredFields the required fields
     * @param file the file
     * @param request the request
     * @param response the response
     * @return the string
     */
    @RequestMapping(value = "/rest/uploadMetadata/add", method = RequestMethod.POST)
    @ResponseBody
    public String addMetadata(@RequestParam("workspace") final String workspace,
            @RequestParam("dataset") final String dataset,
            @RequestParam("requiredFields") final String requiredFields,
            @RequestPart("file") final MultipartFile file,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        try {
            final File zipFile = FileConversionUtils.multipartToFile(file);

            zipFile.getName();

            final File unzippedFile = this.uncompressFile(zipFile);

            if (unzippedFile != null) {

                final File[] metadataFiles = unzippedFile
                        .listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(final File dir,
                                    final String name) {
                                return (name.toLowerCase().endsWith(
                                        UploadMetadataController.XML_EXTENSION));
                            }
                        });

                if (metadataFiles.length == 0) {
                    return "No metadata.";
                }

                if (metadataFiles.length > 1) {
                    this.printOutputMessage(response,
                            HttpServletResponse.SC_BAD_REQUEST,
                            "The archive contains more than one metadata file.");
                    return "The archive contains more than one metadata file.";
                }

                String[] requiredFieldsArray = null;

                if ((requiredFields != null) && (requiredFields.length() > 0)) {

                    requiredFieldsArray = requiredFields.split(",");
                }

                this.checkMetadata(metadataFiles[0], requiredFieldsArray);
                this.addMetadataIngest(workspace, dataset, metadataFiles[0]);

            }

        } catch (final PropertyNotSetException e) {
            try {
                this.printOutputMessage(response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Missing required field in metadata: "
                                + e.getProperty());
                return "Missing required field in metadata: " + e.getProperty();
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        } catch (final UnsupportedMetadataType e) {
            try {
                this.printOutputMessage(response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Metatdata format is not supported");
                return "Metatdata format is not supported";
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        } catch (final IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Adds the metadata ingest.
     *
     * @param workspace the workspace
     * @param dataset the dataset
     * @param metadataFile the metadata file
     * @return true, if successful
     * @throws FileNotFoundException the file not found exception
     * @throws Exception the exception
     * @throws UnsupportedMetadataType the unsupported metadata type
     */
    private boolean addMetadataIngest(final String workspace,
            final String dataset, final File metadataFile)
            throws FileNotFoundException, Exception, UnsupportedMetadataType {

        // MANAGE THE UPLOAD JOB QUEUE HERE
        final UploadFileJob job = new UploadFileJob();
        job.setWorkspace(workspace);
        job.setDataset(dataset);
        job.setFile(metadataFile);

        // Get the values for WMS and WFS from data-ingest (from uploded
        // shapefile)
        try {
            final JSONObject json = JSONReader
                    .readJsonFromUrl(this.dataIngestUrl + "/workspaces/"
                            + workspace + "/datasets/" + dataset + "/");

            final String wms = (String) json.get("WMS");
            final String wfs = (String) json.get("WFS");

            job.setWmsEndPoint(wms);
            job.setWfsEndPoint(wfs);
        } catch (final Exception e) {
            this.logger.error("Unable to load WMS and WFS from Data-Ingest");
        }

        UploadJobQueue.addNewJob(job);

        // Check if the ingest process is already created
        final String name = "UPLOADED FILES METADATA INGESTER";
        Ingest ingest = this.ingestService.findByName(name);

        // START THE PROCESS
        try {
            if (ingest == null) {
                // Create the ingest process
                ingest = new IngestFileUpload();
                ingest.setName(name);
                ingest.setNameOgpRepository("");
                ingest.setFrequency(Frequency.EVERYXMINUTES);
                ingest.setScheduled(true);
                ingest.setUrl("LOCAL");
                ingest.setBeginDate(new Date(System.currentTimeMillis()));
                ingest = this.ingestService.saveAndSchedule(ingest);
            }
        } catch (final Exception e) {
            this.logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Check metadata.
     *
     * @param metadataFile the metadata file
     * @param requiredFields the required fields
     * @throws FileNotFoundException the file not found exception
     * @throws Exception the exception
     * @throws UnsupportedMetadataType the unsupported metadata type
     */
    private void checkMetadata(final File metadataFile,
            final String[] requiredFields)
            throws FileNotFoundException, Exception, UnsupportedMetadataType {

        final FileInputStream in = new FileInputStream(metadataFile);

        final Document doc = XmlUtil.load(in);

        final MetadataType metadataType = XmlUtil.getMetadataType(doc);

        BaseXmlMetadataParser parser;

        if (metadataType.equals(MetadataType.ISO_19139)) {
            parser = new Iso19139MetadataParser();
        } else if (metadataType.equals(MetadataType.FGDC)) {
            parser = new FgdcMetadataParser();
        } else {
            throw new UnsupportedMetadataType();
        }

        final MetadataParserResponse parsedMetadata = parser.parse(doc);

        final Metadata metadata = parsedMetadata.getMetadata();

        if (requiredFields != null) {
            for (final String property : requiredFields) {
                if (!metadata.hasValueForProperty(property)) {
                    throw new PropertyNotSetException(property);
                }
            }
        }

    }

    /**
     * Prints the output message.
     *
     * @param response
     *            the response
     * @param code
     *            the code
     * @param message
     *            the message
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void printOutputMessage(final HttpServletResponse response,
            final int code, final String message) throws IOException {
        response.setStatus(code);
        final PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println(message);
        response.flushBuffer();
    }

    /**
     * Uncompress file.
     *
     * @param packageFile the package file
     * @return the file
     * @throws Exception the exception
     */
    private File uncompressFile(final File packageFile) throws Exception {

        try {
            final File unzipDir = Files.createTempDir();

            packageFile.getName().endsWith("shp.zip");
            packageFile.getName().replace(".shp.zip", "");
            FilenameUtils.removeExtension(packageFile.getName());

            final String packageExtension = (packageFile.getName()
                    .endsWith("shp.zip")) ? "shp.zip"
                            : FilenameUtils.getExtension(packageFile.getName());

            UncompressStrategyFactory.getUncompressStrategy(packageExtension)
                    .uncompress(packageFile, unzipDir);

            return unzipDir;
        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }

    }
}
