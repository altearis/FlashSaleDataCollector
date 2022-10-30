package com.ibp.FlashSaleDataCollector.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse {
	private String respCode;
	private String respMessage; 
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;
	private Object data;
	private List<Object> dataSet;
	private Map<String,String> responseData;
	private List<Object> ErrorMessage;
	private List<Object> list;

	public APIResponse(String responseCode, String responseMessage) {
		super();
		this.timestamp = LocalDateTime.now();
		this.respCode = responseCode;
		this.respMessage = responseMessage;
	}
	public APIResponse(List<Object> ErrorMessage,String responseCode, String responseMessage) {
		super();
		this.timestamp = LocalDateTime.now();
		this.respCode = responseCode;
		this.respMessage = responseMessage;
		this.ErrorMessage = ErrorMessage;
	}
	public APIResponse(String responseCode, String responseMessage,Object data) {
		super();
		this.timestamp = LocalDateTime.now();
		this.respCode = responseCode;
		this.respMessage = responseMessage;
		this.data = data;
	}
	public APIResponse(String responseCode, String responseMessage,List<Object> dataSet) {
		super();
		this.timestamp = LocalDateTime.now();
		this.respCode = responseCode;
		this.respMessage = responseMessage;
		this.dataSet = dataSet;
	}
	public APIResponse(String responseCode, String responseMessage,Map<String, String> returnSet) {
		super();
		this.timestamp = LocalDateTime.now();
		this.respCode = responseCode;
		this.respMessage = responseMessage;
		this.responseData = returnSet;
	}
	
}

