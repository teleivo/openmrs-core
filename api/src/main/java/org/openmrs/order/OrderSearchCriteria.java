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

import org.openmrs.Visit;

/**
 * Encapsulates search criteria used for searching <code>Order</code> objects in the
 * <code>OrderService</code>
 * 
 * @since
 */
public class OrderSearchCriteria {
	
	private Visit visit;
	
	/**
	 * Builder class to construct an <code>OrderSearchCriteria</code>
	 */
	static class Builder {
		
		private Visit visit;
		
		/**
		 * 
		 * Add <code>Visit</code> to the <code>Builder</code>
		 * 
		 * @param visit visit to be added to the builder
		 * @return the Buider object
		 */
		Builder addVisit(Visit visit) {
			this.visit = visit;
			return this;
		}
		
		/**
		 * Builds an <code>OrderSearchCriteria</code>
		 * 
		 * @return new OrderSearchCriteria created from Builder
		 * @should create a new order search criteria populated with data from builder
		 * @should throw an IllegalStateException if visit is null
		 */
		OrderSearchCriteria build() {
			
			if (visit == null)
				throw new IllegalStateException("visit can not be null");
			
			return new OrderSearchCriteria(this);
		}
	}
	
	private OrderSearchCriteria(Builder builder) {
		this.visit = builder.visit;
	}
	
	/**
	 * Visit
	 * 
	 * @return visit
	 */
	public Visit getVisit() {
		return this.visit;
	}
}
