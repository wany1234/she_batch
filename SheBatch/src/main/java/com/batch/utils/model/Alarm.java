package com.batch.utils.model;

import lombok.Data;

@Data
public class Alarm {
    private String batchCd;

    private String batchDay;

    private String batchNm;

    private String batchDesc;

    private int alarmNo;

    private String alarmCd;

    private String alarmNm;

    private String alarmDesk;

    private String alarmText;

    private String useYn;

    private String mailYn;

    private String mailUrl;

    private String templateUrl;
}
