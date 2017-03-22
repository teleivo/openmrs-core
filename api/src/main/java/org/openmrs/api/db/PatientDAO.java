/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.api.db;

import java.util.List;

import org.openmrs.Allergies;
import org.openmrs.Allergy;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;

/**
 * Database methods for the PatientService
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.api.PatientService
 */
public interface PatientDAO {
	
	/**
	 * @see org.openmrs.api.PatientService#savePatient(org.openmrs.Patient)
	 */
	public Patient savePatient(Patient patient) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getPatient(Integer)
	 */
	public Patient getPatient(Integer patientId) throws DAOException;
	
	/**
	 * Delete patient from database. This <b>should not be called</b> except for testing and
	 * administration purposes. Use the void method instead
	 * 
	 * @param patient patient to be deleted
	 * @see org.openmrs.api.PatientService#deletePatient(org.openmrs.Patient)
	 * @see org.openmrs.api.PatientService#voidPatient(Patient, String)
	 */
	public void deletePatient(Patient patient) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getAllPatients(boolean)
	 */
	public List<Patient> getAllPatients(boolean includeVoided) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getPatients(String, Integer, Integer)
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *

	 *
	 *
	 *
	 */
	public List<Patient> getPatients(String query, Integer start, Integer length) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getPatients(String, boolean, Integer, Integer)
	 */
	public List<Patient> getPatients(String query, boolean includeVoided, Integer start, Integer length) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getPatientIdentifiers(java.lang.String, java.util.List,
	 *      java.util.List, java.util.List, java.lang.Boolean)
	 *      
	 */
	public List<PatientIdentifier> getPatientIdentifiers(String identifier,
	        List<PatientIdentifierType> patientIdentifierTypes, List<Location> locations, List<Patient> patients,
	        Boolean isPreferred) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#savePatientIdentifierType(org.openmrs.PatientIdentifierType)
	 */
	public PatientIdentifierType savePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getAllPatientIdentifierTypes(boolean)
	 */
	public List<PatientIdentifierType> getAllPatientIdentifierTypes(boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getPatientIdentifierTypes(java.lang.String,
	 *      java.lang.String, java.lang.Boolean, java.lang.Boolean)
	 */
	public List<PatientIdentifierType> getPatientIdentifierTypes(String name, String format, Boolean required,
	        Boolean hasCheckDigit) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getPatientIdentifierType(java.lang.Integer)
	 */
	public PatientIdentifierType getPatientIdentifierType(Integer patientIdentifierTypeId) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#purgePatientIdentifierType(org.openmrs.PatientIdentifierType)
	 */
	public void deletePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getDuplicatePatientsByAttributes(java.util.List)
	 */
	public List<Patient> getDuplicatePatientsByAttributes(List<String> attributes) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#isIdentifierInUseByAnotherPatient(PatientIdentifier)
	 */
	public boolean isIdentifierInUseByAnotherPatient(PatientIdentifier patientIdentifier);
	
	/**
	 * @param uuid
	 * @return patient or null
	 */
	public Patient getPatientByUuid(String uuid);
	
	public PatientIdentifier getPatientIdentifierByUuid(String uuid);
	
	/**
	 * @param uuid
	 * @return patient identifier type or null
	 */
	public PatientIdentifierType getPatientIdentifierTypeByUuid(String uuid);
	
	/**
	 * @see org.openmrs.api.PatientService#getPatientIdentifier(java.lang.Integer)
	 */
	public PatientIdentifier getPatientIdentifier(Integer patientIdentifierId) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#savePatientIdentifier(org.openmrs.PatientIdentifier)
	 */
	public PatientIdentifier savePatientIdentifier(PatientIdentifier patientIdentifier);
	
	/**
	 * @see org.openmrs.api.PatientService#purgePatientIdentifier(org.openmrs.PatientIdentifier)
	 */
	public void deletePatientIdentifier(PatientIdentifier patientIdentifier) throws DAOException;
	
	/**
	 * @see org.openmrs.api.PatientService#getCountOfPatients(String)
	 *

	 *
	 */
	public Long getCountOfPatients(String query);
	
	/**
	 * @see org.openmrs.api.PatientService#getCountOfPatients(String, boolean)
	 */
	public Long getCountOfPatients(String query, boolean includeVoided);
	
	/**
	 * Gets a list of allergies that a patient has
	 * 
	 * @param patient the patient
	 * @return the allergy list
	 */
	public List<Allergy> getAllergies(Patient patient);
	
	/**
	 * Gets a patient's allergy status
	 * 
	 * @param patient the patient
	 * @return the allergy status
	 */
	public String getAllergyStatus(Patient patient);
	
	/**
	 * Saves patient allergies to the database.
	 * 
	 * @param patient the patient
	 * @param allergies the allergies
	 * @return the saved allergies
	 */
	public Allergies saveAllergies(Patient patient, Allergies allergies);
	
	/**
	 * Gets a allergy matching the given allergyId
	 * 
	 * @param allergyId of allergy to return
	 * @return the allergy matching the given allergyId
	 */
	public Allergy getAllergy(Integer allergyId);
	
	
	/**
	 * Gets a allergy matching the given uuid
	 * 
	 * @param uuid of allergy to return
	 * @since 2.0
	 * @return the allergy matching the given uuid
	 */
	public Allergy getAllergyByUuid(String uuid);
	
	/**
	 * Saves an allergy to the database
	 * 
	 * @param allergy the allergy to save
	 * @return the saved allergy
	 */
	public Allergy saveAllergy(Allergy allergy);
	
}
