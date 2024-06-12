package com.batch.service;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.batch.utils.model.*;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.batch.mapper.CommonMapper;
import com.batch.mapper.EduMasterMapper;
import com.batch.mapper.LawMapper;
import com.batch.utils.ConstVal;
import com.batch.utils.SendMailUtil;

@Service
public class EduMasterService {

    @Autowired
    private EduMasterMapper eduMasterMapper;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private SendMailUtil sendMailUtil;

    @Autowired
    private CommonService commonService;

    @Autowired
    private LogListService logListService;

    @Autowired
    private CommonMapper commonMapper;

    private static final Logger logger = LoggerFactory.getLogger(EduMasterMapper.class);

    public String getUserList(String eduSYmd) throws Exception {
        String resultMsg = "";
        try {
            int resultNo = 0;
            List<EduUser> userList = eduMasterMapper.getUserList(eduSYmd);
            if (userList != null && !userList.isEmpty()) {
                // 대상인원이 존재할 경우 해당 인원들에게 교육기한 도래 알림 메일폼 발송
                for (EduUser user : userList) {
                    List<Alarm> alarms = alarmService.getAlarmByAlarmCd("S10009");
                    for (Alarm alarm : alarms) {
                        if (alarm.getMailYn().equals("Y") && alarm.getUseYn().equals("Y")) {
                            String mailUrl = alarm.getMailUrl();
                            String templateUrl = alarm.getTemplateUrl();
                            String mailTitle = alarm.getAlarmNm();
                            String mailContents = "";
                            String senderId = "";
                            List<User> receivers = null;
                            String receiverNms = "";
                            String[] receiverEmails = null;
                            String senderNm = "";
                            String senderEmail = "";
                            List<MailVo> mailVoList = new ArrayList<>();
                            List<MailResult> results = null;

                            HashMap<String, Object> param = new HashMap<>();
                            param.put("user", user);
                            mailContents = templateService.createMailContents(param, templateUrl);
                            User actUser = commonService.getUser(user.getUserId());
                            if (actUser != null) {
                                receivers = new ArrayList<>();
                                receivers.add(actUser);
                                receiverNms = String.join(",", receivers.stream().map(User::getUserNm).toArray(String[]::new));
                                receiverEmails = receivers.stream().map(User::getEmail).toArray(String[]::new);
                            }

                            // senderId = user.getCreateUserId();
                            // User sender = alarmService.getUser(senderId);
                            CommonUser sender = commonMapper.getCommonUser();
                            if (sender != null) {
                                senderNm = sender.getUserNm();
                                senderEmail = sender.getCode();
                            }

                            List<MailLog> mailLogs = new ArrayList<>();

                            // if (user != null) {
                            for (User receiver : receivers) {
                                MailLog mailLog = new MailLog();
                                mailLog.setSenderId(senderId);
                                mailLog.setSenderNm(senderNm);
                                mailLog.setSenderEmail(senderEmail);
                                mailLog.setReceiverId(user.getUserId());
                                mailLog.setReceiverNm(user.getUserNm());
                                mailLog.setReceiverEmail(user.getEmail());
                                mailLog.setTitle(mailTitle);
                                mailLog.setContent(mailContents);
                                mailLog.setSendYn("Y");
                                mailLog.setTryCount(1);
                                mailLog.setHtmlYn("Y");
                                mailLog.setAlarmNo(alarm.getAlarmNo());

                                mailLogs.add(mailLog);
                            }
                            MailVo mailVo = new MailVo();
                            mailVo.setTitle(mailTitle);
                            mailVo.setMailTitle(mailTitle);
                            mailVo.setContents(mailContents);
                            mailVo.setReceiver(receiverNms);
                            mailVo.setRecipientsEmailAddress(receiverEmails);
                            mailVo.setSenderEmail(senderEmail);
                            mailVo.setMailLogs(mailLogs);
                            mailVo.setLink(mailUrl);

                            mailVoList.add(mailVo);
                            // }

                            results = sendMailUtil.sendMails(mailVoList);
                            if (!results.isEmpty()) {
                                List<MailLog> logs = new ArrayList<>();
                                for (MailResult mailResult : results) {
                                    if (!mailResult.getMailLogs().isEmpty()) {
                                        logs.addAll(mailResult.getMailLogs());
                                    }
                                }

                                for (MailLog maillog : logs) {
                                    resultNo += logListService.createMailLog(maillog);
                                }
                            }
                        }

                    }
                }

            }
            resultMsg = "SUCCESS";
            return resultMsg;
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            resultMsg = "FAIL";
            return resultMsg;
        }
        return resultMsg;

    }

}
