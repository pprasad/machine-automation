package org.automation.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.automation.model.AlarmHistory;
import org.automation.model.AlarmHistoryStatus;
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
	 
	 SchedulerJob saveOrUpdateScheduler(SchedulerJob schedulerJob);
	 
	 Optional<ProductResultHistoryActive> getActiveProductByL1Name(String name);
	 
	 List<ProductResultHistory> getProductHistroyByNameAndProductResult(String name,Long prodResult);
	 
	 List<ProductResultHistory> findProdHistoryWithProdcutResultAndStartDateGe(String name,Long prodResult,Date startDate);
	 
	 List<SchedulerJob> findAllSchedulers();
	 
	 List<AlarmHistory> findAlarmHistoryByEndDateLessThanEqualAndTypeNotContain(Date date,String type);
	 
	 Optional<SchedulerJob> getSchedulerById(String id);
	 
     Optional<SchedulerJob> findByNameAndAlarmNameAndStatusIn(String name,String alarmName,List<String> status);
     
     Optional<SchedulerJob> findByNameAndAlarmNameAndStatusInAndProdStartDateIsNull(String name,String alarmName,List<String> status);
     
     Optional<SchedulerJob> findByNameAndAlarmNameWithStatusAndEndDateProductStartDateIsNull(String name,String alarmName,Date date,List<String> status);
     
     Optional<AlarmHistoryStatus> findById(String id);
     
     AlarmHistoryStatus saveOrUpdateAlarmHistoryStatus(AlarmHistoryStatus alarmHistoryStatus);
}
