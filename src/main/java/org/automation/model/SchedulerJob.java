package org.automation.model;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
/*
 * @Auth prasad
 * @Date 16,Nov2021
 */
@Data
@Document(collection="scheduler_job")
public class SchedulerJob {
  
	@Id
	private String id;
	
	@Field(name="L1Name")
	private String name;
	
	@Field(name="alarm_name")
	private String alarmName;
	
	@Field(name="start_time")
	private String startTime;
	
	@Field(name="end_time")
	private String endTime;
	
	@Field(name="restart_time")
	private String restartTime;
	
	@Field(name="prod_start_time")
	private String prodStartTime;
	
	@Field(name="start_date")
	private Date startDate;
	
	@Field(name="end_date")
	private Date endDate;
	
	@Field(name="scheduler_startdate")
	private LocalDate schedulerStartDate;
	
	@Field(name="scheduler_enddate")
	private LocalDate schedulerEndDate;
	
	@Field(name="status")
	private String status;
	
	@Field(name="ERROR_MSG")
	private String errorMsg;
	
}