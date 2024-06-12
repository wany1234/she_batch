package com.batch.utils.model;

import lombok.Data;

// 법규정보 항(조문 > 항)
@Data
public class LegiLawHang {
    // 법령키
    private String legiKey;

    // 조문키
    private String provKey;

    // 항번호
    private String claNum;

    // 항내용
    private String claContent;

    // 시행령_시행규칙_항제개정유형
    private String claRevType;

    // 시행령_시행규칙_항제개정일자문자열
    private String claRevDateTxt;

    // 순번(정렬용-자동증가)
    private int seqNo;
}
