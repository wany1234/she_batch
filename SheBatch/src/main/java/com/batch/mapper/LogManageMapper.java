package com.batch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("com.batch.mapper.LogManageMapper")
public interface LogManageMapper {

    public int deleteMailLogYear() throws Exception;

    public int deleteAlarmLogYear() throws Exception;

    public int deleteBatchLogYear() throws Exception;

    public int deleteLoginLogYear() throws Exception;

    public int deleteErrorLogYear() throws Exception;

}
