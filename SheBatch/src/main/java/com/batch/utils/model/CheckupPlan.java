package com.batch.utils.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckupPlan {
    // 건강검진 계획번호
    private int heaCheckupPlanNo;

    // 건강검진 계획명칭
    private String heaCheckupPlanNm;

    // 건강검진 분류코드
    private String heaCheckupClassCd;

    // 건강검진 분류명칭
    private String heaCheckupClassNm;

    // 사업장코드
    private String plantCd;

    // 사업장명칭
    private String plantNm;

    // 시작일자
    private String startYmd;

    // 종료일자
    private String endYmd;

    // 완료일자
    private String finishYmd;

    // 건강검진기간
    private String heaCheckupPlanPeriod;

    // 선택항목 필수여부
    private String requiredOptYn;

    // 선택항목 필수여부 명
    private String requiredOptYnNm;

    // 선택항목 선택가능 항목 수
    private int selectOptCount;

    // 대상자 수
    private int checkupUserCount;

    // 검진계획에 따른 검진결과 수
    private int resultCnt;

    // 등록자아이디
    private String createUserId;

    // 등록자명
    private String createUserNm;

    // 등록일
    private String createDt;

    // 수정자아이디
    private String updateUserId;

    // 수정자명
    private String updateUserNm;

    // 수정일
    private String updateDt;

    // 작성자
    private String writerUserNm;

    // 작성일
    private String writerDt;

}
