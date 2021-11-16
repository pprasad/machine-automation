package org.automation.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
	
	private String name;
	
	private Long restartTime;
	
	private Long activeTime;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private String status;
	
}
