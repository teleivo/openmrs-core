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
import java.util.Locale;

import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.OrderFrequency;
import org.openmrs.OrderGroup;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.db.OrderDAO;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.PrivilegeConstants;

/**
 * Contains methods pertaining to creating/deleting/voiding Orders
 */
public interface OrderService extends OpenmrsService {
	
	
	public static final String PARALLEL_ORDERS = "PARALLEL_ORDERS";
	
	/**
	 * Setter for the Order data access object. The dao is used for saving and getting orders
	 * to/from the database
	 * 
	 * @param dao The data access object to use
	 */
	public void setOrderDAO(OrderDAO dao);
	
	/**
	 * Save or update the given <code>order</code> in the database. If the OrderType for the order
	 * is not specified, then it will be set to the one set on the OrderContext if any, if none
	 * exists on the orderContext, then it will be set to the one associated to the ConceptClass of
	 * the ordered concept otherwise the save fails. If the CareSetting field of the order is not
	 * specified then it will default to the one set on the passed in OrderContext if any otherwise
	 * the save fails.
	 * 
	 * @param order the Order to save
	 * @param orderContext the OrderContext object
	 * @return the Order that was saved
	 * @throws APIException
	 *         exists
	 *         settings exists
	 *         settings exists
	 *         concept
	 *         same drug
	 */
	@Authorized({ PrivilegeConstants.EDIT_ORDERS, PrivilegeConstants.ADD_ORDERS })
	public Order saveOrder(Order order, OrderContext orderContext) throws APIException;
	
	/**
	 * Save or update the given retrospective <code>order</code> in the database. If the OrderType
	 * for the order is not specified, then it will be set to the one set on the OrderContext if
	 * any, if none exists on the orderContext, then it will be set to the one associated to the
	 * ConceptClass of the ordered concept otherwise the save fails. If the CareSetting field of the
	 * order is not specified then it will default to the one set on the passed in OrderContext if
	 * any otherwise the save fails. Retrospective entry of orders can affect downstream systems
	 * that acts on orders created. Orders cannot be stopped if they are already stopped in
	 * retrospective entry.
	 *
	 * @param order the Order to save
	 * @param orderContext the OrderContext object
	 * @return the Order that was saved
	 * @throws APIException
	 * @see #saveOrder(Order, OrderContext)
	 */
	@Authorized({ PrivilegeConstants.EDIT_ORDERS, PrivilegeConstants.ADD_ORDERS })
	public Order saveRetrospectiveOrder(Order order, OrderContext orderContext);
	
	/**
	 * Completely delete an order from the database. This should not typically be used unless
	 * desperately needed. Most orders should just be voided. See {@link #voidOrder(Order, String)}
	 * 
	 * @param order The Order to remove from the system
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDERS)
	public void purgeOrder(Order order) throws APIException;
	
	/**
	 * Completely delete an order from the database. This should not typically be used unless
	 * desperately needed. Most orders should just be voided. See {@link #voidOrder(Order, String)}
	 * This method is different from purgeOrder(Order order) above: If param cascade is false will
	 * completely delete an order from the database period If param cascade is true will completely
	 * delete an order from the database and delete any Obs that references the Order.
	 * 
	 * @param order The Order to remove from the system
	 * @param cascade
	 * @throws APIException
	 * @since 1.9.4
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDERS)
	public void purgeOrder(Order order, boolean cascade) throws APIException;
	
	/**
	 * Mark an order as voided. This functionally removes the Order from the system while keeping a
	 * semblance
	 * 
	 * @param voidReason String reason
	 * @param order Order to void
	 * @return the Order that was voided
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.DELETE_ORDERS)
	public Order voidOrder(Order order, String voidReason) throws APIException;
	
	/**
	 * Get order by internal primary key identifier
	 * 
	 * @param orderId internal order identifier
	 * @return order with given internal identifier
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getOrder(Integer orderId) throws APIException;
	
	/**
	 * Get Order by its UUID
	 * 
	 * @param uuid
	 * @return order or null
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getOrderByUuid(String uuid) throws APIException;
	
	/**
	 * Get discontinuation order for the given order, it is the un voided discontinuation order with
	 * a previousOrder that matches the specified order.
	 * 
	 * @param order
	 * @return the discontinuation order or null if none
	 * @throws APIException
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getDiscontinuationOrder(Order order) throws APIException;
	
	/**
	 * Get revision order for the given order, it is the order with the changes that was created as
	 * a replacement for the specified order. In other words, it is the un voided revise order with
	 * a previousOrder that matches the specified order.
	 * 
	 * @param order
	 * @return the revision order or null if none
	 * @throws APIException
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getRevisionOrder(Order order) throws APIException;
	
	/**
	 * Gets all Orders that match the specified parameters excluding discontinuation orders
	 * 
	 * @param patient the patient to match on
	 * @param careSetting the CareSetting to match on
	 * @param orderType The OrderType to match on
	 * @param includeVoided Specifies whether voided orders should be included or not
	 * @return list of Orders matching the parameters
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getOrders(Patient patient, CareSetting careSetting, OrderType orderType, boolean includeVoided);
	
	/**
	 * Gets all orders for the specified patient including discontinuation orders
	 * 
	 * @param patient the patient to match on
	 * @return list of matching {@link org.openmrs.Order}
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getAllOrdersByPatient(Patient patient);
	
	/**
	 * Unvoid order record. Reverse a previous call to {@link #voidOrder(Order, String)}
	 * 
	 * @param order order to be unvoided
	 * @return the Order that was unvoided
	 */
	@Authorized(PrivilegeConstants.DELETE_ORDERS)
	public Order unvoidOrder(Order order) throws APIException;
	
