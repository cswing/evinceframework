/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evinceframework.membership;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

/**
 * A utility class used to hash passwords and to compare a password with a known hash.
 * 
 * @author Craig Swing
 */
@Component
public class PasswordHasher {

	private int hashOperationCount = 1000;
	private String algorithm = "SHA-256";
	private String encoding = "UTF-8";
	private Base64 encoder = new Base64();

	/**
	 * Compares the input to the known hashString by hashing the input and doing a comparison.
	 * 
	 * @param input The input to test.
	 * @param salt The salt to use when hashing the input.
	 * @param hashString The value to compare against.
	 * @return true if the hash of the input matches the given hash, otherwise false.
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public boolean verifyPassword(String input, byte[] salt, String hashString)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		return getHash(input, salt).equals(hashString);		
	}

	/**
	 * Hashes an input using the salt.
	 * 
	 * @param input The value to hash.
	 * @param salt A salt to use when hashing a password.
	 * @return A hash of the given input. 
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public String getHash(String input, byte[] salt) 
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		digest.reset();
		digest.update(salt);
		
		byte[] inputBytes = digest.digest(input.getBytes(encoding));
		for (int i = 0; i < hashOperationCount; i++) {
			digest.reset();
			inputBytes = digest.digest(inputBytes);
		}
		
		return new String(encoder.encode(inputBytes));
	}
}