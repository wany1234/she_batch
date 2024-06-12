package com.batch.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LawServiceTest {
    @Autowired
    private LawService lawService;

    @Rollback(value = true)
    @Test
    public void lawElawIFTest() throws Exception {
        lawService.lawElawIF();
    }

    @Rollback(value = true)
    @Test
    public void lawMakingIFTest() throws Exception {
        lawService.lawMakingIF();
    }

}