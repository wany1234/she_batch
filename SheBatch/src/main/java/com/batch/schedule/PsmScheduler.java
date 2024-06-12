package com.batch.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.service.PsmService;

@Service
public class PsmScheduler extends BaseScheduler {

    @Autowired
    private PsmService psmService;

    private static final Logger logger = LoggerFactory.getLogger(PsmScheduler.class);

    public PsmScheduler(PsmService psmService) {
        this.batchCd = "PSMDOCU_ALARM_SERVICE";
    }

    /**
     * 실행
     */
    public void run() throws Exception {
        String message = this.psmService.run(this.batchCd);
        if (message != null && !message.equals("")) {
            this.schedulerService.insertBatchRunnableLog(this.batchService, message);
        }
        try {

        } catch (Exception e) {
            // TODO: handle exception
            try {
                schedulerService.insertExceptionLog(e);
            } catch (Exception ee) {
                logger.error(ee.getMessage());
            }
        }

    }
}
