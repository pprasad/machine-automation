package org.automation.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.automation.model.AlarmHistory;
import org.automation.model.ProductResultHistory;
import org.automation.model.ProductResultHistoryActive;
import org.automation.model.SchedulerJob;
import org.bson.types.ObjectId;
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
	 
	 List<ProductResultHistory> findProdHistoryWithProdcutResultAndStartDateGe(String name,Long prodResult,Date startDate);
	 
	 List<SchedulerJob> findAllSchedulers();
	 
	 List<AlarmHistory> findAlarmHistoryByEndDateLessThanEqualAndTypeNotContain(Date date,String type);
	 
	 Optional<SchedulerJob> getSchedulerById(String id);
	 
	 List<SchedulerJob> findByNameAndAlarmNameAndProdStartDateAndStatus(String name,String alarmName,Date prodStarDate,String status);
}
