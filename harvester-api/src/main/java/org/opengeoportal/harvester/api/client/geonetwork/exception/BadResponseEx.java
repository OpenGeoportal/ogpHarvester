package org.opengeoportal.harvester.api.client.geonetwork.exception;

public abstract class BadResponseEx extends Exception {
    /**
     * @param message
     * @param cause
     */
    public BadResponseEx(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public BadResponseEx(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public BadResponseEx(Throwable cause) {
        super(cause);
    }
}