package com.batch.utils.model;

import lombok.Data;

// 법규정보 기본정보
@Data
public class LegiLawBasics {
    // 법령키 (법령ID(6자)+공포일(8자)+공포번호+시행일(8자))
    private String legiKey;

    // 법종분류코드 (MGT_LAW_KIND)
    private String lawTypeCd;

    // 법령ID (LAW_TYPE)
    private String legiId;

    // 공포일자
    private String promDate;

    // 공포번호
    private String promNum;

    // 언어
    private String lang;

    // 법종구분
    private String ltypeNm;

    // 법종구분코드
    private String ltypeCd;

    // 법령명_한글
    private String lnameKor;

    // 법령명_영어
    private String lnameEng;

    // 법령명_한자
    private String lnameZh;

    // 법령명약칭
    private String lnameAbb;

    // 편장절관
    private String attriSide;

    // 소관부처
    private String mgrGov;

    // 소관부처코드
    private String mgrGovcd;

    // 시행일자
    private String enfDate;

    // 제개정구분
    private String revDiv;

    // 조문시행일자문자열
    private String provEnfDateTxt;

    // 시행령_별표시행일자문자열
    private String atfmEnfDateTxt;

    // 시행령_시행규칙_별표편집여부
    private String atfmEditFlag;

    // 공포법령여부
    private String promFlag;

    // 등록일
    private String createDt;

    // 수정일
    private String updateDt;

    // 법령일련번호
    private String lmst;
}
