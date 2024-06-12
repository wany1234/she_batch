package com.batch.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.service.FacilityService;
import com.batch.service.PsmService;

@Service
public class FacilityScheduler extends BaseScheduler {

    @Autowired
    private FacilityService facilityService;

    private static final Logger logger = LoggerFactory.getLogger(FacilityScheduler.class);

    public FacilityScheduler(PsmService psmService) {
        this.batchCd = "FAC_CHK_DT_SERVICE";
    }

    /**
     * 실행
     */
    public void run() throws Exception {
        String message = this.facilityService.run(this.batchCd);
        if (message != null && !message.equals("")) {
            // 배치 로직 작성
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
