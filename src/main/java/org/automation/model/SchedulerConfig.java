package org.automation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection="scheduler_config")
public class SchedulerConfig {
   @Id
   private String id;
   
   @Field(name="job_day")
   private int jobDay;
   
   @Field(name="job_type")
   private String jobType;
}
