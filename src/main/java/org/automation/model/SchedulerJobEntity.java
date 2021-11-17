package org.automation.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table
@Entity(name = "SCHEDULER_JOB")
public class SchedulerJobEntity {

	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "L1_NAME")
	private String name;

	@Column(name = "RESTART_TIME")
	private String restartTime;

	@Column(name = "ACTIVE_TIME")
	private String activeTime;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "SCHEDULER_STARTDATE")
	private LocalDate startDate;

	@Column(name = "SCHEDULER_ENDDATE")
	private LocalDate endDate;
	
	@Column(name="ERROR_MSG")
	private String errorMsg;
}
