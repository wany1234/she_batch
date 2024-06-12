package com.batch.utils.model;

import lombok.Data;

// 법규정보 목(조문 > 항 > 호 > 목)
@Data
public class LegiLawMok {
    // 순번
    private int seqNo;

    // 법령키
    private String legiKey;

    // 조문키
    private String provKey;

    // 항번호
    private String claNum;

    // 호번호
    private String numbNum;

    // 법_시행규칙_호가지번호
    private String numbNumBran;

    // 목번호
    private String mokNum;

    // 목내용
    private String mokContent;
}
