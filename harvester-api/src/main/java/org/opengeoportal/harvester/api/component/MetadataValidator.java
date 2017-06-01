package org.opengeoportal.harvester.api.component;

import java.util.Set;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportError;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.domain.IngestReportWarning;
import org.opengeoportal.harvester.api.domain.IngestReportWarningType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportWarningsService;

public class MetadataValidator {

    private final Ingest ingest;

    /**
     * Report error service.
     */
    private final IngestReportErrorService reportErrorService;

    private final IngestReportWarningsService reportWarningsService;

    public MetadataValidator(final Ingest ingest,
            final IngestReportErrorService reportErrorService,
            final IngestReportWarningsService reportWarningService) {
        this.ingest = ingest;
        this.reportErrorService = reportErrorService;
        this.reportWarningsService = reportWarningService;
    }

    public boolean validate(final Metadata metadata,
            final IngestReport report) {
        final Set<String> requiredFields = this.ingest.getRequiredFields();
        boolean isValid = true;

        for (final String field : this.ingest.getValidRequiredFields()) {
            final boolean hasValue = metadata.hasValueForProperty(field);

            if (!hasValue) {
                if (requiredFields.contains(field)) {
                    // Add required empty field error
                    IngestReportError error = new IngestReportError();
                    error.setField(field);
                    error.setType(IngestReportErrorType.REQUIRED_FIELD_ERROR);
                    error.setMessage(
                            "Required field (" + field + ") has no value.");
                    error.setReport(report);
                    error.setMetadata(metadata.getOriginalMetadata());
                    error = this.reportErrorService.save(error);

                    isValid = false;
                } else {
                    // Track a warning if the field is not required, but it's
                    // empty
                    // TODO: the warnings section should be analogous to the
                    // errors section
                    // Add required empty field error
                    IngestReportWarning warning = new IngestReportWarning();
                    warning.setField(field);
                    warning.setType(
                            IngestReportWarningType.UNREQUIRED_FIELD_WARNING);
                    warning.setMessage(
                            "Required field (" + field + ") has no value.");
                    warning.setReport(report);
                    warning = this.reportWarningsService.save(warning);
                    // report.increaseUnrequiredFieldWarnings();
                }
            }
        }

        return isValid;
    }
}
