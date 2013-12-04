package org.opengeoportal.harvester.api.client.geonetwork.exception;


public class BadSoapResponseEx extends BadResponseEx {
    /**
     * @param message
     * @param cause
     */
    public BadSoapResponseEx(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public BadSoapResponseEx(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public BadSoapResponseEx(Throwable cause) {
        super(cause);
    }
}