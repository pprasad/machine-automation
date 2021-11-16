package org.automation.service;

import java.util.List;
import java.util.Optional;

import org.automation.model.ProductResultHistory;
import org.automation.model.ProductResultHistoryActive;
import org.automation.model.SchedulerJob;
/*
 * @Auth prasad
 * @Date 15,Nov 2021
 */
public interface AutomationService {
     
	 void findAllAlarmMachines();
	 
	 List<SchedulerJob> getAllSchedulersByStatus(String status);
	 
	 List<SchedulerJob> getAllSchedulersByStatus(List<String> status);
	 
	 boolean updateScheduler(List<SchedulerJob> schedulerJobs);
	 
	 Optional<ProductResultHistoryActive> getActiveProductByL1Name(String name);
	 
	 List<ProductResultHistory> getProductHistroyByNameAndProductResult(String name,Long prodResult);
}
