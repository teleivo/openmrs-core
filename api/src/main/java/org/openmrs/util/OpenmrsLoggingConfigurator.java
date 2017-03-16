package org.openmrs.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Configures the logging implementation OpenMRS uses i.e. how OpenMRS logs.
 */
public final class OpenmrsLoggingConfigurator {
	
	private OpenmrsLoggingConfigurator() {
		// This constructor will not be invoked, this is a utility class
	}
	
	/**
	 * Apply the log level to the logger of given logger name.
	 * 
	 * @param loggerName the logger name, null treated as {@code OpenmrsConstants.LOG_CLASS_DEFAULT}
	 * @param level the log level to set the logger to
	 * @since 2.2.0
	 */
	public static void applyLogLevel(String loggerName, String level) {
		
		String logger = defaultLoggerNameIfBlank(loggerName);
		if (StringUtils.isBlank(level)) {
			return;
		}
		Level logLevel = Level.toLevel(level);
		Configurator.setLevel(logger, logLevel);
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
}
