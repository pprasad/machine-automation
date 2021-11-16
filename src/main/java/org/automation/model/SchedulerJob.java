package org.automation.model;

import java.time.LocalDate;

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
	
	@Field(name="restart_time")
	private String restartTime;
	
	@Field(name="active_time")
	private String activeTime;
	
	@Field(name="scheduler_startdate")
	private LocalDate startDate;
	
	@Field(name="scheduler_enddate")
	private LocalDate endDate;
	
	private String status;
	
}
