package org.openmrs.test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.notification.Alert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Tests {@link ValidatorTest}.
 */
public class ValidatorTestTest {
	
	private static final String errorCode = "error.null";
	
	private static final String errorNullTarget = "obj must not be null";
	
	private static final String defaultAlertText = "default alert";
	
	private static final String field = "text";
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private CustomAlertValidatorTest validatorTest;
	
	@Before
	public void setUp() throws Exception {
		validatorTest = new CustomAlertValidatorTest();
		validatorTest.setupValidation();
	}
	
	/**
	 * Simple {@code Validator} used for testing.
	 */
	private final class CustomAlertValidator implements Validator {
		
		@Override
		public boolean supports(Class<?> clazz) {
			return Alert.class.isAssignableFrom(clazz);
		}
		
		@Override
		public void validate(Object target, Errors errors) {
			if (target == null) {
				throw new IllegalArgumentException(errorNullTarget);
			}
			ValidationUtils.rejectIfEmpty(errors, field, errorCode);
		}
	}
	
	private final class CustomAlertValidatorTest extends ValidatorTest<CustomAlertValidator, Alert> {
		
		@Override
		protected CustomAlertValidator newValidator() {
			return new CustomAlertValidator();
		}
		
		@Override
		protected Alert newValidationTarget() {
			Alert alert = new Alert();
			alert.setText(defaultAlertText);
			return alert;
		}
	}
	
	@Test
	public void setupShouldInitializeValidatorTargetAndErrors() {
		
		assertNotNull(validatorTest.validator);
		assertNotNull(validatorTest.target);
		assertThat(validatorTest.target.getText(), is(defaultAlertText));
		assertNotNull(validatorTest.errors);
		assertNotNull(validatorTest.errors.getObjectName(), is("target"));
	}
	
	@Test
	public void shouldValidateGivenObjectUsingTheValidatorUnderTest() {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(errorNullTarget);
		validatorTest.validate(null);
	}
	
	@Test
	public void shouldValidateTarget() {
		
		validatorTest.validate();
		
		assertFalse(validatorTest.errors.hasFieldErrors(field));
	}
	
	@Test
	public void shouldValidateTargetAndCollectErrorsIfValidationFailed() {
		
		validatorTest.target.setText("");
		
		validatorTest.validate();
		
		assertThat(validatorTest.errors.getFieldErrors(field).get(0).getCode(), is(errorCode));
	}
	
	@Test
	public void shouldAssertThatGivenFieldHasErrors() {
		
		validatorTest.target.setText("");
		validatorTest.validate();
		assertTrue(validatorTest.errors.hasFieldErrors(field));
		
		try {
			validatorTest.assertThatFieldHasErrors(field);
		}
		catch (Throwable e) {
			fail("should not throw anything, but was thrown: " + e);
		}
	}
	
	@Test
	public void shouldFailAssertionIfGivenFieldHasNoErrors() {
		
		validatorTest.validate();
		assertFalse(validatorTest.errors.hasFieldErrors(field));
		
		expectedException.handleAssertionErrors();
		expectedException.expect(AssertionError.class);
		expectedException.expectMessage("Field '" + field + "' has no errors");
		validatorTest.assertThatFieldHasErrors(field);
	}
	
	@Test
	public void shouldAssertThatGivenFieldHasErrorWithGivenErrorCode() {
		
		validatorTest.target.setText("");
		validatorTest.validate();
		assertTrue(validatorTest.errors.hasFieldErrors(field));
		
		try {
			validatorTest.assertThatFieldHasError(field, errorCode);
		}
		catch (Throwable e) {
			fail("should not throw anything, but was thrown: " + e);
		}
	}
	
	@Test
	public void shouldFailAssertionIfGivenFieldDoesNotHaveErrorOfGivenCode() {
		
		validatorTest.validate();
		assertFalse(validatorTest.errors.hasFieldErrors(field));
		
		expectedException.handleAssertionErrors();
		expectedException.expect(AssertionError.class);
		expectedException.expectMessage("Field '" + field + "' has no errors");
		validatorTest.assertThatFieldHasError(field, errorCode);
	}
}
