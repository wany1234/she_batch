package com.batch.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class TemplateService {
    // FreeMarker config
    @Autowired
    private Configuration cfg;

    @Autowired
    private AlarmService alarmService;

    @Value("${frontend.url}")
    private String domain;

    // 메일 양식 폴더
    private final static String mailPrefix = "mail/";

    // sso 토큰 변수
    private final static String ssoTokenStr = "coviTok=${coviTok}";

    /**
     * 메일 발송내용 HTML 생성
     * @param mailParam
     *          메일발송 업무 정보
     * @return 메일발송 내용 HTML
     * @throws Exception
     */
    public String createMailContents(Map<String, Object> mailParam, String templateUrl) throws Exception {
        /* Get the template (uses cache internally) */
        Template temp = cfg.getTemplate(mailPrefix + templateUrl);

        Map<String, Object> param = new HashMap<>();
        if (mailParam != null && !mailParam.isEmpty()) {
            param.putAll(mailParam);
        }

        StringWriter writer = new StringWriter();
        temp.process(param, writer);

        return writer.toString();
    }
}
