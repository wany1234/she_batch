package com.batch.mapper;

import java.util.HashMap;
import java.util.List;

import com.batch.utils.model.MailLog;
import com.batch.utils.model.User;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.batch.utils.model.Alarm;
import com.batch.utils.model.Mail;
import org.apache.ibatis.annotations.Param;

@Mapper
@Repository("com.batch.mapper.AlarmMapper")
public interface AlarmMapper {
    public Alarm getAlarmInfo(String batchCd) throws Exception;

    public List<HashMap<String, String>> getNextCheckPsmDocuList(int batchDay) throws Exception;

    /**
     * 알람코드로 알람정보 조회
     * 
     * @param alarmCd
     *            알람코드
     * @return 알람목록
     * @throws Exception
     */
    public List<Alarm> getAlarmByAlarmCd(@Param("alarmCd") String alarmCd) throws Exception;

    public User getUser(@Param("userId") String userId) throws Exception;

}
