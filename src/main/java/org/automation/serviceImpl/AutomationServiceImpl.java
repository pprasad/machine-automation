package org.automation.serviceImpl;

import static org.automation.util.AutomationConstant.TRIGGER_TYPE_ALARM;
import static org.automation.util.AutomationConstant.TRIGGER_TYPE_OPERATE;
import static org.automation.util.AutomationConstant.SCHEDULER_STATUS;
import static org.automation.util.AutomationConstant.converTime;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.automation.dao.L1PoolRepository;
import org.automation.dao.ProductResultHistoryActiveRepository;
import org.automation.dao.ProductResultHistoryRepository;
import org.automation.dao.SchedulerJobRepository;
import org.automation.model.L1PoolEntity;
import org.automation.model.ProductResultHistory;
import org.automation.model.ProductResultHistoryActive;
import org.automation.model.SchedulerJob;
import org.automation.service.AutomationService;
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
					    	 Long timeDiff=poolEntity.getEndDate().getTime()-poolEntity.getStartDate().getTime();
					    	 schedulerJob.setRestartTime(converTime(timeDiff));
					    	 schedulerJob.setStartDate(LocalDate.now());
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
		try {
			if(schedulerJobs!=null && !schedulerJobs.isEmpty()) {
			  schedulerJobRepo.saveAll(schedulerJobs);
			  flag=true;
			}
		}catch(Exception ex) {
			LOGGER.error("Exception Occer while saving updatescheduler records",ex);
		}
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
}