	/**
	 * Gets the order identified by a given order number
	 * 
	 * @param orderNumber the order number
	 * @return the order object
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public Order getOrderByOrderNumber(String orderNumber);
	
	/**
	 * Gets all Order objects that use this Concept for a given patient. Orders will be returned in
	 * the order in which they occurred, i.e. sorted by startDate starting with the latest
	 * 
	 * @param patient the patient.
	 * @param concept the concept.
	 * @return the list of orders.
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getOrderHistoryByConcept(Patient patient, Concept concept);
	
	/**
	 * Gets the next available order number seed
	 * 
	 * @return the order number seed
	 */
	@Authorized(PrivilegeConstants.ADD_ORDERS)
	public Long getNextOrderNumberSeedSequenceValue();
	
	/**
	 * Gets the order matching the specified order number and its previous orders in the ordering
	 * they occurred, i.e if this order has a previous order, fetch it and if it also has a previous
	 * order then fetch it until the original one with no previous order is reached
	 * 
	 * @param orderNumber the order number whose history to get
	 * @return a list of orders for given order number
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getOrderHistoryByOrderNumber(String orderNumber);
	
	/**
	 * Gets all active orders for the specified patient matching the specified CareSetting,
	 * OrderType as of the specified date. Below is the criteria for determining an active order:
	 * <pre>
	 * - Not voided
	 * - Not a discontinuation Order i.e one where action != Action#DISCONTINUE
	 * - dateActivated is before or equal to asOfDate
	 * - dateStopped and autoExpireDate are both null OR if it has dateStopped, then it should be
	 * after asOfDate OR if it has autoExpireDate, then it should be after asOfDate. NOTE: If both
	 * dateStopped and autoExpireDate are set then dateStopped wins because an order can never
	 * expire and then stopped later i.e. you stop an order that hasn't yet expired
	 * </pre>
	 * 
	 * @param patient the patient
	 * @param orderType The OrderType to match
	 * @param careSetting the care setting, returns all ignoring care setting if value is null
	 * @param asOfDate defaults to current time
	 * @return all active orders for given patient parameters
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public List<Order> getActiveOrders(Patient patient, OrderType orderType, CareSetting careSetting, Date asOfDate);
	
	/**
	 * Retrieve care setting
	 * 
	 * @param careSettingId
	 * @return the care setting
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public CareSetting getCareSetting(Integer careSettingId);
	
	/**
	 * Gets the CareSetting with the specified uuid
	 * 
	 * @param uuid the uuid to match on
	 * @return CareSetting
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public CareSetting getCareSettingByUuid(String uuid);
	
	/**
	 * Gets the CareSetting with the specified name
	 * 
	 * @param name the name to match on
	 * @return CareSetting
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public CareSetting getCareSettingByName(String name);
	
	/**
	 * Gets all non retired CareSettings if includeRetired is set to true otherwise retired ones are
	 * included too
	 * 
	 * @param includeRetired specifies whether retired care settings should be returned or not
	 * @return A List of CareSettings
	 */
	@Authorized(PrivilegeConstants.GET_CARE_SETTINGS)
	public List<CareSetting> getCareSettings(boolean includeRetired);
	
