package com.batch.service;

import com.batch.mapper.IfSyncMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;

@Service
public class IfSyncService {

    @Autowired
    private IfSyncMapper ifSyncMapper;

    private static final Logger logger = LoggerFactory.getLogger(IfSyncService.class);

    /**
     * HR 사원정보 동기화
     */
    @Transactional
    public String hrUserSync() throws Exception {
        String resultMsg = "";
        try {
            logger.info("### HR 사원정보 동기화 ###");
            HashMap<String, String> resultMap = new HashMap<>();
            String result;
            String errorProcedure;
            String errorMessage;

            resultMap = ifSyncMapper.hrUserSync();
            result = resultMap.getOrDefault("result", "");
            errorMessage = resultMap.getOrDefault("ErrorMessage", "");
            errorProcedure = resultMap.getOrDefault("ErrorProcedure", "");

            // result 값이 1이 아니거나 없을 경우 오류
            if (!result.equals("1")) {
                String errMsg = String.format("사원정보 동기화중 오류가 발생하였습니다. [%s]: %s", errorProcedure, errorMessage);
                throw new Exception(errMsg);
            }

            resultMsg = "SUCCESS";
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return resultMsg;
    }

    /**
     * SAP 협력사정보 동기화
     */
    @Transactional
    public String vendorSync() throws Exception {
        String resultMsg = "";
        try {
            logger.info("### SAP 협력사정보 동기화 ###");
            HashMap<String, String> resultMap = new HashMap<>();
            String result;
            String errorProcedure;
            String errorMessage;

            resultMap = ifSyncMapper.vendorSync();
            result = resultMap.getOrDefault("result", "");
            errorMessage = resultMap.getOrDefault("ErrorMessage", "");
            errorProcedure = resultMap.getOrDefault("ErrorProcedure", "");

            // result 값이 1이 아니거나 없을 경우 오류
            if (!result.equals("1")) {
                String errMsg = String.format("협력사정보 동기화중 오류가 발생하였습니다. [%s]: %s", errorProcedure, errorMessage);
                throw new Exception(errMsg);
            }

            resultMsg = "SUCCESS";
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return resultMsg;
    }
}
