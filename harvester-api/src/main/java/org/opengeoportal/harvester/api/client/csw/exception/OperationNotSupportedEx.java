package org.opengeoportal.harvester.api.client.csw.exception;


public class OperationNotSupportedEx extends CatalogException {
	private static final long serialVersionUID = 8791720423104623497L;

	public OperationNotSupportedEx(String name) {
		super(OPERATION_NOT_SUPPORTED, null, name);
	}

	public OperationNotSupportedEx(String name, CatalogException prev) {
		super(OPERATION_NOT_SUPPORTED, null, name, prev);
	}
}
