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

import com.batch.mapper.EduMasterMapper;
import com.batch.mapper.LawMapper;
import com.batch.mapper.MgtTgtMapper;
import com.batch.utils.ConstVal;
import com.batch.utils.SendMailUtil;
import java.time.LocalDate;
import java.time.Month;

@Service
public class MgtTgtService2 {

    @Autowired
    private LawMapper lawMapper;

    @Autowired
    private EduMasterMapper eduMasterMapper;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SendMailUtil sendMailUtil;

    @Autowired
    private LogListService logListService;

    @Autowired
    private MgtTgtMapper mgtTgtMapper;

    private static final Logger logger = LoggerFactory.getLogger(EduMasterMapper.class);

    public String run(String batchCd) throws Exception {
        System.out.println("테스트");
        return "";
    }

    public String getMgtTgtList2(String date, int batchDay) throws Exception {
        String resultMsg = "";
        int batchDay2 = batchDay;
        String day = Integer.toString(batchDay);
        try {
            int resultNo = 0;
            List<mgtTgtPlan> planList = mgtTgtMapper.getMgtTgtList2(day);

            if (planList != null && !planList.isEmpty()) {
                // 대상인원이 존재할 경우 해당 인원들에게 교육기한 도래 알림 메일폼 발송
                for (mgtTgtPlan plan : planList) {
                    List<Alarm> alarms = alarmService.getAlarmByAlarmCd("S10005");
                    for (Alarm alarm : alarms) {
                        if (alarm.getMailYn().equals("Y") && alarm.getUseYn().equals("Y")) {
                            mgtTgtPlan mgtTgtPlan2 = mgtTgtMapper.getCommonUser();
                            String mailUrl = alarm.getMailUrl();
                            String templateUrl = alarm.getTemplateUrl();
                            String mailTitle = "[사업장 추진실적 등록] 사업장 목표 분기실적 미등록 알림";
                            String mailContents = "";
                            // 메일 발신자: 시스템 공통 메일 발신계정 ( 담당자는 확인중이라 임의로 manager
                            // 계정으로
                            // 설정)

                            List<User> receivers = null;
                            String receiverNms = "";
                            String[] receiverEmails = null;

                            String senderEmail = mgtTgtPlan2.getCode();
                            List<MailVo> mailVoList = new ArrayList<>();
                            List<MailResult> results = null;
                            // 해당 분기의 말일 + 10일을 한 날짜를 넣어줌
                            LocalDate endOfQuarter;
                            // String apprUnitCd = plan.getApprUnitCd();
                            int year = Integer.parseInt(plan.getYear());

                            // switch (apprUnitCd) {
                            // case "QUAT1":
                            // endOfQuarter = LocalDate.of(year, Month.MARCH,
                            // 31);
                            // break;
                            // case "QUAT2":
                            // endOfQuarter = LocalDate.of(year, Month.JUNE,
                            // 30);
                            // break;
                            // case "QUAT3":
                            // endOfQuarter = LocalDate.of(year,
                            // Month.SEPTEMBER, 30);
                            // break;
                            // case "QUAT4":
                            // endOfQuarter = LocalDate.of(year, Month.DECEMBER,
                            // 31);
                            // break;
                            // default:
                            // throw new IllegalArgumentException("알 수 없는 분기 코드:
                            // " + apprUnitCd);
                            // }
                            // LocalDate resultDate =
                            // endOfQuarter.plusDays(batchDay);
                            // date = resultDate.toString();
                            // plan.setBatchDay(batchDay2);
                            // plan.setDate(date);

                            HashMap<String, Object> param = new HashMap<>();
                            param.put("plan", plan);
                            mailContents = templateService.createMailContents(param, templateUrl);

                            User actUser = commonService.getUser(plan.getUserId());
                            if (actUser != null) {
                                receivers = new ArrayList<>();
                                receivers.add(actUser);
                                receiverNms = String.join(",", receivers.stream().map(User::getUserNm).toArray(String[]::new));
                                receiverEmails = receivers.stream().map(User::getEmail).toArray(String[]::new);
                            }

                            List<MailLog> mailLogs = new ArrayList<>();

                            // if (!receivers.isEmpty()) {
                            for (User receiver : receivers) {
                                MailLog mailLog = new MailLog();

                                mailLog.setSenderEmail(senderEmail);
                                mailLog.setReceiverId(plan.getUserId());
                                mailLog.setReceiverNm(plan.getUserNm());
                                mailLog.setReceiverEmail(plan.getEmail());
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
