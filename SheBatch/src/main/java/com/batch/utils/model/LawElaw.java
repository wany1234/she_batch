package com.batch.utils.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LawElaw {

    // 일련번호
    private int seqNo;

    // 법령일련번호
    private int lmst;

    // 현행연혁코드
    private String lstepNm;

    // 법령명한글
    private String lnameKor;

    // 법령약칭명
    private String lnameAbb;

    // 법령ID (LAW_TYPE)
    private String lkey;

    // 공포일자
    private String promDate;

    // 공포번호
    private String promNum;

    // 재개정구분명
    private String revDiv;

    // 소관부처코드
    private String mgrGovcd;

    // 소관부처명
    private String mgrGov;

    // 법령구분명
    private String ltypeNm;

    // 시행일자
    private String enfDate;

    // 자법타법여부
    private String lflagNm;

    // 법령상세링크
    private String ldtlLink;

    // 등록일
    private String regDt;

    // 등록자ID
    private String regerId;

    // 수정일
    private String udtDt;

    // 수정자ID
    private String udterId;

    // 법령상세생성여부
    private String dtlInsFlag;

    // 법종분류코드 (MGT_LAW_KIND)
    private String lawTypeCd;

    // 법령키 (법령ID(6자)+공포일(8자)+공포번호+시행일(8자))
    private String legiKey;

}
