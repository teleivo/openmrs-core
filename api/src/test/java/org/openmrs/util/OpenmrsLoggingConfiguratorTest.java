package org.openmrs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the {@link OpenmrsLoggingConfigurator}.
 */
public class OpenmrsLoggingConfiguratorTest {
	
	@Rule
	public LoggerContextRule init = new LoggerContextRule("org/openmrs/util/OpenmrsLoggingConfiguratorTest.xml");
	
	private Logger defaultClassLogger;
	
	private Level initialDefaultClassLoggerLevel;
	
	@Before
	public void setUp() {
		defaultClassLogger = LogManager.getLogger(OpenmrsConstants.LOG_CLASS_DEFAULT);
		initialDefaultClassLoggerLevel = defaultClassLogger.getLevel();
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldApplyTheGivenLogLevelToTheLoggerOfTheGivenLoggerName() throws Exception {
		
		String loggerName = "org.openmrs.module.reporting";
		
		OpenmrsLoggingConfigurator.applyLogLevel(loggerName, "info");
		
		assertThat(LogManager.getLogger(loggerName).getLevel(), is(Level.INFO));
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
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	//	@Test
	public void applyLogLevel_shouldNotChangeTheLogLevelAndLogAWarningIfGivenLevelIsNull() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, null);
		
		assertThat(defaultClassLogger.getLevel(), is(initialDefaultClassLoggerLevel));
		
		assertThatOneWarningWasLoggedWithMessage(
		    "Trying set invalid log level 'null'. Valid values are trace, debug, info, warn, error or fatal");
	}
	
	private void assertThatOneWarningWasLoggedWithMessage(String message) {
		final ListAppender appender = init.getListAppender("LIST");
		final List<LogEvent> logEvents = appender.getEvents();
		assertThat(logEvents.size(), is(1));
		assertThat(logEvents.get(0).getLevel(), is(Level.WARN));
		assertThat(logEvents.get(0).getLoggerName(), is("org.openmrs.util.OpenmrsLoggingConfigurator"));
		assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is(message));
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldNotChangeTheLogLevelAndLogAWarningIfGivenLevelIsBlank() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, " ");
		
		assertThat(defaultClassLogger.getLevel(), is(initialDefaultClassLoggerLevel));
		
		assertThatOneWarningWasLoggedWithMessage(
		    "Trying set invalid log level ' '. Valid values are trace, debug, info, warn, error or fatal");
	}
}
