/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptStateConversion;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.ProgramWorkflowDAO;
import org.openmrs.util.PrivilegeConstants;

/**
 * Contains methods pertaining to management of Programs, ProgramWorkflows, ProgramWorkflowStates,
 * PatientPrograms, PatientStates, and ConceptStateConversions Use:<br>
 * 
 * <pre>
 *   Program program = new Program();
 *   program.set___(___);
 *   ...etc
 *   Context.getProgramWorkflowService().saveProgram(program);
 * </pre>
 */
public interface ProgramWorkflowService extends OpenmrsService {
	
	/**
	 * Setter for the ProgramWorkflow DataAccessObject (DAO). The DAO is used for saving and
	 * retrieving from the database
	 * 
	 * @param dao - The DAO for this service
	 */
	public void setProgramWorkflowDAO(ProgramWorkflowDAO dao);
	
	// **************************
	// PROGRAM
	// **************************
	
	/**
	 * Save <code>program</code> to database (create if new or update if changed)
	 * 
	 * @param program is the Program to be saved to the database
	 * @return The Program that was saved
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public Program saveProgram(Program program) throws APIException;
	
	/**
	 * Returns a program given that programs primary key <code>programId</code> A null value is
	 * returned if no program exists with this programId.
	 * 
	 * @param programId integer primary key of the program to find
	 * @return Program object that has program.programId = <code>programId</code> passed in.
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public Program getProgram(Integer programId) throws APIException;
	
	/**
	 * Returns a program given the program's exact <code>name</code> A null value is returned if
	 * there is no program with this name
	 * 
	 * @param name the exact name of the program to match on
	 * @return Program matching the <code>name</code> to Program.name
	 * @throws APIException
	 * @throws ProgramNameDuplicatedException when there are more than one program in the dB with
	 *             the given name.
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public Program getProgramByName(String name) throws APIException;
	
	/**
	 * Returns all programs, includes retired programs. This method delegates to the
	 * #getAllPrograms(boolean) method
	 * 
	 * @return List&lt;Program&gt; of all existing programs, including retired programs
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Program> getAllPrograms() throws APIException;
	
	/**
	 * Returns all programs
	 * 
	 * @param includeRetired whether or not to include retired programs
	 * @return List&lt;Program&gt; all existing programs, including retired based on the input parameter
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Program> getAllPrograms(boolean includeRetired) throws APIException;
	
	/**
	 * Returns programs that match the given string. A null list will never be returned. An empty
	 * list will be returned if there are no programs matching this <code>nameFragment</code>
	 * 
	 * @param nameFragment is the string used to search for programs
	 * @return List&lt;Program&gt; - list of Programs whose name matches the input parameter
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Program> getPrograms(String nameFragment) throws APIException;
	
	/**
	 * Completely remove a program from the database (not reversible) This method delegates to
	 * #purgeProgram(program, boolean) method
	 * 
	 * @param program the Program to clean out of the database.
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeProgram(Program program) throws APIException;
	
	/**
	 * Completely remove a program from the database (not reversible)
	 * 
	 * @param cascade <code>true</code> to delete related content
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeProgram(Program program, boolean cascade) throws APIException;
	
	/**
	 * Retires the given program
	 * 
	 * @param program Program to be retired
	 * @param reason String for retiring the program
	 * @return the Program which has been retired
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public Program retireProgram(Program program, String reason) throws APIException;
	
	/**
	 * Unretires the given program
	 * 
	 * @param program Program to be unretired
	 * @return the Program which has been unretired
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public Program unretireProgram(Program program) throws APIException;
	
	// **************************
	// PATIENT PROGRAM 
	// **************************
	
	/**
	 * Get a program by its uuid. There should be only one of these in the database. If multiple are
	 * found, an error is thrown.
	 * 
	 * @param uuid the universally unique identifier
	 * @return the program which matches the given uuid
	 */
	public Program getProgramByUuid(String uuid);
	
	/**
	 * Get a program state by its uuid. There should be only one of these in the database. If
	 * multiple are found, an error is thrown.
	 * 
	 * @param uuid the universally unique identifier
	 * @return the program which matches the given uuid
	 */
	public PatientState getPatientStateByUuid(String uuid);
	
