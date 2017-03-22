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

import org.openmrs.Allergies;
import org.openmrs.Allergy;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.comparator.PatientIdentifierTypeDefaultComparator;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.person.PersonMergeLogData;
import org.openmrs.serialization.SerializationException;
import org.openmrs.util.PrivilegeConstants;

/**
 * Contains methods pertaining to Patients in the system
 * 
 * <pre>
 * Usage:
 * List&lt;Patient&gt; patients = Context.getPatientService().getAllPatients();
 * </pre>
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.Patient
 */
public interface PatientService extends OpenmrsService {
	
	/**
	 * Sets the DAO for this service. This is done by DI and Spring. See the
	 * applicationContext-service.xml definition file.
	 * 
	 * @param dao DAO for this service
	 */
	public void setPatientDAO(PatientDAO dao);
		
	/**
	 * Saved the given <code>patient</code> to the database
	 * 
	 * @param patient patient to be created or updated
	 * @return patient who was created or updated
	 * @throws APIException
	 *         regression 1375
	 */
	@Authorized( { PrivilegeConstants.ADD_PATIENTS, PrivilegeConstants.EDIT_PATIENTS })
	public Patient savePatient(Patient patient) throws APIException;
	
	/**
	 * Get patient by internal identifier
	 * 
	 * @param patientId internal patient identifier
	 * @return patient with given internal identifier
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public Patient getPatient(Integer patientId) throws APIException;
	
	/**
	 * Get patient by internal identifier. If this id is for an existing person then instantiates a
	 * new patient from that person, copying over all the fields.
	 * 
	 * @param patientOrPersonId
	 * @return a new unsaved patient or null if person or patient is not found
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	Patient getPatientOrPromotePerson(Integer patientOrPersonId) throws APIException;
	
	/**
	 * Get patient by universally unique identifier.
	 * 
	 * @param uuid universally unique identifier
	 * @return the patient that matches the uuid
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public Patient getPatientByUuid(String uuid) throws APIException;
	
	/**
	 * Get patient identifier by universally unique identifier.
	 * 
	 * @param uuid universally unique identifier
	 * @return the patient identifier that matches the uuid
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_IDENTIFIERS })
	public PatientIdentifier getPatientIdentifierByUuid(String uuid) throws APIException;
	
	/**
	 * Returns all non voided patients in the system
	 * 
	 * @return non voided patients in the system
	 * @see #getAllPatients(boolean)
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getAllPatients() throws APIException;
	
	/**
	 * Returns patients in the system
	 * 
	 * @param includeVoided if false, will limit the search to non-voided patients
	 * @return patients in the system
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getAllPatients(boolean includeVoided) throws APIException;
		
	/**
	 * Get patients based on given criteria The identifier is matched with the regex
	 * <code>OpenmrsConstants.PATIENT_IDENTIFIER_REGEX</code> All parameters are optional and
	 * nullable. If null, it is not included in the search. Will not return voided patients
	 * 
	 * @param name (optional) this is a slight break from the norm, patients with a partial match on
	 *            this name will be returned
	 * @param identifier (optional) only patients with a matching identifier are returned
	 * @param identifierTypes (optional) the PatientIdentifierTypes to restrict to
	 * @param matchIdentifierExactly (required) if true, then the given <code>identifier</code> must
	 *            equal the id in the database. if false, then the identifier is 'searched' for by
	 *            using a regular expression
	 * @return patients that matched the given criteria (and are not voided)
	 * @throws APIException
	 *         exactly equals false
	 *         equals true
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	        boolean matchIdentifierExactly) throws APIException;
	
	/**
	 * Void patient record (functionally delete patient from system). Voids Person and retires
	 * Users.
	 * 
	 * @param patient patient to be voided
	 * @param reason reason for voiding patient
	 * @return the voided patient
	 */
	@Authorized( { PrivilegeConstants.DELETE_PATIENTS })
	public Patient voidPatient(Patient patient, String reason) throws APIException;
	
	/**
	 * Unvoid patient record. Unvoids Person as well.
	 * 
	 * @param patient patient to be revived
	 * @return the revived Patient
	 */
	@Authorized( { PrivilegeConstants.DELETE_PATIENTS })
	public Patient unvoidPatient(Patient patient) throws APIException;
		
