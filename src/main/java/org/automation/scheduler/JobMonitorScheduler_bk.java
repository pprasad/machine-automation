package org.automation.scheduler;
import static org.automation.util.AutomationConstant.ERROR_OBJECT_EMPTY;
import static org.automation.util.AutomationConstant.ERROR_RECORD_NOTFOUND;
import static org.automation.util.AutomationConstant.ERROR_WEAKUP_STATUS;
import static org.automation.util.AutomationConstant.TRIGGER_TYPE_OPR;
import static org.automation.util.AutomationConstant.isEmpty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.automation.model.AlarmHistory;
import org.automation.model.ProductResultHistory;
import org.automation.model.ProductResultHistoryActive;
import org.automation.model.SchedulerJob;
import org.automation.service.AutomationService;
import org.automation.util.AutomationConstant.SCHEDULER_STATUS;
import org.automation.util.AutomationConstant.SortByDate;
import org.automation.util.AutomationConstant.AlarmHistorySortByDate;
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
//@Component
public class JobMonitorScheduler_bk {
	
	private static final  Logger LOGGER=LoggerFactory.getLogger(JobMonitorScheduler_bk.class);
  
	@Autowired
	private AutomationService automationService;
	/*
	 * @desc it will update failure status if records in started status after re-start scheduler 
	 */
	//@PostConstruct
	public void init() {
		LOGGER.info("****Started PostConstruct*****");
		try{
			List<SchedulerJob> schedulerJobs=automationService.getAllSchedulersByStatus(Arrays.asList(SCHEDULER_STATUS.STARTED.toString()));
			if(schedulerJobs!=null && !schedulerJobs.isEmpty()) {
				schedulerJobs.forEach(e->{
					e.setStatus(SCHEDULER_STATUS.FAILURE.toString());
					e.setErrorMsg("Scheduler Re-Started");
				});
			  boolean flag=automationService.updateScheduler(schedulerJobs);
			}
		}catch(Exception ex) {
			LOGGER.error("Exception:{}",ex);
		}
		LOGGER.info("****Completed PostConstruct*****");
	}
	/***
	 * 
	 * findAlarmMachine method pull alarm trigger machine information from L1Pool collection to scheduler
	 * 
	 * */
	//@Scheduled(fixedDelay=5000)
	public void findAlarmMachines() {
		LOGGER.info("*****Started Finding Alarm Machines Scheduler*****");
		  final List<SchedulerJob> schedulerJobs=new ArrayList<>();
		  try{
				 List<AlarmHistory> alarmHistories=automationService.findAlarmHistoryByEndDateLessThanEqualAndTypeNotContain(new Date(),TRIGGER_TYPE_OPR);
				 Collections.sort(alarmHistories,new AlarmHistorySortByDate());
				 if(alarmHistories!=null && !alarmHistories.isEmpty()) {
					alarmHistories.forEach(e->{
							    LOGGER.info("LineNo:{} & Alarm Name:{} & Type:{}",e.getLineNo(),e.getMessage(),e.getTriggerType());
								Optional<SchedulerJob> schedulerJobOpt=automationService.findByNameAndAlarmNameAndStatusInAndProdStartDateIsNull(e.getLineNo(),e.getMessage()
											,Arrays.asList(SCHEDULER_STATUS.IN_PROGRESS.toString(),SCHEDULER_STATUS.FAILURE.toString(),SCHEDULER_STATUS.STARTED.toString()));
								LOGGER.info("Scheduler is Exist:{}",schedulerJobOpt.isPresent());
								if(!schedulerJobOpt.isPresent()){
									 List<SchedulerJob> filterObjs=schedulerJobs.stream().filter(k->k.getName().equals(e.getLineNo()) && k.getAlarmName().equals(e.getMessage())
											 && e.getStartDate().after(k.getEndDate()))
									 .collect(Collectors.toList());
									 if(filterObjs!=null && !filterObjs.isEmpty()) {
										 SchedulerJob filterObj=filterObjs.get(0);
										 filterObj.setEndDate(e.getStartDate());
										 schedulerJobs.add(filterObj);
									 }else{
									     schedulerJobs.add(preparedSchedulerJob(e));
									 }
								}else{
									/*
									 * line no + alarm combination machine already exsit with status in progress,failure & started and 
									 * it will update enddate with latest record
									 */
				 					Optional<ProductResultHistoryActive> prodResultOpt=automationService.getActiveProductByL1Name(e.getLineNo());
				 					SchedulerJob schedulerJob=schedulerJobOpt.get();
									if((!prodResultOpt.isPresent() && e.getStartDate().after(schedulerJob.getEndDate()))
											|| (prodResultOpt.isPresent() && e.getStartDate().after(schedulerJob.getEndDate()) && schedulerJob.getProdStartDate()==null)){
										schedulerJob.setEndDate(e.getStartDate());
										schedulerJobs.add(schedulerJob);
									}else{
										schedulerJobs.add(preparedSchedulerJob(e));
									}
								}
					      });
							LOGGER.info("*******Saving Data on Scheduler********");
							LOGGER.info("Scheduler Job List:{}",schedulerJobs.isEmpty());
							boolean flag=automationService.updateScheduler(schedulerJobs);
							LOGGER.info("Scheduler Information Saved:{}",flag);
							schedulerJobs.clear();
				}
		 }catch(Exception ex) {
			 LOGGER.error("Unable to Process FindAlarmMachines",ex);
		 }
		LOGGER.info("*****End Completed Finding Alarm Machines Scheduler*****");
	}
	/***
	 * 
	 * findActiveMachines method process active machine information by using productresult_historyactive & 
	 * productresult_history collections
	 * 
	 * */
	//@Scheduled(fixedDelay=10000)
	public void findActiveMachines(){
		LOGGER.info("*****Started Finding Active Machines Scheduler*****");
		try{
			List<SchedulerJob> schedulerJobs=automationService.getAllSchedulersByStatus(Arrays.asList(SCHEDULER_STATUS.IN_PROGRESS.toString(),SCHEDULER_STATUS.FAILURE.toString()));
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
						    //it will fetch great then alarm enddate with start date records on productresult history collection for given machine name
						    List<ProductResultHistory> productResultHistories=automationService.
						    		findProdHistoryWithProdcutResultAndStartDateGe(prodResultOpt.get().getName(),0l,e.getEndDate());
						    LOGGER.info("ProductResultHistory is Empty:{}",!isEmpty(productResultHistories)?productResultHistories.isEmpty():ERROR_OBJECT_EMPTY);
						    if(productResultHistories!=null && !productResultHistories.isEmpty()){
						    	Collections.sort(productResultHistories,new SortByDate());
						    	ProductResultHistory productResultHistory=productResultHistories.get(0);
						    	LOGGER.info("ProductName:{} & ProductStartDate:{}",productResultHistory.getName(),productResultHistory.getStartDate());
						    	e.setProdStartDate(productResultHistory.getStartDate());
						    	e.setSchedulerEndDate(LocalDate.now());
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
	/*
	 * Handle common functionality
	 */
	private SchedulerJob preparedSchedulerJob(AlarmHistory e) {
		 SchedulerJob schedulerJob=new SchedulerJob();
		 schedulerJob.setId(e.getId());
		 schedulerJob.setName(e.getLineNo());
		 schedulerJob.setAlarmName(e.getMessage());
		 schedulerJob.setStartDate(e.getStartDate());
		 schedulerJob.setEndDate(e.getEndDate());
		 schedulerJob.setSchedulerStartDate(LocalDate.now());
		 schedulerJob.setSchedulerEndDate(null);
		 schedulerJob.setStatus(SCHEDULER_STATUS.IN_PROGRESS.toString());
		 return schedulerJob;
	}
}