package org.automation.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Document(collection="L1_Pool")
public class L1PoolEntity {
	
   @Id	
   private String id;	
	
   @Field(name="L1Name")
   private String name;
   
   @Field(name="updatedate")
   private Date startDate;
   
   @Field(name="enddate")
   private Date endDate;
   
   private String value;
   
   private Date maxDate;
}
