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
package com.batch.service;

import com.batch.mapper.BatchMapper;
import com.batch.utils.model.Batch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchService {
    @Autowired
    private BatchMapper batchMapper;

    public Batch getBatch(String batchCd) throws Exception {
        return batchMapper.getBatch(batchCd);
    }

}
