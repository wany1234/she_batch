package com.batch.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.service.SendMailService;

@Service
public class SendMailScheduler extends BaseScheduler {

    @Autowired
    private SendMailService sendMailService;

    private static final Logger logger = LoggerFactory.getLogger(SendMailScheduler.class);

    public SendMailScheduler() {
        this.batchCd = "SEND_MAIL_SERVICE";
    }

    /**
     * 실행
     */
    public void run() throws Exception {
        String message = this.sendMailService.sendCheckUsers();
        if (message != null && !message.equals("")) {
            // 배치 로직 작성
            this.schedulerService.insertBatchRunnableLog(this.batchService, message);
        }
    }
}
