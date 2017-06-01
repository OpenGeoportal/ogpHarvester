package org.opengeoportal.harvester.api.client.csw.exception;

public class OperationNotSupportedEx extends CatalogException {
    private static final long serialVersionUID = 8791720423104623497L;

    public OperationNotSupportedEx(final String name) {
        super(CatalogException.OPERATION_NOT_SUPPORTED, null, name);
    }

    public OperationNotSupportedEx(final String name,
            final CatalogException prev) {
        super(CatalogException.OPERATION_NOT_SUPPORTED, null, name, prev);
    }
}
