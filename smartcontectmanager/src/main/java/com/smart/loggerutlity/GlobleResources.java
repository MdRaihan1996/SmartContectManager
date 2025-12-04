package com.smart.loggerutlity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobleResources {

	public static Logger getLogger(Class className) {
		
		return LoggerFactory.getLogger(className);
	}
}
