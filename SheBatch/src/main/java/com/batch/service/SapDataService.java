package com.batch.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batch.mapper.SapDataMapper;
import com.batch.utils.JcoUtil;

@Service
public class SapDataService {

    private static final Logger logger = LoggerFactory.getLogger(SapDataService.class);

    @Autowired
    private SapDataMapper sapDataMapper;

    /**
     * SAP 거래처 정보 등록
     * 
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapVendorData() throws Exception {
        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_VENDOR", "ET_VEN");
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### vendor data list size: " + sapDataList.size());
            logger.info("### vendor data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                param.put("SHA256", DigestUtils.sha256Hex(String.valueOf(param.get("LIFNR"))));
                result += sapDataMapper.mergeSapVendorData(param);
            }

            logger.info("### vendor data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 자재마스터 등록
     * 
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapMatData() throws Exception {
        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_MAT", "ET_MAT");
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### mat data list size: " + sapDataList.size());
            logger.info("### mat data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapMatData(param);
            }

            logger.info("### mat data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 화학물질실적 월 정보 등록
     * 
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapMatInoutData() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        String beforeMonth = new SimpleDateFormat("yyyyMM").format(calendar.getTime());

        params.put("I_SPMON", beforeMonth);
        logger.info("### mat inout data param(I_SPMON): " + beforeMonth);

        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_CHEMICAL_STOC", "ET_STOC", params);

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### mat inout data list size: " + sapDataList.size());
            sapDataMapper.deleteSapMatInoutData(beforeMonth);

            logger.info("### mat inout data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.insertSapMatInoutData(param);
            }

            logger.info("### mat inout data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 화학물질식적 일 정보 등록
     * 
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapMatStrgStocData() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        Date today = new Date();
        String currentMonth = new SimpleDateFormat("yyyyMM").format(today);

        params.put("I_SPMON", currentMonth);
        logger.info("### mat strg stoc data param(I_SPMON): " + currentMonth);

        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_CHEMICAL_STOC", "ET_STOC", params);

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### mat strg stoc data list size: " + sapDataList.size());
            logger.info("### mat strg stoc data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapMatStrgStocData(param);
            }

            logger.info("### mat strg stoc data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 근태정보 등록
     * 
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapWorkInfoData() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        String beforeMonth = new SimpleDateFormat("yyyyMM").format(calendar.getTime());

        params.put("I_ZYYMM", beforeMonth);
        logger.info("### work info data param(I_ZYYMM): " + beforeMonth);

        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WORK", "ET_WORK", params);

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### work info data list size: " + sapDataList.size());
            logger.info("### work info data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWorkInfoData(param);
            }

            logger.info("### work info data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 부서월별 무재해정보 등록
     * 
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapNacdInfoData() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        String startDate = Integer.toString(year) + Integer.toString(month) + "01";
        String endDate = Integer.toString(year) + Integer.toString(month) + Integer.toString(last);

        params.put("I_ZWKDTF", startDate);
        params.put("I_ZWKDTT", endDate);
        logger.info("### nacd info data params(I_ZWKDTF, I_ZWKDTT): " + startDate + ", " + endDate);

        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_NACD", "ET_NACD", params);

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### nacd info data list size: " + sapDataList.size());
            logger.info("### nacd info data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapNacdInfoData(param);
            }

            logger.info("### nacd info data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 입고시설(저장시설) 등록
     * 
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapMatStrgData() throws Exception {
        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_LGORT", "ET_LGORT");

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### mat strg data list size: " + sapDataList.size());
            logger.info("### mat strg data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapMatStrgData(param);
            }

            logger.info("### mat strg data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 수질분석결과 등록
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapWtrMeasInfoData() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

        params.put("I_ZMDATE", yesterday);
        logger.info("### Wtr Meas Info data param(I_ZMDATE): " + yesterday);

        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WQMEASURE", "ET_MEASURE", params);

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Wtr Meas Info data list size: " + sapDataList.size());
            logger.info("### Wtr Meas Info data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWtrMeasInfoData(param);
            }

            logger.info("### Wtr Meas Info data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 대기방지시설 등록
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapEairInfoData() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

        params.put("I_ZMDATE", yesterday);
        logger.info("### Eair Info data param(I_ZMDATE): " + yesterday);

        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_AIRPREV", "ITAB", params);

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Eair Info data list size: " + sapDataList.size());
            logger.info("### Eair Info data insert start ###");

            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapEairInfoData(param);
            }

            // 대기운영일지 프로시져 호출
            if (result > 0) {
                // 대기운영일지 작성일자 프로시져
                sapDataMapper.intfAirPrevProcedure1(yesterday);
                // 대기운영일지 배출시설 가동시간 프로시져
                sapDataMapper.intfAirPrevProcedure2(yesterday);
                // 대기운영일지 방지시설 전력사용량 프로시져
                sapDataMapper.intfAirPrevProcedure3(yesterday);
                // 대기운영일지 배출시설 연료사용량 프로시져
                sapDataMapper.intfAirPrevProcedure4(yesterday);
                // 대기운영일지 원료사용량 프로시져
                sapDataMapper.intfAirPrevProcedure5(yesterday);
            }

            logger.info("### Eair Info data insert end ###");
            return result;
        } else {
            return 0;
        }
    }

    /**
     * SAP 수질용수량 등록
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapWasteWaterData() throws Exception {
        int result = 0;
        HashMap<String, Object> params = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String yesterday = new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());

        params.put("I_ZMDATE", yesterday);
        logger.info("### Waste Water data param(I_ZMDATE): " + yesterday);

        // SAP 폐수처리시설 관리(Main) 등록
        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WASTEWATER", "ITAB0", params, true);
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Waste Water 400 data list size: " + sapDataList.size());
            logger.info("### Waste Water 400 data insert start ###");

            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWtrLog400Data(param);
            }
            logger.info("### Waste Water 400 data insert end ###");
        }

        // SAP 폐수처리시설 관리(용수) 등록
        sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WASTEWATER", "ITAB1", params);
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Waste Water 401 data list size: " + sapDataList.size());
            logger.info("### Waste Water 401 data insert start ###");

            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWtrSupl401Data(param);
            }
            logger.info("### Waste Water 401 data insert end ###");
        }

        // SAP 폐수처리시설 관리(폐수) 등록
        sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WASTEWATER", "ITAB2", params);
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Waste Water 402 data list size: " + sapDataList.size());
            logger.info("### Waste Water 402 data insert start ###");

            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWtrDisch402Data(param);
            }
            logger.info("### Waste Water 402 data insert end ###");
        }

        // SAP 폐수처리시설 관리(전력) 등록
        sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WASTEWATER", "ITAB3", params);
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Waste Water 403 data list size: " + sapDataList.size());
            logger.info("### Waste Water 403 data insert start ###");

            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWtrPwr403Data(param);
            }
            logger.info("### Waste Water 403 data insert end ###");
        }

        // SAP 폐수처리시설 관리(약품) 등록
        sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WASTEWATER", "ITAB4", params);
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Waste Water 404 data list size: " + sapDataList.size());
            logger.info("### Waste Water 404 data insert start ###");

            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWtrChem404Data(param);
            }
            logger.info("### Waste Water 404 data insert end ###");
        }

        // SAP 폐수처리시설 관리(슬러지) 등록
        sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_WASTEWATER", "ITAB5", params);
        if (CollectionUtils.isNotEmpty(sapDataList)) {
            logger.info("### Waste Water 405 data list size: " + sapDataList.size());
            logger.info("### Waste Water 405 data insert start ###");

            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapWtrSlu405Data(param);
            }
            logger.info("### Waste Water 405 data insert end ###");
        }

        // 수질운영일지 관련 프로시져 호출
        if (result > 0) {
            // 수질운영일지 메인정보-날씨,온도,근무자 프로시져
            sapDataMapper.intfWasteWaterProcedure1(yesterday);
            // 수질운영일지 용수사용량, 폐수배출량(일부) 프로시져
            sapDataMapper.intfWasteWaterProcedure2(yesterday);
            // 수질운영일지 폐수배출량 프로시져
            sapDataMapper.intfWasteWaterProcedure3(yesterday);
            // 수질운영일지 전력사용량 프로시져
            sapDataMapper.intfWasteWaterProcedure4(yesterday);
            // 수질운영일지 약품사용량 프로시져
            sapDataMapper.intfWasteWaterProcedure5(yesterday);
            // 수질운영일지 슬러지처리량 프로시져
            sapDataMapper.intfWasteWaterProcedure6(yesterday);
            // 수질운영일지 배출시설가동시간 프로시져
            sapDataMapper.intfWasteWaterProcedure7(yesterday);
            // 수질운영일지 방지시설가동시간 프로시져
            sapDataMapper.intfWasteWaterProcedure8(yesterday);
            // 수질운영일지 자가측정 분석결과 프로시져
            sapDataMapper.intfWasteWaterProcedure9(yesterday);
        }

        return result;
    }

    /**
     * SAP 자재검토요청 등록
     * 
     * @param startDate
     * @param endDate
     * @param finish
     * @return
     * @throws Exception
     */
    @Transactional
    public int insertSapMatChkRqstData(String startDate, String endDate) throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("I_ERSDAF", startDate);
        params.put("I_ERSDAT", endDate);
        List<Map<String, Object>> sapDataList = JcoUtil.getSapTableData("ZSHE_INTF_MSDS", "ET_MAT", params);

        if (CollectionUtils.isNotEmpty(sapDataList)) {
            int result = 0;
            for (Map<String, Object> param : sapDataList) {
                result += sapDataMapper.mergeSapMatChkRqstData(param);
            }
            return result;
        } else {
            return 0;
        }
    }
}
