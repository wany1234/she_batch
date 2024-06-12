package com.batch.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.service.ImprService;

@Service
public class ImprArriveScheduler extends BaseScheduler {

    @Autowired
    private ImprService imprService;

    private static final Logger logger = LoggerFactory.getLogger(ImprArriveScheduler.class);

    public ImprArriveScheduler(ImprService imprService) {
        this.batchCd = "IMPR_ACT_LMT_DT_SERVICE"; // 개선조치 도래 알림
    }

    /**
     * 실행
     */
    public void run() throws Exception {
        try {

            String message = this.imprService.getImprArriveList();

            if (message != null && !message.equals("")) {
                // 배치 로직 작성
                this.schedulerService.insertBatchRunnableLog(this.batchService, message);
            }
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
