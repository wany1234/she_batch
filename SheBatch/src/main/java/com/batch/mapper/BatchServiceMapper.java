package com.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.batch.utils.model.BatchLog;
import com.batch.utils.model.BatchService;

@Mapper
@Repository("com.batch.mapper.BatchServiceMapper")
public interface BatchServiceMapper {

    /**
     * 배치서비스 목록 조회
     *
     * @return
     * @throws Exception
     */
    public List<BatchService> getBatchServices() throws Exception;

    /**
     * 배치로그 등록
     *
     * @param batchLog
     * @return
     * @throws Exception
     */
    public int insertBatchLog(BatchLog batchLog) throws Exception;

}
