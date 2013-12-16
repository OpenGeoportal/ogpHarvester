package org.opengeoportal.harvester.api.client.csw.exception;


public class VersionNegotiationFailedEx extends CatalogException {
    private static final long serialVersionUID = -3774978601874182838L;

    public VersionNegotiationFailedEx(String versions) {
		super(VERSION_NEGOTIATION_FAILED, versions, null);
	}


	public VersionNegotiationFailedEx(String versions, CatalogException prev) {
		super(VERSION_NEGOTIATION_FAILED, versions, null, prev);
	}
}