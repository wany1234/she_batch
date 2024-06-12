package com.batch.schedule;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.batch.service.BatchService;
import com.batch.service.EduMasterService;
import com.batch.service.LawService;
import com.batch.utils.model.Batch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class EduScheduler extends BaseScheduler {
    @Autowired
    private LawService lawService;

    @Autowired
    private EduMasterService eduService;

    @Autowired
    private BatchService batchService2;

    private static final Logger logger = LoggerFactory.getLogger(EduScheduler.class);

    public EduScheduler(EduMasterService eduMasterService) {
        this.batchCd = "EDU_LMT_DT_SERVICE";
    }

    /**
     * 실행
     */

    public void run() throws Exception {
        try {
            // 배치 로직 작성
            Batch batch = this.batchService2.getBatch(batchCd);

            int batchDay = Integer.parseInt(batch.getBatchDay());

            String cycleHm = batch.getCycleHm();

            // 현재 날짜를 가져오기
            LocalDate currentDate = LocalDate.now();

            // 날짜를 문자열로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String currentDateStr = currentDate.format(formatter);
            // 7일 후 날짜 계산
            LocalDate after7Days = currentDate.plusDays(batchDay);

            // 7일 후 날짜를 문자열로 변환
            String eduSYmd = after7Days.format(formatter);

            // 현재 시간을 얻음
            LocalTime now = LocalTime.now();

            // 원하는 형식으로 시간을 포맷하기 위한 포매터
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HHmm");

            // 시간을 포맷하여 출력
            String formattedTime = now.format(formatter2);

            // if (formattedTime == cycleHm) {
            // 배치서비스 cycleHm과 현재 시간 (formattedTime이 일치할때 실행)
            String message = this.eduService.getUserList(eduSYmd);
            // if (message != null && !message.equals("")) {
            // 배치 로직 작성
            this.schedulerService.insertBatchRunnableLog(this.batchService, message);
            // }
            // }

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
