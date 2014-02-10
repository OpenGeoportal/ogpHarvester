package org.opengeoportal.harvester.api.component;

import java.util.Set;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportError;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;

public class MetadataValidator {
	private Ingest ingest;

	/** Report error service. */
	private IngestReportErrorService reportErrorService;

	public MetadataValidator(Ingest ingest,
			IngestReportErrorService reportErrorService) {
		this.ingest = ingest;
		this.reportErrorService = reportErrorService;
	}

	public boolean validate(Metadata metadata, IngestReport report) {
		Set<String> requiredFields = ingest.getRequiredFields();
		boolean isValid = true;

		for (String field : ingest.getRequiredFields()) {
			boolean hasValue = metadata.hasValueForProperty(field);

			if (!hasValue) {
				if (requiredFields.contains(field)) {
					// Add required empty field error
					IngestReportError error = new IngestReportError();
					error.setField(field);
					error.setType(IngestReportErrorType.REQUIRED_FIELD_ERROR);
					error.setMessage("Required field (" + field
							+ ") has no value.");
					error.setReport(report);
					error.setMetadata(metadata.getOriginalMetadata());
					error = reportErrorService.save(error);
					// report.addError(error);

					isValid = false;
				} else {
					// Track a warning if the field is not required, but it's
					// empty
					report.increaseUnrequiredFieldWarnings();
				}
			}
		}

		return isValid;
	}
}
