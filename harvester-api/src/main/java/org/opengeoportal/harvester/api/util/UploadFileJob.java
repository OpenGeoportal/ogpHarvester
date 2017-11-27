package org.opengeoportal.harvester.api.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class UploadFileJob {

    private String workspace;
    private String dataset;
    private String wmsEndPoint;
    private String wfsEndPoint;
    private File file;
    private long assigned = 0;
    private boolean completed = false;
    private final Set<String> requiredFields = new HashSet<String>();


    public long getAssigned() {
        return this.assigned;
    }

    public String getDataset() {
        return this.dataset;
    }

    public File getFile() {
        return this.file;
    }

    public String getWfsEndPoint() {
        return this.wfsEndPoint;
    }

    public String getWmsEndPoint() {
        return this.wmsEndPoint;
    }

    public String getWorkspace() {
        return this.workspace;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void setAssigned(final long assigned) {
        this.assigned = assigned;
    }

    public void setCompleted(final boolean completed) {
        if (completed == true) {
            try {
                // To free disk space and memory
                this.file.delete();
                this.file = null;
            } catch (final Exception e) {
            }
        }
        this.completed = completed;
    }

    public void setDataset(final String dataset) {
        this.dataset = dataset;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public void setWfsEndPoint(final String wfsEndPoint) {
        this.wfsEndPoint = wfsEndPoint;
    }

    public void setWmsEndPoint(final String wmsEndPoint) {
        this.wmsEndPoint = wmsEndPoint;
    }

    public void setWorkspace(final String workspace) {
        this.workspace = workspace;
    }

    public Set<String> getRequiredFields() {
        return requiredFields;
    }

    @Override
    public String toString() {
        return "UploadFileJob [workspace=" + this.workspace + ", dataset="
                + this.dataset + ", wmsEndPoint=" + this.wmsEndPoint
                + ", wfsEndPoint=" + this.wfsEndPoint + ", file=" + this.file
                + ", assigned=" + this.assigned + ", completed="
                + this.completed + "]";
    }

}
