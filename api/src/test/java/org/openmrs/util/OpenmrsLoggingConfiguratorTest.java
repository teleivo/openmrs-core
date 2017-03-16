package org.openmrs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link OpenmrsLoggingConfigurator}.
 */
public class OpenmrsLoggingConfiguratorTest {
	
	private Logger defaultClassLogger = LogManager.getLogger(OpenmrsConstants.LOG_CLASS_DEFAULT);
	
	@Before
	public void setUp() {
		Configurator.setLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, Level.WARN);
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldApplyTheGivenLogLevelToTheLoggerOfTheGivenLoggerName() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, "info");
		
		assertThat(defaultClassLogger.getLevel(), is(Level.INFO));
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldApplyTheGivenLogLevelToTheDefaultLogClassIfGivenLoggerNameIsNull() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(null, "info");
		
		assertThat(defaultClassLogger.getLevel(), is(Level.INFO));
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldApplyTheGivenLogLevelToTheDefaultLogClassIfGivenLoggerNameIsBlank() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(" ", "info");
		
		assertThat(defaultClassLogger.getLevel(), is(Level.INFO));
	}
}
