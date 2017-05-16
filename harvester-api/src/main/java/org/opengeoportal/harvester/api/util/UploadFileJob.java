package org.opengeoportal.harvester.api.util;

import java.io.File;

public class UploadFileJob {
    
    private String workspace;
    private String dataset;
    private String wmsEndPoint;
    private String wfsEndPoint;
    private File file;
    private long assigned = 0;
    private boolean completed = false;
    
    public String getWorkspace() {
        return workspace;
    }
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
    public String getDataset() {
        return dataset;
    }
    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        if(completed == true) {
            try{
                // To free disk space and memory
                file.delete();
                file = null;
            } catch (Exception e) {}
        }
        this.completed = completed;
    }
    public long getAssigned() {
        return assigned;
    }
    public void setAssigned(long assigned) {
        this.assigned = assigned;
    }   
    public String getWmsEndPoint() {
        return wmsEndPoint;
    }
    public void setWmsEndPoint(String wmsEndPoint) {
        this.wmsEndPoint = wmsEndPoint;
    }
    public String getWfsEndPoint() {
        return wfsEndPoint;
    }
    public void setWfsEndPoint(String wfsEndPoint) {
        this.wfsEndPoint = wfsEndPoint;
    }
    
    @Override
    public String toString() {
        return "UploadFileJob [workspace=" + workspace + ", dataset=" + dataset
                + ", wmsEndPoint=" + wmsEndPoint + ", wfsEndPoint="
                + wfsEndPoint + ", file=" + file + ", assigned=" + assigned
                + ", completed=" + completed + "]";
    }
    
    

}
