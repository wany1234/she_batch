package com.batch.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("com.batch.mapper.SapDataMapper")
public interface SapDataMapper {

    /**
     * SAP 거래처 정보 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapVendorData(Map<String, Object> param) throws Exception;

    /**
     * SAP 자재마스터 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapMatData(Map<String, Object> param) throws Exception;

    /**
     * SAP 화학물질식적 월 정보 삭제
     * 
     * @param yearmon
     * @return
     * @throws Exception
     */
    public int deleteSapMatInoutData(@Param("yearmon") String yearmon) throws Exception;

    /**
     * SAP 화학물질식적 월 정보 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int insertSapMatInoutData(Map<String, Object> param) throws Exception;

    /**
     * SAP 화학물질식적 일 정보 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapMatStrgStocData(Map<String, Object> param) throws Exception;

    /**
     * SAP 근태정보 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWorkInfoData(Map<String, Object> param) throws Exception;

    /**
     * SAP 부서월별 무재해정보 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapNacdInfoData(Map<String, Object> param) throws Exception;

    /**
     * SAP 자재검토요청 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapMatChkRqstData(Map<String, Object> param) throws Exception;

    /**
     * SAP 입고시설(저장시설) 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapMatStrgData(Map<String, Object> param) throws Exception;

    /**
     * SAP 용수량 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrDayInfoData(Map<String, Object> param) throws Exception;

    /**
     * SAP 수질분석결과 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrMeasInfoData(Map<String, Object> param) throws Exception;

    /**
     * SAP 대기방지시설 등록
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapEairInfoData(Map<String, Object> param) throws Exception;

    /**
     * SAP 폐수처리시설 관리(Main)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrLog400Data(Map<String, Object> param) throws Exception;

    /**
     * SAP 폐수처리시설 관리(용수)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrSupl401Data(Map<String, Object> param) throws Exception;

    /**
     * SAP 폐수처리시설 관리(폐수)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrDisch402Data(Map<String, Object> param) throws Exception;

    /**
     * SAP 폐수처리시설 관리(전력)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrPwr403Data(Map<String, Object> param) throws Exception;

    /**
     * SAP 폐수처리시설 관리(약품)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrChem404Data(Map<String, Object> param) throws Exception;

    /**
     * SAP 폐수처리시설 관리(슬러지)
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public int mergeSapWtrSlu405Data(Map<String, Object> param) throws Exception;

    /**
     * 수질운영일지 메인정보-날씨,온도,근무자 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure1(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 용수사용량, 폐수배출량(일부) 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure2(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 폐수배출량 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure3(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 전력사용량 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure4(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 약품사용량 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure5(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 슬러지처리량 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure6(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 배출시설가동시간 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure7(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 방지시설가동시간 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure8(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 수질운영일지 자가측정 분석결과 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfWasteWaterProcedure9(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 대기운영일지 작성일자 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfAirPrevProcedure1(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 대기운영일지 배출시설 가동시간 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfAirPrevProcedure2(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 대기운영일지 방지시설 전력사용량 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfAirPrevProcedure3(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 대기운영일지 배출시설 연료사용량 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfAirPrevProcedure4(@Param("reqDate") String reqDate) throws Exception;

    /**
     * 대기운영일지 원료사용량 프로시져
     * 
     * @param reqDate
     * @return
     * @throws Exception
     */
    public int intfAirPrevProcedure5(@Param("reqDate") String reqDate) throws Exception;
}
