package org.opengeoportal.harvester.api.client.geonetwork.exception;


public class BadXmlResponseEx extends BadResponseEx {

	private static final long serialVersionUID = -1877546510219712896L;

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