package org.automation.scheduler;

import org.automation.model.SchedulerJob;
import org.automation.service.AutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import static org.automation.util.AutomationConstant.SCHEDULER_STATUS;

import java.util.List;
/*
 * @Auth  Prasad
 * @Date  15,Nov 2021
 * @Desc  Scheduler Component to Monitor the Critial & Re-Active Machines
 */
@Component
public class JobMonitorScheduler {
	
	private static final  Logger LOGGER=LoggerFactory.getLogger(JobMonitorScheduler.class);
  
	@Autowired
	private AutomationService automationService;
	
	@Scheduled(fixedDelay=5000)
	public void findAlarmMachines() {
		LOGGER.info("*****Started Finding Alarm Machines Scheduler*****");
		automationService.findAllAlarmMachines();
		LOGGER.info("*****End Completed Finding Alarm Machines Scheduler*****");
	}
	
	@Scheduled(fixedDelay=10000)
	public void findActiveMachines(){
		LOGGER.info("*****Started Finding Active Machines Scheduler*****");
		List<SchedulerJob> schedulerJobs=automationService.getAllSchedulersByStatus(SCHEDULER_STATUS.IN_PROGRESS.toString());
		if(schedulerJobs!=null && !schedulerJobs.isEmpty()) {
			schedulerJobs.forEach(e->{
				e.setStatus(SCHEDULER_STATUS.STARTED.toString());
			});
		}
		LOGGER.info("*****Completed Finding Active Machines Scheduler*****");
	}
}
