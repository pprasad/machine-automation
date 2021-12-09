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
import org.automation.dao.AlarmHistoryStatusRepository;
import org.automation.dao.L1PoolRepository;
import org.automation.dao.ProductResultHistoryActiveRepository;
import org.automation.dao.ProductResultHistoryRepository;
import org.automation.dao.SchedulerConfigRepository;
import org.automation.dao.SchedulerJobRepository;
import org.automation.model.AlarmHistory;
import org.automation.model.AlarmHistoryStatus;
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
	
	@Autowired
	private AlarmHistoryStatusRepository alarmHistStatusRepo;
	
	/*
	 *@desc it will insert default schedulerconfiguration settings if config not available on scheduler_config collection
	 */
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
			}else{
				 SchedulerConfig schedulerConfig=schedulerConfigs.get();
				 schedulerConfig.setJobDay(1);
				 schedulerConfig.setJobType(JOB_SCHEDULER_TIME.D.toString());
				 schedulerConfigRepo.save(schedulerConfig);
			}
		}catch(Exception ex) {
			LOGGER.error("Unable to insert Scheduler Config:{}",ex);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.automation.service.AutomationService#findAllAlarmMachines()
	 * @desc it will pull alarm status records from L1Pool Collection
	 */
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
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#getAllSchedulersByStatus(java.lang.String)
     * @param status
     */
	@Override
	public List<SchedulerJob> getAllSchedulersByStatus(String status) {
		return schedulerJobRepo.findByStatus(status);
	}
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#updateScheduler(java.util.List)
     * @param schuedulerList
     * @desc it will SaveOrUpdate scheduler information on scheduler_job
     */
	
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
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#getActiveProductByL1Name(java.lang.String)
     * @prama name
     */
	@Override
	public Optional<ProductResultHistoryActive> getActiveProductByL1Name(String name) {
		return prodResultHistActRepo.findByName(name);
	}
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#getProductHistroyByNameAndProductResult(java.lang.String, java.lang.Long)
     * @param name
     * @param ProdResult
     */
	@Override
	public List<ProductResultHistory> getProductHistroyByNameAndProductResult(String name, Long prodResult) {
		return prodResultHistoryRepo.findWithProdcutResult(name,prodResult);
	}
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#getAllSchedulersByStatus(java.util.List)
     * @param status(Inprogress/Started/Success/Failure)
     */
	@Override
	public List<SchedulerJob> getAllSchedulersByStatus(List<String> status) {
		return schedulerJobRepo.findByStatusIn(status);
	}
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#findAllSchedulers()
     */
	@Override
	public List<SchedulerJob> findAllSchedulers() {
		return schedulerJobRepo.findAll();
	}
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#findAlarmHistoryByEndDateLessThanEqualAndTypeNotContain(java.util.Date, java.lang.String)
     * @param date
     * @param type 
     */
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
    /*
     * (non-Javadoc)
     * @see org.automation.service.AutomationService#getSchedulerById(java.lang.String)
     * @param id
     */
	@Override
	public Optional<SchedulerJob> getSchedulerById(String id) {
		 return schedulerJobRepo.findById(id);
	}
	/*
	 * (non-Javadoc)
	 * @see org.automation.service.AutomationService#findProdHistoryWithProdcutResultAndStartDateGe(java.lang.String, java.lang.Long, java.util.Date)
	 * @param name Machine Name
	 * @param ProdResult is great then zero
	 * @param startDate Alarm enddate as a paramenter
	 */
	@Override
	public List<ProductResultHistory> findProdHistoryWithProdcutResultAndStartDateGe(String name, Long prodResult,
			Date startDate) {
		return prodResultHistoryRepo.findWithProdcutResultAndStartDateGe(name,prodResult,startDate);
	}
	/*
	 * (non-Javadoc)
	 * @see org.automation.service.AutomationService#findByNameAndAlarmName(java.lang.String, java.lang.String)
	 * @param name machine lineno or machine name
	 * @param alarmName machine alarmName
	 */
	@Override
	public Optional<SchedulerJob> findByNameAndAlarmNameAndStatusIn(String name,String alarmName,List<String> status){
		return schedulerJobRepo.findByNameAndAlarmNameAndStatusIn(name,alarmName,status);
	}
	/*
	 * (non-Javadoc)
	 * @see org.automation.service.AutomationService#findByNameAndAlarmNameAndStatusInAndProdStartDateIsNull(java.lang.String, java.lang.String, java.util.List)
	 * @param name L1name
	 * @param alarmname 
	 * @status INprogress,Started,Failure & Success
	 */
	@Override
	public Optional<SchedulerJob> findByNameAndAlarmNameAndStatusInAndProdStartDateIsNull(String name, String alarmName,
			List<String> status) {
		return schedulerJobRepo.findByNameAndAlarmNameAndStatusInAndProdStartDateIsNull(name,alarmName,status);
	}
	/*
	 * (non-Javadoc)
	 * @see org.automation.service.AutomationService#findByNameAndAlarmNameWithStatusAndEndDateProductStartDateIsNull(java.lang.String, java.lang.String, java.util.Date, java.util.List)
	 * @param name L1Name
	 * @param alarmname
	 * @param date EndDate
	 * @status INprogress,Started,Failure & Success
	 */
	@Override
	public Optional<SchedulerJob> findByNameAndAlarmNameWithStatusAndEndDateProductStartDateIsNull(String name,
			String alarmName, Date date, List<String> status) {
		Date startDate=(Date)date.clone();
		startDate.setHours(date.getHours()-5);
		LOGGER.info("Start Date:{} & End Date:{}",startDate,date);
		return schedulerJobRepo.findByNameAndAlarmNameWithStatusAndEndDateProductStartDateIsNull(name, alarmName,startDate,date,status);
	}
	@Override
	public SchedulerJob saveOrUpdateScheduler(SchedulerJob schedulerJob) {
		LOGGER.info("*******SaveOrUpdateScheduler********");
		return schedulerJobRepo.save(schedulerJob);
	}
	@Override
	public Optional<AlarmHistoryStatus> findById(String id) {
		return alarmHistStatusRepo.findById(id);
	}
	@Override
	public AlarmHistoryStatus saveOrUpdateAlarmHistoryStatus(AlarmHistoryStatus alarmHistoryStatus) {
		return alarmHistStatusRepo.save(alarmHistoryStatus);
	}
}