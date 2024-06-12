package com.batch.schedule;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.batch.service.SchedulerService;
import com.batch.utils.model.BatchService;

@Component
public class SchedulerRunner {

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private AlarmScheduler psmDocuAlarmScheduler;

    @Autowired
    private AlarmScheduler sampleAlarmScheduler;

    @Autowired
    private LawElawScheduler lawElawScheduler;

    @Autowired
    private LawMakingScheduler lawMakingScheduler;

    @Autowired
    private HrSyncScheduler hrSyncScheduler;

    @Autowired
    private VendorSyncScheduler vendorSyncScheduler;

    @Autowired
    private EduScheduler eduScheduler;

    @Autowired
    private MgtTgtScheduler mgtTgtScheduler;

    @Autowired
    private MgtTgtScheduler2 mgtTgtScheduler2;

    @Autowired
    private ImprArriveScheduler imprArriveScheduler;

    @Autowired
    private ImprOverScheduler imprOverScheduler;

    @Autowired
    private PsmScheduler psmScheduler;

    @Autowired
    private FacilityScheduler facilityScheduler;

    @Autowired
    private SendMailScheduler sendMailScheduler;

    private List<BaseScheduler> schedulerList;

    private static final Logger logger = LoggerFactory.getLogger(SchedulerRunner.class);

    /**
     * 메일 스케줄러
     */
    @Scheduled(fixedDelay = 600000) // 60000
    public void run() {
        try {
            // 메인 배치 실행 로그 : 주석처리함 - 배치별 로그만 남기도록 설정.
            if (schedulerList == null || schedulerList.size() == 0) {
                schedulerList = new ArrayList<BaseScheduler>();

                // 스케줄러 목록 등록
                schedulerList.add(lawElawScheduler);
                schedulerList.add(lawMakingScheduler);
                schedulerList.add(hrSyncScheduler);
                schedulerList.add(vendorSyncScheduler);
                schedulerList.add(mgtTgtScheduler);
                schedulerList.add(mgtTgtScheduler2);
                schedulerList.add(eduScheduler);
                schedulerList.add(imprArriveScheduler);
                schedulerList.add(imprOverScheduler);
                schedulerList.add(psmScheduler);
                schedulerList.add(facilityScheduler);
                schedulerList.add(sendMailScheduler);
                // 알람별로 배치CD 세팅이 필요
                // psmDocuAlarmScheduler.setBatchCd("PSMDOCU_ALARM_SERVICE");
                mgtTgtScheduler.setBatchCd("TGT_DEPT_LMT_DT_SERVICE");
                mgtTgtScheduler2.setBatchCd("TGT_PLANT_LMT_DT_SERVICE");
                eduScheduler.setBatchCd("EDU_LMT_DT_SERVICE");
                // schedulerList.add(psmDocuAlarmScheduler);
                imprArriveScheduler.setBatchCd("IMPR_ACT_LMT_DT_SERVICE");
                imprOverScheduler.setBatchCd("IMPR_OVER_ACT_LMT_DT_SERVICE");

                // 배치CD 세팅 Sample
                // sampleAlarmScheduler.setBatchCd("SAMPLE_ALARM_SERVICE");
                // schedulerList.add(sampleAlarmScheduler);
            }

            // 배치서비스 조회
            List<BatchService> batchList = schedulerService.getBatchServices();
            for (BatchService batchService : batchList) {
                for (BaseScheduler scheduler : schedulerList) {
                    if (scheduler.getBatchCd().equals(batchService.getBatchCd()) && !scheduler.getState(batchService)) {
                        scheduler.startScheduler(batchService);
                    }
                }
            }

        } catch (Exception e) {
            try {
                schedulerService.insertExceptionLog(e);
            } catch (Exception ee) {
                logger.error(ee.getMessage());
            }
        }
    }

}
