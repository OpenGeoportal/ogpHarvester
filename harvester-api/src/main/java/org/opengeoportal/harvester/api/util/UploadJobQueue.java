package org.opengeoportal.harvester.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UploadJobQueue {

    private static long TIMEOUT = 120000;

    private static List<UploadFileJob> list = Collections
            .synchronizedList(new ArrayList<UploadFileJob>());

    public static void addNewJob(final UploadFileJob job) {
        synchronized (UploadJobQueue.list) {
            // REMOVE A RANDOM COMPLETE JOB
            for (final UploadFileJob uploadFileJob : UploadJobQueue.list) {
                if (uploadFileJob.isCompleted()) {
                    UploadJobQueue.list.remove(uploadFileJob);
                    break;
                }
            }
            // ADD A NEW ONE
            UploadJobQueue.list.add(job);
        }
        for (final UploadFileJob uploadFileJob : UploadJobQueue.list) {
            System.out.println(uploadFileJob.toString());
        }

    }

    public static UploadFileJob getAJob() {
        UploadFileJob job = null;
        synchronized (UploadJobQueue.list) {
            final long curtime = System.currentTimeMillis();
            for (final UploadFileJob uploadFileJob : UploadJobQueue.list) {
                if (((curtime
                        - uploadFileJob.getAssigned()) > UploadJobQueue.TIMEOUT)
                        && !uploadFileJob.isCompleted()) {
                    job = uploadFileJob;
                    uploadFileJob.setAssigned(curtime);
                }
            }
        }
        return job;
    }

    public static List<UploadFileJob> getJobList() {

        return UploadJobQueue.list;
    }

}
