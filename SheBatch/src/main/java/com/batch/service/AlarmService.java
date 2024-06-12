package com.batch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.batch.utils.ConstVal;
import com.batch.utils.model.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.batch.mapper.AlarmMapper;
import com.batch.utils.SendMailUtil;
import com.batch.utils.SendSmsUtil;

@Service
public class AlarmService {

    private static Logger logger = LoggerFactory.getLogger(AlarmService.class);
    @Autowired
    private AlarmMapper alarmMapper;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CommonService commonService;

    /**
     * 실행
     *
     * @return
     * @throws Exception
     */
    public String run(String batchCd) throws Exception {
        String message = "Success";
        int resultNo = 0;
        //
        // try {
        // switch (batchCd) {
        // case ConstVal.ALARM_BATCH_CD_PSMDOCU:
        // resultNo = this.getAlarmPsmDocu(batchCd);
        // break;
        // case "":
        // break;
        // }
        // } catch (IOException e) {
        // logger.error(e.getMessage());
        // throw e;
        // } catch (Exception e) {
        // logger.error(e.getMessage());
        // throw e;
        // }

        if (resultNo <= 0) {
            message = "Fail";
        }

        return message;
    }

    @Transactional
    private int getAlarmPsmDocu(String batchCd) throws Exception {
        int resultNo = 0;

        Alarm alarm = alarmMapper.getAlarmInfo(batchCd);
        int batchDay = alarm.getBatchDay() == null ? 0 : Integer.parseInt(Strings.isEmpty(alarm.getBatchDay().trim()) ? "0" : alarm.getBatchDay());

        List<HashMap<String, String>> psmDocuList = alarmMapper.getNextCheckPsmDocuList(batchDay);

        // 메일 내용 작성
        String templateUrl = alarm.getTemplateUrl();
        String mailUrl = alarm.getTemplateUrl();
        String mailTitle = alarm.getAlarmNm();
        String mailContents = "";
        List<User> receivers = null;
        String receiverNms = "";
        String[] receiverEmails = null;
        String senderId = "SHE";
        String senderNm = "SHE시스템"; // 발송자가 시스템일 경우
        String senderEmail = "SHE@kggroup.co.kr"; // 발송자가 시스템일 경우
        List<MailVo> mailVoList = new ArrayList<>();
        List<MailResult> results = null;

        for (HashMap<String, String> psmDocu : psmDocuList) {
            HashMap<String, Object> param = new HashMap<>();
            param.put("psmDocu", psmDocu);
            // 공정안전문서중 조치담당자가 없는 문서의 차기점검일의 경우 알람메일 발송하지 않도록 처리
            if (psmDocu.get("actMgrId") != null && Strings.isNotEmpty(psmDocu.get("actMgrId").trim())) {
                mailContents = templateService.createMailContents(param, templateUrl);
                // 공정안전문서 차기점검일 도래시
                User actUser = commonService.getUser(psmDocu.get("actMgrId"));
                if (actUser != null) {
                    receivers = new ArrayList<>();
                    receivers.add(actUser);
                    receiverNms = String.join(",", receivers.stream().map(User::getUserNm).toArray(String[]::new));
                    receiverEmails = receivers.stream().map(User::getEmail).toArray(String[]::new);
                }
                User sender = commonService.getUser(senderId);
                if (sender != null) {
                    senderNm = sender.getUserNm();
                    senderEmail = sender.getEmail();
                }

                List<MailLog> mailLogs = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(receivers)) {
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
                }
            }
        }

        results = SendMailUtil.sendMails(mailVoList);
        if (!results.isEmpty()) {
            List<MailLog> logs = new ArrayList<>();
            for (MailResult mailResult : results) {
                if (!mailResult.getMailLogs().isEmpty()) {
                    logs.addAll(mailResult.getMailLogs());
                }
            }

            for (MailLog maillog : logs) {
                resultNo += commonService.createMailLog(maillog);
            }
        }

        return resultNo;
    }

    /**
     * 알람코드로 알람정보 조회
     * 
     * @param alarmCd
     *            알람코드
     * @return 알람목록
     * @throws Exception
     */
    public List<Alarm> getAlarmByAlarmCd(String alarmCd) throws Exception {
        return alarmMapper.getAlarmByAlarmCd(alarmCd);
    }

    public User getUser(String userId) throws Exception {
        return alarmMapper.getUser(userId);
    }
}
