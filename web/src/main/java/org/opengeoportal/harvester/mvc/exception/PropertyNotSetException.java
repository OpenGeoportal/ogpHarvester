package org.opengeoportal.harvester.mvc.exception;

public class PropertyNotSetException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String property;

    public PropertyNotSetException(final String property) {
        super();
        this.property = property;
    }

    public String getProperty() {
        return this.property;
    }

}
