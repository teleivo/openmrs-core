package org.openmrs.test;

import static org.junit.Assert.assertThat;
import static org.openmrs.test.matchers.HasFieldErrors.hasFieldErrors;

import org.junit.Before;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Basic fields and convenient methods helping to test a {@code Validator}.
 * 
 * @param <V> the validator under test
 * @param <T> the type of object validated by the valdiator under test
 * @see Validator
 * @since 2.2.0
 */
public abstract class ValidatorTest<V extends Validator, T extends Object> extends BaseContextSensitiveTest {
	
	/**
	 * The validator under test.
	 */
	protected V validator;
	
	/**
	 * The target object which can be passed to the validator.
	 */
	protected T target;
	
	/**
	 * The contextual state about the validation process.
	 */
	protected Errors errors;
	
	/**
	 * Creates a new instance of the validator under test.
	 * 
	 * @return a new validator instance
	 */
	protected abstract V newValidator();
	
	/**
	 * Creates a new instance of the validation target.
	 * 
	 * @return a new target instance
	 */
	protected abstract T newValidationTarget();
	
	/**
	 * Initializes members necessary to test the validator.
	 * <p>
	 * Delegates to {@link #newValidator()} and {@link #newValidationTarget()} to initialize
	 * validator and validation target respectively. Enables you to modify the {@code target} before
	 * validation.
	 * </p>
	 */
	@Before
	public void setupValidation() throws Exception {
		validator = newValidator();
		target = newValidationTarget();
		errors = new BindException(target, "target");
	}
	
	/**
	 * Validate the supplied object by delegating to the validator under test.
	 * <p>
	 * Assert the validation result using {@code errors} afterwards.
	 * </p>
	 * 
	 * @param object the object to validate
	 */
	public void validate(T object) {
		validator.validate(object, errors);
	}
	
	/**
	 * Validate the {@code target} by delegating to the validator under test.
	 * <p>
	 * Assert the validation result using {@code errors} afterwards.
	 * </p>
	 */
	public void validate() {
		validator.validate(target, errors);
	}
	
	public void assertThatFieldHasErrors(String field) {
		assertThat(errors, hasFieldErrors(field));
	}
	
	public void assertThatFieldHasError(String field, String errorCode) {
		assertThat(errors, hasFieldErrors(field, errorCode));
	}
}
