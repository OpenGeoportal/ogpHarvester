package org.opengeoportal.harvester.api.client.csw.exception;


public class InvalidUpdateSequenceEx extends CatalogException{
	private static final long serialVersionUID = 8899240805483632480L;

	public InvalidUpdateSequenceEx(String value)
	{
		super(INVALID_UPDATE_SEQUENCE, value, null);
	}

	public InvalidUpdateSequenceEx(String value, CatalogException prev)
	{
		super(INVALID_UPDATE_SEQUENCE, value, null, prev);
	}
}