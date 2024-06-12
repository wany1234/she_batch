package com.batch.utils.model;

import lombok.Data;

@Data
public class EduUser {
    private String userId;

    private String userNm;

    private String deptCd;

    private String deptNm;

    private String deptCdHr;

    private String deptNmHr;

    private String email;

    private int safEduMstNo;

    private int safEduCourseNo;

    private String eduCourseNm;

    private String eduNm;

    private String period;

    private String eduPlace;

    private String createUserId;

    private String eduSYmd;
}
