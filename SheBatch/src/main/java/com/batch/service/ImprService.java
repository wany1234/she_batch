package com.batch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batch.mapper.CommonMapper;
import com.batch.mapper.ImprMapper;
import com.batch.utils.SendMailUtil;
import com.batch.utils.model.Alarm;
import com.batch.utils.model.CommonUser;
import com.batch.utils.model.Impr;
import com.batch.utils.model.MailLog;
import com.batch.utils.model.MailResult;
import com.batch.utils.model.MailVo;
import com.batch.utils.model.User;

@Service
public class ImprService {

    @Autowired
    private ImprMapper ImprMapper;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private SendMailUtil sendMailUtil;

    @Autowired
    private LogListService logListService;

    @Autowired
    private CommonMapper commonMapper;

    private static final Logger logger = LoggerFactory.getLogger(ImprMapper.class);

    @Transactional
    public String getImprArriveList() throws Exception {

        try {
            List<Impr> imprList = ImprMapper.getImprArriveList();

            if (imprList != null && !imprList.isEmpty()) {
                // 대상인원이 존재할 경우 해당 인원들에게 개선사항 조치도래 알림 메일폼 발송
                for (Impr impr : imprList) {
                    List<Alarm> alarms = alarmService.getAlarmByAlarmCd("S10041");

                    for (Alarm alarm : alarms) {
                        String mailUrl = alarm.getMailUrl();
                        String templateUrl = alarm.getTemplateUrl();
                        String mailTitle = "개선조치 조치기한(" + impr.getImprRqstYmd() + ") 알림";
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
                        param.put("impr", impr);
                        mailContents = templateService.createMailContents(param, templateUrl);

                        User actUser = commonMapper.getUser(impr.getActUserId());
                        receivers = new ArrayList<>();
                        receivers.add(actUser);
                        receiverNms = String.join(",", receivers.stream().map(User::getUserNm).toArray(String[]::new));
                        receiverEmails = receivers.stream().map(User::getEmail).toArray(String[]::new);

                        // 공통코드 com_rps_mail
                        CommonUser sender = commonMapper.getCommonUser();
                        if (sender != null) {
                            senderNm = sender.getCodeNm();
                            senderEmail = sender.getCode();
                        }

                        List<MailLog> mailLogs = new ArrayList<>();
                        if (!receivers.isEmpty()) {
                            for (User receiver : receivers) {
                                MailLog mailLog = new MailLog();
                                mailLog.setSenderId(senderId);
                                mailLog.setSenderNm(senderNm);
                                mailLog.setSenderEmail(senderEmail);
                                mailLog.setReceiverId(receiver.getUserId());
                                mailLog.setReceiverNm(receiver.getUserNm());
                                mailLog.setReceiverEmail(receiver.getEmail());
                                mailLog.setTitle(mailTitle);
                                mailLog.setContent(mailContents);
                                mailLog.setSendYn("Y");
                                mailLog.setTryCount(1);
                                mailLog.setHtmlYn("Y");
                                mailLog.setAlarmNo(alarm.getAlarmNo());

                                mailLogs.add(mailLog);

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
                            }
                        }

                        if (mailVoList.size() > 0) {
                            results = SendMailUtil.sendMails(mailVoList);
                        }
                        if (!results.isEmpty()) {
                            List<MailLog> logs = new ArrayList<>();
                            for (MailResult mailResult : results) {
                                if (!mailResult.getMailLogs().isEmpty()) {
                                    logs.addAll(mailResult.getMailLogs());
                                }
                            }

                            for (MailLog maillog : logs) {
                                logListService.createMailLog(maillog);
                            }
                        }
                    }
                }
            }

            return "SUCCESS";
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "FAIL";
        }
        return null;
    }

    @Transactional
    public String getImprOverList() throws Exception {
        try {
            List<Impr> imprList = ImprMapper.getImprOverList();

            if (imprList != null && !imprList.isEmpty()) {
                // 대상인원이 존재할 경우 해당 인원들에게 개선사항 조치도래 알림 메일폼 발송
                for (Impr impr : imprList) {
                    List<Alarm> alarms = alarmService.getAlarmByAlarmCd("S10042");

                    for (Alarm alarm : alarms) {
                        String mailUrl = alarm.getMailUrl();
                        String templateUrl = alarm.getTemplateUrl();
                        String mailTitle = "개선조치 조치기한(" + impr.getImprRqstYmd() + ")내 미조치건  알림";
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
                        param.put("impr", impr);
                        mailContents = templateService.createMailContents(param, templateUrl);

                        User actUser = commonMapper.getUser(impr.getActUserId());
                        receivers = new ArrayList<>();
                        receivers.add(actUser);
                        receiverNms = String.join(",", receivers.stream().map(User::getUserNm).toArray(String[]::new));
                        receiverEmails = receivers.stream().map(User::getEmail).toArray(String[]::new);

                        // 공통코드 com_rps_mail
                        CommonUser sender = commonMapper.getCommonUser();
                        if (sender != null) {
                            senderNm = sender.getCodeNm();
                            senderEmail = sender.getCode();
                        }

                        List<MailLog> mailLogs = new ArrayList<>();
                        if (!receivers.isEmpty()) {
                            for (User receiver : receivers) {
                                MailLog mailLog = new MailLog();
                                mailLog.setSenderId(senderId);
                                mailLog.setSenderNm(senderNm);
                                mailLog.setSenderEmail(senderEmail);
                                mailLog.setReceiverId(receiver.getUserId());
                                mailLog.setReceiverNm(receiver.getUserNm());
                                mailLog.setReceiverEmail(receiver.getEmail());
                                mailLog.setTitle(mailTitle);
                                mailLog.setContent(mailContents);
                                mailLog.setSendYn("Y");
                                mailLog.setTryCount(1);
                                mailLog.setHtmlYn("Y");
                                mailLog.setAlarmNo(alarm.getAlarmNo());

                                mailLogs.add(mailLog);

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
                            }
                        }

                        if (mailVoList.size() > 0) {
                            results = SendMailUtil.sendMails(mailVoList);
                        }
                        if (!results.isEmpty()) {
                            List<MailLog> logs = new ArrayList<>();
                            for (MailResult mailResult : results) {
                                if (!mailResult.getMailLogs().isEmpty()) {
                                    logs.addAll(mailResult.getMailLogs());
                                }
                            }

                            for (MailLog maillog : logs) {
                                logListService.createMailLog(maillog);
                            }
                        }
                    }
                }
            }

            return "SUCCESS";
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "FAIL";
        }
        return null;
    }
}
