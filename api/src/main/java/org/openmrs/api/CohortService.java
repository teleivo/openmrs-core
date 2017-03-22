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

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.CohortDAO;
import org.openmrs.util.PrivilegeConstants;

/**
 * API methods related to Cohorts and CohortDefinitions
 * <ul>
 * <li>A Cohort is a list of patient ids.</li>
 * <li>A CohortDefinition is a search strategy which can be used to arrive at a cohort. Therefore,
 * the patients returned by running a CohortDefinition can be different depending on the data that
 * is stored elsewhere in the database.</li>
 * </ul>
 * 
 * @see org.openmrs.Cohort
 */
public interface CohortService extends OpenmrsService {
	
	/**
	 * Sets the CohortDAO for this service to use
	 * 
	 * @param dao
	 */
	public void setCohortDAO(CohortDAO dao);

	/**
	 * Save a cohort to the database (create if new, or update if changed) This method will throw an
	 * exception if any patientIds in the Cohort don't exist.
	 * 
	 * @param cohort the cohort to be saved to the database
	 * @return The cohort that was passed in
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.ADD_COHORTS, PrivilegeConstants.EDIT_COHORTS })
	public Cohort saveCohort(Cohort cohort) throws APIException;
	
	/**
	 * Voids the given cohort, deleting it from the perspective of the typical end user.
	 * 
	 * @param cohort the cohort to delete
	 * @param reason the reason this cohort is being retired
	 * @return The cohort that was passed in
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.DELETE_COHORTS })
	public Cohort voidCohort(Cohort cohort, String reason) throws APIException;
	
	/**
	 * Completely removes a Cohort from the database (not reversible)
	 * 
	 * @param cohort the Cohort to completely remove from the database
	 * @throws APIException
	 */
	public Cohort purgeCohort(Cohort cohort) throws APIException;
	
	/**
	 * Gets a Cohort by its database primary key
	 * 
	 * @param id
	 * @return the Cohort with the given primary key, or null if none exists
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public Cohort getCohort(Integer id) throws APIException;
	
	/**
	 * Gets a non voided Cohort by its name
	 * 
	 * @param name
	 * @return the Cohort with the given name, or null if none exists
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public Cohort getCohort(String name) throws APIException;
	
	/**
	 * Gets all Cohorts (not including voided ones)
	 * 
	 * @return All Cohorts in the database (not including voided ones)
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public List<Cohort> getAllCohorts() throws APIException;
	
	/**
	 * Gets all Cohorts, possibly including the voided ones
	 * 
	 * @param includeVoided whether or not to include voided Cohorts
	 * @return All Cohorts, maybe including the voided ones
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public List<Cohort> getAllCohorts(boolean includeVoided) throws APIException;
	
	/**
	 * Returns Cohorts whose names match the given string. Returns an empty list in the case of no
	 * results. Returns all Cohorts in the case of null or empty input
	 * 
	 * @param nameFragment
	 * @return list of cohorts matching the name fragment
	 * @throws APIException
	 */
	public List<Cohort> getCohorts(String nameFragment) throws APIException;
	
	/**
	 * Find all Cohorts that contain the given patient. (Not including voided Cohorts)
	 *
	 * @since 2.1
	 * @param patient patient used to find the cohorts
	 * @return Cohorts that contain the given patient
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public List<Cohort> getCohortsContainingPatient(Patient patient, Boolean voided) throws APIException;
	
	/**
	 * Find all Cohorts that contain the given patient. (Not including voided Cohorts)
	 * 
	 * @param patient patient used to find the cohorts
	 * @return All non-voided Cohorts that contain the given patient
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public List<Cohort> getCohortsContainingPatient(Patient patient) throws APIException;
	
	/**
	 * Find all Cohorts that contain the given patientId. (Not including voided Cohorts)
	 *
	 * @param patientId patient id used to find the cohorts
	 * @return All non-voided Cohorts that contain the given patientId
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public List<Cohort> getCohortsContainingPatientId(Integer patientId) throws APIException;
	
	/**
	 * Adds a new patient to a Cohort. If the patient is not already in the Cohort, then they are
	 * added, and the Cohort is saved, marking it as changed.
	 * 
	 * @param cohort the cohort to receive the given patient
	 * @param patient the patient to insert into the cohort
	 * @return The cohort that was passed in with the new patient in it
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.EDIT_COHORTS })
	public Cohort addPatientToCohort(Cohort cohort, Patient patient) throws APIException;
	
	/**
	 * Removes a patient from a Cohort. If the patient is in the Cohort, then they are removed, and
	 * the Cohort is saved, marking it as changed.
	 * 
	 * @param cohort the cohort containing the given patient
	 * @param patient the patient to remove from the given cohort
	 * @return The cohort that was passed in with the patient removed
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.EDIT_COHORTS })
	public Cohort removePatientFromCohort(Cohort cohort, Patient patient) throws APIException;
	
	/**
	 * Get Cohort by its UUID
	 * 
	 * @param uuid
	 * @return cohort or null
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_COHORTS })
	public Cohort getCohortByUuid(String uuid);

	/**
	 * Adds membership to a Cohort
	 *
	 * @since 2.1
	 * @param cohort cohort to add the membership
	 * @param cohortMembership new membership that will be added to cohort
	 * @return the Cohort that was passed in with the CohortMembership
	 */
	@Authorized( {PrivilegeConstants.EDIT_COHORTS })
	Cohort addMembershipToCohort(Cohort cohort, CohortMembership cohortMembership);

	/**
	 * Removes membership from a Cohort
	 *
	 * @since 2.1
	 * @param cohort cohort to remove the membership
	 * @param cohortMembership membership that will be removed from cohort
	 * @return the Cohort that was passed in with the CohortMembership
	 */
	@Authorized( {PrivilegeConstants.EDIT_COHORTS })
	Cohort removeMemberShipFromCohort(Cohort cohort, CohortMembership cohortMembership);

	/**
	 * Void membership of Cohort that contain the voided Patients
	 *
	 * @since 2.1
	 * @param patient patient that was voided
	 */
	@Authorized( {PrivilegeConstants.EDIT_COHORTS })
	void patientVoided(Patient patient);
	
	/**
	 * Unvoid membership of Cohort that contain the unvoided Patients
	 *
	 * @since 2.1
	 * @param patient patient that was unvoided
	 */
	@Authorized( {PrivilegeConstants.EDIT_COHORTS })
	void patientUnvoided(Patient patient, User voidedBy, Date dateVoided, String voidReason);
}
