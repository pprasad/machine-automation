package org.automation.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResponseDto<T> implements Serializable {
   public String status;
   public String errorMsg;
   public T response;
}
