package org.opengeoportal.harvester.api.client.geonetwork.exception;


public class BadXmlResponseEx extends BadResponseEx {
    /**
     * @param message
     * @param cause
     */
    public BadXmlResponseEx(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public BadXmlResponseEx(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public BadXmlResponseEx(Throwable cause) {
        super(cause);
    }
}