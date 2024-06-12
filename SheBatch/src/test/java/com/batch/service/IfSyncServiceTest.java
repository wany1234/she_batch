package com.batch.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IfSyncServiceTest {
    @Autowired
    private IfSyncService ifSyncService;

    @Rollback(value = true)
    @Test
    public void hrUserSyncTest() throws Exception {
        ifSyncService.hrUserSync();
    }

    @Rollback(value = true)
    @Test
    public void vendorSyncTest() throws Exception {
        ifSyncService.vendorSync();
    }

}