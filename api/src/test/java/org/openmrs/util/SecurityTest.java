/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the methods on the {@link Security} class
 */
public class SecurityTest {
	
	private static final int HASH_LENGTH = 128;
	
	/**
	 * @see Security#encodeString(String)
	 */
	@Test
	public void encodeString_shouldEncodeStringsTo128Characters() {
		String hash = Security.encodeString("test" + "c788c6ad82a157b712392ca695dfcf2eed193d7f");
		Assert.assertEquals(HASH_LENGTH, hash.length());
	}
	
	/**
	 * @see Security#encodeString(String)
	 */
	@Test
	public void encodeString_shouldEncodeStringsToXCharactersWithXCharactersSalt() {
		String hash = Security.encodeString("test" + Security.getRandomToken());
		Assert.assertEquals(HASH_LENGTH, hash.length());
	}
	
	/**
	 * @see Security#hashMatches(String,String)
	 */
	@Test
	public void hashMatches_shouldMatchStringsHashedWithSha1Algorithm() {
		Assert.assertTrue(Security.hashMatches("4a1750c8607d0fa237de36c6305715c223415189", "test"
		        + "c788c6ad82a157b712392ca695dfcf2eed193d7f"));
	}
	
	/**
	 * @see Security#hashMatches(String,String)
	 */
	@Test
	public void hashMatches_shouldMatchStringsHashedWithSha512AlgorithmAnd128CharactersSalt() {
		String password = "1d1436658853aceceadd72e92f1ae9089a0000fbb38cea519ce34eae9f28523930ecb212177dbd607d83dc275fde3e9ca648deb557d503ad0bcd01a955a394b2";
		String passwordToHash = "test"
		        + "0d7bb319434295261601202e14494b959cdd69c6ceb54ee3890e176ae780ce9edf797f48afde5f39906a6bd75b8a5feeac8f5339615acf7429c7dda85220d329";
		Assert.assertTrue(Security.hashMatches(password, passwordToHash));
	}
	
	/**
	 * @see Security#hashMatches(String,String)
	 */
	@Test
	public void hashMatches_shouldMatchStringsHashedWithIncorrectSha1Algorithm() {
		Assert.assertTrue(Security.hashMatches("4a1750c8607dfa237de36c6305715c223415189", "test"
		        + "c788c6ad82a157b712392ca695dfcf2eed193d7f"));
	}
}
