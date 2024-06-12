package com.batch.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class JcoUtil {

    private static final Logger logger = LoggerFactory.getLogger(JcoUtil.class);

    /**
     * SAP 테이블 데이터 조회
     * 
     * @param functionName
     *            RFC함수명
     * @param tableName
     *            조회할테이블명
     * @return
     */
    public static List<Map<String, Object>> getSapTableData(String functionName, String tableName) {
        return getSapTableData(functionName, tableName, null);
    }

    /**
     * SAP 테이블 데이터 조회
     * 
     * @param functionName
     *            RFC함수명
     * @param tableName
     *            조회할테이블명
     * @param params
     *            입력파라미터
     * @return
     */
    public static List<Map<String, Object>> getSapTableData(String functionName, String tableName,
            Map<String, Object> params) {
        return getSapTableData(functionName, tableName, params, false);
    }

    /**
     * SAP 테이블 데이터 조회
     * 
     * @param functionName
     *            RFC함수명
     * @param tableName
     *            조회할테이블명
     * @param params
     *            입력파라미터
     * @param isNeedTime
     *            날짜에 시분초 추가 여부
     * @return
     */
    public static List<Map<String, Object>> getSapTableData(String functionName, String tableName,
            Map<String, Object> params, boolean isNeedTime) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        try {
            logger.info("#### getSapTableData start ####");
            SapDestinationMgr.start();

            logger.info("#### SapDestinationMgr start ####");

            JCoDestination jCoDestination = JCoDestinationManager.getDestination("ABAP_AS_WITHOUT_POOL");

            logger.info("#### Attributes: " + jCoDestination.getAttributes()); // 연결정보확인

            JCoFunction function = jCoDestination.getRepository().getFunction(functionName); // RFC함수호출

            if (function == null) {
                throw new RuntimeException(functionName + " not found in SAP.");
            }

            // 파라미터 셋팅
            if (params != null) {
                for (Map.Entry<String, Object> element : params.entrySet()) {
                    function.getImportParameterList().setValue(element.getKey(), element.getValue());
                }
            }

            function.execute(jCoDestination); // REC실행
            logger.info("#### RFC execute ####");

            JCoParameterList emlist = function.getExportParameterList();
            logger.info("#### result code: " + emlist.getString("E_MSGTYP"));
            logger.info("#### result message: " + emlist.getString("E_MESSAGE"));

            // 펑션에서 테이블 호출
            JCoTable codes = function.getTableParameterList().getTable(tableName);
            logger.info("#### SAP getNumRows : " + codes.getNumRows());

            // 데이터 추출
            for (int j = 0; j < codes.getNumRows(); j++) {
                codes.setRow(j);
                JCoFieldIterator iter = codes.getFieldIterator();
                Map<String, Object> map = new HashMap<String, Object>();

                while (iter.hasNextField()) {
                    JCoField field = iter.nextField();
                    map.put(field.getName(), codes.getValue(field.getName()));
                    // date 형식은 yyyymmdd String 으로 변경하여 저장
                    if (map.get(field.getName()) instanceof Date) {
                        if (isNeedTime) {
                            // 용수량은 시분초가 필요하여 추가
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            map.replace(field.getName(), dateFormat.format(codes.getValue(field.getName())));
                        } else {
                            // 기존은 년월일만 필요
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                            map.replace(field.getName(), dateFormat.format(codes.getValue(field.getName())));
                        }
                    }
                }

                resultList.add(map);
            }
            logger.info("#### getSapTableData end ####");

        } catch (IOException e) {
        	logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return resultList;
    }
}
