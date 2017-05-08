package org.opengeoportal.harvester.mvc.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * Class to manage the uncompresss of tar gzip files.
 *
 * @author Jose García
 */

public class UncompressStrategyTarGz extends UncompressStrategyTar
        implements UncompressStrategy {

    /*
     * (non-Javadoc)
     *
     * @see org.opengeoportal.dataingest.utils.UncompressStrategyTar#
     * createTarArchiveInputStream(java.io.File)
     */
    @Override
    protected TarArchiveInputStream createTarArchiveInputStream(final File file)
            throws Exception {
        return new TarArchiveInputStream(new GzipCompressorInputStream(
                new BufferedInputStream(new FileInputStream(file))));
    }
}
