package org.opengeoportal.harvester.api.client.csw.exception;


public class NoApplicableCodeEx extends CatalogException {
	private static final long serialVersionUID = -2651752961299461115L;

	public NoApplicableCodeEx(String message) {
		super(NO_APPLICABLE_CODE, message, null);
	}

	public NoApplicableCodeEx(String message, String locator) {
		super(NO_APPLICABLE_CODE, message, locator);
	}

	public NoApplicableCodeEx(String message, CatalogException prev) {
		super(NO_APPLICABLE_CODE, message, null, prev);
	}
}