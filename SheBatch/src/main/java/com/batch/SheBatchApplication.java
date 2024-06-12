package com.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.batch.service.SchedulerService;

@SpringBootApplication
@EnableScheduling
public class SheBatchApplication {

    public static void main(String[] args) {

    	SpringApplication.run(SheBatchApplication.class, args);
        
    }
}
