package org.automation.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection="Alarm_History")
public class AlarmHistory {
  @Id
  private String id;
  
  @Field(name="L1Name")
  private String lineNo;
  
  @Field(name="L0Name")
  private String alarmName;
  
  @Field(name="updatedate") 
  private Date startDate;
  
  @Field(name="enddate")
  private Date endDate;
  
  private String message;
  
  private int level;
  
  @Field(name="type")
  private String triggerType;
  
}
