package org.opengeoportal.harvester.mvc.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * The Class FileConversionUtils.
 */
public final class FileConversionUtils {

    /**
     * Multipart to file.
     *
     * @param multipart
     *            the multipart
     * @return the file
     * @throws IllegalStateException
     *             the illegal state exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static File multipartToFile(final MultipartFile multipart)
            throws IllegalStateException, IOException {
        final File convFile = new File(
                "/tmp/" + multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }

    /**
     * Instantiates a new file conversion utils.
     */
    private FileConversionUtils() {

    }

}
