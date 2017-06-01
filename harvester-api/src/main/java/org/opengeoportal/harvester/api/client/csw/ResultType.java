package org.opengeoportal.harvester.api.client.csw;

import org.opengeoportal.harvester.api.client.csw.exception.InvalidParameterValueEx;

/**
 * Result type enum.
 */
public enum ResultType {
    HITS("hits"), RESULTS("results"), VALIDATE("validate");

    public static ResultType parse(final String type)
            throws InvalidParameterValueEx {
        if (type == null) {
            return HITS;
        }
        for (final ResultType rtype : ResultType.values()) {
            if (type.equals(rtype.toString())) {
                return rtype;
            }
        }
        throw new InvalidParameterValueEx("resultType", type);
    }

    private String type;

    private ResultType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }

}