	/**
	 * Delete patient from database. This <b>should not be called</b> except for testing and
	 * administration purposes. Use the void method instead.
	 * 
	 * @param patient patient to be deleted
	 * @throws APIException
	 * @see #voidPatient(org.openmrs.Patient,java.lang.String)
	 */
	@Authorized( { PrivilegeConstants.PURGE_PATIENTS })
	public void purgePatient(Patient patient) throws APIException;
		
	/**
	 * Get all patientIdentifiers that match all of the given criteria Voided identifiers are not
	 * returned
	 * 
	 * @param identifier the full identifier to match on
	 * @param patientIdentifierTypes the type of identifiers to get
	 * @param locations the locations of the identifiers to match
	 * @param patients the patients containing these identifiers
	 * @param isPreferred if true, limits to only preferred identifiers if false, only non
	 *            preferred. if null, ignores preferred status
	 * @return PatientIdentifiers matching these criteria
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_IDENTIFIERS })
	public List<PatientIdentifier> getPatientIdentifiers(String identifier,
	        List<PatientIdentifierType> patientIdentifierTypes, List<Location> locations, List<Patient> patients,
	        Boolean isPreferred) throws APIException;
		
	/**
	 * Create or update a PatientIdentifierType
	 * 
	 * @param patientIdentifierType PatientIdentifierType to create or update
	 * @return the saved type
	 * @throws APIException
	 *         types are locked
	 */
	@Authorized( { PrivilegeConstants.MANAGE_IDENTIFIER_TYPES })
	public PatientIdentifierType savePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws APIException;
		
	/**
	 * Get all patientIdentifier types
	 * <p>
	 * Ordered same as {@link PatientIdentifierTypeDefaultComparator}.
	 * 
	 * @return patientIdentifier types list
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_IDENTIFIER_TYPES })
	public List<PatientIdentifierType> getAllPatientIdentifierTypes() throws APIException;
	
	/**
	 * Get all patientIdentifier types.
	 * <p>
	 * Ordered same as {@link PatientIdentifierTypeDefaultComparator}.
	 * 
	 * @param includeRetired true/false whether retired types should be included
	 * @return patientIdentifier types list
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_IDENTIFIER_TYPES })
	public List<PatientIdentifierType> getAllPatientIdentifierTypes(boolean includeRetired) throws APIException;
	
	/**
	 * Get all patientIdentifier types that match the given criteria
	 * <p>
	 * Ordered same as {@link PatientIdentifierTypeDefaultComparator}.
	 * 
	 * @param name name of the type to match on
	 * @param format the string format to match on
	 * @param required if true, limits to only identifiers marked as required if false, only non
	 *            required. if null, ignores required bit
	 * @param hasCheckDigit if true, limits to only check digit'd identifiers if false, only non
	 *            checkdigit'd. if null, ignores checkDigit
	 * @return patientIdentifier types list
	 * @throws APIException
	 *         false
	 */
	@Authorized( { PrivilegeConstants.GET_IDENTIFIER_TYPES })
	public List<PatientIdentifierType> getPatientIdentifierTypes(String name, String format, Boolean required,
	        Boolean hasCheckDigit) throws APIException;
	
	/**
	 * Get patientIdentifierType by internal identifier
	 * 
	 * @param patientIdentifierTypeId
	 * @return patientIdentifierType with specified internal identifier
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_IDENTIFIER_TYPES })
	public PatientIdentifierType getPatientIdentifierType(Integer patientIdentifierTypeId) throws APIException;
	
	/**
	 * Get patient identifierType by universally unique identifier
	 * 
	 * @param uuid
	 * @return patientIdentifierType with specified internal identifier
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_IDENTIFIER_TYPES })
	public PatientIdentifierType getPatientIdentifierTypeByUuid(String uuid) throws APIException;
		
	/**
	 * Get patientIdentifierType by exact name
	 * 
	 * @param name
	 * @return patientIdentifierType with given name
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_IDENTIFIER_TYPES })
	public PatientIdentifierType getPatientIdentifierTypeByName(String name) throws APIException;
	
	/**
	 * Retire a type of patient identifier
	 * 
	 * @param patientIdentifierType type of patient identifier to be retired
	 * @param reason the reason to retire this identifier type
	 * @return the retired type
	 * @throws APIException
	 *         types are locked
	 */
	@Authorized( { PrivilegeConstants.MANAGE_IDENTIFIER_TYPES })
	public PatientIdentifierType retirePatientIdentifierType(PatientIdentifierType patientIdentifierType, String reason)
	        throws APIException;
	
