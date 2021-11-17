package org.automation.controller;

import java.util.List;

import org.automation.dto.ResponseDto;
import org.automation.model.SchedulerJobEntity;
import org.automation.service.AutomationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.automation.util.AutomationConstant.SCHEDULER_STATUS;

@RestController
public class AutomationRestController {
	
	@Autowired
	private AutomationService automationService;
   
	@GetMapping(path={"/getAllSchedulers"},produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> getAllSchedulers(){
		ResponseEntity<ResponseDto<List<SchedulerJobEntity>>> response=null;
		List<SchedulerJobEntity> schedulerJobs=automationService.findAllSchedulers();
		ResponseDto<List<SchedulerJobEntity>> responseDto=new ResponseDto<>();
		responseDto.setStatus(SCHEDULER_STATUS.SUCCESS.toString());
		responseDto.setResponse(schedulerJobs);
		response=new ResponseEntity<ResponseDto<List<SchedulerJobEntity>>>(responseDto,HttpStatus.OK);
		return response;
	}
}
