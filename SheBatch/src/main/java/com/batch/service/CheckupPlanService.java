package com.batch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.mapper.CheckupPlanMapper;
import com.batch.utils.model.CheckupPlan;
import com.batch.utils.model.MailLog;

@Service
public class CheckupPlanService {

    @Autowired
    private CheckupPlanMapper checkupPlanMapper;

    /**
     * 건강검진계획 조회
     * 
     * @return 건강검진계획 목록
     * @throws Exception
     */
    public List<CheckupPlan> getCheckupPlans(int batchDay) throws Exception {
        return this.checkupPlanMapper.getCheckupPlans(batchDay);
    }

    /**
     * 검색조건 해당 대상자 조회
     * 
     * @param heaCheckupPlanNo
     *            건강검진계획번호
     * @return 대상자목록
     * @throws Exception
     */
    public List<MailLog> getCheckupUsersMail(int heaCheckupPlanNo) throws Exception {
        return this.checkupPlanMapper.getCheckupUsersMail(heaCheckupPlanNo);
    }

}
