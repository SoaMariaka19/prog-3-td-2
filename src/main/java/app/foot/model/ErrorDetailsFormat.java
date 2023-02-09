package com.hei.prog3td2.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorDetailsFormat {
	private Date timestamp;
	private String message;
	private String details;
}
