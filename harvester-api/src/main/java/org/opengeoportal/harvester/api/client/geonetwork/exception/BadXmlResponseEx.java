package org.opengeoportal.harvester.api.client.geonetwork.exception;

public class BadXmlResponseEx extends BadResponseEx {

    private static final long serialVersionUID = -1877546510219712896L;

    /**
     * @param message
     */
    public BadXmlResponseEx(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BadXmlResponseEx(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public BadXmlResponseEx(final Throwable cause) {
        super(cause);
    }
}