package org.automation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection="Alarm_History_Status")
public class AlarmHistoryStatus {
  @Id
  private String id;
  
  @Field(name="scheduler_flag")
  private int schedulerFlag;
  
}
