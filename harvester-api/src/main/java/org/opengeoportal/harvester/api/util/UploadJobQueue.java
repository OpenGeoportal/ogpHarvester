package org.opengeoportal.harvester.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UploadJobQueue {
    
    private static long TIMEOUT = 120000;
    
   private static List<UploadFileJob> list = Collections.synchronizedList(new ArrayList<UploadFileJob>());
    
    public static List<UploadFileJob> getJobList() {
        
        return list;
    }
    
    public static void addNewJob(UploadFileJob job) {        
        synchronized (list) {          
        
            list.add(job);
        }
        for (UploadFileJob uploadFileJob : list) {
            System.out.println(uploadFileJob.toString());
        }
        
    }
    
    public static UploadFileJob getAJob() {
        UploadFileJob job = null;
        synchronized (list) {          
            long curtime = System.currentTimeMillis();
            for (UploadFileJob uploadFileJob : list) {
                if(curtime - uploadFileJob.getAssigned() > TIMEOUT && !uploadFileJob.isCompleted()) {
                    job = uploadFileJob;
                    uploadFileJob.setAssigned(curtime);
                }
            }
        }
        return job;        
    }

}
