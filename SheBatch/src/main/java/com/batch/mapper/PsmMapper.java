package com.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.batch.utils.model.CodeMaster;
import com.batch.utils.model.EduUser;
import com.batch.utils.model.Facility;
import com.batch.utils.model.LawElaw;
import com.batch.utils.model.LawMaking;
import com.batch.utils.model.LegiLawBasics;
import com.batch.utils.model.LegiLawHang;
import com.batch.utils.model.LegiLawHo;
import com.batch.utils.model.LegiLawJomun;
import com.batch.utils.model.LegiLawMok;
import com.batch.utils.model.Psm;

@Mapper
@Repository("com.batch.mapper.PsmMapper")
public interface PsmMapper {

    /**
     * 코드목록 조회
     *
     * @param codeGroupCd
     *            코드그룹
     * @return 코드목록
     * @throws Exception
     */
    public List<CodeMaster> getSelect(@Param("codeGroupCd") String codeGroupCd) throws Exception;

    /**
     * 개정법규 seq 조회
     *
     * @return 개정법규 seq
     * @throws Exception
     *             예외
     */
    public int getlawElawSeq() throws Exception;

    /**
     * 개정법규 I/F 저장
     *
     * @param lawElaw
     *            개정법규
     * @return 변경행수
     * @throws Exception
     *             예외
     */
    public int mergeLawIF(LawElaw lawElaw) throws Exception;

    /**
     * 저장된 개정법규 I/F 목록 조회
     * 
     * @return
     * @throws Exception
     */
    public List<LegiLawBasics> selectLawBasicList() throws Exception;

    /**
     * 개정법규 기본정보 I/F 저장
     *
     * @param legiLawBasics
     *            개정법규 기본정보
     * @return 변경행수
     * @throws Exception
     */
    public int mergeLawBasicIF(LegiLawBasics legiLawBasics) throws Exception;

    /**
     * 개정법규 조문정보 I/F 저장
     *
     * @param legiLawJomun
     *            개정법규 기본정보
     * @return 변경행수
     * @throws Exception
     */
    public int mergeLawJomunIf(LegiLawJomun legiLawJomun) throws Exception;

    /**
     * 개정법규 항정보 I/F 저장
     *
     * @param legiLawHang
     *            개정법규 기본정보
     * @return 변경행수
     * @throws Exception
     */
    public int mergeLawHangIf(LegiLawHang legiLawHang) throws Exception;

    /**
     * 개정법규 호정보 I/F 저장
     *
     * @param legiLawHo
     *            개정법규 기본정보
     * @return 변경행수
     * @throws Exception
     */
    public int mergeLawHoIf(LegiLawHo legiLawHo) throws Exception;

    /**
     * 개정법규 목정보 I/F 저장
     *
     * @param legiLawMok
     *            개정법규 기본정보
     * @return 변경행수
     * @throws Exception
     */
    public int mergeLawMokIf(LegiLawMok legiLawMok) throws Exception;

    /**
     * 개정법규 목록 업데이트 (상세 생성 후 생성여부 Y로 변경)
     * 
     * @return
     * @throws Exception
     */
    public int updateLawElaw() throws Exception;

    /**
     * 입법예고 seq 조회
     *
     * @return 입법예고 seq
     * @throws Exception
     *             예외
     */
    public int getlawMakingSeq() throws Exception;

    /**
     * 입법예고 I/F 저장
     *
     * @param lawMaking
     *            입법예고
     * @return 변경행수
     * @throws Exception
     */
    public int mergeLawMakingIF(LawMaking lawMaking) throws Exception;

    /**
     * 교육시작일 7일 전 대상자 목록 조회
     *
     */
    public List<EduUser> getUserList(@Param("eduSYmd") String eduSYmd) throws Exception;

    /**
     * 목표관리 부서별 실적 중 작성단계 조회
     *
     */
    public List<Psm> getPsmScheduler(@Param("batchDay") String batchDay) throws Exception;

    /**
     * 시스템 공통 메일 발신계정 조회
     *
     */
    public Psm getPsmCommonUser() throws Exception;

    /**
     * 설비마스터 조회
     *
     */
    public List<Facility> getFacilityScheduler(@Param("batchDay") String batchDay) throws Exception;

    /**
     * 배치날짜 조회
     *
     */
    public String getBatchDay(@Param("batchCd") String batchCd) throws Exception;

    /**
     * 유효성 여부 변경
     *
     */
    public int updatePsmEffectiveYn(@Param("psmDocuNo") int psmDocuNo) throws Exception;

    public int updateFacilityEffectiveYn(@Param("safFacilityCd") String safFacilityCd) throws Exception;
}
