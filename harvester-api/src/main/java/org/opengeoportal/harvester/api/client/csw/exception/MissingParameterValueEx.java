package org.opengeoportal.harvester.api.client.csw.exception;


public class MissingParameterValueEx extends CatalogException {
	private static final long serialVersionUID = -5797750194252680005L;

	public MissingParameterValueEx(String name) {
		super(MISSING_PARAMETER_VALUE, null, name);
	}

	public MissingParameterValueEx(String name, CatalogException prev) {
		super(MISSING_PARAMETER_VALUE, null, name, prev);
	}
}