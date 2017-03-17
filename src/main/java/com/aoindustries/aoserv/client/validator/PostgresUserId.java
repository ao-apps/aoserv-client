/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2010-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.PostgresServer;
import com.aoindustries.dto.DtoFactory;
import com.aoindustries.util.Internable;
import com.aoindustries.validation.InvalidResult;
import com.aoindustries.validation.ValidResult;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a PostgreSQL user ID.  PostgreSQL user ids must:
 * <ul>
 *   <li>Be non-null</li>
 *   <li>Be non-empty</li>
 *   <li>Be between 1 and 31 characters</li>
 *   <li>Must start with <code>[a-z]</code></li>
 *   <li>The rest of the characters may contain [a-z], [0-9], and underscore (_)</li>
 *   <li>Must not be a PostgreSQL reserved word</li>
 *   <li>Must be a valid <code>UserId</code> - this is implied by the above rules</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresUserId implements
	Comparable<PostgresUserId>,
	Serializable,
	ObjectInputValidation,
	DtoFactory<com.aoindustries.aoserv.client.dto.PostgresUserId>,
	Internable<PostgresUserId>
{

	private static final long serialVersionUID = -6813817717836611580L;

	/**
	 * The maximum length of a PostgreSQL username.
	 */
	public static final int MAX_LENGTH = 31;

	/**
	 * Validates a user id.
	 */
	public static ValidationResult validate(String id) {
		if(id==null) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.isNull");
		int len = id.length();
		if(len==0) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.isEmpty");
		if(len > MAX_LENGTH) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.tooLong", MAX_LENGTH, len);

		// The first character must be [a-z] or [0-9]
		char ch = id.charAt(0);
		if(
			(ch < 'a' || ch > 'z')
			&& (ch<'0' || ch>'9')
		) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.startAtoZor0to9");

		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = id.charAt(c);
			if (
				(ch<'a' || ch>'z')
				&& (ch<'0' || ch>'9')
				&& ch!='_'
			) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.illegalCharacter");
		}
		if(
			id.equals("sameuser")
			|| id.equals("samegroup")
			|| id.equals("all")
			|| PostgresServer.ReservedWord.isReservedWord(id)
		) return new InvalidResult(ApplicationResources.accessor, "PostgresUserId.validate.reservedWord");
		return ValidResult.getInstance();
	}

	private static final ConcurrentMap<String,PostgresUserId> interned = new ConcurrentHashMap<>();

	public static PostgresUserId valueOf(String id) throws ValidationException {
		//PostgresUserId existing = interned.get(id);
		//return existing!=null ? existing : new PostgresUserId(id);
		return new PostgresUserId(id);
	}

	final private String id;

	private PostgresUserId(String id) throws ValidationException {
		this.id = id;
		validate();
	}

	private void validate() throws ValidationException {
		ValidationResult result = validate(id);
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
			O!=null
			&& O instanceof PostgresUserId
			&& id.equals(((PostgresUserId)O).id)
		;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public int compareTo(PostgresUserId other) {
		return this==other ? 0 : id.compareTo(other.id);
	}

	@Override
	public String toString() {
		return id;
	}

	/**
	 * Interns this id much in the same fashion as <code>String.intern()</code>.
	 *
	 * @see  String#intern()
	 */
	@Override
	public PostgresUserId intern() {
		// Interning implies interning the eqvilalent UserId
		getUserId().intern();
		try {
			PostgresUserId existing = interned.get(id);
			if(existing==null) {
				String internedId = id.intern();
				PostgresUserId addMe = id==internedId ? this : new PostgresUserId(internedId);
				existing = interned.putIfAbsent(internedId, addMe);
				if(existing==null) existing = addMe;
			}
			return existing;
		} catch(ValidationException err) {
			// Should not fail validation since original object passed
			throw new AssertionError(err.getMessage());
		}
	}

	@Override
	public com.aoindustries.aoserv.client.dto.PostgresUserId getDto() {
		return new com.aoindustries.aoserv.client.dto.PostgresUserId(id);
	}

	/**
	 * A PostgresUserId is always a valid UserId.
	 */
	public UserId getUserId() {
		try {
			return UserId.valueOf(id);
		} catch(ValidationException err) {
			throw new AssertionError(err.getMessage());
		}
	}
}
