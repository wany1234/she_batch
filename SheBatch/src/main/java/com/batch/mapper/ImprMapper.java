package com.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.batch.utils.model.Impr;

@Mapper
@Repository("com.batch.mapper.ImprMapper")
public interface ImprMapper {

    /**
     * 개선조치 조치기한 도래 목록 조회
     *
     */
    public List<Impr> getImprArriveList() throws Exception;

    /**
     * 개선조치 조치기한 초과 목록 조회
     *
     */
    public List<Impr> getImprOverList() throws Exception;

}
