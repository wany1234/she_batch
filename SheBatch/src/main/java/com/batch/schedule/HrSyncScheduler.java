package com.batch.schedule;

import com.batch.service.IfSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.batch.service.LawService;
import org.springframework.stereotype.Service;

@Service
public class HrSyncScheduler extends BaseScheduler {

    @Autowired
    private IfSyncService ifSyncService;

    private static final Logger logger = LoggerFactory.getLogger(HrSyncScheduler.class);

    public HrSyncScheduler() {
        this.batchCd = "HR_SYNC_SERVICE";
    }

    /**
     * 실행
     */
    public void run() throws Exception {
        String message = this.ifSyncService.hrUserSync();
        if (message != null && !message.equals("")) {
            // 배치 로직 작성
            this.schedulerService.insertBatchRunnableLog(this.batchService, message);
        }
    }
}
