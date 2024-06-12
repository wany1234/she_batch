package com.batch.utils.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class BatchService {
    private String batchCd;

    private String batchNm;

    private String batchDesc;

    private String cycleStartYmd;

    private String cycleDayCd;

    private String cycleDayNm;

    private String cycleDay;

    private String cycleHmCd;

    private String cycleHmNm;

    private String cycleHm;

    private String useYn;

    private String useYnNm;

    private String createUserId;

    private String createUserNm;

    private String createDt;

    private String updateUserId;

    private String updateUserNm;

    private String updateDt;

    private String batchDay;

    /**
     * 배치주기 비교
     *
     * @param batchService
     * @return
     */
    public boolean checkCycle(BatchService batchService) {
        return this.cycleStartYmd.equals(batchService.getCycleStartYmd()) && this.cycleDayCd.equals(batchService.getCycleDayCd()) && this.cycleDay.equals(batchService.getCycleDay()) && this.cycleHmCd.equals(batchService.getCycleHmCd()) && this.cycleHm.equals(batchService.getCycleHm()) && this.useYn.equals(batchService.getUseYn());
    }

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    /**
     * 배치실행 여부조회
     *
     * @return
     */
    public boolean isRunnable() {
        boolean runnable = false;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = format.parse(format.format(new Date()));
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentTime = format2.parse(format2.format(new Date()));
            Date startDate = format.parse(this.cycleStartYmd);

            if (this.useYn != null && this.useYn.equals("Y") && currentDate.compareTo(startDate) > -1) {
                SimpleDateFormat formatYmd = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatDd = new SimpleDateFormat("dd");
                SimpleDateFormat formatHHmm = new SimpleDateFormat("HHmm");

                boolean ddSuccess = false;
                String dd = formatDd.format(currentDate);
                if (this.cycleDayCd.equals("S")) {
                    // 지정일
                    if (dd.equals(this.cycleDay)) {
                        ddSuccess = true;
                    }
                } else {
                    // 반복일
                    if (formatYmd.format(currentDate).equals(formatYmd.format(startDate))) {
                        // 당일은 무조건 실행
                        ddSuccess = true;
                    } else {
                        long time = currentDate.getTime() - startDate.getTime();
                        long days = time / (1000 * 60 * 60 * 24);
                        days = Math.abs(days);

                        if ((int) (days % Integer.valueOf(this.cycleDay)) == 0) {
                            ddSuccess = true;
                        }
                    }
                }

                if (ddSuccess) {
                    String hhMm = formatHHmm.format(currentTime);
                    if (this.cycleHmCd.equals("S")) {
                        // 지정시분
                        if (hhMm.equals(this.cycleHm)) {
                            runnable = true;
                        }
                    } else {
                        long time = currentTime.getTime() - startDate.getTime();
                        long minutes = time / (1000 * 60);
                        minutes = Math.abs(minutes);

                        if ((int) (minutes % Integer.valueOf(this.cycleHm)) == 0) {
                            runnable = true;
                        }
                    }
                }
            }
        } catch (ParseException pe) {
            logger.error(pe.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return runnable;
    }
}
