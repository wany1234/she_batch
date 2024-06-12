package com.batch.utils.model;

import lombok.Data;

import java.util.List;

@Data
public class MailVo {

    private String title;

    // 메일 본문 제목
    private String mailTitle;

    private String contents;

    // 수신자 사람 이름
    private String receiver;

    private String[] recipientsEmailAddress;

    private String sender;

    private String senderEmail;

    // 메일로그
    private List<MailLog> mailLogs;

    // SHE 링크
    private String link;

}
