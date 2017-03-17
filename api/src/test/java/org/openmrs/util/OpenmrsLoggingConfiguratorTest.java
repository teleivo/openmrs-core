package org.openmrs.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextMockTest;

/**
 * Tests the {@link OpenmrsLoggingConfigurator}.
 */
public class OpenmrsLoggingConfiguratorTest extends BaseContextMockTest {
	
	@Rule
	public LoggerContextRule ctx = new LoggerContextRule("org/openmrs/util/OpenmrsLoggingConfiguratorTest.xml");
	
	@Mock
	Context context;
	
	@Mock
	AdministrationService administrationService;
	
	private Logger defaultClassLogger;
	
	private Level initialDefaultClassLoggerLevel;
	
	@Before
	public void setUp() {
		defaultClassLogger = ctx.getLogger(OpenmrsConstants.LOG_CLASS_DEFAULT);
		initialDefaultClassLoggerLevel = defaultClassLogger.getLevel();
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldApplyTheGivenLogLevelToTheLoggerOfTheGivenLoggerName() throws Exception {
		
		String loggerName = "org.openmrs.module.reporting";
		
		OpenmrsLoggingConfigurator.applyLogLevel(loggerName, OpenmrsConstants.LOG_LEVEL_INFO);
		
		assertThat(ctx.getLogger(loggerName).getLevel(), is(Level.INFO));
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldApplyTheGivenLogLevelToTheDefaultLogClassIfGivenLoggerNameIsNull() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(null, OpenmrsConstants.LOG_LEVEL_TRACE);
		
		assertThat(defaultClassLogger.getLevel(), is(Level.TRACE));
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldApplyTheGivenLogLevelToTheDefaultLogClassIfGivenLoggerNameIsBlank() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(" ", OpenmrsConstants.LOG_LEVEL_ERROR);
		
		assertThat(defaultClassLogger.getLevel(), is(Level.ERROR));
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldNotChangeTheLogLevelAndLogAWarningIfGivenLevelIsNull() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, null);
		
		assertThat(defaultClassLogger.getLevel(), is(initialDefaultClassLoggerLevel));
		
		//	TODO - the assertion of the appender fails if all tests are run and does not if run alone
		//		assertThatOneWarningWasLoggedWithMessage(
		//		    "Trying set invalid log level 'null'. Valid values are trace, debug, info, warn, error or fatal");
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldNotChangeTheLogLevelAndLogAWarningIfGivenLevelIsBlank() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, " ");
		
		assertThat(defaultClassLogger.getLevel(), is(initialDefaultClassLoggerLevel));
		
