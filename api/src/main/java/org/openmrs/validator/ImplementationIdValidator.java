/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ImplementationId;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates attributes on the {@link ImplementationId} object.
 * 
 */
@Handler(supports = { ImplementationId.class }, order = 50)
public class ImplementationIdValidator implements Validator {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(ImplementationId.class);
	}
	
	/*
	 * 
	 */

	@Override
	public void validate(Object obj, Errors errors) throws APIException {
		ImplementationId implId = (ImplementationId) obj;
		char[] illegalChars = { '^', '|' };
		if (implId == null) {
			throw new APIException("ImplementationId.null", (Object[]) null);
		} else {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "ImplementationId.name.empty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "implementationId", "ImplementationId.implementationId.empty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passphrase", "ImplementationId.passphrase.empty");
			if (implId.getImplementationId() != null && StringUtils.containsAny(implId.getImplementationId(), illegalChars)) {
				errors.rejectValue("implementationId", "ImplementationId.implementationId.invalidCharacter");
			}
		}
	}
}
