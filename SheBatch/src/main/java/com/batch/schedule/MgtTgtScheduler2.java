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
import com.batch.service.MgtTgtService;
import com.batch.service.MgtTgtService2;
import com.batch.utils.model.Batch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class MgtTgtScheduler2 extends BaseScheduler {
    @Autowired
    private LawService lawService;

    @Autowired
    private EduMasterService eduService;

    @Autowired
    private MgtTgtService mgtTgtService;

    @Autowired
    private MgtTgtService2 mgtTgtService2;

    @Autowired
    private BatchService batchService2;

    private static final Logger logger = LoggerFactory.getLogger(MgtTgtScheduler2.class);

    public MgtTgtScheduler2(EduMasterService eduMasterService) {
        this.batchCd = "TGT_PLANT_LMT_DT_SERVICE";
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

            // 1. 현재 날짜 추출
            LocalDate currentDate = LocalDate.now();

            // 2. 해당 분기월 계산
            int quarter = (currentDate.getMonthValue() - 1) / 3 + 1;

            // 3. 분기의 말일 계산
            LocalDate endOfQuarter = currentDate.with(TemporalAdjusters.lastDayOfMonth());

            // 4. 말일에서 10일 또는 배치서비스에 등록된 배치기준일수를 더한 날짜 계산
            LocalDate resultDate = endOfQuarter.plusDays(batchDay);

            // 5. 날짜를 "yyyy-MM-dd" 형식으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date = resultDate.format(formatter);

            // 현재 시간을 얻음
            LocalTime now = LocalTime.now();

            // 원하는 형식으로 시간을 포맷하기 위한 포매터
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HHmm");

            // 시간을 포맷하여 출력
            String formattedTime = now.format(formatter2);

            // if (formattedTime == cycleHm) {
            // 배치서비스 cycleHm과 현재 시간 (formattedTime이 일치할때 실행)
            String message = this.mgtTgtService2.getMgtTgtList2(date, batchDay);
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
