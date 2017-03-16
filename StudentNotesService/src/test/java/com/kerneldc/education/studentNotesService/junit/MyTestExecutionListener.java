package com.kerneldc.education.studentNotesService.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class MyTestExecutionListener extends AbstractTestExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		LOGGER.debug("Before test: {}", testContext.getTestMethod().getName());
	}
	
	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		LOGGER.debug("After test: {}", testContext.getTestMethod().getName());
	}
}
