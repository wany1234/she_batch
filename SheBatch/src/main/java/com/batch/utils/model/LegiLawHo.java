package com.batch.utils.model;

import lombok.Data;

// 법규정보 호(조문 > 항 > 호)
@Data
public class LegiLawHo {
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

    // 호내용
    private String numbContent;
}
