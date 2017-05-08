package org.opengeoportal.harvester.mvc.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.opengeoportal.harvester.mvc.exception.UncompressStrategyException;

/**
 * Class to manage the uncompresss of 7z files.
 *
 * @author Jose García
 */

public class UncompressStrategy7z implements UncompressStrategy {

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
        SevenZFile sevenZFile = null;
        final OutputStream out = null;

        try {
            sevenZFile = new SevenZFile(file);
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();

            while (entry != null) {
                byte[] btoRead = new byte[1024];
                final BufferedOutputStream bout = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(uncompressDir.getAbsolutePath(),
                                        entry.getName())));

                try {
                    int len = 0;

                    while ((len = sevenZFile.read(btoRead)) != -1) {
                        bout.write(btoRead, 0, len);
                    }

                    btoRead = null;
                } finally {
                    IOUtils.closeQuietly(bout);
                }

                entry = sevenZFile.getNextEntry();
            }

        } catch (final Exception ex) {
            ex.printStackTrace();
            FileUtils.deleteQuietly(uncompressDir);
            throw new UncompressStrategyException(ex.getMessage());

        } finally {
            IOUtils.closeQuietly(sevenZFile);
        }
    }
}
