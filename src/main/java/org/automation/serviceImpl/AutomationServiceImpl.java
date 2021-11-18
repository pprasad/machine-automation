package org.automation.serviceImpl;

import static org.automation.util.AutomationConstant.TRIGGER_TYPE_ALARM;
import static org.automation.util.AutomationConstant.TRIGGER_TYPE_OPERATE;
import static org.automation.util.AutomationConstant.isEmpty;
import static org.automation.util.AutomationConstant.ERROR_OBJECT_EMPTY;
import static org.automation.util.AutomationConstant.JOB_SCHEDULER_TIME;
import static org.automation.util.AutomationConstant.JOB_SCHDEULER_ID;
import static org.automation.util.AutomationConstant.startDate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.automation.dao.AlarmHistoryRepository;
import org.automation.dao.L1PoolRepository;
import org.automation.dao.ProductResultHistoryActiveRepository;
import org.automation.dao.ProductResultHistoryRepository;
import org.automation.dao.SchedulerConfigRepository;
import org.automation.dao.SchedulerJobRepository;
import org.automation.model.AlarmHistory;
import org.automation.model.L1PoolEntity;
import org.automation.model.ProductResultHistory;
import org.automation.model.ProductResultHistoryActive;
import org.automation.model.SchedulerConfig;
import org.automation.model.SchedulerJob;
import org.automation.service.AutomationService;
import org.automation.util.AutomationConstant.SCHEDULER_STATUS;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/*
 * @Auth prasad
 * @Date 16,Nov2021
 */
@Service
public class AutomationServiceImpl implements AutomationService{

	private static final Logger LOGGER=LoggerFactory.getLogger(AutomationServiceImpl.class);
	
	@Autowired
	private L1PoolRepository poolRepository;
	
	@Autowired
	private SchedulerJobRepository schedulerJobRepo;
	
	@Autowired
	private ProductResultHistoryActiveRepository prodResultHistActRepo;
	
	@Autowired
	private ProductResultHistoryRepository prodResultHistoryRepo;
	
	@Autowired
	private AlarmHistoryRepository alarmHistoryRepo;
	
	@Autowired
	private SchedulerConfigRepository schedulerConfigRepo;
	
	@PostConstruct
	public void intit() {
		try{
			Optional<SchedulerConfig> schedulerConfigs=schedulerConfigRepo.findById(JOB_SCHDEULER_ID);
			if(!schedulerConfigs.isPresent()) {
				 SchedulerConfig schedulerConfig=new SchedulerConfig();
				 schedulerConfig.setId(JOB_SCHDEULER_ID);
				 schedulerConfig.setJobDay(1);
				 schedulerConfig.setJobType(JOB_SCHEDULER_TIME.M.toString());
				 schedulerConfigRepo.save(schedulerConfig);
			}
		}catch(Exception ex) {
			LOGGER.error("Unable to insert Scheduler Config:{}",ex);
		}
	}
	@Override
	public void findAllAlarmMachines() {
		try {
			List<L1PoolEntity> alarmPools=poolRepository.groupBy();
			if(alarmPools!=null && !alarmPools.isEmpty()){
				alarmPools.forEach(e->{
					LOGGER.info("L1Name:{}",e.getId());
					LOGGER.info("EndDate:{}",e.getMaxDate());
					Optional<L1PoolEntity> filterData=poolRepository.findByNameAndEndDateAndValueIn(e.getId(),e.getMaxDate(),Arrays.asList(TRIGGER_TYPE_OPERATE,TRIGGER_TYPE_ALARM));
					if(filterData.isPresent()){
						LOGGER.info("**Found Alarm Trigger Machine**");
						L1PoolEntity poolEntity=filterData.get();
						LOGGER.info("L1Name:{}",poolEntity.getName());
						LOGGER.info("TriggerType:{}",poolEntity.getValue());
						Optional<SchedulerJob> schedulerJobOpt=schedulerJobRepo.findById(poolEntity.getId());
						/*it will insert machine name in scheduler table for future scheduler identify either machine is active or non-active 
						if machine not exist*/
					    if(!schedulerJobOpt.isPresent()){
					    	 SchedulerJob schedulerJob=new SchedulerJob();
					    	 schedulerJob.setId(poolEntity.getId());
					    	 schedulerJob.setName(poolEntity.getName());
					    	 schedulerJob.setSchedulerStartDate(LocalDate.now());
					    	 schedulerJob.setStatus(SCHEDULER_STATUS.IN_PROGRESS.toString());
					    	 schedulerJobRepo.save(schedulerJob);
					    }
					}
				});
			}
		}catch(Exception ex) {
			LOGGER.error("Exception Occer under finding Alarm Machines",ex);
		}
	}

	@Override
	public List<SchedulerJob> getAllSchedulersByStatus(String status) {
		return schedulerJobRepo.findByStatus(status);
	}

	@Override
	public boolean updateScheduler(List<SchedulerJob> schedulerJobs) {
		boolean flag=false;
		LOGGER.info("*****Calling updateScheduler********");
		try {
			LOGGER.info("schedulerJobs Is Emtpy:{}",!isEmpty(schedulerJobs)?schedulerJobs.isEmpty():ERROR_OBJECT_EMPTY);
			if(schedulerJobs!=null && !schedulerJobs.isEmpty()) {
			  schedulerJobRepo.saveAll(schedulerJobs);
			  flag=true;
			}
		}catch(Exception ex) {
			LOGGER.error("Exception Occer while saving updatescheduler records",ex);
		}
		LOGGER.info("*****Completed updateScheduler********");
		return flag;
	}

	@Override
	public Optional<ProductResultHistoryActive> getActiveProductByL1Name(String name) {
		return prodResultHistActRepo.findByName(name);
	}

	@Override
	public List<ProductResultHistory> getProductHistroyByNameAndProductResult(String name, Long prodResult) {
		return prodResultHistoryRepo.findWithProdcutResult(name,prodResult);
	}

	@Override
	public List<SchedulerJob> getAllSchedulersByStatus(List<String> status) {
		return schedulerJobRepo.findByStatusIn(status);
	}

	@Override
	public List<SchedulerJob> findAllSchedulers() {
		return schedulerJobRepo.findAll();
	}

	@Override
	public List<AlarmHistory> findAlarmHistoryByEndDateLessThanEqualAndTypeNotContain(Date date, String type) {
		Date startDate=null;
		Optional<SchedulerConfig> schedulerConfigs=schedulerConfigRepo.findById(JOB_SCHDEULER_ID);
		if(schedulerConfigs.isPresent()) {
			startDate=startDate(schedulerConfigs.get());
			LOGGER.info("StartDate:{}",new SimpleDateFormat("dd-MM-yyyy").format(startDate));
			LOGGER.info("EndDate:{}",new SimpleDateFormat("dd-MM-yyyy").format(date));
			return alarmHistoryRepo.findByEndDateBetweenAndTypeNotContain(startDate,date,type);
		}else {
			return alarmHistoryRepo.findByEndDateLessThanEqualAndTypeNotContain(date,type);
		}
	}

	@Override
	public Optional<SchedulerJob> getSchedulerById(String id) {
		 return schedulerJobRepo.findById(id);
	}
	@Override
	public List<ProductResultHistory> findProdHistoryWithProdcutResultAndStartDateGe(String name, Long prodResult,
			Date startDate) {
		return prodResultHistoryRepo.findWithProdcutResultAndStartDateGe(name,prodResult,startDate);
	}
}