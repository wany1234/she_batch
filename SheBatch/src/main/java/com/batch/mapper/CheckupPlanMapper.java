/**
 * Copyright (C) 2019, 2019 All Right Reserved, http://www.yullin.com/
 *
 * SHE Business can not be copied and/or distributed without the express
 * permission of Yullin Technologies
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.batch.utils.model.CheckupPlan;
import com.batch.utils.model.MailLog;

/**
 * 건강검진 계획 맵퍼
 *
 */
@Mapper
@Repository("com.batch.mapper.CheckupPlanMapper")
public interface CheckupPlanMapper {

    /**
     * 건강검진계획 조회
     * 
     * @return 건강검진계획 목록
     * @throws Exception
     */
    public List<CheckupPlan> getCheckupPlans(@Param("batchDay") int batchDay) throws Exception;

    /**
     * 검색조건 해당 대상자 조회
     * 
     * @param heaCheckupPlanNo
     *            건강검진계획번호
     * 
     * @return 대상자목록
     * @throws Exception
     */
    public List<MailLog> getCheckupUsersMail(@Param("heaCheckupPlanNo") int heaCheckupPlanNo) throws Exception;

}
