package com.batch.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.batch.config.GlobalSettings;
import com.batch.utils.SendMailUtil;
import com.batch.utils.model.Mail;
import com.batch.utils.model.MailResult;
import com.batch.utils.model.MailVo;

@Service
public class SendMailService {

    @Autowired
    private CheckupPlanService checkupPlanService;

    @Autowired
    private MailService mailService;

    @Autowired
    private GlobalSettings globalSettings;

    private static final Logger logger = LoggerFactory.getLogger(SendMailService.class);

    /*
     * 메일발송 인원 체크
     */

    public synchronized String sendCheckUsers() throws Exception {
        String result = "";
        int mailResult = 0;
        List<Mail> mailList = mailService.getMailLogs();
        mailService.updateIng(mailList);

        for (int i = 0; i < mailList.size(); i++) {
            mailResult += this.sendMail(mailList.get(i));
        }
        if (mailResult >= 1) {
            result = "SUCCESS";
        } else if (mailResult < 0) {
            result = "FAIL";
        }

        return result;
    }

    /*
     * 메일발송
     */
    public int sendMail(Mail mail) throws Exception {
        int result = 0;

        String[] recipientsEmail = { mail.getReceiverEmail() };
        String senderEmail = mail.getSenderEmail();
        String title = mail.getTitle();

        String contents = mail.getContent();

        MailVo mailVo = new MailVo();
        mailVo.setTitle(title.toString());
        mailVo.setContents(contents);
        mailVo.setRecipientsEmailAddress(recipientsEmail);
        mailVo.setSenderEmail(senderEmail);

        // 메일 smtp 계정 발급시 실행 예정
        MailResult mailResult = SendMailUtil.sendMail(mailVo);

        if (mailResult.getResultCd().equals("SUCCESS")) {
            mailService.updateSendSuccess(mail.getLogNo());
            result = 1;
        } else if (mailResult.getResultCd().equals("FAILURE")) {
            mailService.updateSendFail(mail.getLogNo(), mailResult.getMailLog().getFailDesc());
            result = -1;
        }

        return result;
    }
}
