package com.batch.schedule;

import com.batch.service.SchedulerService;
import com.batch.utils.model.BatchService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

public abstract class BaseScheduler implements IBaseScheduler {

    @Autowired
    protected SchedulerService schedulerService;

    /**
     * -- GETTER --
     *  배치코드조회
     */
    @Getter
    @Setter
    protected String batchCd;
    private boolean state = false;
    protected BatchService batchService;
    private ThreadPoolTaskScheduler scheduler;

    private static final Logger logger = LoggerFactory.getLogger(LawElawScheduler.class);

    /**
     * 상태 조회
     *
     * @return
     */
    public boolean getState(BatchService batchService) {
        if (scheduler == null) {
            return false;
        }

        if (!this.batchService.checkCycle(batchService)) {
            this.stopScheduler();
        }

        return this.state;
    }

    /**
     * 스케줄러 정지
     */
    public void stopScheduler() {
        if (scheduler == null) {
            return;
        }

        this.state = false;
        this.scheduler.shutdown();
        try {
            this.schedulerService.insertBatchStopLog(this.batchService);
        } catch (SecurityException se) {
            logger.error(se.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 스케줄러 시작
     */
    public void startScheduler(BatchService batchService) {
        this.state = true;
        this.batchService = batchService;
        this.scheduler = new ThreadPoolTaskScheduler();
        this.scheduler.initialize();

        // 스케줄러 시작
        this.scheduler.schedule(this.getRunnable(), new PeriodicTrigger(1, TimeUnit.MINUTES));
        try {
            this.schedulerService.insertBatchStartLog(this.batchService);
        } catch (SecurityException se) {
            logger.error(se.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 실행내용
     *
     * @return
     */
    private Runnable getRunnable() {
        return () -> {
            if (this.batchService.isRunnable()) {
                try {
                    this.run();
                } catch (Exception e) {
                    try {
                        this.schedulerService.insertBatchExceptionLog(this.batchService, e);
                    } catch (Exception ee) {
                        logger.error(ee.getMessage());
                    }
                }
            }
        };
    }

    /**
     * 실행
     */
    public abstract void run() throws Exception;
}
