package org.automation.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Data;

@Data
@Document(collection="ProductResult_History")
public class ProductResultHistory{
   @Id
   private String id;
   
   @Field(name="L1Name")
   private String name;
   
   @Field(name="ProductName")
   private String productName;
   
   @Field(name="updatedate")
   private Date startDate;
   
   @Field(name="enddate")
   private Date endDate;
   
   @Field(name="productresult",targetType=FieldType.INT64)
   private Long productResult;
   
   @Field(name="productresult_accumulate")
   private String prodResultAccumulate;
}
