package org.automation.service;

import java.util.List;

import org.automation.model.L1PoolEntity;
import org.automation.model.SchedulerJob;
/*
 * @Auth prasad
 * @Date 15,Nov 2021
 */
public interface AutomationService {
     
	 void findAllAlarmMachines();
	 
	 List<SchedulerJob> getAllSchedulersByStatus(String status);
	 
	 boolean updateScheduler(List<SchedulerJob> schedulerJobs);
}
