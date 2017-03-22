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

import org.openmrs.Role;
import org.openmrs.annotation.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates attributes on the {@link Role} object
 * 
 * @since 1.5
 */
@Handler(supports = { Role.class }, order = 50)
public class RoleValidator implements Validator {
	
	/** Logger for this class and subclasses */
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> c) {
		return c.equals(Role.class);
	}
	
	/**
	 * Checks the form object for any inconsistencies/errors
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		Role role = (Role) obj;
		if (role == null) {
			errors.rejectValue("role", "error.general");
		} else {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role", "error.role");
			
			// reject any role that has a leading or trailing space
			if (!role.getRole().equals(role.getRole().trim())) {
				errors.rejectValue("role", "error.trailingSpaces");
			}
		}
		if (obj == null) {
			throw new IllegalArgumentException("validated role object should not be null");
		} else {
			ValidateUtil.validateFieldLengths(errors, obj.getClass(), "role", "description");
		}
	}
	
}
