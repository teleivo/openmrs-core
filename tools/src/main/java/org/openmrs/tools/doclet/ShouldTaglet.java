/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.tools.doclet;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 */
public class ShouldTaglet implements Taglet {
	
	private static final String NAME = "should";
	
	private static final String HEADER = "Expected Behaviour:";
	
	/**
	 * Return the name of this custom tag.
	 */
	@Override
	public String getName() {
		return NAME;
	}
	
	/**
	 * 
	 *         otherwise.
	 */
	@Override
	public boolean inField() {
		return false;
	}
	
	/**
	 * 
	 *         otherwise.
	 */
	@Override
	public boolean inConstructor() {
		return true;
	}
	
	/**
	 * 
	 *         otherwise.
	 */
	@Override
	public boolean inMethod() {
		return true;
	}
	
	/**
	 * 
	 *         otherwise.
	 */
	@Override
	public boolean inOverview() {
		return true;
	}
	
	/**
	 * 
	 *         otherwise.
	 */
	@Override
	public boolean inPackage() {
		return false;
	}
	
	/**
	 * interfaces).
	 * 
	 *         otherwise.
	 */
	@Override
	public boolean inType() {
		return true;
	}
	
	/**
	 * 
	 */
	
	@Override
	public boolean isInlineTag() {
		return false;
	}
	
	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap the map to register this tag to.
	 */
	public static void register(Map tagletMap) {
		ShouldTaglet tag = new ShouldTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}
	
	/**
	 * Given the <code>Tag</code> representation of this custom tag, return its string
	 * representation.
	 * 
	 * @param tag the <code>Tag</code> representation of this custom tag.
	 */
	@Override
	public String toString(Tag tag) {
		return "\n<DT><B>" + HEADER + "</B></DT>\n  <DD>Should " + tag.text() + "</DD>";
	}
	
	/**
	 * Given an array of <code>Tag</code>s representing this custom tag, return its string
	 * representation.
	 * 
	 * @param tags the array of <code>Tag</code>s representing of this custom tag.
	 */
	@Override
	public String toString(Tag[] tags) {
		if (tags.length == 0) {
			return null;
		}
		StringBuilder result = new StringBuilder("\n<DT><B>").append(HEADER).append("</B></DT>");
		for (int i = 0; i < tags.length; i++) {
			result.append("\n  <DD>Should ").append(tags[i].text()).append("</DD>");
		}
		return result.toString();
	}
}