	/**
	 * Unretire a type of patient identifier
	 * 
	 * @param patientIdentifierType type of patient identifier to be unretired
	 * @return the unretired type
	 * @throws APIException
	 *         identifier types are locked
	 */
	@Authorized( { PrivilegeConstants.MANAGE_IDENTIFIER_TYPES })
	public PatientIdentifierType unretirePatientIdentifierType(PatientIdentifierType patientIdentifierType)
	        throws APIException;
	
	/**
	 * Purge PatientIdentifierType (cannot be undone)
	 * 
	 * @param patientIdentifierType PatientIdentifierType to purge from the database
	 * @throws APIException
	 *         types are locked
	 */
	@Authorized( { PrivilegeConstants.PURGE_IDENTIFIER_TYPES })
	public void purgePatientIdentifierType(PatientIdentifierType patientIdentifierType) throws APIException;
		
	/**
	 * Convenience method to validate all identifiers for a given patient
	 * 
	 * @param patient patient for which to validate identifiers
	 * @see #checkPatientIdentifiers(Patient)
	 * @throws PatientIdentifierException if one or more of the identifiers are invalid
	 *         identifiers
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_IDENTIFIERS })
	public void checkPatientIdentifiers(Patient patient) throws PatientIdentifierException;
		
	/**
	 * Generic search on patients based on the given string. Implementations can use this string to
	 * search on name, identifier, etc Voided patients are not returned in search results
	 * 
	 * @param query the string to search on
	 * @return a list of matching Patients
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getPatients(String query) throws APIException;
	
	/**
	 * Generic search on patients based on the given string and returns a specific number of them
	 * from the specified starting position. Implementations can use this string to search on name,
	 * identifier, searchable person attributes etc. Voided patients are not returned in search
	 * results. If start is 0 and length is not specified, then all matches are returned
	 * 
	 * @param query the string to search on
	 * @param start the starting index
	 * @param length the number of patients to return
	 * @return a list of matching Patients
	 * @throws APIException
	 * @since 1.8
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getPatients(String query, Integer start, Integer length) throws APIException;
	
	/**
	 * @param query the string to search on
	 * @param includeVoided true/false whether or not to included voided patients
	 * @param start the starting index
	 * @param length the number of patients to return
	 * @return a list of matching Patients
	 * @throws APIException
	 * @since 1.11
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getPatients(String query, boolean includeVoided, Integer start, Integer length) throws APIException;
		
	/**
	 * This method tries to find a patient in the database given the attributes on the given
	 * <code>patientToMatch</code> object. Assumes there could be a PersonAttribute on this Patient
	 * with PersonAttributeType.name = "Other Matching Information". This PersonAttribute has a
	 * "value" that is just key value pairs in the form of key:value;nextkey:nextvalue;
	 * 
	 * @param patientToMatch
	 * @return null if no match found, a fresh patient object from the db if is found
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public Patient getPatientByExample(Patient patientToMatch) throws APIException;
		
	/**
	 * Search the database for patients that both share the given attributes. Each attribute that is
	 * passed in must be identical to what is stored for at least one other patient for both
	 * patients to be returned.
	 * 
	 * @param attributes attributes on a Person or Patient object. similar to: [gender, givenName,
	 *            middleName, familyName]
	 * @return list of patients that match other patients
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getDuplicatePatientsByAttributes(List<String> attributes) throws APIException;
	
	/**
	 * Convenience method to join two patients' information into one record.
	 * <ol>
	 * <li>Moves object (encounters/obs) pointing to <code>nonPreferred</code> to point at
	 * <code>preferred</code></li>
	 * <li>Copies data (gender/birthdate/names/ids/etc) from <code>nonPreferred</code> to
	 * <code>preferred</code> IFF the data is missing or null in <code>preferred</code></li>
	 * <li><code>notPreferred</code> is marked as voided</li>
	 * </ol>
	 * 
	 * @param preferred The Patient to merge to
	 * @param notPreferred The Patient to merge from (and then void)
	 * @throws APIException
	 * @throws SerializationException
	 * @see PersonMergeLogData
	 *         patient
	 */
	@Authorized( { PrivilegeConstants.EDIT_PATIENTS })
	public void mergePatients(Patient preferred, Patient notPreferred) throws APIException, SerializationException;
	
