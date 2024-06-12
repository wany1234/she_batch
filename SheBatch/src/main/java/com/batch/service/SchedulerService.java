package com.batch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.mapper.BatchServiceMapper;
import com.batch.utils.model.BatchLog;
import com.batch.utils.model.BatchService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SchedulerService {

    @Autowired
    private BatchServiceMapper batchServiceMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 배치 로그작성
     *
     * @param batchLog
     * @return
     * @throws Exception
     */
    private int insertBatchLog(BatchLog batchLog) throws Exception {
        return this.batchServiceMapper.insertBatchLog(batchLog);
    }

    /**
     * 배치 오류 로그작성
     *
     * @param message
     * @return
     * @throws Exception
     */
    public int insertExceptionLog(Exception e) throws Exception {
        String remark = objectMapper.writeValueAsString(e);

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchCd("");
        batchLog.setRunResult("SHE 배치 프로그램 오류: " + e.getMessage());
        batchLog.setRemark(remark);

        return this.insertBatchLog(batchLog);
    }

    /**
     * 배치 실행중 여부 로그작성
     *
     * @return
     * @throws Exception
     */
    public int insertRunnableLog() throws Exception {
        BatchLog batchLog = new BatchLog();
        batchLog.setBatchCd("SHE_BATCH");
        batchLog.setRunResult("SHE 배치 프로그램 정상 실행");

        return this.insertBatchLog(batchLog);
    }

    /**
     * 배치서비스 정상시작 로그작성
     *
     * @param batchService
     * @return
     * @throws Exception
     */
    public int insertBatchStartLog(BatchService batchService) throws Exception {
        String remark = objectMapper.writeValueAsString(batchService);

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchCd(batchService.getBatchCd());
        batchLog.setRunResult("배치작업 정상 시작");
        batchLog.setRemark(remark);

        return this.insertBatchLog(batchLog);
    }

    /**
     * 배치서비스 정상중지 로그작성
     *
     * @param batchService
     * @return
     * @throws Exception
     */
    public int insertBatchStopLog(BatchService batchService) throws Exception {
        String remark = objectMapper.writeValueAsString(batchService);

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchCd(batchService.getBatchCd());
        batchLog.setRunResult("배치작업 정상 종료");
        batchLog.setRemark(remark);

        return this.insertBatchLog(batchLog);
    }

    /**
     * 배치서비스 정상실행 로그작성
     *
     * @param batchService
     * @param message
     * @return
     * @throws Exception
     */
    public int insertBatchRunnableLog(BatchService batchService, String message) throws Exception {
        String remark = objectMapper.writeValueAsString(batchService);

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchCd(batchService.getBatchCd());
        batchLog.setRunResult("배치작업 정상 실행: " + message);
        batchLog.setRemark(remark);

        return this.insertBatchLog(batchLog);
    }

    /**
     * 배치서비스 오류 로그작성
     *
     * @param batchService
     * @param message
     * @return
     * @throws Exception
     */
    public int insertBatchExceptionLog(BatchService batchService, Exception e) throws Exception {
        String remark = objectMapper.writeValueAsString(e);

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchCd(batchService.getBatchCd());
        batchLog.setRunResult("배치작업 오류: " + e.getMessage());
        batchLog.setRemark(remark);

        return this.insertBatchLog(batchLog);
    }

    /**
     * 배치목록조회
     *
     * @return
     * @throws Exception
     */
    public List<BatchService> getBatchServices() throws Exception {
        return this.batchServiceMapper.getBatchServices();
    }

}
