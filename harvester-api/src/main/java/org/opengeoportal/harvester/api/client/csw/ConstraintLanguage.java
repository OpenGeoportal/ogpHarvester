package org.opengeoportal.harvester.api.client.csw;

import org.opengeoportal.harvester.api.client.csw.exception.MissingParameterValueEx;
import org.opengeoportal.harvester.api.client.csw.exception.InvalidParameterValueEx;

/**
 * Constraint language enum.
 *
 */
public enum ConstraintLanguage {
	CQL("CQL_TEXT"), FILTER("FILTER");

    private String language;

	private ConstraintLanguage(String language) {
        this.language = language;
    }

	public String toString() {
        return language;
    }

    /**
     * Parse a string containing the constraint language value.
     *
     * @param language
     * @return
     * @throws MissingParameterValueEx
     * @throws InvalidParameterValueEx
     */
	public static ConstraintLanguage parse(String language) throws MissingParameterValueEx, InvalidParameterValueEx {
		if (language == null)
			throw new MissingParameterValueEx("constraintLanguage");

		if (language.equals(CQL.toString())) return CQL;
		if (language.equals(FILTER.toString()))	return FILTER;

		throw new InvalidParameterValueEx("constraintLanguage", language);
	}
}