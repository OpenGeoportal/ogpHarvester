package org.opengeoportal.harvester.api.client.csw.exception;


public class InvalidParameterValueEx extends CatalogException {
	private static final long serialVersionUID = 2351490694044128159L;

	public InvalidParameterValueEx(String name, String value)
	{
		super(INVALID_PARAMETER_VALUE, value, name);
	}

	public InvalidParameterValueEx(String name, String value, CatalogException prev)
	{
		super(INVALID_PARAMETER_VALUE, value, name, prev);
	}
}