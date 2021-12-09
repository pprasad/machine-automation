package org.automation.controller;

import java.util.List;

import org.automation.dto.ResponseDto;
import org.automation.model.SchedulerJob;
import org.automation.service.AutomationService;
import org.automation.util.AutomationConstant.SCHEDULER_STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutomationRestController {
	
	@Autowired
	private AutomationService automationService;
   
	@GetMapping(path={"/getAllSchedulers"},produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getAllSchedulers(){
		ResponseEntity<ResponseDto<List<SchedulerJob>>> response=null;
		List<SchedulerJob> schedulerJobs=automationService.findAllSchedulers();
		ResponseDto<List<SchedulerJob>> responseDto=new ResponseDto<>();
		responseDto.setStatus(SCHEDULER_STATUS.SUCCESS.toString());
		responseDto.setResponse(schedulerJobs);
		response=new ResponseEntity<ResponseDto<List<SchedulerJob>>>(responseDto,HttpStatus.OK);
		return response;
	}
}
