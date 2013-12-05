package org.opengeoportal.harvester.api.metadata.parser;

import java.util.Collections;
import java.util.List;

public class IngestResponse {
    protected List<IngestInfo> ingestErrors;
    protected List<IngestInfo> ingestWarnings;

    public List<IngestInfo> getIngestErrors() {
        return Collections.unmodifiableList(ingestErrors);
    }

    public List<IngestInfo> getIngestWarnings() {
        return Collections.unmodifiableList(ingestWarnings);
    }

	public void addError(String field, String nativeName, String error, String message){
		ingestErrors.add(new IngestInfo(field, nativeName, error, message));
	}
	
	public void addWarning(String field, String nativeName, String error, String message){
		ingestWarnings.add(new IngestInfo(field, nativeName, error, message));
	}
	
	public class IngestInfo {
		String field;
		String nativeName;
		String error;
		String message;
		
		IngestInfo(String field, String nativeName, String error, String message){
			this.field = field;
			this.nativeName = nativeName;
			this.error = error;
			this.message = message;
		}

		public String getField() {
			return field;
		}

		public String getNativeName() {
			return nativeName;
		}

		public String getError() {
			return error;
		}

		public String getMessage() {
			return message;
		}
	}
}
