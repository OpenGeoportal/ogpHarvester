package org.opengeoportal.harvester.mvc.exception;

/**
 * The Class UncompressStrategyException.
 */
public class UncompressStrategyException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new uncompress strategy exception.
     */
    public UncompressStrategyException() {
    }

    /**
     * Instantiates a new uncompress strategy exception.
     *
     * @param message
     *            the message
     */
    public UncompressStrategyException(final String message) {
        super(message);
    }
}
