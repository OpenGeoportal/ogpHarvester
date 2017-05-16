package org.opengeoportal.harvester.mvc.exception;

public class PropertyNotSetException extends Exception {
    
    private String property;

    public PropertyNotSetException(String property) {
        super();
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

}
