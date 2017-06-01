package org.opengeoportal.harvester.api.metadata.parser;

import java.util.Collections;
import java.util.List;

public class IngestResponse {
    public class IngestInfo {
        String field;
        String nativeName;
        String error;
        String message;

        IngestInfo(final String field, final String nativeName,
                final String error, final String message) {
            this.field = field;
            this.nativeName = nativeName;
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return this.error;
        }

        public String getField() {
            return this.field;
        }

        public String getMessage() {
            return this.message;
        }

        public String getNativeName() {
            return this.nativeName;
        }
    }

    protected List<IngestInfo> ingestErrors;

    protected List<IngestInfo> ingestWarnings;

    public void addError(final String field, final String nativeName,
            final String error, final String message) {
        this.ingestErrors
                .add(new IngestInfo(field, nativeName, error, message));
    }

    public void addWarning(final String field, final String nativeName,
            final String error, final String message) {
        this.ingestWarnings
                .add(new IngestInfo(field, nativeName, error, message));
    }

    public List<IngestInfo> getIngestErrors() {
        return Collections.unmodifiableList(this.ingestErrors);
    }

    public List<IngestInfo> getIngestWarnings() {
        return Collections.unmodifiableList(this.ingestWarnings);
    }
}
