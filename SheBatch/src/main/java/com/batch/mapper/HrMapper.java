package com.batch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository("com.batch.mapper.HrMapper")
public interface HrMapper {

    public int deptIf() throws Exception;

    public int userIf() throws Exception;

    public int positionIf() throws Exception;

    public int dutyIf() throws Exception;

}
