package org.opengeoportal.harvester.mvc.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.opengeoportal.harvester.mvc.exception.UncompressStrategyException;

/**
 * Class to manage the uncompresss of tar files.
 *
 * @author Jose García
 */

public class UncompressStrategyTar implements UncompressStrategy {

    /**
     * Creates the tar archive input stream.
     *
     * @param file
     *            the file
     * @return the tar archive input stream
     * @throws Exception
     *             the exception
     */
    protected TarArchiveInputStream createTarArchiveInputStream(final File file)
            throws Exception {
        return new TarArchiveInputStream(
                new BufferedInputStream(new FileInputStream(file)));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opengeoportal.dataingest.utils.UncompressStrategy#uncompress(java.io.
     * File, java.io.File)
     */
    @Override
    public void uncompress(final File file, final File uncompressDir)
            throws UncompressStrategyException {
        uncompressDir.mkdir();
        TarArchiveInputStream tarIn = null;
        OutputStream out = null;

        try {
            tarIn = this.createTarArchiveInputStream(file);

            TarArchiveEntry tarEntry = tarIn.getNextTarEntry();

            while (tarEntry != null) {
                // create a file with the same name as the tarEntry
                final File destPath = new File(uncompressDir,
                        tarEntry.getName());

                if (tarEntry.isDirectory()) {
                    destPath.mkdirs();

                } else {
                    destPath.createNewFile();

                    try {
                        out = new FileOutputStream(destPath);
                        IOUtils.copy(tarIn, out);
                    } finally {
                        IOUtils.closeQuietly(out);
                    }

                }

                tarEntry = tarIn.getNextTarEntry();
            }

        } catch (final Exception ex) {
            ex.printStackTrace();
            FileUtils.deleteQuietly(uncompressDir);
            throw new UncompressStrategyException(ex.getMessage());

        } finally {
            IOUtils.closeQuietly(tarIn);
        }
    }
}
