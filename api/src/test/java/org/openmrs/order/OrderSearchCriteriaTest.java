/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.order;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Visit;

/**
 * Tests {@link OrderSearchCriteria}
 */
public class OrderSearchCriteriaTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see OrderSearchCriteria#Build#build()
	 * @verifies create a new order search criteria populated with data from builder
	 */
	@Test
	public void build_shouldCreateANewOrderSearchCriteriaPopulatedWithDataFromBuilder() {
		
		//given
		Visit visit = new Visit();
		
		OrderSearchCriteria orderSearchCriteria = new OrderSearchCriteria.Builder().addVisit(visit).build();
		
		assertNotNull(orderSearchCriteria);
		assertThat(orderSearchCriteria.getVisit(), is(visit));
	}
	
	/**
	 * @see OrderSearchCriteria#Build#build()
	 * @verifies throw an IllegalStateException if visit is null
	 */
	@Test
	public void build_shouldThrowIllegalStateExceptionGivenNull() {
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("visit can not be null");
		new OrderSearchCriteria.Builder().build();
	}
	
}
