package com.batch.utils.model;

import lombok.Data;

@Data
public class MailLog {
    private int logNo;

    private String senderId;

    private String senderNm;

    private String senderEmail;

    private String receiverId;

    private String receiverNm;

    private String receiverEmail;

    private String title;

    private String content;

    private String contents;

    private String contentHtml;

    private String sendDt;

    private String sendYn;

    private String sendYnNm;

    private String failDesc;

    private int tryCount;

    private String tryDt;

    private String htmlYn;

    private String reqDt;

    private int alarmNo;
}
