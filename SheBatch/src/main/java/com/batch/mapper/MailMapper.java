package com.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.batch.utils.model.Mail;

@Mapper
@Repository("com.batch.mapper.MailMapper")
public interface MailMapper {

    /**
     * 메일로그 작성
     *
     * @param mail
     * @return
     * @throws Exception
     */
    public int insertMailLog(Mail mail) throws Exception;

    /**
     * 발송해야할 메일로그 조회
     *
     * @return
     * @throws Exception
     */
    public List<Mail> getMailLogs() throws Exception;

    /**
     * 메일로그 상세조회
     *
     * @param logNo
     * @return
     * @throws Exception
     */
    public Mail getMailLog(@Param("logNo") int logNo) throws Exception;

    /**
     * 메일발송 결과 수정
     *
     * @param mail
     * @return
     * @throws Exception
     */
    public int updateSendResult(Mail mail) throws Exception;

}
