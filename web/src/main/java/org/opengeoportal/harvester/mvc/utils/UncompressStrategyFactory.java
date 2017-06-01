package org.opengeoportal.harvester.mvc.utils;

import org.opengeoportal.harvester.mvc.exception.UncompressStrategyException;

/**
 * A factory for creating UncompressStrategy objects.
 */
public final class UncompressStrategyFactory {

    /**
     * Gets the uncompress strategy.
     *
     * @param type
     *            the type
     * @return the uncompress strategy
     * @throws UncompressStrategyException
     *             the uncompress strategy exception
     */
    public static UncompressStrategy getUncompressStrategy(final String type)
            throws UncompressStrategyException {
        if (type.equalsIgnoreCase("zip") || type.equalsIgnoreCase("shz")
                || type.equalsIgnoreCase("shp.zip")) {
            return new UncompressStrategyZip();
        } else if (type.equalsIgnoreCase("7z")) {
            return new UncompressStrategy7z();
        } else if (type.equalsIgnoreCase("tar")) {
            return new UncompressStrategyTar();
        } else if (type.equalsIgnoreCase("gz")
                || type.equalsIgnoreCase("tgz")) {
            return new UncompressStrategyTarGz();
        } else {
            throw new UncompressStrategyException("Format not supported");
        }
    }

    /**
     * No constructor.
     */
    private UncompressStrategyFactory() {

    }
}
