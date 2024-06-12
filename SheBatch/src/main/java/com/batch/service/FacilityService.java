package com.batch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.mapper.EduMasterMapper;
import com.batch.mapper.PsmMapper;
import com.batch.utils.SendMailUtil;
import com.batch.utils.model.Alarm;
import com.batch.utils.model.Facility;
import com.batch.utils.model.MailLog;
import com.batch.utils.model.MailResult;
import com.batch.utils.model.MailVo;
import com.batch.utils.model.Psm;
import com.batch.utils.model.User;

@Service
public class FacilityService {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private SendMailUtil sendMailUtil;

    @Autowired
    private LogListService logListService;

    @Autowired
    private PsmMapper psmMapper;

    @Autowired
    private CommonService commonService;

    private static final Logger logger = LoggerFactory.getLogger(EduMasterMapper.class);

    public String run(String batchCd) throws Exception {
        String message = "";

        try {
            message = this.getFacilityScheduler(batchCd);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return message;
    }

    public String getFacilityScheduler(String batchCd) throws Exception {
        String resultMsg = "";
        try {
            String batchDay = psmMapper.getBatchDay(batchCd);
            List<Facility> planList = psmMapper.getFacilityScheduler(batchDay);

            if (planList != null && !planList.isEmpty()) {

                for (Facility plan : planList) {
                    List<Alarm> alarms = alarmService.getAlarmByAlarmCd("S10043");
                    for (Alarm alarm : alarms) {
                        Psm senderMail = psmMapper.getPsmCommonUser();
                        String mailUrl = alarm.getMailUrl();
                        String templateUrl = alarm.getTemplateUrl();
                        String mailTitle = alarm.getAlarmNm();
                        mailTitle = "[설비점검]" + plan.getSafFacilityTypeNm() + "/" + plan.getSafFacilityCd() + "/" + plan.getFacilityDesc() + "설비점검 점검일자(" + plan.getSelfNextChkYmd() + ") 도래 알림";
                        String mailContents = "";
                        List<User> receivers = null;

                        String receiverNms = "";
                        String[] receiverEmails = null;

                        String senderEmail = senderMail.getCode();

                        List<MailVo> mailVoList = new ArrayList<>();
                        List<MailResult> results = null;

                        HashMap<String, Object> param = new HashMap<>();
                        param.put("facility", plan);
                        mailContents = templateService.createMailContents(param, templateUrl);

                        User actUser = commonService.getUser(plan.getUserId());
                        if (actUser != null) {
                            receivers = new ArrayList<>();
                            receivers.add(actUser);
                            receiverNms = String.join(",", receivers.stream().map(User::getUserNm).toArray(String[]::new));
                            receiverEmails = receivers.stream().map(User::getEmail).toArray(String[]::new);
                        }

                        List<MailLog> mailLogs = new ArrayList<>();

                        if (!receivers.isEmpty()) {
                            for (User receiver : receivers) {
                                MailLog mailLog = new MailLog();
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

                        results = sendMailUtil.sendMails(mailVoList);
                        if (!results.isEmpty()) {
                            List<MailLog> logs = new ArrayList<>();
                            for (MailResult mailResult : results) {
                                if (!mailResult.getMailLogs().isEmpty()) {
                                    logs.addAll(mailResult.getMailLogs());
                                    psmMapper.updateFacilityEffectiveYn(plan.getSafFacilityCd());
                                }
                            }

                            for (MailLog maillog : logs) {
                                logListService.createMailLog(maillog);
                            }
                        }
                    }
                }

            }
            resultMsg = "SUCCESS";
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
