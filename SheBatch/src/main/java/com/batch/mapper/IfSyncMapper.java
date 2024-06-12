package com.batch.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface IfSyncMapper {
    /**
     * HR 사원정보 동기화
     * @return 결과
     * @throws Exception
     */
    public HashMap<String, String> hrUserSync() throws Exception;

    /**
     * SAP 협력사정보 동기화
     * @return 결과
     * @throws Exception
     */
    public HashMap<String, String> vendorSync() throws Exception;
}
