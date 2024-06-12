package com.batch.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.batch.service.SapDataService;

//원본소스, SAP 인터페이스용으로 주석처리함
//@Component
public class SapDataScheduler {

    // private static final Logger logger =
    // LoggerFactory.getLogger(SapDataScheduler.class);

    @Autowired
    private SapDataService sapDataService;

    // /**
    // * SAP 거래처 정보 등록 (주기: 매일 02시 00분)
    // */
    // @Scheduled(cron = "0 0 2 * * ?")
    // public void sapVendorDataSchedule() {
    // logger.info("### SAP Vendor Data Scheduler Start ###");
    // try {
    // sapDataService.insertSapVendorData();
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // }
    // logger.info("### SAP Vendor Data Scheduler end ###");
    // }
    //
    // /**
    // * SAP 자재마스터 정보 등록 (주기: 매일 02시 30분)
    // */
    // @Scheduled(cron = "0 30 2 * * ?")
    // public void sapMatDataSchedule() {
    // logger.info("### SAP Mat Data Scheduler Start ###");
    // try {
    // sapDataService.insertSapMatData();
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // }
    // logger.info("### SAP Mat Data Scheduler end ###");
    // }
    //
    // /**
    // * SAP 입고시설(저장시설) 등록 (주기: 매일 03시 00분)
    // */
    // @Scheduled(cron = "0 0 3 * * ?")
    // public void sapMatStrgDataSchedule() {
    // logger.info("### SAP Mat Strg Data Scheduler Start ###");
    // try {
    // sapDataService.insertSapMatStrgData();
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // }
    // logger.info("### SAP Mat Strg Data Scheduler end ###");
    // }
    //
    // /*
    // * SAP 화학물질식적 일 정보 등록 (주기: 매일 03시 30분)
    // */
    // @Scheduled(cron = "0 30 3 * * ?")
    // public void sapMatStrgStocDataSchedule() {
    // logger.info("### SAP Mat strg stoc Data Scheduler Start ###");
    // try {
    // sapDataService.insertSapMatStrgStocData();
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // }
    // logger.info("### SAP Mat strg stoc Data Scheduler end ###");
    // }
    //
    // /**
    // * SAP 수질분석결과 등록 (주기: 매일 04시 00분)
    // */
    // // @Scheduled(cron = "0 0 4 * * ?")
    // // public void sapWtrMeasInfoDataSchedule() {
    // // logger.info("### SAP Wtr Meas Info Data Scheduler Start ###");
    // // try {
    // // sapDataService.insertSapWtrMeasInfoData();
    // // } catch (Exception e) {
    // // logger.error(e.getMessage());
    // // }
    // // logger.info("### SAP Wtr Meas Info Data Scheduler end ###");
    // // }
    //
    // /**
    // * SAP 대기방지시설 등록 (주기: 매일 04시 30분)
    // */
    // // @Scheduled(cron = "0 30 4 * * ?")
    // // public void sapEairInfoDataSchedule() {
    // // logger.info("### SAP Eair Info Data Scheduler Start ###");
    // // try {
    // // sapDataService.insertSapEairInfoData();
    // // } catch (Exception e) {
    // // logger.error(e.getMessage());
    // // }
    // // logger.info("### SAP Eair Info Data Scheduler end ###");
    // // }
    //
    // /**
    // * SAP 수질용수량 등록 (주기: 매일 05시 00분)
    // */
    // // @Scheduled(cron = "0 0 5 * * ?")
    // // public void sapWasteWaterData() {
    // // logger.info("### SAP Waste Water Scheduler Start ###");
    // // try {
    // // sapDataService.insertSapWasteWaterData();
    // // } catch (Exception e) {
    // // logger.error(e.getMessage());
    // // }
    // // logger.info("### SAP Waste Water Data Scheduler end ###");
    // // }
    //
    // /**
    // * SAP 화학물질식적 월 정보 등록 (주기: 매월 8일 04시 00분)
    // */
    // @Scheduled(cron = "0 0 4 8 * ?")
    // public void sapMatInoutDataSchedule() {
    // logger.info("### SAP Mat Inout Data Scheduler Start ###");
    // try {
    // sapDataService.insertSapMatInoutData();
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // }
    // logger.info("### SAP Mat Inout Data Scheduler end ###");
    // }
    //
    // /**
    // * SAP 근태정보 등록 (주기: 매월 8일 04시 30분)
    // */
    // @Scheduled(cron = "0 30 4 8 * ?")
    // public void sapWorkInfoDataSchedule() {
    // logger.info("### SAP Work Info Data Scheduler Start ###");
    // try {
    // sapDataService.insertSapWorkInfoData();
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // }
    // logger.info("### SAP Work Info Data Scheduler end ###");
    // }
    //
    // /**
    // * SAP 부서월별 무재해정보 등록 (주기: 매월 8일 05시 00분)
    // */
    // @Scheduled(cron = "0 0 5 8 * ?")
    // public void sapNacdInfoDataSchedule() {
    // logger.info("### SAP Nacd Info Data Scheduler Start ###");
    // try {
    // sapDataService.insertSapNacdInfoData();
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // }
    // logger.info("### SAP Nacd Info Data Scheduler end ###");
    // }
}
