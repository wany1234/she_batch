package com.batch.mapper;

import com.batch.utils.model.CommonUser;
import com.batch.utils.model.MailLog;
import com.batch.utils.model.User;
import com.batch.utils.model.mgtTgtPlan;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommonMapper {
    /**
     * 유저목록 조회
     * 
     * @param plantCd
     *            사업장코드
     * @param deptCd
     *            부서코드
     * @param deptSubYn
     *            하위부서조회여부
     * @param useYn
     *            사용여부
     * @return 유저목록
     * @throws Exception
     */
    public List<User> getUsers(@Param("plantCd") String plantCd, @Param("deptCd") String deptCd, @Param("deptSubYn") String deptSubYn, @Param("useYn") String useYn) throws Exception;

    /**
     * 유저정보 조회
     * 
     * @param userId
     *            사번
     * @return 유저정보
     * @throws Exception
     */
    public User getUser(@Param("userId") String userId) throws Exception;

    /**
     * 메일로그 등록
     * 
     * @param mailLog
     *            메일로그
     * @return 결과
     * @throws Exception
     */
    public int createMailLog(MailLog mailLog) throws Exception;

    /**
     * 메일로그 수정 메일 재발송 후 결과 수정
     * 
     * @param mailLog
     *            메일로그
     * @return 결과
     * @throws Exception
     */
    public int updateMailLog(MailLog mailLog) throws Exception;

    /**
     * 재발송 메일 목록 조회
     * 
     * @return 재발송 메일 목록
     * @throws Exception
     */
    public List<MailLog> getFailMails() throws Exception;

    /**
     * 시스템 공통 메일 발신계정 조회
     *
     */
    public CommonUser getCommonUser() throws Exception;
}
