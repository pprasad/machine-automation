package org.automation.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection="ProductResult_History_Active")
public class ProductResultHistoryActive {
   
	@Id
	private String id;
	
	@Field(name="L1Name")
	private String name;
	
	@Field(name="productname")
	private String productName;
	
	@Field(name="updatedate")
	private Date startDate;
		
}
