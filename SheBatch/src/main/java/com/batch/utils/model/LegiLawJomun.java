package com.batch.utils.model;

import lombok.Data;

// 법규정보 조문(조문)
@Data
public class LegiLawJomun {
    // 법령키 (법령ID(6자)+공포일(8자)+공포번호+시행일(8자))
    private String legiKey;

    // 조문키
    private String provKey;

    // 조문번호
    private String provNum;

    // 조문가지번호
    private String provNumBran;

    // 조문여부
    private String provYn;

    // 조문제목
    private String provTitle;

    // 조문시행일자
    private String provEnfDate;

    // 법_시행령_조문제개정유형
    private String provRevType;

    // 법_시행령_조문제개정일자문자열
    private String provRevDateTxt;

    // 조문이동이전
    private String provPrev;

    // 조문이동이후
    private String provNext;

    // 조문변경여부
    private String provChngYn;

    // 조문내용
    private String provContent;

    // 조문참고자료
    private String provRef;

    // 법_조문참고자료2
    private String provRef2;

}
