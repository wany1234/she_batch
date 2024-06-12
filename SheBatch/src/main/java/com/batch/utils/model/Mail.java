package com.batch.utils.model;

import lombok.Data;

@Data
public class Mail {
    private int logNo;

    private String senderId;

    private String senderEmail;

    private String receiverId;

    private String receiverEmail;

    private String title;

    private String content;

    private String contents;

    private String sendDt;

    private String sendYn;

    private String failDesc;

    private int tryCount;

    private String tryDt;

}