	/**
	 * Gets OrderType that matches the specified name
	 * 
	 * @param orderTypeName the name to match against
	 * @return OrderType
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByName(String orderTypeName);
	
	/**
	 * Gets OrderFrequency that matches the specified orderFrequencyId
	 * 
	 * @param orderFrequencyId the id to match against
	 * @return OrderFrequency
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public OrderFrequency getOrderFrequency(Integer orderFrequencyId);
	
	/**
	 * Gets OrderFrequency that matches the specified uuid
	 * 
	 * @param uuid the uuid to match against
	 * @return OrderFrequency
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public OrderFrequency getOrderFrequencyByUuid(String uuid);
	
	/**
	 * Gets an OrderFrequency that matches the specified concept
	 * 
	 * @param concept the concept to match against
	 * @return OrderFrequency
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public OrderFrequency getOrderFrequencyByConcept(Concept concept);
	
	/**
	 * Gets all order frequencies
	 * 
	 * @return List&lt;OrderFrequency&gt;
	 * @since 1.10
	 * @param includeRetired specifies whether retired ones should be included or not
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public List<OrderFrequency> getOrderFrequencies(boolean includeRetired);
	
	/**
	 * Gets all non retired order frequencies associated to concepts that match the specified search
	 * phrase
	 * 
	 * @param searchPhrase The string to match on
	 * @param locale The locale to match on when searching in associated concept names
	 * @param exactLocale If false then order frequencies associated to concepts with names in a
	 *            broader locale will be matched e.g in case en_GB is passed in then en will be
	 *            matched
	 * @param includeRetired Specifies if retired order frequencies that match should be included or
	 *            not
	 * @return List&lt;OrderFrequency&gt;
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_FREQUENCIES)
	public List<OrderFrequency> getOrderFrequencies(String searchPhrase, Locale locale, boolean exactLocale,
	        boolean includeRetired);
	
	/**
	 * Discontinues an order. Creates a new order that discontinues the orderToDiscontinue
	 * 
	 * @param orderToDiscontinue
	 * @param reasonCoded
	 * @param discontinueDate
	 * @param orderer
	 * @param encounter
	 * @return the new order that discontinued orderToDiscontinue
	 * @throws APIException if the <code>action</code> of orderToDiscontinue is
	 *             <code>Order.Action.DISCONTINUE</code>
	 * @since 1.10
	 */
	@Authorized({ PrivilegeConstants.ADD_ORDERS, PrivilegeConstants.EDIT_ORDERS })
	public Order discontinueOrder(Order orderToDiscontinue, Concept reasonCoded, Date discontinueDate, Provider orderer,
	        Encounter encounter);
	
	/**
	 * Discontinues an order. Creates a new order that discontinues the orderToDiscontinue.
	 * 
	 * @param orderToDiscontinue
	 * @param reasonNonCoded
	 * @param discontinueDate
	 * @param orderer
	 * @param encounter
	 * @return the new order that discontinued orderToDiscontinue
	 * @throws APIException if the <code>action</code> of orderToDiscontinue is
	 *             <code>Order.Action.DISCONTINUE</code>
	 * @since 1.10
	 */
	@Authorized({ PrivilegeConstants.ADD_ORDERS, PrivilegeConstants.EDIT_ORDERS })
	public Order discontinueOrder(Order orderToDiscontinue, String reasonNonCoded, Date discontinueDate, Provider orderer,
	        Encounter encounter);
	
