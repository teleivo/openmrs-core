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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.notification.Alert;
import org.openmrs.test.ValidatorTest;

/**
 * Tests methods on the {@link AlertValidator} class.
 *
 */
public class AlertValidatorTest extends ValidatorTest<AlertValidator, Alert> {
	
	@Override
	protected AlertValidator newValidator() {
		return new AlertValidator();
	}
	
	@Override
	protected Alert newValidationTarget() {
		return new Alert();
	}
	
	@Test
	public void shouldFailValidationIfAlertTextIsNull() {
		
		validate();
		
		assertThatFieldTextHasError();
	}

	@Test
	public void shouldFailValidationIfAlertTextIsEmpty() {
		
		target.setText("");
		
		validate();
		
		assertThatFieldTextHasError();
	}
	
	@Test
	public void shouldFailValidationIfAlertTextIsOnlyWhitespaces() {
		
		target.setText(" ");
		
		validate();
		
		assertThatFieldTextHasError();
	}
	
	@Test
	public void validate_shouldPassValidationIfAllRequiredValuesAreSet() {
		
		target.setText("Alert Text");
		
		validate();
		
		Assert.assertFalse(errors.hasErrors());
	}
	
	@Test
	public void validate_shouldFailValidationIfFieldLengthsAreNotCorrect() {
		
		target
		        .setText("too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text too long text");
		
		validate();
		
		assertThatFieldTextHasError();
	}
	
	private void assertThatFieldTextHasError() {
		assertThatFieldHasErrors("text");
	}
}
