package org.opengeoportal.harvester.api.client.csw.exception;

public class VersionNegotiationFailedEx extends CatalogException {
    private static final long serialVersionUID = -3774978601874182838L;

    public VersionNegotiationFailedEx(final String versions) {
        super(CatalogException.VERSION_NEGOTIATION_FAILED, versions, null);
    }

    public VersionNegotiationFailedEx(final String versions,
            final CatalogException prev) {
        super(CatalogException.VERSION_NEGOTIATION_FAILED, versions, null,
                prev);
    }
}