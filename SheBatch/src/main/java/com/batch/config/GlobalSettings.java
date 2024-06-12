package com.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "globals")
public class GlobalSettings {

    /**
     * 메일서버 주소
     */
    private String smtpHost;

    /**
     * 메일서버 포트
     */
    private String smtpPort;

    /**
     * 공용 메일발송 email
     */
    private String sendMailEm;

    /**
     * 공용 메일발송 명
     */
    private String sendMailNm;

}
