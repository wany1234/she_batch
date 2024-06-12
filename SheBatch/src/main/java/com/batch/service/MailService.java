package com.batch.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.batch.config.GlobalSettings;
import com.batch.mapper.MailMapper;
import com.batch.utils.model.Mail;

@Service
public class MailService {

    @Autowired
    private MailMapper mailMapper;

    @Autowired
    private GlobalSettings globalSettings;

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    /**
     * 파일내용가져오기
     *
     * @param fileName
     * @return
     */
    private String getFileContent(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource("mail/" + fileName);
            InputStream inputStream = classPathResource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "euc-kr"));

            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();

            return sb.toString();
        } catch (IllegalArgumentException ile) {
            logger.error(ile.getMessage());
            return "";
        } catch (IOException ie) {
            logger.error(ie.getMessage());
            return "";
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "";
        }
    }

    /**
     * 개발 메일HTML 내용만들기
     *
     * @param fileName
     * @param keyValue
     * @return
     * @throws Exception
     */
    public String makeContent(String fileName, HashMap<String, String> keyValue) throws Exception {
        String content = this.getFileContent(fileName);

        for (String key : keyValue.keySet()) {
            content = content.replace(key, keyValue.get(key));
        }

        return content;
    }

    /**
     * 메일HTML 내용만들기
     *
     * @param subTitle
     * @param mailDesc
     * @param mailContent
     * @param linkVisible
     * @return
     * @throws Exception
     */
    public String makeMailContent(String subTitle, String mailDesc, String mailContent, boolean linkVisible) throws Exception {
        String content = this.getFileContent("common.html");
        content = content.replace("[$MAIL_CONTENT$]", mailContent);

        return content;
    }

    /**
     * 메일로그 작성
     *
     * @param mail
     * @return
     * @throws Exception
     */
    public int insertMailLog(Mail mail) throws Exception {
        return this.mailMapper.insertMailLog(mail);
    }

    /**
     * 메일로그 작성
     *
     * @param title
     * @param content
     * @param toem
     * @param tonm
     * @return
     * @throws Exception
     */
    public int insertMailLog(String title, String content, String toem, String tonm) throws Exception {
        Mail mail = new Mail();
        // mail.setSendem(globalSettings.getSendMailEm());
        // mail.setSendnm(globalSettings.getSendMailNm());
        // mail.setTitle(title);
        // mail.setContent(content);
        // mail.setToem(toem);
        // mail.setTonm(tonm);

        return this.insertMailLog(mail);
    }

    /**
     * 발송해야할 메일로그 조회
     *
     * @return
     * @throws Exception
     */
    public List<Mail> getMailLogs() throws Exception {
        return this.mailMapper.getMailLogs();
    }

    /**
     * 메일발송 성공
     *
     * @param mail
     * @return
     * @throws Exception
     */
    public int updateSendSuccess(int logNo) throws Exception {
        Mail mail = this.mailMapper.getMailLog(logNo);
        mail.setSendYn("Y");
        mail.setTryCount(mail.getTryCount() + 1);

        return this.mailMapper.updateSendResult(mail);
    }

    /**
     * 메일발송 실패
     *
     * @param logNo
     * @param failDesc
     * @return
     * @throws Exception
     */
    public int updateSendFail(int logNo, String failDesc) throws Exception {
        Mail mail = this.mailMapper.getMailLog(logNo);
        mail.setSendYn("N");
        mail.setTryCount(mail.getTryCount() + 1);
        mail.setFailDesc(failDesc);

        return this.mailMapper.updateSendResult(mail);
    }

    /**
     * 메일발송 성공
     *
     * @param mail
     * @return
     * @throws Exception
     */
    public int updateIng(List<Mail> mailList) throws Exception {
        int result = 0;

        for (int i = 0; i < mailList.size(); i++) {
            mailList.get(i).setSendYn("I");
            mailList.get(i).setTryCount(mailList.get(i).getTryCount());
            mailList.get(i).setFailDesc("");

            result += this.mailMapper.updateSendResult(mailList.get(i));
        }

        return result;
    }
}
