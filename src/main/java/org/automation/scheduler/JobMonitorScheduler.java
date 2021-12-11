package org.automation.scheduler;
import static org.automation.util.AutomationConstant.ERROR_OBJECT_EMPTY;
import static org.automation.util.AutomationConstant.ERROR_RECORD_NOTFOUND;
import static org.automation.util.AutomationConstant.ERROR_WEAKUP_STATUS;
import static org.automation.util.AutomationConstant.TRIGGER_TYPE_OPR;
import static org.automation.util.AutomationConstant.converTime;
import static org.automation.util.AutomationConstant.convertDateTotime;
import static org.automation.util.AutomationConstant.isEmpty;
import static org.automation.util.AutomationConstant.distinctByKey;
import static org.automation.util.AutomationConstant.SchedulerSortByDate;

import java.time.LocalDate;
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
	/*
	 * @desc it will update failure status if records in started status after re-start scheduler 
	 */
	@PostConstruct
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
	@Scheduled(fixedDelay=5000)
	public void findAlarmMachines() {
		LOGGER.info("*****Started Finding Alarm Machines Scheduler*****");
		 final List<SchedulerJob> schedulerJobs=new ArrayList<>();
		 List<AlarmHistory> alarmHistories=automationService.findAlarmHistoryByEndDateLessThanEqualAndTypeNotContain(new Date(),TRIGGER_TYPE_OPR);
		 if(alarmHistories!=null && !alarmHistories.isEmpty()) {
			alarmHistories.forEach(e->{
				LOGGER.info("LineNo:{} & Alarm Name:{} & Type:{}",e.getLineNo(),e.getAlarmName(),e.getTriggerType());
				Optional<SchedulerJob> schedulerJobOpt=automationService.getSchedulerById(e.getId());
				if(!schedulerJobOpt.isPresent()) {
					SchedulerJob schedulerJob=new SchedulerJob();
					schedulerJob.setId(e.getId());
					schedulerJob.setName(e.getLineNo());
					schedulerJob.setAlarmName(e.getMessage());
					schedulerJob.setStartDate(e.getStartDate());
					schedulerJob.setEndDate(e.getEndDate());
					schedulerJob.setSchedulerStartDate(LocalDate.now());
					schedulerJob.setSchedulerEndDate(null);
					schedulerJob.setStatus(SCHEDULER_STATUS.IN_PROGRESS.toString());
					schedulerJobs.add(schedulerJob);
				}
			});
			LOGGER.info("*******Saving Data on Scheduler********");
			LOGGER.info("Scheduler Job List:{}",schedulerJobs.isEmpty());
			boolean flag=automationService.updateScheduler(schedulerJobs);
			LOGGER.info("Scheduler Information Saved:{}",flag);
			schedulerJobs.clear();
		}
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
	 * this scheduler update duplicate records with end date as a ProdStartDate
	 */
	@Scheduled(fixedDelay=10000)
	public void duplicateScheduler(){
		try{
			List<SchedulerJob> schedulerJobs=automationService.getAllSchedulersByStatus(Arrays.asList(SCHEDULER_STATUS.SUCCESS.toString()));
			if(schedulerJobs!=null && !schedulerJobs.isEmpty()){
				List<SchedulerJob> filterSchedulers=schedulerJobs.stream().filter(distinctByKey(e->e.getProdStartDate()))
						.collect(Collectors.toList());
				LOGGER.info("Filter scheduler:{}",filterSchedulers.isEmpty());
				if(filterSchedulers!=null && !filterSchedulers.isEmpty()) {
					 filterSchedulers.forEach(e->{
					  if(e.getName().equalsIgnoreCase("SFL1-TUR1") && e.getAlarmName().equalsIgnoreCase("ROW2 INDIVIDUALIZER1 ACTUATION")){
						 LOGGER.info("Machine :{} && Alarm:{} && ProdStartDate:{}",e.getName(),e.getAlarmName(),e.getProdStartDate());
						 List<SchedulerJob> existSchedulerJobs=automationService.
								 findByNameAndAlarmNameAndProdStartDateAndStatus(e.getName(),e.getAlarmName(),e.getProdStartDate(),SCHEDULER_STATUS.SUCCESS.toString());
					     LOGGER.info("Duplicate Schedulers is Empty:{} && Size:{}",existSchedulerJobs.isEmpty(),existSchedulerJobs.size());
						 if(existSchedulerJobs!=null && !existSchedulerJobs.isEmpty() && existSchedulerJobs.size()>1) {
					    	 Collections.sort(existSchedulerJobs,new SchedulerSortByDate());
					    	 existSchedulerJobs.subList(0,existSchedulerJobs.size()-1).forEach(k->{
					    		 k.setProdStartDate(k.getEndDate());
					    		 automationService.updateScheduler(Arrays.asList(k));
					    	 });
					     }
					 }});
				}
			}
		}catch(Exception ex){
			LOGGER.error("Unable to process Duplicat records:{}",ex);
		}
	}
}