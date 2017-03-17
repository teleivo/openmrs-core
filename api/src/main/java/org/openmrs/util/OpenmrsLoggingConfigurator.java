package org.openmrs.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Configures the logging implementation OpenMRS uses i.e. how OpenMRS logs.
 */
public final class OpenmrsLoggingConfigurator {
	
	private static final Logger log = LogManager.getLogger(OpenmrsLoggingConfigurator.class);
	
	private static final List<String> OPENMRS_LOG_LEVELS = Arrays.asList(OpenmrsConstants.LOG_LEVEL_DEBUG,
	    OpenmrsConstants.LOG_LEVEL_ERROR, OpenmrsConstants.LOG_LEVEL_FATAL, OpenmrsConstants.LOG_LEVEL_INFO,
	    OpenmrsConstants.LOG_LEVEL_TRACE, OpenmrsConstants.LOG_LEVEL_WARN);
	
	private OpenmrsLoggingConfigurator() {
		// This constructor will not be invoked, this is a utility class
	}
	
	/**
	 * Apply the log level to the logger of a given name.
	 * <p>
	 * Valid log {@code level}'s are:
	 * <ul>
	 * <li>{@link OpenmrsConstants#LOG_LEVEL_DEBUG}</li>
	 * <li>{@link OpenmrsConstants#LOG_LEVEL_ERROR}</li>
	 * <li>{@link OpenmrsConstants#LOG_LEVEL_FATAL}</li>
	 * <li>{@link OpenmrsConstants#LOG_LEVEL_INFO}</li>
	 * <li>{@link OpenmrsConstants#LOG_LEVEL_TRACE}</li>
	 * <li>{@link OpenmrsConstants#LOG_LEVEL_WARN}</li>
	 * <li></li>
	 * </ul>
	 * </p>
	 * 
	 * @param loggerName the logger name, null or blank string treated as
	 *            {@code OpenmrsConstants.LOG_CLASS_DEFAULT}
	 * @param level the log level to set the logger to, null or blank values are ignored, matching
	 *            against allowed levels is case in-sensitive
	 * @since 2.2.0
	 */
	public static void applyLogLevel(String loggerName, String level) {
		if (isInvalidLogLevel(level)) {
			log.warn("Trying set invalid log level '{}'. Valid values are trace, debug, info, warn, error or fatal", level);
			return;
		}
		Level logLevel = Level.toLevel(level);
		String logger = defaultLoggerNameIfBlank(loggerName);
		Configurator.setLevel(logger, logLevel);
	}
	
	private static boolean isInvalidLogLevel(String level) {
		return StringUtils.isBlank(level) || isNotInAllowedLogLevelConstants(level);
	}
	
	private static boolean isNotInAllowedLogLevelConstants(String level) {
		return !OPENMRS_LOG_LEVELS.stream().filter(l -> l.equalsIgnoreCase(level)).findFirst().isPresent();
	}
	
	private static String defaultLoggerNameIfBlank(String loggerName) {
		String logger;
		if (StringUtils.isBlank(loggerName)) {
			logger = OpenmrsConstants.LOG_CLASS_DEFAULT;
		} else {
			logger = loggerName;
		}
		return logger;
	}
	
	/**
	 * Set the log level of multiple loggers.
	 * <p>
	 * Expects a comma separated list of "loggerName:logLevel" pairs.<br>
	 * <ul>
	 * <li>Example 1: "org.openmrs.api:warn" will set
	 * <ul>
	 * <li>log level of logger "org.openmrs.api" to {@code WARN}</li>
	 * </ul>
	 * </li>
	 * <li>Example 2: "org.openmrs.api:warn,org.openmrs.module.reporting:debug" will set
	 * <ul>
	 * <li>log level of logger "org.openmrs.api" to {@code WARN}</li>
	 * <li>log level of logger "org.openmrs.module.reporting" to {@code DEBUG}</li>
	 * </ul>
	 * </li>
	 * <li>Example 3: "org.hibernate:info,org.openmrs.module.reporting:debug,debug" will set
	 * <ul>
	 * <li>log level of logger "org.hibernate" to {@code INFO}</li>
	 * <li>log level of logger "org.openmrs.module.reporting" to {@code DEBUG}</li>
	 * <li>log level of logger "org.openmrs.api" to {@code DEBUG}; the single log level is applied
	 * to {@link OpenmrsConstants#LOG_CLASS_DEFAULT}</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * Splits the given string into logger name and log level pairs which are passed to
	 * {@link OpenmrsLoggingConfigurator#applyLogLevel(String, String)}.
	 * 
	 * @param loggerNameLevelPairs the logger names with log levels
	 * @since 2.2.0
	 */
	public static void applyLogLevels(String loggerNameLevelPairs) {
		StrTokenizer loggersWithLevels = StrTokenizer.getCSVInstance(loggerNameLevelPairs);
		for (String loggerWithLevel : loggersWithLevels.getTokenArray()) {
			String[] lnlv = loggerWithLevel.split(":");
			if (lnlv.length == 1) {
				applyLogLevel(OpenmrsConstants.LOG_CLASS_DEFAULT, lnlv[0].trim());
			} else {
				applyLogLevel(lnlv[0].trim(), lnlv[1].trim());
			}
		}
	}
}
