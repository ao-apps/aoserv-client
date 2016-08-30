/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2010-2013, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.validator;

import com.aoindustries.aoserv.client.DtoFactory;
import com.aoindustries.util.Base64Coder;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents a hashed password.  May be any of:
 * <ul>
 *   <li>Must not be null</li>
 *   <li>* - will not match anything</li>
 *   <li>Result of SHA1 hash (preferred) - 28 characters</li>
 *   <li>Result of crypt (deprecated) - 13 characters</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class HashedPassword implements
	Comparable<HashedPassword>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.HashedPassword>
{

	private static final long serialVersionUID = 6198625525641344394L;

	/**
	 * Indicates that no password is set.
	 */
	public static final String NO_PASSWORD = "*";

	public static ValidationResult validate(String hashedPassword) {
		// May be null
		if(hashedPassword==null) return new InvalidResult(ApplicationResources.accessor, "HashedPassword.validate.isNull");
		// Be non-empty
		int len = hashedPassword.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "HashedPassword.validate.empty");
		// May be *
		if(!NO_PASSWORD.equals(hashedPassword)) {
			// SHA1
			if(len!=28) {
				// Crypt
				if(len!=13) {
					return new InvalidResult(ApplicationResources.accessor, "HashedPassword.validate.wrongLength");
				}
			}
		}
		return ValidResult.getInstance();
	}

	/**
	 * Performs a one-way hash of the plaintext value using SHA-1.
	 *
	 * @exception  WrappedException  if any problem occurs.
	 */
	public static String hash(String plaintext) throws WrappedException {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(plaintext.getBytes("UTF-8"));
			return new String(Base64Coder.encode(md.digest()));
		} catch(NoSuchAlgorithmException err) {
			throw new WrappedException(err);
		} catch(UnsupportedEncodingException err) {
			throw new WrappedException(err);
		}
	}

	public static HashedPassword valueOf(String hashedPassword) throws ValidationException {
		return hashedPassword==null ? null : new HashedPassword(hashedPassword);
	}

	final private String hashedPassword;

	private HashedPassword(String hashedPassword) throws ValidationException {
		this.hashedPassword = hashedPassword;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(hashedPassword);
		if(!result.isValid()) throw new ValidationException(result);
	}

	/**
	 * Perform same validation as constructor on readObject.
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		validateObject();
	}

	@Override
	public void validateObject() throws InvalidObjectException {
		try {
			validate();
		} catch(ValidationException err) {
			InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
			newErr.initCause(err);
			throw newErr;
		}
	}

	@Override
	public boolean equals(Object O) {
		return
			(O instanceof HashedPassword)
			&& hashedPassword.equals(((HashedPassword)O).hashedPassword)
		;
	}

	@Override
	public int hashCode() {
		return hashedPassword.hashCode();
	}

	@Override
	public int compareTo(HashedPassword other) {
		return this==other ? 0 : hashedPassword.compareTo(other.hashedPassword);
	}

	@Override
	public String toString() {
		return hashedPassword;
	}

	@Override
	public com.aoindustries.aoserv.client.dto.HashedPassword getDto() {
		return new com.aoindustries.aoserv.client.dto.HashedPassword(hashedPassword);
	}

	@SuppressWarnings("deprecation")
	public boolean passwordMatches(String plaintext) {
		if(hashedPassword.length()==28) return hash(plaintext).equals(hashedPassword);
		if(hashedPassword.length()==13) return com.aoindustries.util.UnixCrypt.crypt(plaintext, hashedPassword.substring(0,2)).equals(hashedPassword);
		return false;
	}
}
