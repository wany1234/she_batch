package com.batch.utils.model;

import lombok.Data;

import java.util.List;

@Data
public class MailResult {

    private String resultMsg;

    private String resultCd;

    // 메일발송결과로그
    private List<MailLog> mailLogs;

    private MailLog mailLog;
}
