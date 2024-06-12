package com.batch.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.service.LawService;

@Service
public class LawMakingScheduler extends BaseScheduler {

    @Autowired
    private LawService lawService;

    private static final Logger logger = LoggerFactory.getLogger(LawMakingScheduler.class);

    public LawMakingScheduler() {
        this.batchCd = "LAW_MAKING_SERVICE";
    }

    /**
     * 실행
     */
    public void run() throws Exception {
        String message = this.lawService.lawMakingIF();
        if (message != null && !message.equals("")) {
            // 배치 로직 작성
            this.schedulerService.insertBatchRunnableLog(this.batchService, message);
        }
    }
}
