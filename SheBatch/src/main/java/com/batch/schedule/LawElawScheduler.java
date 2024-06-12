package com.batch.schedule;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.service.LawService;

@Service
public class LawElawScheduler extends BaseScheduler {
    @Autowired
    private LawService lawService;

    private static final Logger logger = LoggerFactory.getLogger(LawElawScheduler.class);

    public LawElawScheduler(LawService lawService) {
        this.batchCd = "LAW_ELAW_SERVICE";
    }

    /**
     * 실행
     */
    public void run() throws Exception {
        String message = this.lawService.lawElawIF();
        if (message != null && !message.equals("")) {
            // 배치 로직 작성
            this.schedulerService.insertBatchRunnableLog(this.batchService, message);
        }
    }
}
