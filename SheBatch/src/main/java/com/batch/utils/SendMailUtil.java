package com.batch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.batch.utils.model.MailResult;
import com.batch.utils.model.MailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMailUtil {

    private static final Logger logger = LoggerFactory.getLogger(SendMailUtil.class);
    private static final String SMTP_HOST_NAME = "spamout.kggroup.co.kr";
    private static final String SMTP_PORT = "25";
    private static final String SMTP_HOST_ID = "kgdbstl_sysmail";
    private static final String SMTP_HOST_PASSWORD = "dnsdud!@12";
    private static final String SMTP_ENCODING = "UTF-8";

    private final static String ssoTokenStr = "coviTok=${coviTok}";

    private final static String systemName = "[안전보건관리시스템]";

    /**
     * 파일내용가져오기
     *
     * @param fileName
     * @return
     */
    private static String getFileContent(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource("mail/" + fileName);
            InputStream inputStream = classPathResource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

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
     * 메일HTML 내용만들기
     *
     * @param mailTitle
     * @param mailContent
     * @param link
     * @return
     * @throws Exception
     */
    public static String makeMailContent(String mailTitle, String mailContent, String link) throws Exception {
        Properties properties = new Properties();
        String resource = "application.properties";
        Reader reader = Resources.getResourceAsReader(resource);
        properties.load(reader);
        String active = properties.getProperty("spring.profiles.active");

        resource = String.format("application-%s.properties", active);
        reader = Resources.getResourceAsReader(resource);
        properties.load(reader);
        String frontendUrl = properties.getProperty("frontend.url");

        String content = getFileContent("common.html");
        String imgLink = frontendUrl + "/src/assets/images/she.png";
        String mailLink = frontendUrl + link + "?" + ssoTokenStr;

        if (content != null && !"".equals(content)) {
            content = content.replace("[$SUB_TITLE$]", mailTitle);
            content = content.replace("[$MAIL_CONTENT$]", mailContent);
            content = content.replace("[$IMG_LINK$]", imgLink);
            content = content.replace("[$MAIL_LINK$]", mailLink);
            return content;
        } else {
            return mailContent;
        }
    }

    /**
     * 메일 전송
     *
     * @param mailVo
     * @return
     * @throws MessagingException
     */
    public static MailResult sendMail(MailVo mailVo) {
        MailResult result = new MailResult();

        try {
            // 서버 확인
            Properties properties = new Properties();
            String resource = "application.properties";
            Reader reader = Resources.getResourceAsReader(resource);
            properties.load(reader);
            String active = properties.getProperty("spring.profiles.active");

            if (!"prd".equals(active)) {
                result.setResultCd("SUCCESS"); // 결과코드
                result.setResultMsg("개발은 메일 서비스 이용에 제한됩니다."); // 결과메세지
                makeMailContent(mailVo.getMailTitle(), mailVo.getContents(), mailVo.getLink());
            } else {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.host", SMTP_HOST_NAME);
                    props.put("mail.smtp.port", SMTP_PORT);
                    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(SMTP_HOST_ID, SMTP_HOST_PASSWORD);
                        }
                    });

                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(mailVo.getSenderEmail(), mailVo.getSender(), "8859_1"));

                    String[] recipients = mailVo.getRecipientsEmailAddress();
                    InternetAddress[] addressTo = new InternetAddress[recipients.length];
                    int i = 0;
                    for (String recipient : recipients) {
                        addressTo[i] = new InternetAddress(recipient);
                        // addressTo[i] = new
                        // InternetAddress("pe911642@poscoict.com");
                        i++;
                    }
                    msg.setRecipients(Message.RecipientType.TO, addressTo);
                    msg.setSubject(systemName + " " + mailVo.getTitle(), "utf-8");
                    msg.setContent(makeMailContent(mailVo.getMailTitle(), mailVo.getContents(), mailVo.getLink()), "text/html;charset=euc-kr");
                    msg.setHeader("Content-Transfer-Encoding", "base64");

                    Transport transport = session.getTransport();

                    try {
                        Transport.send(msg);

                        result.setResultCd("SUCCESS"); // 결과코드
                        result.setResultMsg("메일이 성공적으로 발송되었습니다."); // 결과메세지
                    } catch (SendFailedException se) {
                        result.setResultCd("FAILURE"); // 결과코드
                        result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + se.getMessage()); // 결과메세지
                    } catch (Exception e) {
                        result.setResultCd("FAILURE"); // 결과코드
                        result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
                    } finally {
                        // Close and terminate the connection.
                        transport.close();
                    }
                } catch (IllegalWriteException iwe) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + iwe.getMessage()); // 결과메세지
                } catch (MessagingException me) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + me.getMessage()); // 결과메세지
                } catch (Exception e) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
                }
            }
        } catch (IOException ie) {
            result.setResultCd("FAILURE"); // 결과코드
            result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + ie.getMessage()); // 결과메세지
        } catch (Exception e) {
            result.setResultCd("FAILURE"); // 결과코드
            result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
        }
        return result;
    }

    /**
     * 메일 전송 테스트용
     *
     * @param mailVo
     * @return
     * @throws MessagingException
     */
    public static MailResult sendMailTest(MailVo mailVo) {
        MailResult result = new MailResult();

        try {
            // 서버 확인
            Properties properties = new Properties();
            String resource = "application.properties";
            Reader reader = Resources.getResourceAsReader(resource);
            properties.load(reader);
            String active = properties.getProperty("spring.profiles.active");
            if ("dev".equals(active)) {

                try {
                    Properties props = new Properties();
                    props.put("mail.use", "true");
                    props.put("mail.smtp.host", SMTP_HOST_NAME);
                    props.put("mail.smtp.port", SMTP_PORT);
                    props.put("mail.host.auth.flag", "true");
                    props.put("mail.smtp.encoding", SMTP_ENCODING);
                    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(SMTP_HOST_ID, SMTP_HOST_PASSWORD);
                        }
                    });

                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(mailVo.getSenderEmail()));

                    String[] recipients = mailVo.getRecipientsEmailAddress();
                    InternetAddress[] addressTo = new InternetAddress[recipients.length];
                    int i = 0;
                    for (String recipient : recipients) {
                        addressTo[i] = new InternetAddress(recipient);
                        i++;
                    }
                    msg.setRecipients(Message.RecipientType.TO, addressTo);
                    msg.setSubject(systemName + " " + mailVo.getTitle(), "utf-8");
                    msg.setContent(makeMailContent(mailVo.getMailTitle(), mailVo.getContents(), mailVo.getLink()), "text/html;charset=utf-8");
                    msg.setHeader("Content-Transfer-Encoding", "base64");

                    Transport transport = session.getTransport();

                    try {
                        Transport.send(msg);
                        result.setResultCd("SUCCESS"); // 결과코드
                        result.setResultMsg("메일이 성공적으로 발송되었습니다."); // 결과메세지
                    } catch (Exception e) {
                        result.setResultCd("FAILURE"); // 결과코드
                        result.setResultMsg("메일이 발송중 오류가 발생했습니다. [사유: " + e.getMessage() + "]"); // 결과메세지
                    } finally {
                        // Close and terminate the connection.
                        transport.close();
                    }
                } catch (Exception e) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("메일이 발송중 오류가 발생했습니다. [사유: " + e.getMessage() + "]"); // 결과메세지
                }
            }

            // if (!"prd".equals(active)) {
            // result.setResultCd("SUCCESS"); // 결과코드
            // result.setResultMsg("개발은 메일 서비스 이용에 제한됩니다."); // 결과메세지
            // makeMailContent(mailVo.getMailTitle(), mailVo.getContents(),
            // mailVo.getLink());
            // }
            else {
                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.host", SMTP_HOST_NAME);
                    props.put("mail.smtp.port", SMTP_PORT);
                    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(SMTP_HOST_ID, SMTP_HOST_PASSWORD);
                        }
                    });

                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(mailVo.getSenderEmail(), mailVo.getSender(), "8859_1"));

                    String[] recipients = mailVo.getRecipientsEmailAddress();
                    InternetAddress[] addressTo = new InternetAddress[recipients.length];
                    int i = 0;
                    for (String recipient : recipients) {
                        addressTo[i] = new InternetAddress(recipient);
                        // addressTo[i] = new
                        // InternetAddress("pe911642@poscoict.com");
                        i++;
                    }
                    msg.setRecipients(Message.RecipientType.TO, addressTo);
                    msg.setSubject(systemName + " " + mailVo.getTitle(), "utf-8");
                    msg.setContent(makeMailContent(mailVo.getMailTitle(), mailVo.getContents(), mailVo.getLink()), "text/html;charset=euc-kr");
                    msg.setHeader("Content-Transfer-Encoding", "base64");

                    Transport transport = session.getTransport();

                    try {
                        Transport.send(msg);

                        result.setResultCd("SUCCESS"); // 결과코드
                        result.setResultMsg("메일이 성공적으로 발송되었습니다."); // 결과메세지
                    } catch (SendFailedException se) {
                        result.setResultCd("FAILURE"); // 결과코드
                        result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + se.getMessage()); // 결과메세지
                    } catch (Exception e) {
                        result.setResultCd("FAILURE"); // 결과코드
                        result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
                    } finally {
                        // Close and terminate the connection.
                        transport.close();
                    }
                } catch (IllegalWriteException iwe) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + iwe.getMessage()); // 결과메세지
                } catch (MessagingException me) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + me.getMessage()); // 결과메세지
                } catch (Exception e) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
                }
            }
        } catch (IOException ie) {
            result.setResultCd("FAILURE"); // 결과코드
            result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + ie.getMessage()); // 결과메세지
        } catch (Exception e) {
            result.setResultCd("FAILURE"); // 결과코드
            result.setResultMsg("메일이 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
        }
        return result;
    }

    public static List<MailResult> sendMails(List<MailVo> mailVoList) {
        MailResult mailResult = new MailResult();
        List<MailResult> results = new ArrayList<>();
        if (mailVoList != null) {
            for (MailVo mailVo : mailVoList) {
                mailResult = sendMail(mailVo);
                sendMailTest(mailVo);
                String resultCd = mailResult.getResultCd();
                String resultMsg = mailResult.getResultMsg();
                mailResult.setMailLogs(mailVo.getMailLogs().stream().map(log -> {
                    log.setSendYn(resultCd.equals("FAILURE") ? "N" : "Y");
                    if (resultCd.equals("FAILURE")) {
                        log.setFailDesc(resultMsg);
                    }
                    return log;
                }).collect(Collectors.toList()));
                results.add(mailResult);
            }
        }

        return results;
    }

}