	/**
	 * Convenience method to join multiple patients' information into one record.
	 * 
	 * @param preferred
	 * @param notPreferred
	 * @throws APIException
	 * @throws SerializationException
	 */
	public void mergePatients(Patient preferred, List<Patient> notPreferred) throws APIException, SerializationException;
		
	/**
	 * Convenience method to establish that a patient has died. In addition to exiting the patient
	 * from care (see above), this method will also set the appropriate patient characteristics to
	 * indicate that they have died, when they died, etc.
	 * 
	 * @param patient - the patient who has died
	 * @param dateDied - the declared date/time of the patient's death
	 * @param causeOfDeath - the concept that corresponds with the reason the patient died
	 * @param otherReason - if the concept representing the reason is OTHER NON-CODED, and a
	 *            string-based "other" reason is supplied
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.EDIT_PATIENTS })
	public void processDeath(Patient patient, Date dateDied, Concept causeOfDeath, String otherReason) throws APIException;
	
	/**
	 * Convenience method that saves the Obs that indicates when and why the patient died (including
	 * any "other" reason there might be)
	 * 
	 * @param patient - the patient who has died
	 * @param dateDied - the declared date/time of the patient's death
	 * @param causeOfDeath - the concept that corresponds with the reason the patient died
	 * @param otherReason - if the concept representing the reason is OTHER NON-CODED, and a
	 *            string-based "other" reason is supplied
	 * @throws APIException
	 */
	@Authorized(value = { PrivilegeConstants.GET_PATIENTS, PrivilegeConstants.EDIT_OBS }, requireAll = true)
	public void saveCauseOfDeathObs(Patient patient, Date dateDied, Concept causeOfDeath, String otherReason)
	        throws APIException;
	
	/**
	 * Gets an identifier validator matching the given class.
	 * 
	 * @param clazz identifierValidator which validator to get.
	 */
	public IdentifierValidator getIdentifierValidator(Class<IdentifierValidator> clazz);
	
	/**
	 */
	public IdentifierValidator getIdentifierValidator(String pivClassName);
	
	/**
	 * @return the default IdentifierValidator
	 */
	public IdentifierValidator getDefaultIdentifierValidator();
	
	/**
	 * @return All registered PatientIdentifierValidators
	 */
	public Collection<IdentifierValidator> getAllIdentifierValidators();
	
	/**
	 * Checks whether the given patient identifier is already assigned to a patient other than
	 * patientIdentifier.patient
	 * 
	 * @param patientIdentifier the patient identifier to look for in other patients
	 * @return whether or not the identifier is in use by a patient other than
	 *         patientIdentifier.patient
	 *         id
	 *         id
	 *         this id
	 */
	@Authorized(PrivilegeConstants.GET_PATIENTS)
	public boolean isIdentifierInUseByAnotherPatient(PatientIdentifier patientIdentifier);
	
	/**
	 * Returns a patient identifier that matches the given patientIndentifier id
	 * 
	 * @param patientIdentifierId the patientIdentifier id
	 * @return the patientIdentifier matching the Id
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENT_IDENTIFIERS })
	public PatientIdentifier getPatientIdentifier(Integer patientIdentifierId) throws APIException;
	
	/**
	 * Void patient identifier (functionally delete patient identifier from system)
	 * 
	 * @param patientIdentifier patientIdentifier to be voided
	 * @param reason reason for voiding patient identifier
	 * @return the voided patient identifier
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.DELETE_PATIENT_IDENTIFIERS })
	public PatientIdentifier voidPatientIdentifier(PatientIdentifier patientIdentifier, String reason) throws APIException;
	
	/**
	 * Saved the given <code>patientIndentifier</code> to the database
	 * 
	 * @param patientIdentifier patientIndentifier to be created or updated
	 * @return patientIndentifier that was created or updated
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.ADD_PATIENT_IDENTIFIERS, PrivilegeConstants.EDIT_PATIENT_IDENTIFIERS })
	public PatientIdentifier savePatientIdentifier(PatientIdentifier patientIdentifier) throws APIException;
	
	/**
	 * Purge PatientIdentifier (cannot be undone)
	 * 
	 * @param patientIdentifier PatientIdentifier to purge from the database
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.PURGE_PATIENT_IDENTIFIERS })
	public void purgePatientIdentifier(PatientIdentifier patientIdentifier) throws APIException;
	
	/**
	 * Gets allergies for a given patient
	 * 
	 * @param patient the patient
	 * @return the allergies object
	 */
	Allergies getAllergies(Patient patient);
	
