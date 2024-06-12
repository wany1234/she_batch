package com.batch.service;

import com.batch.mapper.CommonMapper;
import com.batch.utils.SendMailUtil;
import com.batch.utils.model.MailLog;
import com.batch.utils.model.MailResult;
import com.batch.utils.model.MailVo;
import com.batch.utils.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommonService {
    @Autowired
    private CommonMapper commonMapper;

    /**
     * 유저목록 조회
     * @param plantCd
     *          사업장코드
     * @param deptCd
     *          부서코드
     * @param deptSubYn
     *          하위부서조회여부
     * @param useYn
     *          사용여부
     * @return 유저목록
     * @throws Exception
     */
    public List<User> getUsers(String plantCd, String deptCd, String deptSubYn, String useYn) throws Exception {
        return commonMapper.getUsers(plantCd, deptCd, deptSubYn, useYn);
    }

    /**
     * 유저정보 조회
     * @param userId
     *          사번
     * @return 유저정보
     * @throws Exception
     */
    public User getUser(String userId) throws Exception {
        return commonMapper.getUser(userId);
    }

    /**
     * 메일로그 등록
     * @param mailLog
     *          메일로그
     * @return  결과
     * @throws Exception
     */
    public int createMailLog(MailLog mailLog) throws Exception {
        return commonMapper.createMailLog(mailLog);
    }

    /**
     * 필요할 경우 호출
     * 메일재발송
     * 발송여부(send_yn)가 N인 경우 재발송 처리
     * 재발송은 한번만 처리
     * @throws Exception
     */
    @Transactional
    public void failAlarmExecute() throws Exception {
        List<MailLog> failMails = commonMapper.getFailMails();
        if (failMails != null && failMails.size() > 0) {
            for (MailLog maillog : failMails) {
                MailVo mailVo = new MailVo();
                mailVo.setSenderEmail(maillog.getSenderEmail());
                mailVo.setSender(maillog.getSenderId());
                mailVo.setRecipientsEmailAddress(new String[] { maillog.getReceiverEmail() });
                mailVo.setTitle(maillog.getTitle());
                mailVo.setContents(maillog.getContent());

                MailResult mailResult = SendMailUtil.sendMail(mailVo);
                if ("SUCCESS".equals(mailResult.getResultCd())) {
                    maillog.setSendYn("Y");
                    maillog.setFailDesc("");
                } else {
                    maillog.setSendYn("N");
                    maillog.setFailDesc(mailResult.getResultMsg());
                }
                commonMapper.updateMailLog(maillog);
            }
        }
    }
}
