/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.propertyeditor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.test.BaseContextMockTest;

public class UserEditorTest extends BaseContextMockTest {
	
	private UserEditor editor;
	
	private static final String EXISTING_ID = "501";
	
	private static final String NON_EXISTING_ID = "999999";
	
	private static final String EXISTING_UUID = "c1d8f5c2-e131-11de-babe-001e378eb67e";
	
	private static final String NON_EXISTING_UUID = "9999xxxx-e131-11de-babe-001e378eb67e";
	
	@Mock
	private UserService userService;
	
	@Before
	public void setUp() {
		editor = new UserEditor(userService);
	}
	
	@Test
	public void shouldSetTheEditorValueToNullIfGivenNull() {
		
		editor.setAsText(null);
		
		assertNull(editor.getValue());
	}
	
	@Test
	public void shouldSetTheEditorValueToNullIfGivenAnEmptyString() {
		
		editor.setAsText("  ");
		
		assertNull(editor.getValue());
	}
	
	@Test
	public void shouldSetTheEditorValueToObjectAssociatedWithGivenId() {
		
		User u = new User();
		u.setId(Integer.valueOf(EXISTING_ID));
		when(userService.getUser(Integer.valueOf(EXISTING_ID))).thenReturn(u);

		editor.setAsText(EXISTING_ID);
		
		assertThat(editor.getValue(), is(u));
	}
	
	@Test
	public void shouldSetTheEditorValueToNullIfGivenIdDoesNotExist() {
		
		when(userService.getUser(any())).thenReturn(null);
		
		editor.setAsText(NON_EXISTING_ID);
		
		assertNull(editor.getValue());
	}
	
	@Test
	public void shouldSetTheEditorValueToObjectAssociatedWithGivenUuid() {
		
		User u = new User();
		u.setUuid(EXISTING_UUID);
		when(userService.getUserByUuid(EXISTING_UUID)).thenReturn(u);
		
		editor.setAsText(EXISTING_UUID);
		
		assertThat(editor.getValue(), is(u));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailToSetTheEditorValueIfGivenUuidDoesNotExist() {
		
		when(userService.getUser(any())).thenReturn(null);
		when(userService.getUserByUuid(any())).thenReturn(null);
		
		editor.setAsText(NON_EXISTING_UUID);
	}
	
	@Test
	public void shouldReturnEmptyStringIfValueIsNull() {
		
		assertThat(editor.getAsText(), is(""));
	}
	
	@Test
	public void shouldReturnTheObjectIdIfValueIsNotNull() {
		
		User u = new User();
		u.setId(Integer.valueOf(EXISTING_ID));
		editor.setValue(u);
		
		assertThat(editor.getAsText(), is(EXISTING_ID));
	}
}
