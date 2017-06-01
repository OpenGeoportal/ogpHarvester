package org.opengeoportal.harvester.api.client.geonetwork.exception;

public class BadSoapResponseEx extends BadResponseEx {

    private static final long serialVersionUID = -3441153507513692436L;

    /**
     * @param message
     */
    public BadSoapResponseEx(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BadSoapResponseEx(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public BadSoapResponseEx(final Throwable cause) {
        super(cause);
    }
}