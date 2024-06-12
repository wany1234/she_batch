package com.batch.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import com.batch.service.AlarmService;
import com.batch.service.SchedulerService;
import com.batch.utils.model.BatchService;

@Service
public class AlarmScheduler extends BaseScheduler {

	@Autowired
	private AlarmService alarmService;

	private static final Logger logger = LoggerFactory.getLogger(AlarmScheduler.class);

	/**
	 * 실행
	 */
	public void run() throws Exception {
		String message = this.alarmService.run(this.batchCd);
		if (message != null && !message.equals("")) {
			// 배치 로직 작성
			this.schedulerService.insertBatchRunnableLog(this.batchService, message);
		}
	}
}
