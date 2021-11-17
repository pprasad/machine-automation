package org.automation.scheduler;

import static org.automation.util.AutomationConstant.isEmpty;
import static org.automation.util.AutomationConstant.converTime;
import static org.automation.util.AutomationConstant.ERROR_RECORD_NOTFOUND;
import static org.automation.util.AutomationConstant.ERROR_WEAKUP_STATUS;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.automation.model.ProductResultHistory;
import org.automation.model.ProductResultHistoryActive;
import org.automation.model.SchedulerJobEntity;
import org.automation.service.AutomationService;
import org.automation.util.AutomationConstant.SCHEDULER_STATUS;
import org.automation.util.AutomationConstant.SortByDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/*
 * @Auth  Prasad
 * @Date  15,Nov 2021
 * @Desc  Monitoring Critial & Re-Active Machines
 */
@Component
public class JobMonitorScheduler {
	
	private static final  Logger LOGGER=LoggerFactory.getLogger(JobMonitorScheduler.class);
  
	@Autowired
	private AutomationService automationService;
	/***
	 * 
	 * findAlarmMachine method pull alarm trigger machine information from L1Pool collection to scheduler
	 * 
	 * */
	@Scheduled(fixedDelay=5000)
	public void findAlarmMachines() {
		LOGGER.info("*****Started Finding Alarm Machines Scheduler*****");
		automationService.findAllAlarmMachines();
		LOGGER.info("*****End Completed Finding Alarm Machines Scheduler*****");
	}
	/***
	 * 
	 * findActiveMachines method process active machine information by using productresult_historyactive & 
	 * productresult_history collections
	 * 
	 * */
	@Scheduled(fixedDelay=10000)
	public void findActiveMachines(){
		LOGGER.info("*****Started Finding Active Machines Scheduler*****");
		try{
			List<SchedulerJobEntity> schedulerJobs=automationService.getAllSchedulersByStatus(Arrays.asList(SCHEDULER_STATUS.IN_PROGRESS.toString(),SCHEDULER_STATUS.FAILURE.toString()));
			if(schedulerJobs!=null && !schedulerJobs.isEmpty()) {
				schedulerJobs.forEach(e->{
					e.setStatus(SCHEDULER_STATUS.STARTED.toString());
				});
				boolean flag=automationService.updateScheduler(schedulerJobs);
				if(flag){
				   schedulerJobs.forEach(e->{
					   Optional<ProductResultHistoryActive> prodResultOpt=automationService.getActiveProductByL1Name(e.getName());
					   if(prodResultOpt.isPresent()){
						    LOGGER.info("Product Name:{}",prodResultOpt.get().getName());
						    List<ProductResultHistory> productResultHistories=automationService.
						    		getProductHistroyByNameAndProductResult(prodResultOpt.get().getName(),0l);
						    LOGGER.info("ProductResultHistory is Empty:{}",isEmpty(productResultHistories)?productResultHistories.isEmpty():true);
						    if(productResultHistories!=null && !productResultHistories.isEmpty()){
						    	Collections.sort(productResultHistories,new SortByDate());
						    	ProductResultHistory productResultHistory=productResultHistories.get(0);
						    	LOGGER.info("ProductName:{}",productResultHistory.getName());
						    	Long timeDiff=productResultHistory.getEndDate().getTime()-productResultHistory.getStartDate().getTime();
						    	e.setActiveTime(converTime(timeDiff));
						    	e.setEndDate(LocalDate.now());
						    	e.setStatus(SCHEDULER_STATUS.SUCCESS.toString());
						    	e.setErrorMsg(null);
						    }else {
						    	e.setStatus(SCHEDULER_STATUS.FAILURE.toString());
						    	e.setErrorMsg(ERROR_WEAKUP_STATUS);
						    }
						   automationService.updateScheduler(Arrays.asList(e));
					   }else{
						   e.setStatus(SCHEDULER_STATUS.FAILURE.toString());
						   e.setErrorMsg(ERROR_RECORD_NOTFOUND);
						   automationService.updateScheduler(Arrays.asList(e));
					   }
				   });
				  
				}
			}
		}catch(Exception ex){
			LOGGER.error("unable to process ActiveMachines:{}",ex);
		}
		LOGGER.info("*****Completed Finding Active Machines Scheduler*****");
	}
}
