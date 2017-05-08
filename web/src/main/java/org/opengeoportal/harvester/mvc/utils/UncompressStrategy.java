package org.opengeoportal.harvester.mvc.utils;

import java.io.File;

import org.opengeoportal.harvester.mvc.exception.UncompressStrategyException;

/**
 * The Interface UncompressStrategy.
 */
public interface UncompressStrategy {

    /**
     * Uncompress.
     *
     * @param file
     *            the file
     * @param uncompressDir
     *            the uncompress dir
     * @throws UncompressStrategyException
     *             the uncompress strategy exception
     */
    void uncompress(File file, File uncompressDir)
            throws UncompressStrategyException;
}
