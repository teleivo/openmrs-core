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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflow;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * This class validates a {@link PatientProgram} object
 *
 * @since 1.9
 */
@Handler(supports = { PatientProgram.class }, order = 50)
public class PatientProgramValidator implements Validator {
	
	private static final Logger log = LoggerFactory.getLogger(PatientProgramValidator.class);
	
	/**
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> c) {
		return PatientProgram.class.isAssignableFrom(c);
	}
	
	/**
	 * Validates the given PatientProgram.
	 *
	 * @param obj The patient program to validate.
	 * @param errors Errors
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName() + ".validate...");
		}
		
		if (obj == null) {
			throw new IllegalArgumentException("The parameter obj should not be null");
		}
		MessageSourceService mss = Context.getMessageSourceService();
		PatientProgram patientProgram = (PatientProgram) obj;
		ValidationUtils.rejectIfEmpty(errors, "patient", "error.required",
		    new Object[] { mss.getMessage("general.patient") });
		ValidationUtils.rejectIfEmpty(errors, "program", "error.required",
		    new Object[] { mss.getMessage("Program.program") });
		
		if (errors.hasErrors()) {
			return;
		}
		
		ValidationUtils.rejectIfEmpty(errors, "dateEnrolled", "error.patientProgram.enrolledDateEmpty");
		
		Date today = new Date();
		if (patientProgram.getDateEnrolled() != null && today.before(patientProgram.getDateEnrolled())) {
			errors.rejectValue("dateEnrolled", "error.patientProgram.enrolledDateDateCannotBeInFuture");
		}
		
		if (patientProgram.getDateCompleted() != null && today.before(patientProgram.getDateCompleted())) {
			errors.rejectValue("dateCompleted", "error.patientProgram.completionDateCannotBeInFuture");
		}
		
		// if enrollment or complete date of program is in future or complete date has come before enroll date we should throw error
		if (patientProgram.getDateEnrolled() != null
		        && OpenmrsUtil.compareWithNullAsLatest(patientProgram.getDateCompleted(), patientProgram.getDateEnrolled()) < 0) {
			errors.rejectValue("dateCompleted", "error.patientProgram.enrolledDateShouldBeBeforecompletionDate");
		}
		
		Set<ProgramWorkflow> workFlows = patientProgram.getProgram().getWorkflows();
		//Patient state validation is specific to a work flow
		for (ProgramWorkflow workFlow : workFlows) {
			Set<PatientState> patientStates = patientProgram.getStates();
			if (patientStates != null) {
				//Set to store to keep track of unique valid state and start date combinations
				Set<String> statesAndStartDates = new HashSet<>();
				PatientState latestState = null;
				boolean foundCurrentPatientState = false;
				boolean foundStateWithNullStartDate = false;
				for (PatientState patientState : patientStates) {
					if (patientState.getVoided()) {
						continue;
					}
					
					String missingRequiredFieldCode = null;
					//only the initial state can have a null start date
					if (patientState.getStartDate() == null) {
						if (foundStateWithNullStartDate) {
							missingRequiredFieldCode = "general.dateStart";
						} else {
							foundStateWithNullStartDate = true;
						}
					} else if (patientState.getState() == null) {
						missingRequiredFieldCode = "State.state";
					}
					
					if (missingRequiredFieldCode != null) {
						errors.rejectValue("states", "PatientState.error.requiredField", new Object[] { mss
						        .getMessage(missingRequiredFieldCode) }, null);
						return;
					}
					
					//state should belong to one of the workflows in the program
					// note that we are iterating over getAllWorkflows() here because we want to include
					// retired workflows, and the workflows variable does not include retired workflows
					boolean isValidPatientState = false;
					for (ProgramWorkflow wf : patientProgram.getProgram().getAllWorkflows()) {
						if (wf.getStates().contains(patientState.getState())) {
							isValidPatientState = true;
							break;
						}
					}
					
					if (!isValidPatientState) {
						errors.rejectValue("states", "PatientState.error.invalidPatientState",
						    new Object[] { patientState }, null);
						return;
					}
					
					//will validate it with other states in its workflow
					if (!patientState.getState().getProgramWorkflow().equals(workFlow)) {
						continue;
					}
					
					if (OpenmrsUtil.compareWithNullAsLatest(patientState.getEndDate(), patientState.getStartDate()) < 0) {
						errors.rejectValue("states", "PatientState.error.endDateCannotBeBeforeStartDate");
						return;
					} else if (statesAndStartDates.contains(patientState.getState().getUuid() + ""
					        + patientState.getStartDate())) {
						// we already have a patient state with the same work flow state and start date
						errors.rejectValue("states", "PatientState.error.duplicatePatientStates");
						return;
					}
					
					//Ensure that the patient is only in one state at a given time
					if (!foundCurrentPatientState && patientState.getEndDate() == null) {
						foundCurrentPatientState = true;
					} else if (foundCurrentPatientState && patientState.getEndDate() == null) {
						errors.rejectValue("states", "PatientProgram.error.cannotBeInMultipleStates");
						return;
					}
					
					if (latestState == null) {
						latestState = patientState;
					} else {
						if (patientState.compareTo(latestState) > 0) {
							//patient should have already left this state since it is older
							if (latestState.getEndDate() == null) {
								errors.rejectValue("states", "PatientProgram.error.cannotBeInMultipleStates");
								return;
							} else if (OpenmrsUtil.compareWithNullAsEarliest(patientState.getStartDate(), latestState
							        .getEndDate()) < 0) {
								//current state was started before a previous state was ended
								errors.rejectValue("states", "PatientProgram.error.foundOverlappingStates", new Object[] {
								        patientState.getStartDate(), latestState.getEndDate() }, null);
								return;
							}
							latestState = patientState;
						} else if (patientState.compareTo(latestState) < 0) {
							//patient should have already left this state since it is older
							if (patientState.getEndDate() == null) {
								errors.rejectValue("states", "PatientProgram.error.cannotBeInMultipleStates");
								return;
							} else if (OpenmrsUtil.compareWithNullAsEarliest(latestState.getStartDate(), patientState
							        .getEndDate()) < 0) {
								//latest state was started before a previous state was ended
								errors.rejectValue("states", "PatientProgram.error.foundOverlappingStates");
								return;
							}
						}
					}
					
					statesAndStartDates.add(patientState.getState().getUuid() + "" + patientState.getStartDate());
				}
			}
		}
		ValidateUtil.validateFieldLengths(errors, obj.getClass(), "voidReason");
		//
	}
}