	/**
	 * Save patientProgram to database (create if new or update if changed)
	 * 
	 * @param patientProgram is the PatientProgram to be saved to the database
	 * @return PatientProgram - the saved PatientProgram
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.ADD_PATIENT_PROGRAMS, PrivilegeConstants.EDIT_PATIENT_PROGRAMS })
	public PatientProgram savePatientProgram(PatientProgram patientProgram) throws APIException;
	
	/**
	 * Returns a PatientProgram given that PatientPrograms primary key <code>patientProgramId</code>
	 * A null value is returned if no PatientProgram exists with this patientProgramId.
	 * 
	 * @param patientProgramId integer primary key of the PatientProgram to find
	 * @return PatientProgram object that has patientProgram.patientProgramId =
	 *         <code>patientProgramId</code> passed in.
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public PatientProgram getPatientProgram(Integer patientProgramId) throws APIException;
	
	/**
	 * Returns PatientPrograms that match the input parameters. If an input parameter is set to
	 * null, the parameter will not be used. Calling this method will all null parameters will
	 * return all PatientPrograms in the database A null list will never be returned. An empty list
	 * will be returned if there are no programs matching the input criteria
	 * 
	 * @param patient if supplied all PatientPrograms returned will be for this Patient
	 * @param program if supplied all PatientPrograms returned will be for this Program
	 * @param minEnrollmentDate if supplied will limit PatientPrograms to those with enrollments on
	 *            or after this Date
	 * @param maxEnrollmentDate if supplied will limit PatientPrograms to those with enrollments on
	 *            or before this Date
	 * @param minCompletionDate if supplied will limit PatientPrograms to those completed on or
	 *            after this Date OR not yet completed
	 * @param maxCompletionDate if supplied will limit PatientPrograms to those completed on or
	 *            before this Date
	 * @param includeVoided if true, will also include voided PatientPrograms
	 * @return List&lt;PatientProgram&gt; of PatientPrograms that match the passed input parameters
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<PatientProgram> getPatientPrograms(Patient patient, Program program, Date minEnrollmentDate,
	        Date maxEnrollmentDate, Date minCompletionDate, Date maxCompletionDate, boolean includeVoided)
	        throws APIException;
	
	/**
	 * Completely remove a patientProgram from the database (not reversible) This method delegates
	 * to #purgePatientProgram(patientProgram, boolean) method
	 * 
	 * @param patientProgram the PatientProgram to clean out of the database.
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.PURGE_PATIENT_PROGRAMS })
	public void purgePatientProgram(PatientProgram patientProgram) throws APIException;
	
	/**
	 * Completely remove a patientProgram from the database (not reversible)
	 * 
	 * @param patientProgram the PatientProgram to clean out of the database.
	 * @param cascade <code>true</code> to delete related content
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.PURGE_PATIENT_PROGRAMS })
	public void purgePatientProgram(PatientProgram patientProgram, boolean cascade) throws APIException;
	
	/**
	 * Voids the given patientProgram
	 * 
	 * @param patientProgram patientProgram to be voided
	 * @param reason is the reason why the patientProgram is being voided
	 * @return the voided PatientProgram
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.DELETE_PATIENT_PROGRAMS })
	public PatientProgram voidPatientProgram(PatientProgram patientProgram, String reason) throws APIException;
	
	/**
	 * Unvoids the given patientProgram
	 * 
	 * @param patientProgram patientProgram to be un-voided
	 * @return the voided PatientProgram
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.DELETE_PATIENT_PROGRAMS })
	public PatientProgram unvoidPatientProgram(PatientProgram patientProgram) throws APIException;
	
	/**
	 * Get all possible outcome concepts for a program. Will return all concept answers
	 * {@link org.openmrs.Concept#getAnswers()} if they exist, then all concept set members
	 * {@link org.openmrs.Concept#getSetMembers()} if they exist, then empty List.
	 * 
	 * @param programId
	 * @return outcome concepts or empty List if none exist
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<Concept> getPossibleOutcomes(Integer programId);
	
	// **************************
	// CONCEPT STATE CONVERSION
	// **************************
	
	/**
	 * Get ProgramWorkflow by its UUID
	 * 
	 * @param uuid
	 * @return program work flow or null
	 */
	public ProgramWorkflow getWorkflowByUuid(String uuid);
	