	/**
	 * Creates or updates the given order frequency in the database
	 * 
	 * @param orderFrequency the order frequency to save
	 * @return the order frequency created/saved
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_FREQUENCIES)
	public OrderFrequency saveOrderFrequency(OrderFrequency orderFrequency) throws APIException;
	
	/**
	 * Retires the given order frequency in the database
	 * 
	 * @param orderFrequency the order frequency to retire
	 * @param reason the retire reason
	 * @return the retired order frequency
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_FREQUENCIES)
	public OrderFrequency retireOrderFrequency(OrderFrequency orderFrequency, String reason);
	
	/**
	 * Restores an order frequency that was previously retired in the database
	 * 
	 * @param orderFrequency the order frequency to unretire
	 * @return the unretired order frequency
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_FREQUENCIES)
	public OrderFrequency unretireOrderFrequency(OrderFrequency orderFrequency);
	
	/**
	 * Completely removes an order frequency from the database
	 * 
	 * @param orderFrequency the order frequency to purge
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDER_FREQUENCIES)
	public void purgeOrderFrequency(OrderFrequency orderFrequency) throws APIException;
	
	/**
	 * Get OrderType by orderTypeId
	 * 
	 * @param orderTypeId the orderTypeId to match on
	 * @since 1.10
	 * @return order type object associated with given id
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderType(Integer orderTypeId);
	
	/**
	 * Get OrderType by uuid
	 * 
	 * @param uuid the uuid to match on
	 * @since 1.10
	 * @return order type object associated with given uuid
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByUuid(String uuid);
	
	/**
	 * Get all order types, if includeRetired is set to true then retired ones will be included
	 * otherwise not
	 * 
	 * @param includeRetired boolean flag which indicate search needs to look at retired order types
	 *            or not
	 * @return list of order types
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public List<OrderType> getOrderTypes(boolean includeRetired);
	
	/**
	 * Creates or updates the given order type in the database
	 * 
	 * @param orderType the order type to save
	 * @return the order type created/saved
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	public OrderType saveOrderType(OrderType orderType);
	
	/**
	 * Completely removes an order type from the database
	 * 
	 * @param orderType the order type to purge
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.PURGE_ORDER_TYPES)
	public void purgeOrderType(OrderType orderType) throws APIException;
	
	/**
	 * Retires the given order type in the database
	 * 
	 * @param orderType the order type to retire
	 * @param reason the retire reason
	 * @return the retired order type
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	public OrderType retireOrderType(OrderType orderType, String reason);
	
	/**
	 * Restores an order type that was previously retired in the database
	 * 
	 * @param orderType the order type to unretire
	 * @return the unretired order type
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.MANAGE_ORDER_TYPES)
	public OrderType unretireOrderType(OrderType orderType);
	
	/**
	 * Returns all descendants of a given order type for example Given TEST will get back LAB TEST
	 * and RADIOLOGY TEST; and Given LAB TEST, will might get back SEROLOGY, MICROBIOLOGY, and
	 * CHEMISTRY
	 * 
	 * @param orderType the order type which needs to search for its' dependencies
	 * @param includeRetired boolean flag for include retired order types or not
	 * @return list of order type which matches the given order type
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public List<OrderType> getSubtypes(OrderType orderType, boolean includeRetired);
	
	/**
	 * Gets the order type mapped to a given concept class
	 * 
	 * @param conceptClass the concept class
	 * @return the matching order type
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByConceptClass(ConceptClass conceptClass);
	
	/**
	 * Gets the order type mapped to a given concept
	 * 
	 * @param concept the concept
	 * @return the matching order type
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_ORDER_TYPES)
	public OrderType getOrderTypeByConcept(Concept concept);
	
	/**
	 * Gets the possible drug routes, i.e the set members for the concept that matches the uuid
	 * specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DRUG_ROUTES_CONCEPT_UUID}
	 * 
	 * @return concept list of drug routes
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDrugRoutes();
	
	/**
	 * Gets the possible drug dosing units, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DRUG_DOSING_UNITS_CONCEPT_UUID}
	 * 
	 * @return concept list of drug dosing units
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDrugDosingUnits();
	
	/**
	 * Gets the possible units of dispensing, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DRUG_DISPENSING_UNITS_CONCEPT_UUID}
	 * 
	 * @return concept list of units of dispensing
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDrugDispensingUnits();
	
	/**
	 * Gets the possible units of duration, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_DURATION_UNITS_CONCEPT_UUID}
	 * 
	 * @return concept list of units of duration
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getDurationUnits();
	
	/**
	 * Gets the possible test specimen sources, i.e the set members for the concept that matches the
	 * uuid specified as the value for the global property
	 * {@link OpenmrsConstants#GP_TEST_SPECIMEN_SOURCES_CONCEPT_UUID}
	 * 
	 * @return concept list of specimen sources
	 * @since 1.10
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public List<Concept> getTestSpecimenSources();
	
	/**
	 * Gets the non coded drug concept, i.e the concept that matches the uuid specified as the value
	 * for the global property {@link OpenmrsConstants#GP_DRUG_NON_CODED_CONCEPT_UUID
	 *
	 * @return concept of non coded drug
	 * @since 1.12
	 */
	@Authorized(PrivilegeConstants.GET_CONCEPTS)
	public Concept getNonCodedDrugConcept();
	
	/**
	 * Fetches the OrderGroup By Uuid.
	 * 
	 * @param uuid Uuid Of the OrderGroup
	 * @return saved OrderGroup
	 * @since 1.12
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public OrderGroup getOrderGroupByUuid(String uuid) throws APIException;
	
	/**
	 * Fetches the OrderGroup by Id.
	 * 
	 * @param orderGroupId Id of the OrderGroup
	 * @return saved OrderGroup
	 * @since 1.12
	 * @throws APIException
	 */
	@Authorized(PrivilegeConstants.GET_ORDERS)
	public OrderGroup getOrderGroup(Integer orderGroupId) throws APIException;
	
	/**
	 * Saves the orderGroup. It also saves the list of orders that are present within the
	 * orderGroup.
	 *
	 * @param orderGroup the orderGroup to be saved
	 * @since 1.12
	 * @throws APIException
	 */
	@Authorized({ PrivilegeConstants.EDIT_ORDERS, PrivilegeConstants.ADD_ORDERS })
	public OrderGroup saveOrderGroup(OrderGroup orderGroup) throws APIException;
}
