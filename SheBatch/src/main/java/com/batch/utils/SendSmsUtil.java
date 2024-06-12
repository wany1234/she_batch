package com.batch.utils;

import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.ibatis.io.Resources;

import com.batch.utils.model.SmsResult;
import com.batch.utils.model.SmsVo;

public class SendSmsUtil {

    private static final String HOST_NAME = "http://sms.postown.net/bin/sms/Sender?table=posdata_ep";

    /**
     * SMS 전송
     * 
     * @param smsVo
     * @return
     * @throws MessagingException
     */
    public static SmsResult sendSms(SmsVo smsVo) {
        SmsResult result = new SmsResult();

        try {
            // 서버 확인
            Properties properties = new Properties();
            String resource = "application.properties";
            Reader reader = Resources.getResourceAsReader(resource);
            properties.load(reader);
            String active = properties.getProperty("spring.profiles.active");

            if ("dev".equals(active)) {
                result.setResultCd("SUCCESS"); // 결과코드
                result.setResultMsg("개발은 SMS 서비스 이용에 제한됩니다."); // 결과메세지
            } else {
                HttpURLConnection huc = null;
                try {
                    String requestUrl = HOST_NAME + "&sndr=" + smsVo.getSndr() + "&callback=" + smsVo.getCallback() + "&rcvr=" + smsVo.getRcvr() + "&rcvrnum=" + smsVo.getRcvrnum() + "&msg=" + smsVo.getMsg();

                    requestUrl = requestUrl.replace(" ", "%20");
                    URL url = new URL(requestUrl);
                    huc = (HttpURLConnection) url.openConnection();

                    String message = huc.getResponseMessage();
                    int returnCode = huc.getResponseCode();

                    if (returnCode == HttpURLConnection.HTTP_OK) {
                        result.setResultCd("SUCCESS"); // 결과코드
                        result.setResultMsg("SMS 발송에 성공하였습니다. message : " + message); // 결과메세지
                    } else {
                        result.setResultCd("FAILURE"); // 결과코드
                        result.setResultMsg("SMS 발송에 실패하였습니다. message: " + message); // 결과메세지
                    }
                } catch (IOException ie) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("SMS 발송중 오류가 발생했습니다. message: " + ie.getMessage()); // 결과메세지
                } catch (Exception e) {
                    result.setResultCd("FAILURE"); // 결과코드
                    result.setResultMsg("SMS 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
                } finally {
                    if (huc != null) {
                        huc.disconnect();
                    }
                }
            }
        } catch (IOException ie) {
            result.setResultCd("FAILURE"); // 결과코드
            result.setResultMsg("SMS 발송중 오류가 발생했습니다. message: " + ie.getMessage()); // 결과메세지
        } catch (Exception e) {
            result.setResultCd("FAILURE"); // 결과코드
            result.setResultMsg("SMS 발송중 오류가 발생했습니다. message: " + e.getMessage()); // 결과메세지
        }
        return result;
    }
}
