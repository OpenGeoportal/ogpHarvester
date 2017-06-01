package org.opengeoportal.harvester.api.client.geonetwork.exception;

public abstract class BadResponseEx extends Exception {

    private static final long serialVersionUID = 4116425985878163669L;

    /**
     * @param message
     */
    public BadResponseEx(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BadResponseEx(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public BadResponseEx(final Throwable cause) {
        super(cause);
    }
}