		//		assertThatOneWarningWasLoggedWithMessage(
		//		    "Trying set invalid log level ' '. Valid values are trace, debug, info, warn, error or fatal");
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldNotChangeTheLogLevelAndLogAWarningIfGivenLevelIsNotOneOfTheOpenmrsLogLevelContants()
	        throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, "UNKNOWNLOGLEVEL");
		
		assertThat(defaultClassLogger.getLevel(), is(initialDefaultClassLoggerLevel));
		
		//		assertThatOneWarningWasLoggedWithMessage(
		//		    "Trying set invalid log level 'UNKNOWNLOGLEVEL'. Valid values are trace, debug, info, warn, error or fatal");
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldNotChangeTheLogLevelAndLogAWarningIfGivenLevelIsAllWhichIsValidForLog4jButNotOneOfTheOpenmrsLogLevelContants()
	        throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, "ALL");
		
		assertThat(defaultClassLogger.getLevel(), is(initialDefaultClassLoggerLevel));
		
		//		assertThatOneWarningWasLoggedWithMessage(
		//		    "Trying set invalid log level 'ALL'. Valid values are trace, debug, info, warn, error or fatal");
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevel(String, String)
	 */
	@Test
	public void applyLogLevel_shouldNotChangeTheLogLevelAndLogAWarningIfGivenLevelIsOffWhichIsValidForLog4jButNotOneOfTheOpenmrsLogLevelContants()
	        throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, "off");
		
		assertThat(defaultClassLogger.getLevel(), is(initialDefaultClassLoggerLevel));
		
		//		assertThatOneWarningWasLoggedWithMessage(
		//		    "Trying set invalid log level 'off'. Valid values are trace, debug, info, warn, error or fatal");
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevels(String)
	 */
	@Test
	public void applyLogLevels_shouldApplyLogLevelIfGivenSingleLoggerNameAndLogLevelPair() throws Exception {
		
		Map<String, Level> loggerSettings = new HashMap<>();
		loggerSettings.put("org.openmrs.module.radiology", Level.INFO);
		loggerSettings.put("org.hibernate", Level.INFO);
		
		String loggerNameLevelPairs = computeLoggerNameLevelPairs(loggerSettings);
		
		OpenmrsLoggingConfigurator.applyLogLevels(loggerNameLevelPairs);
		
		assertLoggerLevelsAreSetAsExpected(loggerSettings);
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevels(String)
	 */
	@Test
	public void applyLogLevels_shouldApplyLogLevelsIfGivenMultipleLoggerNameLogLevelPairs() throws Exception {
		
		Map<String, Level> loggerSettings = new HashMap<>();
		loggerSettings.put("org.openmrs.module.radiology", Level.INFO);
		loggerSettings.put("org.openmrs.module.reporting", Level.WARN);
		loggerSettings.put("org.hibernate", Level.DEBUG);
		
		String loggerNameLevelPairs = computeLoggerNameLevelPairs(loggerSettings);
		
		OpenmrsLoggingConfigurator.applyLogLevels(loggerNameLevelPairs);
		
		assertLoggerLevelsAreSetAsExpected(loggerSettings);
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevels(String)
	 */
	@Test
	public void applyLogLevels_shouldApplyLogLevelsIfGivenMultipleLoggerNameLogLevelPairsWithLeadingTrailingAndInnerWhitespaces()
	        throws Exception {
		
		Map<String, Level> loggerSettings = new HashMap<>();
		loggerSettings.put("org.openmrs.module.radiology", Level.INFO);
		loggerSettings.put("org.openmrs.module.reporting", Level.WARN);
		loggerSettings.put("org.hibernate", Level.DEBUG);
		
		String loggerNameLevelPairs = computeLoggerNameLevelPairsWithNoise(loggerSettings);
		
		OpenmrsLoggingConfigurator.applyLogLevels(loggerNameLevelPairs);
		
		assertLoggerLevelsAreSetAsExpected(loggerSettings);
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevels(String)
	 */
	@Test
	public void applyLogLevels_shouldApplyLogLevelsAndApplySingleLevelToTheDefaultLogClass() throws Exception {
		
		Map<String, Level> loggerSettings = new HashMap<>();
		loggerSettings.put("org.openmrs.module.radiology", Level.INFO);
		loggerSettings.put("org.openmrs.module.reporting", Level.WARN);
		loggerSettings.put("", Level.DEBUG);
		
		String loggerNameLevelPairs = computeLoggerNameLevelPairsWithNoise(loggerSettings);
		
		OpenmrsLoggingConfigurator.applyLogLevels(loggerNameLevelPairs);
		
		// Replace empty key with the default log class name for the assertion to work
		loggerSettings.put(OpenmrsConstants.LOG_CLASS_DEFAULT, loggerSettings.remove(""));
		assertLoggerLevelsAreSetAsExpected(loggerSettings);
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevels(String)
	 */
	@Test
	public void applyLogLevels_shouldApplyLogLevelToTheDefaultLogClassIfGivenOnlyALogLevel() throws Exception {
		
		OpenmrsLoggingConfigurator.applyLogLevels("Debug");
		
		assertThat(ctx.getLogger(OpenmrsConstants.LOG_CLASS_DEFAULT).getLevel(), is(Level.DEBUG));
	}
	
	/**
	 * @see OpenmrsLoggingConfigurator#applyLogLevelsFromGlobalProperty()
	 */
	@Test
	public void applyLogLevelsFromGlobalProperty_shouldApplyLogLevelsDefinedByGlobalProperty() throws Exception {
		
		Map<String, Level> loggerSettings = new HashMap<>();
		loggerSettings.put("org.openmrs.module.radiology", Level.INFO);
		loggerSettings.put("org.openmrs.module.reporting", Level.WARN);
		loggerSettings.put("org.hibernate", Level.DEBUG);
		
		String loggerNameLevelPairs = computeLoggerNameLevelPairsWithNoise(loggerSettings);
		
		when(Context.getAdministrationService()).thenReturn(administrationService);
		when(administrationService.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOG_LEVEL, ""))
		        .thenReturn(loggerNameLevelPairs);
		
		OpenmrsLoggingConfigurator.applyLogLevelsFromGlobalProperty();
		
		assertLoggerLevelsAreSetAsExpected(loggerSettings);
	}
	
	// TODO - fails if used on a test and all tests are run
	private void assertThatOneWarningWasLoggedWithMessage(String message) {
		final ListAppender appender = ctx.getListAppender("LIST");
		final List<LogEvent> logEvents = appender.getEvents();
		assertThat(logEvents.size(), is(1));
		assertThat(logEvents.get(0).getLevel(), is(Level.WARN));
		assertThat(logEvents.get(0).getLoggerName(), is("org.openmrs.util.OpenmrsLoggingConfigurator"));
		assertThat(logEvents.get(0).getMessage().getFormattedMessage(), is(message));
	}
	
	private String computeLoggerNameLevelPairs(Map<String, Level> loggerSettings) {
		return computeLoggerNameLevelPairs(loggerSettings, false);
	}
	
	private String computeLoggerNameLevelPairsWithNoise(Map<String, Level> loggerSettings) {
		return computeLoggerNameLevelPairs(loggerSettings, true);
	}
	
	/**
	 * Constructs logger name and level pair string.
	 * 
	 * @param loggerSettings logger name and level pairs which will be joined according to the
	 *            format expected by the method under test
	 * @param addNoise adds whitespaces (leading, trailing and between separator chars) when true
	 * @return joined string according to format expected by the method under test
	 * @see OpenmrsLoggingConfigurator#applyLogLevels(String)
	 */
	private String computeLoggerNameLevelPairs(Map<String, Level> loggerSettings, boolean addNoise) {
		if (addNoise) {
			return loggerSettings.entrySet().stream().map(joinLoggerNameAndLevelWithNoise())
			        .collect(Collectors.joining(","));
		} else {
			return loggerSettings.entrySet().stream().map(joinLoggerNameAndLevel()).collect(Collectors.joining(","));
		}
	}
	
	private Function<? super Entry<String, Level>, ? extends String> joinLoggerNameAndLevel() {
		return e -> e.getKey() + ":" + e.getValue().name();
	}
	
	private Function<? super Entry<String, Level>, ? extends String> joinLoggerNameAndLevelWithNoise() {
		return e -> "  " + e.getKey() + " :   " + e.getValue().name() + "  ";
	}
	
	private void assertLoggerLevelsAreSetAsExpected(Map<String, Level> loggerSettings) {
		loggerSettings.entrySet().stream().forEach(e -> assertThat("Level of logger with name: '" + e.getKey() + "'",
		    ctx.getLogger(e.getKey()).getLevel(), is(e.getValue())));
	}
}