	/**
	 * Updates the patient's allergies
	 * 
	 * @param patient the patient
	 * @param allergies the allergies
	 * @return the saved allergies
	 */
	Allergies setAllergies(Patient patient, Allergies allergies);
	
	/**
	 * Returns the Allergy identified by internal Ingerger Id
	 * 
	 * @param allergyListId identifies allergy by internal Ingerger Id
	 * @return the allergy
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_ALLERGIES })
	public Allergy getAllergy(Integer allergyListId) throws APIException;
	
	/**
	 * Returns the Allergy identified by uuid
	 * 
	 * @since 2.0
	 * @param uuid identifies allergy 
	 * @return the allergy matching the given uuid
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.GET_ALLERGIES })
	public Allergy getAllergyByUuid(String uuid) throws APIException;
	
	/**
	 * Creates an AllergyListItem to the Patient's Allergy Active List. Sets the start date to now,
	 * if it is null.
	 * 
	 * @param allergy the Allergy
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.ADD_ALLERGIES, PrivilegeConstants.EDIT_ALLERGIES })
	public void saveAllergy(Allergy allergy) throws APIException;
	
	/**
	 * Resolving the allergy, effectively removes the Allergy from the Patient's Active List by
	 * setting the stop date to now, if null.
	 * 
	 * @param allergy the Allergy
	 * @param reason the reason of remove
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.EDIT_ALLERGIES })
	public void removeAllergy(Allergy allergy, String reason) throws APIException;
	
	/**
	 * Used only in cases where the Allergy was entered by error
	 * 
	 * @param allergy
	 * @param reason
	 * @throws APIException
	 */
	@Authorized( { PrivilegeConstants.DELETE_ALLERGIES })
	public void voidAllergy(Allergy allergy, String reason) throws APIException;
	
	/**
	 * Return the number of unvoided patients with names or patient identifiers or searchable person
	 * attributes starting with or equal to the specified text
	 * 
	 * @param query the string to search on
	 * @return the number of patients matching the given search phrase
	 * @since 1.8
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public Integer getCountOfPatients(String query);
	
	/**
	 * @param query the string to search on
	 * @param includeVoided true/false whether or not to included voided patients
	 * @return the number of patients matching the given search phrase
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public Integer getCountOfPatients(String query, boolean includeVoided);
	
	/**
	 * Get a limited size of patients from a given start index based on given criteria The
	 * identifier is matched with the regex <code>OpenmrsConstants.PATIENT_IDENTIFIER_REGEX</code>
	 * All parameters are optional and nullable. If null, it is not included in the search. Will not
	 * return voided patients
	 * 
	 * @param name (optional) this is a slight break from the norm, patients with a partial match on
	 *            this name will be returned
	 * @param identifier (optional) only patients with a matching identifier are returned
	 * @param identifierTypes (optional) the PatientIdentifierTypes to restrict to
	 * @param matchIdentifierExactly (required) if true, then the given <code>identifier</code> must
	 *            equal the id in the database. if false, then the identifier is 'searched' for by
	 *            using a regular expression
	 * @param start the starting index
	 * @param length the number of patients to return
	 * @return patients that matched the given criteria (and are not voided)
	 * @throws APIException
	 * @since 1.8
	 */
	@Authorized( { PrivilegeConstants.GET_PATIENTS })
	public List<Patient> getPatients(String name, String identifier, List<PatientIdentifierType> identifierTypes,
	        boolean matchIdentifierExactly, Integer start, Integer length) throws APIException;
	
	/**
	 * Check if patient identifier types are locked, and if they are, throws an exception during
	 * manipulation of a patient identifier type
	 * 
	 * @throws PatientIdentifierTypeLockedException
	 */
	public void checkIfPatientIdentifierTypesAreLocked() throws PatientIdentifierTypeLockedException;
}
