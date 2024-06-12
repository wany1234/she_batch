package com.batch.schedule;

import com.batch.utils.model.BatchService;

public interface IBaseScheduler {
    /**
     * 배치코드조회
     *
     * @return
     */
    public String getBatchCd();

    /**
     * 상태 조회
     *
     * @return
     */
    public boolean getState(BatchService batchService);

    /**
     * 스케줄러 정지
     */
    public void stopScheduler();

    /**
     * 스케줄러 시작
     */
    public void startScheduler(BatchService batchService);
}
