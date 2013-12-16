package org.opengeoportal.harvester.api.client.csw;

import org.opengeoportal.harvester.api.client.csw.exception.InvalidParameterValueEx;

/**
 * Result type enum.
 */
public enum ResultType {
	HITS("hits"),
    RESULTS("results"),
    VALIDATE("validate");

    private String type;

	private ResultType(String type) {
        this.type = type;
    }

	public String toString() {
        return type;
    }

	public static ResultType parse(String type) throws InvalidParameterValueEx {
		if (type == null) {
            return HITS;
        }
		for (ResultType rtype : ResultType.values()) {
			if (type.equals(rtype.toString()))
				return rtype;
		}
		throw new InvalidParameterValueEx("resultType", type);
	}

}