	/**
	 * Save ConceptStateConversion to database (create if new or update if changed)
	 * 
	 * @param conceptStateConversion - The ConceptStateConversion to save
	 * @return ConceptStateConversion - The saved ConceptStateConversion
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.ADD_PATIENT_PROGRAMS, PrivilegeConstants.EDIT_PATIENT_PROGRAMS })
	public ConceptStateConversion saveConceptStateConversion(ConceptStateConversion conceptStateConversion)
	        throws APIException;
	
	/**
	 * Returns a conceptStateConversion given that conceptStateConversions primary key
	 * <code>conceptStateConversionId</code> A null value is returned if no conceptStateConversion
	 * exists with this conceptStateConversionId.
	 * 
	 * @param conceptStateConversionId integer primary key of the conceptStateConversion to find
	 * @return ConceptStateConversion object that has
	 *         conceptStateConversion.conceptStateConversionId =
	 *         <code>conceptStateConversionId</code> passed in.
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public ConceptStateConversion getConceptStateConversion(Integer conceptStateConversionId) throws APIException;
	
	/**
	 * Returns all conceptStateConversions
	 * 
	 * @return List&lt;ConceptStateConversion&gt; of all ConceptStateConversions that exist
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PROGRAMS })
	public List<ConceptStateConversion> getAllConceptStateConversions() throws APIException;
	
	/**
	 * Completely remove a conceptStateConversion from the database (not reversible) This method
	 * delegates to #purgeConceptStateConversion(conceptStateConversion, boolean) method
	 * 
	 * @param conceptStateConversion the ConceptStateConversion to clean out of the database.
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion) throws APIException;
	
	/**
	 * Completely remove a conceptStateConversion from the database (not reversible)
	 * 
	 * @param conceptStateConversion the ConceptStateConversion to clean out of the database.
	 * @param cascade <code>true</code> to delete related content
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.MANAGE_PROGRAMS })
	public void purgeConceptStateConversion(ConceptStateConversion conceptStateConversion, boolean cascade)
	        throws APIException;
		
	/**
	 * Retrieves the ConceptStateConversion that matches the passed <code>ProgramWorkflow</code> and
	 * <code>Concept</code>
	 * 
	 * @param workflow - the ProgramWorkflow to check
	 * @param trigger - the Concept to check
	 * @return ConceptStateConversion that matches the passed <code>ProgramWorkflow</code> and
	 *         <code>Concept</code>
	 * @throws APIException
	 */
	public ConceptStateConversion getConceptStateConversion(ProgramWorkflow workflow, Concept trigger) throws APIException;
	
	/**
	 * Get a state by its uuid. There should be only one of these in the database. If multiple are
	 * found, an error is thrown.
	 * 
	 * @param uuid the universally unique identifier
	 * @return the program workflow state which matches the given uuid
	 */
	public ProgramWorkflowState getStateByUuid(String uuid);
					
	/**
	 * Get a patient program by its uuid. There should be only one of these in the database. If
	 * multiple are found, an error is thrown.
	 * 
	 * @param uuid the universally unique identifier
	 * @return the patient program which matches the given uuid
	 */
	public PatientProgram getPatientProgramByUuid(String uuid);
	
	/**
	 * 
	 * @param cohort
	 * @param programs
	 * @return List&lt;PatientProgram&gt; for all Patients in the given Cohort that are in the given
	 *         programs
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<PatientProgram> getPatientPrograms(Cohort cohort, Collection<Program> programs);
		
	/**
	 * Returns a list of Programs that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of Programs
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<Program> getProgramsByConcept(Concept concept);
	
	/**
	 * Returns a list of ProgramWorkflows that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of ProgramWorkflows
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<ProgramWorkflow> getProgramWorkflowsByConcept(Concept concept);
	
	/**
	 * Returns a list of ProgramWorkflowStates that are using a particular concept.
	 * 
	 * @param concept - The Concept being used.
	 * @return - A List of ProgramWorkflowStates
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_PROGRAMS })
	public List<ProgramWorkflowState> getProgramWorkflowStatesByConcept(Concept concept);
	
	/**
	 * Get a concept state conversion by its uuid. There should be only one of these in the
	 * database. If multiple are found, an error is thrown.
	 * 
	 * @param uuid the universally unique identifier
	 * @return the concept state conversion which matches the given uuid
	 */
	public ConceptStateConversion getConceptStateConversionByUuid(String uuid);
}
