package com.batch.service;

import com.batch.utils.ConstVal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class AlarmServiceTest {
    @Autowired
    private AlarmService alarmService;

    @Rollback(value = true)
    @Test
    public void alarmRunTest() throws Exception {
        alarmService.run(ConstVal.ALARM_BATCH_CD_PSMDOCU);
    }
}